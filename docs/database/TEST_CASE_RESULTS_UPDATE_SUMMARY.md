# TestCaseResults表结构升级总结

## 更新概述

根据新的数据库表结构需求，对`TestCaseResults`表进行了重大升级，添加了20个新字段以支持更详细的测试结果分析、追踪和管理功能。

## 新增字段说明

### 1. 用例关联字段
- `case_id` (INT): 关联TestCases表的用例ID，建立正式外键关系
- `case_code` (VARCHAR): 用例编码，方便快速识别
- `case_name` (VARCHAR): 用例名称，冗余存储提高查询性能

### 2. 层级信息字段
- `module_name` (VARCHAR): 模块名称
- `api_name` (VARCHAR): 接口名称
- `suite_name` (VARCHAR): 测试套件名称
- `package_name` (VARCHAR): 包名/命名空间

### 3. Allure/BDD相关字段
- `epic_name` (VARCHAR): Epic名称
- `feature_name` (VARCHAR): Feature名称
- `story_name` (VARCHAR): Story名称

### 4. 测试分类字段
- `test_layer` (ENUM): 测试层级（UNIT, INTEGRATION, API, E2E, PERFORMANCE, SECURITY）
- `test_type` (ENUM): 测试类型（POSITIVE, NEGATIVE, BOUNDARY, SECURITY, PERFORMANCE, USABILITY）

### 5. 不稳定用例追踪字段
- `flaky_count` (INT): 不稳定次数统计
- `last_flaky_time` (DATETIME): 最后一次不稳定时间

### 6. JSON扩展字段
- `history_trend` (JSON): 历史趋势数据
- `custom_labels` (JSON): 自定义标签

### 7. 问题分析字段
- `root_cause_analysis` (TEXT): 根因分析描述
- `impact_assessment` (ENUM): 影响评估（HIGH, MEDIUM, LOW）

### 8. 复测相关字段
- `retest_result` (ENUM): 复测结果（PASSED, FAILED, NOT_RETESTED）
- `retest_notes` (TEXT): 复测备注

## 代码修改清单

### 1. 实体类更新
- ✅ `TestCaseResult.java` - 添加所有新字段的属性定义

### 2. Mapper XML更新

#### TestExecutionMapper.xml
- ✅ `insertTestCaseResult` - 更新INSERT语句包含所有新字段
- ✅ `findTestCaseResultsByReportId` - 更新SELECT查询
- ✅ `findTestCaseResultByExecutionId` - 更新SELECT查询
- ✅ `updateTestCaseResult` - 更新UPDATE语句
- ✅ `findTestResults` - 更新分页查询
- ✅ `findTestResultById` - 更新详情查询

#### ReportMapper.xml
- ✅ `selectReportTestResults` - 更新报告导出查询，使用COALESCE优先取结果表字段
- ✅ `selectReportStatistics` - 更新统计查询，使用COALESCE处理优先级和严重程度

### 3. Service实现更新

#### TestExecutionServiceImpl.java
- ✅ `buildTestCaseResult` - 主要构建方法，添加新字段设置逻辑
- ✅ `recordModuleTestCaseResult` - 模块测试结果记录
- ✅ `recordModuleTestCaseFailure` - 模块测试失败记录
- ✅ `recordProjectTestCaseResult` - 项目测试结果记录
- ✅ `recordProjectTestCaseFailure` - 项目测试失败记录
- ✅ `recordApiTestCaseResult` - 接口测试结果记录
- ✅ `recordApiTestCaseFailure` - 接口测试失败记录
- ✅ `recordSuiteTestCaseResult` - 测试套件结果记录
- ✅ `recordSuiteTestCaseFailure` - 测试套件失败记录

## 数据库索引优化

新增索引以提升查询性能：
```sql
idx_case_id (case_id)           -- 用例ID查询
idx_test_layer (test_layer)     -- 按测试层级过滤
idx_suite_name (suite_name)     -- 按套件名称查询
idx_package_name (package_name) -- 按包名查询
idx_epic_name (epic_name)       -- 按Epic查询
```

## 向后兼容性

所有新增字段均设置为NULL或有默认值，确保向后兼容：
- 数值字段默认为0
- 枚举字段有默认值（如`test_layer='API'`, `test_type='POSITIVE'`, `retest_result='NOT_RETESTED'`）
- 字符串字段允许NULL

## 使用建议

### 1. 设置新字段值
在创建TestCaseResult时，建议从TestCase对象中提取以下信息：
```java
testCaseResult.setCaseId(testCase.getCaseId());
testCaseResult.setCaseCode(testCase.getCaseCode());
testCaseResult.setCaseName(testCase.getName());
testCaseResult.setTestLayer("API");
testCaseResult.setTestType("POSITIVE");
testCaseResult.setFlakyCount(0);
testCaseResult.setRetestResult("NOT_RETESTED");
```

### 2. 查询优化
查询报告统计时，优先使用TestCaseResults表中的字段：
```sql
SELECT 
    COALESCE(tcr.case_code, tc.case_code) AS caseCode,
    COALESCE(tcr.priority, tc.priority) AS priority,
    COALESCE(tcr.severity, tc.severity) AS severity
FROM TestCaseResults tcr
LEFT JOIN TestCases tc ON tcr.ref_id = tc.case_id
```

### 3. 数据迁移
如需迁移历史数据，可执行SQL文件中的数据迁移脚本部分。

## 验证测试

### 1. 单元测试
- 验证实体类字段映射正确
- 验证INSERT/UPDATE/SELECT操作正常

### 2. 集成测试
- 测试用例执行后结果记录完整
- 报告导出包含新字段信息
- 统计查询性能符合预期

### 3. 数据库测试
```sql
-- 验证表结构
DESC TestCaseResults;

-- 验证索引
SHOW INDEX FROM TestCaseResults;

-- 验证数据
SELECT * FROM TestCaseResults 
WHERE is_deleted = FALSE 
LIMIT 10;
```

## 升级步骤

1. **备份数据库**
   ```bash
   mysqldump -u username -p database_name TestCaseResults > backup_test_case_results.sql
   ```

2. **执行升级脚本**
   ```bash
   mysql -u username -p database_name < update_test_case_results_table.sql
   ```

3. **验证升级结果**
   - 检查表结构
   - 验证索引创建
   - 测试应用程序

4. **部署新代码**
   - 更新实体类
   - 更新Mapper XML
   - 更新Service实现
   - 重启应用

## 注意事项

1. **性能考虑**：新增字段为冗余存储，权衡了查询性能与存储空间
2. **数据一致性**：确保新字段值与TestCases表保持同步
3. **扩展性**：JSON字段(`history_trend`, `custom_labels`)提供灵活扩展能力
4. **测试覆盖**：确保所有涉及TestCaseResult的功能都经过充分测试

## 完成状态

✅ 所有计划任务已完成
- 实体类更新
- Mapper XML更新
- Service实现更新
- SQL升级脚本
- 文档编写

## 相关文件

- `update_test_case_results_table.sql` - 数据库升级脚本
- `src/main/java/com/victor/iatms/entity/po/TestCaseResult.java` - 实体类
- `src/main/resources/mapper/TestExecutionMapper.xml` - 执行Mapper
- `src/main/resources/mapper/ReportMapper.xml` - 报告Mapper
- `src/main/java/com/victor/iatms/service/impl/TestExecutionServiceImpl.java` - 执行Service

