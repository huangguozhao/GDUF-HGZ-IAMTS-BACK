# 测试结果列表接口 API 文档

## 接口信息

**接口路径**: `/api/test-results`
**请求方式**: `GET`
**接口描述**: 分页查询测试执行结果列表，支持多种过滤和排序条件。**此接口需要认证。**

## 请求参数

### 请求头 (Headers)

| 参数名          | 类型   | 是否必须 | 备注                             |
| :-------------- | :----- | :------- | :------------------------------- |
| `Authorization` | string | 必须     | 认证令牌，格式: `Bearer {token}` |

### 查询参数 (Query String)

| 参数名称           | 是否必须 | 类型    | 示例                   | 备注                                                         |
| :----------------- | :------- | :------ | :--------------------- | :----------------------------------------------------------- |
| `task_type`        | 否       | string  | `test_case`            | 任务类型过滤。可选: `test_suite`, `test_case`, `project`, `module`, `api_monitor` |
| `ref_id`           | 否       | integer | `101`                  | 根据task_type关联的ID过滤                                    |
| `status`           | 否       | string  | `passed`               | 执行状态过滤。可选: `passed`, `failed`, `broken`, `skipped`, `unknown` |
| `environment`      | 否       | string  | `test`                 | 执行环境过滤                                                 |
| `priority`         | 否       | string  | `P0,P1`                | 优先级过滤（支持多个，逗号分隔）。可选: `P0`, `P1`, `P2`, `P3` |
| `severity`         | 否       | string  | `critical`             | 严重程度过滤。可选: `blocker`, `critical`, `normal`, `minor`, `trivial` |
| `start_time_begin` | 否       | string  | `2024-09-01T00:00:00Z` | 开始时间范围查询（ISO格式）                                  |
| `start_time_end`   | 否       | string  | `2024-09-16T23:59:59Z` | 结束时间范围查询（ISO格式）                                  |
| `duration_min`     | 否       | long    | `1000`                 | 最小执行时长（毫秒）                                         |
| `duration_max`     | 否       | long    | `5000`                 | 最大执行时长（毫秒）                                         |
| `search_keyword`   | 否       | string  | `登录`                 | 关键字搜索（用例名称、失败信息等）                           |
| `sort_by`          | 否       | string  | `start_time`           | 排序字段。可选: `start_time`, `duration`, `priority`, `severity` |
| `sort_order`       | 否       | string  | `desc`                 | 排序顺序。可选: `asc`, `desc`，默认: `desc`                  |
| `page`             | 否       | integer | `1`                    | 分页查询的页码，默认为 `1`                                   |
| `page_size`        | 否       | integer | `20`                   | 分页查询的每页记录数，默认为 `20`，最大 `100`                |

### 请求示例

```
GET /api/test-results
GET /api/test-results?task_type=test_case&status=failed&environment=test&page=1&page_size=50
GET /api/test-results?start_time_begin=2024-09-01T00:00:00&start_time_end=2024-09-16T23:59:59&status=failed
GET /api/test-results?ref_id=101&task_type=test_case&priority=P0,P1&sort_by=start_time&sort_order=desc
GET /api/test-results?search_keyword=超时&duration_min=5000
```

## 响应数据

### 参数格式

`application/json`

### 通用参数说明

| 参数名 | 类型   | 是否必须 | 备注                    |
| :----- | :----- | :------- | :---------------------- |
| `code` | number | 必须     | 业务状态码。`1`代表成功 |
| `msg`  | string | 必须     | 操作的详细结果消息      |
| `data` | object | 必须     | 分页数据对象            |

### 分页数据对象 (`data`) 结构

| 参数名      | 类型     | 说明                     |
| :---------- | :------- | :----------------------- |
| `total`     | number   | 符合条件的数据总条数     |
| `items`     | object[] | 当前页的测试结果数据列表 |
| `page`      | number   | 当前页码                 |
| `page_size` | number   | 当前每页条数             |
| `summary`   | object   | 结果统计摘要             |

### 统计摘要对象 (`summary`) 结构

| 参数名         | 类型   | 说明                 |
| :------------- | :----- | :------------------- |
| `total_count`  | number | 总记录数             |
| `passed`       | number | 通过数               |
| `failed`       | number | 失败数               |
| `broken`       | number | 异常数               |
| `skipped`      | number | 跳过数               |
| `unknown`      | number | 未知数               |
| `success_rate` | number | 成功率（百分比）     |
| `avg_duration` | number | 平均执行时长（毫秒） |

### 测试结果对象 (`items[]`) 结构

| 参数名            | 类型   | 说明                 |
| :---------------- | :----- | :------------------- |
| `result_id`       | number | 结果ID               |
| `report_id`       | number | 报告ID               |
| `execution_id`    | number | 执行记录ID           |
| `task_type`       | string | 任务类型             |
| `ref_id`          | number | 关联对象ID           |
| `ref_name`        | string | 关联对象名称         |
| `full_name`       | string | 完整名称（包含路径） |
| `status`          | string | 执行状态             |
| `duration`        | number | 执行耗时（毫秒）     |
| `start_time`      | string | 开始时间             |
| `end_time`        | string | 结束时间             |
| `priority`        | string | 优先级               |
| `severity`        | string | 严重程度             |
| `environment`     | string | 执行环境             |
| `failure_message` | string | 失败信息（摘要）     |
| `failure_type`    | string | 失败类型             |
| `retry_count`     | number | 重试次数             |
| `browser`         | string | 浏览器信息           |
| `os`              | string | 操作系统             |
| `logs_link`       | string | 日志链接             |
| `screenshot_link` | string | 截图链接             |

### 成功响应示例 (HTTP 200)

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 125,
    "items": [
      {
        "result_id": 10001,
        "report_id": 5001,
        "execution_id": 30001,
        "task_type": "test_case",
        "ref_id": 101,
        "ref_name": "用户登录接口",
        "full_name": "用户管理模块/用户登录接口/用户登录-成功场景",
        "status": "passed",
        "duration": 1245,
        "start_time": "2024-09-16T10:30:00.000Z",
        "end_time": "2024-09-16T10:30:01.245Z",
        "priority": "P0",
        "severity": "critical",
        "environment": "test",
        "failure_message": null,
        "failure_type": null,
        "retry_count": 0,
        "browser": "Chrome 115",
        "os": "Windows 10",
        "logs_link": "/api/test-results/10001/logs",
        "screenshot_link": "/api/test-results/10001/screenshot"
      },
      {
        "result_id": 10002,
        "report_id": 5001,
        "execution_id": 30001,
        "task_type": "test_case",
        "ref_id": 102,
        "ref_name": "用户注册接口",
        "full_name": "用户管理模块/用户注册接口/用户注册-邮箱已存在",
        "status": "failed",
        "duration": 856,
        "start_time": "2024-09-16T10:30:02.000Z",
        "end_time": "2024-09-16T10:30:02.856Z",
        "priority": "P1",
        "severity": "normal",
        "environment": "test",
        "failure_message": "预期状态码为409，实际返回200",
        "failure_type": "assertion_error",
        "retry_count": 1,
        "browser": "Chrome 115",
        "os": "Windows 10",
        "logs_link": "/api/test-results/10002/logs",
        "screenshot_link": "/api/test-results/10002/screenshot"
      }
    ],
    "page": 1,
    "page_size": 20,
    "summary": {
      "total_count": 125,
      "passed": 98,
      "failed": 20,
      "broken": 2,
      "skipped": 5,
      "unknown": 0,
      "success_rate": 78.4,
      "avg_duration": 1567
    }
  }
}
```

### 失败响应示例

```json
// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看测试结果",
  "data": null
}

// 参数错误 (HTTP 400)
{
  "code": -3,
  "msg": "时间范围格式错误",
  "data": null
}
```

### 可能的HTTP状态码

*   `HTTP 200`: 查询成功。
*   `HTTP 400`: 参数错误（如时间格式错误、枚举值无效等）。
*   `HTTP 401`: 未提供Token或Token无效/过期。
*   `HTTP 403`: 权限不足。
*   `HTTP 500`: 服务器内部错误。

## 接口逻辑说明

1.  **认证与授权**: 验证 Token 和用户权限。
2.  **参数校验**: 
    *   校验分页参数（page, page_size）
    *   校验任务类型、执行状态、严重程度、优先级等枚举值
    *   校验时间范围和执行时长范围的逻辑有效性
    *   设置默认排序参数
3.  **构建查询**: 
    *   查询 `TestCaseResults` 表
    *   根据查询参数构建复杂的过滤条件
    *   关联相关表获取引用对象名称信息
4.  **关键字搜索**: 如果提供 `search_keyword`，在多个字段中进行模糊搜索（full_name, failure_message）。
5.  **排序处理**: 根据 `sort_by` 和 `sort_order` 参数进行排序。
6.  **分页处理**: 根据 `page` 和 `page_size` 计算分页偏移量。
7.  **统计摘要**: 计算当前筛选条件下的统计信息（总数、通过数、失败数等）。
8.  **DTO转换**: 将PO对象转换为DTO对象，格式化时间、生成链接等。
9.  **返回结果**: 返回分页的测试结果列表和统计摘要。

## 注意事项

*   该接口支持复杂的多条件筛选，适合构建测试结果分析平台
*   对于大规模数据查询，已建立合适的数据库索引
*   关键字搜索限制在full_name和failure_message字段，避免性能问题
*   统计摘要提供快速的数据概览，便于用户了解整体情况
*   结果中的链接字段提供快速访问详细信息的入口
*   分页最大条数限制为100，避免单次查询数据量过大
*   时间参数需要使用ISO 8601格式
*   优先级参数支持多个值，使用逗号分隔

## 技术实现说明

### 1. 实现的类和文件

#### 实体类和DTO
- `TestResultQuery.java` - 查询参数类
- `TestResultDTO.java` - 测试结果DTO
- `TestResultSummaryDTO.java` - 统计摘要DTO
- `TestResultPageResultDTO.java` - 分页结果DTO（包含统计摘要）
- `ResultSeverityEnum.java` - 测试结果严重程度枚举

#### Mapper层
- `TestExecutionMapper.java` - 添加了3个查询方法
  - `findTestResults()` - 分页查询测试结果列表
  - `countTestResults()` - 统计测试结果总数
  - `getTestResultSummary()` - 统计测试结果摘要
- `TestExecutionMapper.xml` - 添加了对应的SQL映射

#### Service层
- `TestExecutionService.java` - 添加了接口方法声明
  - `getTestResults()` - 分页获取测试结果列表
- `TestExecutionServiceImpl.java` - 实现了接口方法
  - `getTestResults()` - 主要业务逻辑
  - `validateTestResultQuery()` - 参数校验
  - `convertToTestResultDTOList()` - DTO转换
  - `getRefName()` - 获取引用对象名称

#### Controller层
- `TestExecutionController.java` - 添加了REST接口
  - `GET /api/test-results` - 分页获取测试结果列表

### 2. 使用的技术栈

- SpringBoot
- MyBatis
- Lombok
- Jackson (JSON序列化)
- JWT (认证)

### 3. 数据库索引

查询涉及的索引：
- `idx_task_type`
- `idx_ref_id`
- `idx_status`
- `idx_environment`
- `idx_priority`
- `idx_severity`
- `idx_start_time`
- `idx_duration`
- `idx_is_deleted`

### 4. 权限控制

接口使用`@GlobalInterceptor`注解进行权限控制：
- `checkLogin = true` - 需要登录认证
- `checkPermission = {"testcase:view"}` - 需要测试用例查看权限

## 使用示例

### 使用curl测试

```bash
# 获取所有测试结果（第一页，20条）
curl -X GET "http://localhost:8080/api/test-results" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 查询失败的测试用例
curl -X GET "http://localhost:8080/api/test-results?status=failed&page=1&page_size=50" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 查询特定时间范围内的结果
curl -X GET "http://localhost:8080/api/test-results?start_time_begin=2024-09-01T00:00:00&start_time_end=2024-09-30T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 查询高优先级的测试结果
curl -X GET "http://localhost:8080/api/test-results?priority=P0,P1&sort_by=priority&sort_order=asc" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 关键字搜索
curl -X GET "http://localhost:8080/api/test-results?search_keyword=登录&page=1&page_size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 复合查询
curl -X GET "http://localhost:8080/api/test-results?task_type=test_case&ref_id=101&environment=test&status=failed&sort_by=duration&sort_order=desc" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 使用Postman测试

1. 在Postman中创建新的GET请求
2. 输入URL: `http://localhost:8080/api/test-results`
3. 在Headers中添加: `Authorization: Bearer YOUR_TOKEN`
4. 在Params中添加查询参数
5. 发送请求查看响应

## 后续优化建议

1. **性能优化**
   - 考虑增加Redis缓存，缓存常用的查询结果
   - 对于大数据量查询，考虑使用异步查询
   - 增加慢查询日志和监控

2. **功能扩展**
   - 支持导出查询结果（Excel、CSV等）
   - 支持更多的统计维度（按时间段、按模块等）
   - 支持测试结果趋势分析

3. **安全性增强**
   - 增加查询频率限制
   - 增加数据脱敏功能
   - 增加审计日志

4. **用户体验**
   - 提供查询模板保存功能
   - 提供查询历史记录
   - 提供数据可视化功能

