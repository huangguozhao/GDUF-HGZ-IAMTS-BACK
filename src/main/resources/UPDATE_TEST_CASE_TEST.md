# 编辑测试用例接口测试指南

## 接口信息

**请求路径**: `/apis/{api_id}/test-cases/{case_id}`  
**请求方式**: `PUT`  
**Content-Type**: `application/json`  
**认证要求**: 需要Bearer Token认证和testcase:update权限

## 测试步骤

### 1. 先登录获取Token

```bash
POST /auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "登录成功",
  "data": {
    "user": {
      "user_id": 1,
      "name": "管理员",
      "email": "admin@example.com",
      "avatar_url": "https://example.com/avatars/admin.jpg",
      "phone": "13800138000",
      "department_id": 1,
      "employee_id": "EMP001",
      "position": "系统管理员",
      "description": "系统初始管理员账户",
      "status": "active",
      "last_login_time": "2025-09-16T10:30:00.000Z"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 2. 使用Token编辑测试用例

```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "用户登录-成功场景-更新",
  "description": "更新后的测试用例描述",
  "priority": "P0",
  "severity": "critical",
  "tags": ["冒烟测试", "登录功能", "重要"],
  "request_override": "{\"request_body\":{\"username\":\"updated_user\",\"password\":\"NewPassword123!\"}}",
  "expected_http_status": 200,
  "assertions": "[{\"type\":\"status_code\",\"expected\":200},{\"type\":\"json_path\",\"expression\":\"$.code\",\"expected\":1},{\"type\":\"response_time\",\"max_time_ms\":1000}]",
  "is_enabled": true
}
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "测试用例更新成功",
  "data": {
    "case_id": 1001,
    "case_code": "TC-API-101-001",
    "api_id": 101,
    "name": "用户登录-成功场景-更新",
    "priority": "P0",
    "severity": "critical",
    "is_enabled": true,
    "updated_at": "2024-09-16T14:25:00.000Z"
  }
}
```

## 测试用例

### 用例1: 正常更新测试用例

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-成功场景-更新",
  "description": "更新后的测试用例描述",
  "priority": "P0",
  "severity": "critical",
  "is_enabled": true
}
```

**预期响应**: `code: 1, msg: "测试用例更新成功"`，返回更新后的测试用例信息

### 用例2: 部分字段更新

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-新名称",
  "priority": "P1"
}
```

**预期响应**: 只更新提供的字段，其他字段保持不变

### 用例3: 更新用例编码

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "case_code": "TC-API-101-UPDATED",
  "name": "用户登录-更新编码"
}
```

**预期响应**: 更新用例编码，验证编码唯一性

### 用例4: 更新标签

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "tags": ["回归测试", "登录功能", "重要"]
}
```

**预期响应**: 更新标签数组

### 用例5: 清空标签

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "tags": []
}
```

**预期响应**: 清空标签

### 用例6: 更新复杂配置

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "pre_conditions": "[{\"type\":\"setup\",\"description\":\"准备测试数据\"}]",
  "test_steps": "[{\"step\":1,\"action\":\"发送登录请求\",\"expected\":\"返回成功响应\"}]",
  "request_override": "{\"request_body\":{\"username\":\"testuser\",\"password\":\"Password123!\"}}",
  "expected_http_status": 200,
  "expected_response_schema": "{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"number\"}}}",
  "expected_response_body": "{\"code\":1,\"msg\":\"success\"}",
  "assertions": "[{\"type\":\"status_code\",\"expected\":200},{\"type\":\"json_path\",\"expression\":\"$.code\",\"expected\":1}]",
  "extractors": "[{\"name\":\"token\",\"expression\":\"$.data.token\"}]",
  "validators": "[{\"type\":\"json_schema\",\"schema\":\"{\\\"type\\\":\\\"object\\\"}\"}]"
}
```

**预期响应**: 更新所有复杂配置字段

### 用例7: 更新模板用例

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "template_id": 1002,
  "is_template": false
}
```

**预期响应**: 更新模板用例ID和模板状态

### 用例8: 更新版本号

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "version": "2.0"
}
```

**预期响应**: 更新版本号

### 用例9: 无效的优先级

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "priority": "P5"
}
```

**预期响应**: `HTTP 400, code: -3, msg: "无效的优先级值"`

### 用例10: 无效的严重程度

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "severity": "invalid"
}
```

**预期响应**: `HTTP 400, code: -3, msg: "无效的严重程度值"`

### 用例11: 用例编码冲突

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "case_code": "TC-API-101-002"
}
```

**预期响应**: `HTTP 200, code: 0, msg: "用例编码已被其他用例使用"`

### 用例12: 模板用例不存在

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "template_id": 9999
}
```

**预期响应**: `HTTP 200, code: 0, msg: "模板用例不存在"`

### 用例13: 接口不存在

**请求**:
```bash
PUT /apis/999/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-接口不存在"
}
```

**预期响应**: `HTTP 200, code: -4, msg: "接口不存在"`

### 用例14: 测试用例不存在

**请求**:
```bash
PUT /apis/101/test-cases/9999
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-用例不存在"
}
```

**预期响应**: `HTTP 200, code: -4, msg: "测试用例不存在"`

### 用例15: 用例不属于指定接口

**请求**:
```bash
PUT /apis/101/test-cases/2001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-用例不属于接口"
}
```

**预期响应**: `HTTP 200, code: -4, msg: "测试用例不存在"`

### 用例16: 未提供Token

**请求**:
```bash
PUT /apis/101/test-cases/1001
Content-Type: application/json

{
  "name": "用户登录-未认证"
}
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例17: Token无效

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer invalid_token
Content-Type: application/json

{
  "name": "用户登录-无效Token"
}
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例18: 权限不足

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <token_without_permission>
Content-Type: application/json

{
  "name": "用户登录-权限不足"
}
```

**预期响应**: `HTTP 403, code: -2, msg: "权限不足"`

### 用例19: 空请求体

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{}
```

**预期响应**: `code: 1, msg: "测试用例更新成功"`，不更新任何字段

### 用例20: 清空字段

**请求**:
```bash
PUT /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "description": "",
  "pre_conditions": "",
  "test_steps": "",
  "request_override": "",
  "expected_response_schema": "",
  "expected_response_body": "",
  "assertions": "",
  "extractors": "",
  "validators": ""
}
```

**预期响应**: 清空指定的字段

## 请求参数说明

| 参数名 | 类型 | 是否必须 | 说明 | 示例值 |
|--------|------|----------|------|--------|
| api_id | number | 必须 | 接口ID（路径参数） | 101 |
| case_id | number | 必须 | 用例ID（路径参数） | 1001 |
| case_code | string | 否 | 用例编码 | "TC-API-101-001" |
| name | string | 否 | 用例名称 | "用户登录-成功场景" |
| description | string | 否 | 用例描述 | "测试用户登录成功的情况" |
| priority | string | 否 | 优先级 | "P0", "P1", "P2", "P3" |
| severity | string | 否 | 严重程度 | "critical", "high", "medium", "low" |
| tags | string[] | 否 | 标签数组 | ["冒烟测试", "登录功能"] |
| pre_conditions | string | 否 | 前置条件配置（JSON字符串） | "[{\"type\":\"setup\"}]" |
| test_steps | string | 否 | 测试步骤（JSON字符串） | "[{\"step\":1,\"action\":\"发送请求\"}]" |
| request_override | string | 否 | 请求参数覆盖配置（JSON字符串） | "{\"request_body\":{}}" |
| expected_http_status | number | 否 | 预期HTTP状态码 | 200 |
| expected_response_schema | string | 否 | 预期响应Schema（JSON字符串） | "{\"type\":\"object\"}" |
| expected_response_body | string | 否 | 预期响应体 | "{\"code\":1}" |
| assertions | string | 否 | 断言规则（JSON字符串） | "[{\"type\":\"status_code\"}]" |
| extractors | string | 否 | 响应提取规则（JSON字符串） | "[{\"name\":\"token\"}]" |
| validators | string | 否 | 验证器配置（JSON字符串） | "[{\"type\":\"json_schema\"}]" |
| is_enabled | boolean | 否 | 是否启用 | true, false |
| is_template | boolean | 否 | 是否为模板用例 | true, false |
| template_id | number | 否 | 模板用例ID | 1001 |
| version | string | 否 | 版本号 | "1.0" |

## 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| case_id | number | 用例ID |
| case_code | string | 用例编码 |
| api_id | number | 接口ID |
| name | string | 用例名称 |
| priority | string | 优先级 |
| severity | string | 严重程度 |
| is_enabled | boolean | 是否启用 |
| updated_at | string | 更新时间 |

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理方式 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| 0 | 业务逻辑失败 | 200 | 展示msg给用户 |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -3 | 参数校验失败 | 400 | 提示用户检查输入 |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 业务逻辑说明

1. **认证与授权**: 验证Token和用户权限（testcase:update权限）
2. **验证接口**: 检查接口是否存在且状态为active
3. **验证用例**: 检查用例是否存在且属于指定接口
4. **参数校验**: 验证枚举字段值的有效性
5. **编码唯一性**: 如果提供新编码，验证在同一接口下的唯一性
6. **模板验证**: 如果提供templateId，验证模板用例存在
7. **部分更新**: 只更新请求体中提供的字段
8. **返回结果**: 返回更新后的用例基本信息

## 注意事项

1. **部分更新**: 此接口支持部分更新，只修改请求体中提供的字段
2. **编码唯一性**: 在同一接口下，用例编码必须唯一
3. **JSON字段**: 复杂配置字段需要以JSON字符串形式传递
4. **空值处理**: 空字符串会被转换为null值
5. **权限要求**: 需要testcase:update权限和对应API的访问权限
6. **版本管理**: 支持版本号更新，便于版本控制

## 相关接口

- [分页获取接口相关用例列表](./TEST_CASE_LIST_TEST.md)
- [添加测试用例接口](./CREATE_TEST_CASE_TEST.md)
- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_TEST.md)

## TODO: 操作日志记录

**注意**: 根据接口文档要求，更新操作应该记录操作日志，便于审计和追踪变更历史。此功能暂未实现，需要在后续版本中添加。

建议实现：
1. 在更新成功后记录操作日志
2. 记录变更的字段和变更前后的值
3. 记录操作人、操作时间等信息
4. 支持日志查询和审计功能
