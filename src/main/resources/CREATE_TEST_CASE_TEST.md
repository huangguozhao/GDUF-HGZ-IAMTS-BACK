# 添加测试用例接口测试指南

## 接口信息

**请求路径**: `/apis/{api_id}/test-cases`  
**请求方式**: `POST`  
**Content-Type**: `application/json`  
**认证要求**: 需要Bearer Token认证和testcase:create权限

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

### 2. 使用Token创建测试用例

```bash
POST /apis/101/test-cases
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "用户登录-成功场景",
  "description": "测试用户使用正确凭据登录的成功情况",
  "priority": "P0",
  "severity": "high",
  "tags": ["冒烟测试", "登录功能"],
  "request_override": "{\"request_body\":{\"username\":\"testuser\",\"password\":\"Password123!\"}}",
  "expected_http_status": 200,
  "expected_response_schema": "{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"number\"},\"msg\":{\"type\":\"string\"},\"data\":{\"type\":\"object\",\"properties\":{\"token\":{\"type\":\"string\"}}}}",
  "assertions": "[{\"type\":\"status_code\",\"expected\":200},{\"type\":\"json_path\",\"expression\":\"$.code\",\"expected\":1}]",
  "is_enabled": true
}
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "测试用例创建成功",
  "data": {
    "case_id": 1001,
    "case_code": "TC-API-101-001",
    "api_id": 101,
    "name": "用户登录-成功场景",
    "created_at": "2024-09-16T10:30:00.000Z",
    "updated_at": "2024-09-16T10:30:00.000Z"
  }
}
```

## 测试用例

### 用例1: 正常创建测试用例

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-成功场景",
  "description": "测试用户登录成功的情况",
  "priority": "P0",
  "severity": "high",
  "tags": ["冒烟测试", "登录功能"],
  "is_enabled": true
}
```

**预期响应**: `code: 1, msg: "测试用例创建成功"`，返回创建的测试用例信息

### 用例2: 自动生成用例编码

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-密码错误",
  "description": "测试密码错误时的登录情况"
}
```

**预期响应**: 自动生成用例编码，如`TC-API-101-002`

### 用例3: 指定用例编码

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "case_code": "TC-API-101-CUSTOM",
  "name": "用户登录-自定义编码",
  "description": "使用自定义编码的测试用例"
}
```

**预期响应**: 使用指定的用例编码

### 用例4: 基于模板创建用例

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-基于模板",
  "description": "基于模板创建的测试用例",
  "template_id": 1001
}
```

**预期响应**: 基于模板创建测试用例

### 用例5: 完整配置的测试用例

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-完整配置",
  "description": "包含完整配置的测试用例",
  "priority": "P1",
  "severity": "medium",
  "tags": ["回归测试", "登录功能"],
  "pre_conditions": "[{\"type\":\"setup\",\"description\":\"准备测试数据\"}]",
  "test_steps": "[{\"step\":1,\"action\":\"发送登录请求\",\"expected\":\"返回成功响应\"}]",
  "request_override": "{\"request_body\":{\"username\":\"testuser\",\"password\":\"Password123!\"}}",
  "expected_http_status": 200,
  "expected_response_schema": "{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"number\"}}}",
  "expected_response_body": "{\"code\":1,\"msg\":\"success\"}",
  "assertions": "[{\"type\":\"status_code\",\"expected\":200},{\"type\":\"json_path\",\"expression\":\"$.code\",\"expected\":1}]",
  "extractors": "[{\"name\":\"token\",\"expression\":\"$.data.token\"}]",
  "validators": "[{\"type\":\"json_schema\",\"schema\":\"{\\\"type\\\":\\\"object\\\"}\"}]",
  "is_enabled": true,
  "is_template": false
}
```

**预期响应**: 创建包含完整配置的测试用例

### 用例6: 用例名称为空

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "",
  "description": "测试用例名称为空的情况"
}
```

**预期响应**: `HTTP 400, code: -3, msg: "用例名称不能为空"`

### 用例7: 无效的优先级

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-无效优先级",
  "priority": "P5"
}
```

**预期响应**: `HTTP 400, code: -3, msg: "无效的优先级值"`

### 用例8: 无效的严重程度

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-无效严重程度",
  "severity": "invalid"
}
```

**预期响应**: `HTTP 400, code: -3, msg: "无效的严重程度值"`

### 用例9: 用例编码已存在

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "case_code": "TC-API-101-001",
  "name": "用户登录-重复编码",
  "description": "使用已存在的用例编码"
}
```

**预期响应**: `HTTP 200, code: 0, msg: "用例编码已存在"`

### 用例10: 模板用例不存在

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-无效模板",
  "template_id": 9999
}
```

**预期响应**: `HTTP 200, code: 0, msg: "模板用例不存在"`

### 用例11: 接口不存在

**请求**:
```bash
POST /apis/999/test-cases
Authorization: Bearer <valid_token>
Content-Type: application/json

{
  "name": "用户登录-接口不存在",
  "description": "为不存在的接口创建测试用例"
}
```

**预期响应**: `HTTP 200, code: -4, msg: "接口不存在"`

### 用例12: 未提供Token

**请求**:
```bash
POST /apis/101/test-cases
Content-Type: application/json

{
  "name": "用户登录-未认证",
  "description": "未提供Token的请求"
}
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例13: Token无效

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer invalid_token
Content-Type: application/json

{
  "name": "用户登录-无效Token",
  "description": "使用无效Token的请求"
}
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例14: 权限不足

**请求**:
```bash
POST /apis/101/test-cases
Authorization: Bearer <token_without_permission>
Content-Type: application/json

{
  "name": "用户登录-权限不足",
  "description": "没有testcase:create权限的请求"
}
```

**预期响应**: `HTTP 403, code: -2, msg: "权限不足"`

## 请求参数说明

| 参数名 | 类型 | 是否必须 | 说明 | 示例值 |
|--------|------|----------|------|--------|
| api_id | number | 必须 | 接口ID（路径参数） | 101 |
| case_code | string | 否 | 用例编码，如不提供则自动生成 | "TC-API-101-001" |
| name | string | 必须 | 用例名称 | "用户登录-成功场景" |
| description | string | 否 | 用例描述 | "测试用户登录成功的情况" |
| priority | string | 否 | 优先级，默认P2 | "P0", "P1", "P2", "P3" |
| severity | string | 否 | 严重程度，默认medium | "critical", "high", "medium", "low" |
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
| is_enabled | boolean | 否 | 是否启用，默认true | true, false |
| is_template | boolean | 否 | 是否为模板用例，默认false | true, false |
| template_id | number | 否 | 模板用例ID | 1001 |

## 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| case_id | number | 用例ID |
| case_code | string | 用例编码 |
| api_id | number | 接口ID |
| name | string | 用例名称 |
| created_at | string | 创建时间 |
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

1. **认证与授权**: 验证Token和用户权限（testcase:create权限）
2. **验证接口**: 检查接口是否存在且状态为active
3. **参数校验**: 验证必填字段和枚举值有效性
4. **用例编码处理**: 自动生成或验证用户提供的编码唯一性
5. **模板验证**: 如果提供templateId，验证模板用例存在
6. **创建用例**: 向TestCases表插入新记录
7. **返回结果**: 返回新创建的用例基本信息

## 注意事项

1. **用例编码唯一性**: 在同一接口下，用例编码必须唯一
2. **自动生成编码**: 格式为`TC-API-{apiId}-{序列号}`，如`TC-API-101-001`
3. **JSON字段**: 复杂配置字段需要以JSON字符串形式传递
4. **默认值**: 优先级默认为P2，严重程度默认为medium，启用状态默认为true
5. **权限要求**: 需要testcase:create权限和对应API的访问权限

## 相关接口

- [分页获取接口相关用例列表](./TEST_CASE_LIST_TEST.md)
- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_TEST.md)
