# 断言功能增强总结

## 改进内容

### 1. ✅ 详细的断言日志

**改进前：**
```
断言执行完成，通过: 3, 失败: 1
```

**改进后：**
```
========== 断言执行详情 ==========
断言 #1 [status_code]
  期望值: 200
  实际值: 200
  结果: ✓ 通过

断言 #2 [json_path]
  期望值: 1
  实际值: 1
  结果: ✓ 通过

断言 #3 [json_path]
  期望值: 登录成功
  实际值: 登录失败
  结果: ✗ 失败
  错误: 字段 $.msg 值不匹配: 期望 登录成功，实际 登录失败

断言 #4 [json_path_exists]
  期望值: 字段存在
  实际值: 存在
  结果: ✓ 通过

总计: 4 个断言，通过 3，失败 1
=====================================
```

### 2. ✅ 失败信息记录到数据库

断言失败时，会自动生成详细的失败信息并保存到 `TestCaseResults` 表：

```sql
failure_message = "断言失败：1/4 个断言未通过
  - [json_path] 字段 $.msg 值不匹配: 期望 登录成功，实际 登录失败"

failure_type = "ASSERTION_FAILED"
```

### 3. ✅ 返回给前端的详细信息

**响应示例：**
```json
{
  "code": 1,
  "msg": "用例执行完成",
  "data": {
    "executionId": 1761011584992,
    "caseId": 1,
    "caseName": "正常登录测试",
    "status": "failed",
    "duration": 507,
    "responseStatus": 200,
    "responseBody": "{\"code\":0,\"msg\":\"登录失败\",\"data\":null}",
    "assertionsPassed": 3,
    "assertionsFailed": 1,
    "assertionDetails": [
      {
        "assertionId": 1,
        "assertionType": "status_code",
        "description": "验证HTTP状态码",
        "expectedValue": "200",
        "actualValue": "200",
        "passed": true,
        "errorMessage": null
      },
      {
        "assertionId": 2,
        "assertionType": "json_path",
        "description": "验证JSON字段值",
        "expectedValue": "1",
        "actualValue": "0",
        "passed": false,
        "errorMessage": "字段 $.code 值不匹配: 期望 1，实际 0",
        "jsonPath": "$.code"
      },
      {
        "assertionId": 3,
        "assertionType": "json_path",
        "description": "验证JSON字段值",
        "expectedValue": "登录成功",
        "actualValue": "登录失败",
        "passed": false,
        "errorMessage": "字段 $.msg 值不匹配: 期望 登录成功，实际 登录失败",
        "jsonPath": "$.msg"
      },
      {
        "assertionId": 4,
        "assertionType": "json_path_exists",
        "description": "验证JSON字段存在",
        "expectedValue": "字段存在",
        "actualValue": "不存在",
        "passed": false,
        "errorMessage": "字段 $.data.token 不存在",
        "jsonPath": "$.data.token"
      }
    ],
    "failureMessage": "断言失败：3/4 个断言未通过\n  - [json_path] 字段 $.code 值不匹配: 期望 1，实际 0\n  - [json_path] 字段 $.msg 值不匹配: 期望 登录成功，实际 登录失败\n  - [json_path_exists] 字段 $.data.token 不存在\n",
    "failureType": "ASSERTION_FAILED",
    "extractedVariables": {},
    "logsLink": "/api/test-results/1761011584992/logs",
    "reportId": 29
  }
}
```

## 数据库记录增强

### TestCaseResults 表字段

| 字段 | 说明 | 示例值 |
|------|------|--------|
| `failure_message` | 详细的失败信息 | "断言失败：1/4 个断言未通过\n  - [json_path] 字段 $.msg 值不匹配..." |
| `failure_type` | 失败类型 | "ASSERTION_FAILED" |
| `failure_trace` | 堆栈跟踪（如果有异常） | "java.lang.AssertionError: ..." |
| `steps_json` | 执行步骤详情（包含断言） | JSON数组 |
| `parameters_json` | 请求参数和响应参数 | `{"request": {...}, "response": {...}}` |

## 前端展示建议

### 1. 断言结果表格

| # | 断言类型 | 描述 | 期望值 | 实际值 | 结果 |
|---|---------|------|--------|--------|------|
| 1 | status_code | 验证HTTP状态码 | 200 | 200 | ✓ 通过 |
| 2 | json_path | 验证JSON字段值 | 1 | 0 | ✗ 失败 |
| 3 | json_path | 验证JSON字段值 | 登录成功 | 登录失败 | ✗ 失败 |
| 4 | json_path_exists | 验证JSON字段存在 | 字段存在 | 不存在 | ✗ 失败 |

### 2. 失败断言详情卡片

```
❌ 断言失败 (3/4)

断言 #2 失败
类型: json_path
路径: $.code  
期望: 1
实际: 0
错误: 字段 $.code 值不匹配

断言 #3 失败
类型: json_path
路径: $.msg
期望: 登录成功
实际: 登录失败
错误: 字段 $.msg 值不匹配
```

### 3. 响应对比视图

```
期望响应:
{
  "code": 1,
  "msg": "登录成功",
  "data": {
    "token": "*"
  }
}

实际响应:
{
  "code": 0,        ← 不匹配
  "msg": "登录失败",  ← 不匹配
  "data": null      ← 字段不存在
}
```

## 自动断言生成规则

当 `assertions` 字段为空时，系统会根据 `expected_response_body` 自动生成断言：

### 生成规则

| expected_response_body | 生成的断言 |
|----------------------|-----------|
| `{"code": 1}` | `json_path: $.code = 1` |
| `{"msg": "成功"}` | `json_path: $.msg = "成功"` |
| `{"token": "*"}` | `json_path_exists: $.token` |
| `{"data": {"*"}}` | 验证 $.data 对象存在 |

### 示例

**输入：**
```json
{
  "expected_http_status": 200,
  "expected_response_body": {
    "code": 1,
    "msg": "登录成功",
    "data": {
      "token": "*",
      "userId": "*"
    }
  }
}
```

**自动生成的断言：**
```json
[
  {"type": "status_code", "expected": 200},
  {"type": "json_path", "path": "$.code", "expected": "1"},
  {"type": "json_path", "path": "$.msg", "expected": "登录成功"},
  {"type": "json_path_exists", "path": "$.data.token"},
  {"type": "json_path_exists", "path": "$.data.userId"}
]
```

## 支持的断言类型

| 类型 | 说明 | 参数 | 示例 |
|------|------|------|------|
| `status_code` | HTTP状态码验证 | `expected` | `{"type": "status_code", "expected": 200}` |
| `json_path` | JSON字段值验证 | `path`, `expected` | `{"type": "json_path", "path": "$.code", "expected": "1"}` |
| `json_path_exists` | JSON字段存在性验证 | `path` | `{"type": "json_path_exists", "path": "$.data.token"}` |
| `response_time` | 响应时间验证 | `max_ms` | `{"type": "response_time", "max_ms": 1000}` |
| `schema` | JSON Schema验证 | `schema` | `{"type": "schema", "schema": {...}}` |

## 使用建议

### 方式1：简化配置（推荐）

只定义 `expected_response_body`，系统自动生成断言：

```sql
UPDATE TestCases SET
    expected_http_status = 200,
    expected_response_body = '{"code":1,"msg":"登录成功","data":{"token":"*"}}',
    assertions = NULL  -- 留空，自动生成
WHERE case_id = 1;
```

### 方式2：完整配置（精细控制）

手动定义所有断言规则：

```sql
UPDATE TestCases SET
    expected_http_status = 200,
    expected_response_body = '{"code":1,"msg":"登录成功"}',
    assertions = '[
        {"type": "status_code", "expected": 200},
        {"type": "json_path", "path": "$.code", "expected": "1"},
        {"type": "json_path", "path": "$.msg", "expected": "登录成功"},
        {"type": "json_path_exists", "path": "$.data.token"},
        {"type": "response_time", "max_ms": 1000}
    ]'
WHERE case_id = 1;
```

## 总结

✅ **详细日志**：每个断言的执行结果都有详细记录
✅ **数据库保存**：失败信息完整保存到 `TestCaseResults` 表
✅ **前端展示**：返回详细的断言列表，便于前端展示
✅ **自动生成**：支持自动生成基础断言，简化配置
✅ **易于调试**：清晰的日志和错误信息帮助快速定位问题

