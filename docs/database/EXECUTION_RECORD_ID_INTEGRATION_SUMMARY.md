# TestCaseResults 表添加 execution_record_id 字段实现总结

## 概述
在 `TestCaseResults` 表中添加 `execution_record_id` 字段，用于关联 `TestExecutionRecords` 表，建立测试结果与执行记录之间的关系。

## 数据库变更

### 1. 字段添加
```sql
ALTER TABLE TestCaseResults 
ADD COLUMN execution_record_id BIGINT NOT NULL COMMENT '测试执行记录ID，关联TestExecutionRecords表' AFTER result_id;
```

**字段说明**：
- **字段名**: `execution_record_id`
- **类型**: `BIGINT`
- **约束**: `NOT NULL`
- **位置**: 在 `result_id` 字段之后
- **用途**: 关联 `TestExecutionRecords` 表的主键

### 2. 索引创建
```sql
CREATE INDEX idx_execution_record_id ON TestCaseResults(execution_record_id);
```

**索引说明**：
- **索引名**: `idx_execution_record_id`
- **用途**: 优化通过执行记录ID查询测试结果的性能

## 代码变更

### 1. 实体类修改
**文件**: `src/main/java/com/victor/iatms/entity/po/TestCaseResult.java`

**变更内容**：
```java
/**
 * 测试执行记录ID，关联TestExecutionRecords表
 */
private Long executionRecordId;
```

**位置**: 在 `resultId` 字段之后添加

### 2. Mapper XML 修改
**文件**: `src/main/resources/mapper/TestExecutionMapper.xml`

**变更内容**：在 `insertTestCaseResult` 的 SQL 语句中添加 `execution_record_id` 字段

**修改前**：
```xml
INSERT INTO TestCaseResults (
    report_id, execution_id, task_type, ...
) VALUES (
    #{reportId}, #{executionId}, #{taskType}, ...
)
```

**修改后**：
```xml
INSERT INTO TestCaseResults (
    execution_record_id, report_id, execution_id, task_type, ...
) VALUES (
    #{executionRecordId}, #{reportId}, #{executionId}, #{taskType}, ...
)
```

### 3. 服务层修改
**文件**: `src/main/java/com/victor/iatms/service/impl/TestExecutionServiceImpl.java`

#### 3.1 修改方法签名
**方法**: `buildTestCaseResult`

**修改前**：
```java
private TestCaseResult buildTestCaseResult(
    TestCaseExecutionDTO executionDTO, 
    Long executionId, 
    Integer userId)
```

**修改后**：
```java
private TestCaseResult buildTestCaseResult(
    TestCaseExecutionDTO executionDTO, 
    Long executionId, 
    Long executionRecordId,  // 新增参数
    Integer userId)
```

#### 3.2 设置执行记录ID
在 `buildTestCaseResult` 方法中添加：
```java
result.setExecutionRecordId(executionRecordId);
```

#### 3.3 修改调用位置
在 `executeTestCase` 方法中：

**修改前**：
```java
TestCaseResult testCaseResult = buildTestCaseResult(result, executionId, userId);
```

**修改后**：
```java
TestCaseResult testCaseResult = buildTestCaseResult(
    result, 
    executionId, 
    executionRecord.getRecordId(),  // 传入执行记录ID
    userId
);
```

## 数据流程

### 执行测试用例时的数据流
1. **创建执行记录**
   ```java
   TestExecutionRecord executionRecord = createExecutionRecord(...);
   testExecutionRecordMapper.insertExecutionRecord(executionRecord);
   // executionRecord.getRecordId() 被自动设置（数据库自增）
   ```

2. **执行测试用例**
   ```java
   TestCaseExecutionDTO result = testCaseExecutor.executeTestCase(executionDTO);
   ```

3. **创建测试结果并关联执行记录**
   ```java
   TestCaseResult testCaseResult = buildTestCaseResult(
       result, 
       executionId, 
       executionRecord.getRecordId(),  // 关联执行记录ID
       userId
   );
   testExecutionMapper.insertTestCaseResult(testCaseResult);
   ```

4. **更新执行记录状态**
   ```java
   updateExecutionRecordOnCompletion(executionRecord, result, reportId);
   testExecutionRecordMapper.updateExecutionRecord(executionRecord);
   ```

## 数据关联关系

```
TestExecutionRecords (执行记录表)
    ↓ (1:N)
TestCaseResults (测试结果表)

一个执行记录可以包含多个测试结果
每个测试结果必须关联一个执行记录
```

### 关联查询示例

#### 查询执行记录及其所有测试结果
```sql
SELECT 
    ter.record_id,
    ter.scope_name,
    ter.status as execution_status,
    ter.start_time,
    ter.end_time,
    tcr.result_id,
    tcr.full_name as case_name,
    tcr.status as case_status,
    tcr.duration
FROM 
    TestExecutionRecords ter
    LEFT JOIN TestCaseResults tcr ON ter.record_id = tcr.execution_record_id
WHERE 
    ter.record_id = ?
    AND ter.is_deleted = FALSE
    AND tcr.is_deleted = FALSE;
```

#### 统计执行记录的测试结果
```sql
SELECT 
    ter.record_id,
    ter.scope_name,
    COUNT(tcr.result_id) as total_results,
    SUM(CASE WHEN tcr.status = 'passed' THEN 1 ELSE 0 END) as passed_count,
    SUM(CASE WHEN tcr.status = 'failed' THEN 1 ELSE 0 END) as failed_count
FROM 
    TestExecutionRecords ter
    LEFT JOIN TestCaseResults tcr ON ter.record_id = tcr.execution_record_id
WHERE 
    ter.executed_by = ?
    AND ter.start_time >= ?
    AND ter.is_deleted = FALSE
    AND tcr.is_deleted = FALSE
GROUP BY 
    ter.record_id, ter.scope_name;
```

## 使用场景

### 1. 查询执行记录的详细结果
```java
// 根据执行记录ID查询所有测试结果
Long executionRecordId = 12345L;
List<TestCaseResult> results = testCaseResultMapper
    .findByExecutionRecordId(executionRecordId);
```

### 2. 追溯测试结果的执行信息
```java
// 通过测试结果找到对应的执行记录
TestCaseResult result = testCaseResultMapper.findById(resultId);
TestExecutionRecord executionRecord = testExecutionRecordMapper
    .findExecutionRecordById(result.getExecutionRecordId());
```

### 3. 批量分析执行历史
```java
// 查询某个时间段内的所有执行及其结果
List<ExecutionWithResults> executions = executionRecordMapper
    .findExecutionsWithResults(startTime, endTime);
```

## 兼容性说明

### 已有数据处理
如果表中已有数据，添加 `NOT NULL` 字段需要注意：

**方案1**：如果表中已有数据，先添加为可空字段
```sql
ALTER TABLE TestCaseResults 
ADD COLUMN execution_record_id BIGINT NULL COMMENT '测试执行记录ID，关联TestExecutionRecords表' AFTER result_id;

-- 为已有数据填充默认值或迁移数据
UPDATE TestCaseResults SET execution_record_id = 0 WHERE execution_record_id IS NULL;

-- 再修改为 NOT NULL
ALTER TABLE TestCaseResults MODIFY COLUMN execution_record_id BIGINT NOT NULL;
```

**方案2**：先清空测试数据
```sql
-- 如果是测试环境，可以先清空数据
TRUNCATE TABLE TestCaseResults;

-- 然后再添加 NOT NULL 字段
ALTER TABLE TestCaseResults 
ADD COLUMN execution_record_id BIGINT NOT NULL COMMENT '测试执行记录ID，关联TestExecutionRecords表' AFTER result_id;
```

## 性能影响

### 1. 写入性能
- **影响**: 每次插入测试结果时需要写入额外的 `execution_record_id` 字段
- **评估**: 影响极小，只是一个 `BIGINT` 字段

### 2. 查询性能
- **优化**: 已创建索引 `idx_execution_record_id`
- **适用**: 通过执行记录ID查询测试结果的场景
- **效果**: 大幅提升关联查询性能

### 3. 存储空间
- **增加**: 每条记录增加 8 字节（BIGINT）
- **估算**: 100万条记录约增加 8MB 存储空间

## 测试建议

### 1. 单元测试
```java
@Test
public void testExecutionRecordIdMapping() {
    // 创建执行记录
    TestExecutionRecord record = new TestExecutionRecord();
    // ... 设置属性
    testExecutionRecordMapper.insertExecutionRecord(record);
    
    // 创建测试结果并关联
    TestCaseResult result = new TestCaseResult();
    result.setExecutionRecordId(record.getRecordId());
    // ... 设置其他属性
    testExecutionMapper.insertTestCaseResult(result);
    
    // 验证关联
    TestCaseResult saved = testExecutionMapper.findById(result.getResultId());
    assertEquals(record.getRecordId(), saved.getExecutionRecordId());
}
```

### 2. 集成测试
```java
@Test
public void testExecuteTestCaseWithRecordId() {
    // 执行测试用例
    ExecutionResultDTO result = testExecutionService.executeTestCase(
        caseId, executeDTO, userId);
    
    // 验证执行记录创建
    List<TestExecutionRecord> records = testExecutionRecordMapper
        .findExecutionRecordsByScope("test_case", caseId, 1);
    assertFalse(records.isEmpty());
    
    // 验证测试结果关联了执行记录
    TestCaseResult caseResult = testExecutionMapper
        .findTestCaseResultByExecutionId(result.getExecutionId());
    assertEquals(records.get(0).getRecordId(), caseResult.getExecutionRecordId());
}
```

## 注意事项

1. **数据一致性**: 确保在同一个事务中创建执行记录和测试结果，保证 `execution_record_id` 的有效性

2. **外键约束**: 建议添加外键约束（可选）
   ```sql
   ALTER TABLE TestCaseResults 
   ADD CONSTRAINT fk_execution_record 
   FOREIGN KEY (execution_record_id) 
   REFERENCES TestExecutionRecords(record_id);
   ```

3. **级联删除**: 如果使用外键约束，考虑级联行为
   ```sql
   ON DELETE CASCADE  -- 删除执行记录时同时删除测试结果
   -- 或
   ON DELETE RESTRICT -- 禁止删除有关联结果的执行记录
   ```

4. **软删除**: 由于使用软删除机制，建议不使用外键的级联删除

## 部署步骤

### 1. 执行数据库迁移
```bash
mysql -u username -p database_name < add_execution_record_id_to_test_case_results.sql
```

### 2. 部署应用代码
- 构建新版本应用
- 部署到测试环境验证
- 部署到生产环境

### 3. 验证功能
- 执行测试用例
- 检查数据库中的 `execution_record_id` 是否正确填充
- 验证关联查询功能

## 回滚方案

如果需要回滚：

### 1. 回滚数据库
```sql
-- 删除索引
DROP INDEX idx_execution_record_id ON TestCaseResults;

-- 删除字段
ALTER TABLE TestCaseResults DROP COLUMN execution_record_id;
```

### 2. 回滚代码
- 恢复到之前的代码版本
- 重新部署应用

---

**实现时间**: 2025-10-22  
**实现人**: AI Assistant  
**版本**: v1.0  
**状态**: ✅ 已完成

