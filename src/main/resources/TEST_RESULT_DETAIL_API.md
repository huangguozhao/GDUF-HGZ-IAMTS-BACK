# 测试结果详情接口 API 文档

## 接口信息

**接口路径**: `/api/test-results/{result_id}`
**请求方式**: `GET`
**接口描述**: 获取指定测试结果的详细信息，包括执行步骤、断言结果、附件信息等。**此接口需要认证。**

## 请求参数

### 请求头 (Headers)

| 参数名          | 类型   | 是否必须 | 备注                             |
| :-------------- | :----- | :------- | :------------------------------- |
| `Authorization` | string | 必须     | 认证令牌，格式: `Bearer {token}` |

### 路径参数 (Path Parameters)

| 参数名      | 类型   | 是否必须 | 备注       |
| :---------- | :----- | :------- | :--------- |
| `result_id` | number | 必须     | 测试结果ID |

### 查询参数 (Query String)

| 参数名称              | 是否必须 | 类型    | 示例    | 备注                                     |
| :-------------------- | :------- | :------ | :------ | :--------------------------------------- |
| `include_steps`       | 否       | boolean | `true`  | 是否包含详细的测试步骤信息，默认: `true` |
| `include_assertions`  | 否       | boolean | `true`  | 是否包含断言详情，默认: `true`           |
| `include_artifacts`   | 否       | boolean | `false` | 是否包含附件信息，默认: `false`          |
| `include_environment` | 否       | boolean | `true`  | 是否包含环境信息，默认: `true`           |

### 请求示例

```
GET /api/test-results/10001
GET /api/test-results/10001?include_steps=true&include_assertions=true
GET /api/test-results/10001?include_artifacts=true
GET /api/test-results/10001?include_steps=false&include_assertions=false&include_environment=false
```

## 响应数据

### 成功响应示例 (HTTP 200)

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "result_info": {
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
      "retry_count": 0,
      "flaky": false
    },
    "execution_context": {
      "environment": "test",
      "base_url": "https://test-api.example.com",
      "request_url": "https://test-api.example.com/auth/login",
      "request_method": "POST",
      "request_headers": {
        "Content-Type": "application/json",
        "Authorization": "Bearer ***"
      },
      "request_body": "{\"username\":\"testuser\",\"password\":\"Test123!\"}",
      "response_status": 200,
      "response_headers": {
        "Content-Type": "application/json"
      },
      "response_body": "{\"code\":1,\"msg\":\"success\",\"data\":{...}}",
      "response_size": 156,
      "variables": {
        "username": "testuser"
      }
    },
    "test_steps": [
      {
        "step_id": 1,
        "name": "发送登录请求",
        "description": "向认证接口发送登录请求",
        "status": "passed",
        "duration": 450,
        "start_time": "2024-09-16T10:30:00.100Z",
        "end_time": "2024-09-16T10:30:00.550Z",
        "parameters": {
          "url": "https://test-api.example.com/auth/login",
          "method": "POST"
        },
        "logs": [
          "请求发送成功，耗时450ms",
          "响应状态码: 200"
        ]
      }
    ],
    "assertions": [],
    "artifacts": [
      {
        "type": "log",
        "name": "execution.log",
        "url": "/api/test-results/10001/logs",
        "size": null
      },
      {
        "type": "screenshot",
        "name": "screenshot.png",
        "url": "/api/test-results/10001/screenshot",
        "size": null
      }
    ],
    "environment": {
      "browser": "Chrome 115",
      "os": "Windows 10",
      "device": "Desktop",
      "screen_resolution": "1920x1080",
      "language": "zh-CN",
      "timezone": "UTC+8"
    },
    "performance": {
      "response_time": 1245,
      "throughput": 0.80,
      "memory_usage": 256,
      "cpu_usage": 15.5
    }
  }
}
```

### 失败响应示例

```json
// 测试结果不存在 (HTTP 404)
{
  "code": -4,
  "msg": "测试结果不存在",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看该测试结果",
  "data": null
}

// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

## 接口逻辑说明

1. **认证与授权**: 验证 Token 和用户权限
2. **验证测试结果**: 根据 result_id 检查测试结果是否存在
3. **数据查询**: 从 TestCaseResults 表查询完整信息
4. **JSON解析**: 解析 steps_json, parameters_json, attachments_json 等字段
5. **数据处理**: 
   - 根据查询参数决定返回哪些详细信息
   - 敏感信息脱敏处理
   - 时间格式化
6. **构建响应**: 返回结构化的测试结果详情

## 技术实现说明

### 实现的文件

- `TestResultDetailDTO.java` - 详情总DTO
- `TestResultInfoDTO.java` - 基本信息DTO
- `ExecutionContextDTO.java` - 执行上下文DTO
- `TestStepDTO.java` - 测试步骤DTO
- `AssertionDTO.java` - 断言结果DTO
- `ArtifactDTO.java` - 附件信息DTO
- `EnvironmentInfoDTO.java` - 环境信息DTO
- `PerformanceDTO.java` - 性能指标DTO

### Mapper层
- 添加了 `findTestResultById()` 方法
- 对应的SQL映射在 `TestExecutionMapper.xml`

### Service层
- 添加了 `getTestResultDetail()` 方法
- 实现了多个辅助方法用于构建各部分数据

### Controller层
- 添加了 `GET /api/test-results/{result_id}` 接口
- 支持4个可选的查询参数控制返回内容

## 使用示例

### curl测试

```bash
# 获取完整详情
curl -X GET "http://localhost:8080/api/test-results/1"

# 只获取基本信息和执行上下文
curl -X GET "http://localhost:8080/api/test-results/1?include_steps=false&include_assertions=false&include_environment=false"

# 包含附件信息
curl -X GET "http://localhost:8080/api/test-results/1?include_artifacts=true"
```

### Postman测试

1. 创建GET请求
2. URL: `http://localhost:8080/api/test-results/1`
3. 添加查询参数（可选）
4. 发送请求

## 注意事项

- 响应体可能较大，建议前端做分页或懒加载处理
- 敏感信息（如密码）会自动脱敏
- 默认包含测试步骤、断言和环境信息，不包含附件信息
- 附件信息仅当explicitly请求时返回（include_artifacts=true）
- 性能指标中的吞吐量自动计算（1000/duration）

## 权限控制

使用 `@GlobalInterceptor` 注解：
- `checkLogin = true` - 需要登录
- `checkPermission = {"testcase:view"}` - 需要测试用例查看权限

## 测试建议

1. 先测试基本查询（无参数）
2. 测试不同的参数组合
3. 测试不存在的result_id（应返回404）
4. 测试性能（大数据量时的响应时间）



