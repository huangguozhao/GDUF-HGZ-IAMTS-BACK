# 测试执行记录管理模块实现文档

## 概述
基于 `TestExecutionRecords` 数据库表，完整实现了测试执行记录管理模块的后端接口，包括增删改查和统计分析功能。

## 实现内容

### 1. 数据传输对象（DTO）

#### 1.1 查询相关
- **TestExecutionRecordQuery** - 查询参数类
  - 支持多维度筛选（执行范围、执行人、环境、状态等）
  - 支持时间范围筛选
  - 支持分页和排序
  - 文件位置：`src/main/java/com/victor/iatms/entity/query/TestExecutionRecordQuery.java`

#### 1.2 响应相关
- **TestExecutionRecordDetailDTO** - 执行记录详情DTO
  - 包含完整的执行记录信息
  - 包含执行人姓名（关联查询）
  - 提供静态方法 `fromEntity()` 用于实体转换
  - 文件位置：`src/main/java/com/victor/iatms/entity/dto/TestExecutionRecordDetailDTO.java`

- **TestExecutionRecordPageResultDTO** - 分页结果DTO
  - 符合统一分页规范
  - 文件位置：`src/main/java/com/victor/iatms/entity/dto/TestExecutionRecordPageResultDTO.java`

- **TestExecutionRecordStatisticsDTO** - 统计信息DTO
  - 包含执行次数统计
  - 包含用例统计
  - 包含耗时统计
  - 包含成功率统计
  - 文件位置：`src/main/java/com/victor/iatms/entity/dto/TestExecutionRecordStatisticsDTO.java`

#### 1.3 更新相关
- **UpdateTestExecutionRecordDTO** - 更新执行记录DTO
  - 支持部分字段更新
  - 文件位置：`src/main/java/com/victor/iatms/entity/dto/UpdateTestExecutionRecordDTO.java`

### 2. 服务层

#### 2.1 服务接口
**TestExecutionRecordService** - 定义核心业务方法

主要方法：
- `findExecutionRecords()` - 分页查询执行记录
- `findExecutionRecordById()` - 查询执行记录详情
- `findRecentExecutionRecordsByScope()` - 查询最近的执行记录
- `updateExecutionRecord()` - 更新执行记录
- `deleteExecutionRecord()` - 删除执行记录（软删除）
- `batchDeleteExecutionRecords()` - 批量删除
- `getExecutionStatistics()` - 获取统计信息
- `findExecutionRecordsByExecutor()` - 查询执行人的执行记录

文件位置：`src/main/java/com/victor/iatms/service/TestExecutionRecordService.java`

#### 2.2 服务实现
**TestExecutionRecordServiceImpl** - 服务接口实现

特性：
- 完整的参数校验
- 事务管理（更新、删除操作）
- 异常处理和日志记录
- 自动填充执行人姓名
- 分页参数默认值设置

文件位置：`src/main/java/com/victor/iatms/service/impl/TestExecutionRecordServiceImpl.java`

### 3. 数据访问层

#### 3.1 Mapper 接口扩展
在已有的 `TestExecutionRecordMapper` 基础上添加了统计方法：
- `getExecutionStatistics()` - 获取执行统计信息

文件位置：`src/main/java/com/victor/iatms/mappers/TestExecutionRecordMapper.java`

#### 3.2 MyBatis XML 配置
在 `TestExecutionRecordMapper.xml` 中添加了统计查询 SQL：
- 使用聚合函数统计各项指标
- 支持动态条件筛选

文件位置：`src/main/resources/mapper/TestExecutionRecordMapper.xml`

### 4. 控制器层

**TestExecutionRecordController** - REST API 控制器

基础路径：`/execution-records`

实现的接口：
1. `GET /execution-records` - 分页查询执行记录
2. `GET /execution-records/{recordId}` - 查询执行记录详情
3. `GET /execution-records/scope/{executionScope}/{refId}` - 查询指定范围的最近执行记录
4. `PUT /execution-records/{recordId}` - 更新执行记录
5. `DELETE /execution-records/{recordId}` - 删除执行记录
6. `DELETE /execution-records/batch` - 批量删除执行记录
7. `GET /execution-records/statistics` - 获取统计信息
8. `GET /execution-records/executor/{executedBy}` - 查询执行人的执行记录

权限控制：
- 查询操作：需要 `testcase:view` 权限
- 更新操作：需要 `testcase:execute` 权限
- 删除操作：需要 `testcase:delete` 权限

文件位置：`src/main/java/com/victor/iatms/controller/TestExecutionRecordController.java`

### 5. 辅助功能

#### 5.1 UserMapper 扩展
添加了 `findNameById()` 方法用于查询用户姓名，以便在执行记录中显示执行人姓名。

文件位置：
- `src/main/java/com/victor/iatms/mappers/UserMapper.java`
- `src/main/resources/mapper/UserMapper.xml`

## API 接口文档

### 1. 分页查询执行记录

**请求**
```
GET /execution-records?execution_scope=test_case&page=1&page_size=10
```

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| execution_scope | string | 否 | 执行范围类型（api, module, project, test_suite, test_case） |
| ref_id | integer | 否 | 关联ID |
| executed_by | integer | 否 | 执行人ID |
| execution_type | string | 否 | 执行类型（manual, scheduled, triggered） |
| environment | string | 否 | 测试环境 |
| status | string | 否 | 执行状态（running, completed, failed, cancelled） |
| start_time_begin | string | 否 | 开始时间-起始（ISO 8601格式） |
| start_time_end | string | 否 | 开始时间-结束（ISO 8601格式） |
| search_keyword | string | 否 | 关键字搜索 |
| browser | string | 否 | 浏览器类型 |
| app_version | string | 否 | 应用版本 |
| sort_by | string | 否 | 排序字段（默认：start_time） |
| sort_order | string | 否 | 排序顺序（asc/desc，默认：desc） |
| page | integer | 否 | 页码（默认：1） |
| page_size | integer | 否 | 每页条数（默认：10，最大：100） |

**响应示例**
```json
{
  "code": 1,
  "msg": "查询执行记录成功",
  "data": {
    "total": 100,
    "items": [
      {
        "recordId": 1,
        "executionScope": "test_case",
        "refId": 123,
        "scopeName": "测试用例名称",
        "executedBy": 1,
        "executorName": "张三",
        "executionType": "manual",
        "environment": "test",
        "status": "completed",
        "startTime": "2025-10-22T10:00:00",
        "endTime": "2025-10-22T10:05:00",
        "durationSeconds": 300,
        "totalCases": 10,
        "executedCases": 10,
        "passedCases": 8,
        "failedCases": 2,
        "skippedCases": 0,
        "successRate": 80.00,
        "reportUrl": "/api/reports/12345"
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

### 2. 查询执行记录详情

**请求**
```
GET /execution-records/{recordId}
```

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| recordId | long | 是 | 记录ID |

**响应**：返回单个执行记录详情对象

### 3. 查询指定范围的最近执行记录

**请求**
```
GET /execution-records/scope/{executionScope}/{refId}?limit=10
```

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| executionScope | string | 是 | 执行范围类型 |
| refId | integer | 是 | 关联ID |

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| limit | integer | 否 | 限制数量（默认：10，最大：100） |

**响应**：返回执行记录列表

### 4. 更新执行记录

**请求**
```
PUT /execution-records/{recordId}
```

**请求体示例**
```json
{
  "status": "completed",
  "endTime": "2025-10-22T10:05:00",
  "durationSeconds": 300,
  "passedCases": 8,
  "failedCases": 2,
  "successRate": 80.00,
  "reportUrl": "/api/reports/12345"
}
```

**响应**：返回更新后的执行记录详情

### 5. 删除执行记录

**请求**
```
DELETE /execution-records/{recordId}
```

**响应示例**
```json
{
  "code": 1,
  "msg": "删除执行记录成功",
  "data": true
}
```

### 6. 批量删除执行记录

**请求**
```
DELETE /execution-records/batch
```

**请求体示例**
```json
[1, 2, 3, 4, 5]
```

**响应示例**
```json
{
  "code": 1,
  "msg": "批量删除执行记录成功，共删除5条记录",
  "data": 5
}
```

### 7. 获取统计信息

**请求**
```
GET /execution-records/statistics?execution_scope=module&ref_id=10
```

**查询参数**：支持与分页查询相同的筛选参数

**响应示例**
```json
{
  "code": 1,
  "msg": "获取统计信息成功",
  "data": {
    "totalExecutions": 100,
    "runningExecutions": 2,
    "completedExecutions": 85,
    "failedExecutions": 10,
    "cancelledExecutions": 3,
    "avgDurationSeconds": 250.5,
    "maxDurationSeconds": 600,
    "minDurationSeconds": 30,
    "totalCases": 1000,
    "totalPassedCases": 850,
    "totalFailedCases": 120,
    "totalSkippedCases": 30,
    "avgSuccessRate": 85.50
  }
}
```

### 8. 查询执行人的执行记录

**请求**
```
GET /execution-records/executor/{executedBy}?limit=20
```

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| executedBy | integer | 是 | 执行人ID |

**查询参数**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| limit | integer | 否 | 限制数量（默认：10，最大：100） |

**响应**：返回执行记录列表

## 错误处理

所有接口遵循统一的错误响应格式：

### 参数错误（400）
```json
{
  "code": -3,
  "msg": "记录ID不能为空",
  "data": null
}
```

### 资源不存在（404）
```json
{
  "code": -4,
  "msg": "执行记录不存在",
  "data": null
}
```

### 服务器错误（500）
```json
{
  "code": -5,
  "msg": "查询执行记录失败：数据库连接异常",
  "data": null
}
```

## 特性说明

### 1. 权限控制
所有接口都添加了 `@GlobalInterceptor` 注解，进行以下验证：
- 登录验证：`checkLogin = true`
- 权限验证：通过 `checkPermission` 参数指定所需权限

### 2. 软删除
删除操作使用软删除机制：
- 设置 `is_deleted = TRUE`
- 记录删除时间 `deleted_at`
- 记录删除人 `deleted_by`
- 查询时自动过滤已删除记录

### 3. 事务管理
更新和删除操作使用 `@Transactional` 注解确保数据一致性

### 4. 参数校验
- 自动设置默认值（页码、每页条数等）
- 限制最大值（如 `page_size` 最大为100）
- 必填参数验证

### 5. 关联查询
自动填充执行人姓名，提供更友好的数据展示

## 使用示例

### 查询某个模块的所有执行记录
```bash
curl -X GET "http://localhost:8080/execution-records?execution_scope=module&ref_id=10&page=1&page_size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 查询某个用户最近的执行记录
```bash
curl -X GET "http://localhost:8080/execution-records/executor/123?limit=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 更新执行记录状态
```bash
curl -X PUT "http://localhost:8080/execution-records/456" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "completed",
    "successRate": 95.5
  }'
```

### 获取测试环境的统计信息
```bash
curl -X GET "http://localhost:8080/execution-records/statistics?environment=production" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 注意事项

1. **时间格式**：所有时间参数和响应均使用 ISO 8601 格式（`YYYY-MM-DDTHH:mm:ss`）

2. **分页限制**：`page_size` 最大值为 100，超过将自动调整为 100

3. **权限要求**：确保用户具有相应权限才能访问接口

4. **软删除**：删除的记录不会物理删除，但在查询时会被自动过滤

5. **执行记录创建**：执行记录通常由测试执行接口自动创建，本模块主要提供查询和管理功能

## 数据库索引

确保以下索引已创建以优化查询性能：
- `idx_execution_scope_ref` (execution_scope, ref_id)
- `idx_executed_by` (executed_by)
- `idx_start_time` (start_time)
- `idx_status` (status)
- `idx_environment` (environment)
- `idx_created_at` (created_at)

---

**版本**: v1.0
**创建时间**: 2025-10-22
**最后更新**: 2025-10-22
**状态**: ✅ 已完成并测试通过

