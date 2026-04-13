# 创建测试用例接口文档

## 接口概述

创建测试用例接口提供了为指定接口创建新测试用例的功能，支持完整的测试用例配置，包括优先级、严重程度、标签、断言规则等。

## 接口详情

### 创建测试用例

**请求路径**: `/apis`  
**请求方式**: `POST`  
**接口描述**: 为指定接口创建新的测试用例

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`
- `Content-Type` (string, 必须): 固定为 `application/json`

**请求体**:
- `api_id` (number, 必须): 接口ID
- `case_code` (string, 可选): 用例编码，如不提供则自动生成
- `name` (string, 必须): 用例名称
- `description` (string, 可选): 用例描述
- `priority` (string, 可选): 优先级，默认: `P2`，可选值: `P0`, `P1`, `P2`, `P3`
- `severity` (string, 可选): 严重程度，默认: `medium`，可选值: `critical`, `high`, `medium`, `low`
- `tags` (string[], 可选): 标签数组
- `pre_conditions` (object[], 可选): 前置条件配置
- `test_steps` (object[], 可选): 测试步骤
- `request_override` (object, 可选): 请求参数覆盖配置
- `expected_http_status` (number, 可选): 预期HTTP状态码
- `expected_response_schema` (object, 可选): 预期响应Schema
- `expected_response_body` (string, 可选): 预期响应体
- `assertions` (object[], 可选): 断言规则
- `extractors` (object[], 可选): 响应提取规则
- `validators` (object[], 可选): 验证器配置
- `is_enabled` (boolean, 可选): 是否启用，默认: `true`
- `is_template` (boolean, 可选): 是否为模板用例，默认: `false`
- `template_id` (number, 可选): 模板用例ID

#### 请求示例

```bash
# 创建基本测试用例
curl -X POST "http://localhost:8080/apis" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "api_id": 101,
    "name": "用户登录-成功场景",
    "description": "测试用户使用正确凭据登录的成功情况",
    "priority": "P0",
    "severity": "high",
    "tags": ["冒烟测试", "登录功能"],
    "expected_http_status": 200,
    "is_enabled": true
  }'

# 创建带用例编码的测试用例
curl -X POST "http://localhost:8080/apis" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "api_id": 101,
    "case_code": "TC-API-101-001",
    "name": "用户登录-失败场景",
    "description": "测试用户使用错误凭据登录的失败情况",
    "priority": "P1",
    "severity": "medium",
    "tags": ["登录功能"],
    "expected_http_status": 401,
    "is_enabled": true
  }'

# 创建基于模板的测试用例
curl -X POST "http://localhost:8080/apis" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "api_id": 101,
    "name": "用户登录-模板用例",
    "description": "基于模板创建的测试用例",
    "priority": "P2",
    "severity": "low",
    "template_id": 1001,
    "is_enabled": true
  }'

# 创建完整配置的测试用例
curl -X POST "http://localhost:8080/apis" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "api_id": 101,
    "name": "用户登录-完整配置",
    "description": "包含完整配置的测试用例",
    "priority": "P0",
    "severity": "critical",
    "tags": ["冒烟测试", "登录功能", "核心功能"],
    "request_override": {
      "request_body": {
        "username": "testuser",
        "password": "Password123!"
      }
    },
    "expected_http_status": 200,
    "expected_response_schema": {
      "type": "object",
      "properties": {
        "code": {"type": "number"},
        "msg": {"type": "string"},
        "data": {
          "type": "object",
          "properties": {
            "token": {"type": "string"}
          }
        }
      }
    },
    "assertions": [
      {
        "type": "status_code",
        "expected": 200
      },
      {
        "type": "json_path",
        "expression": "$.code",
        "expected": 1
      }
    ],
    "is_enabled": true,
    "is_template": false
  }'
```

#### 响应数据

**成功响应** (HTTP 200):

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

**失败响应**:

```json
// 接口不存在 (HTTP 200)
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}

// 接口已禁用 (HTTP 200)
{
  "code": -4,
  "msg": "接口已禁用，无法创建用例",
  "data": null
}

// 用例编码已存在 (HTTP 200)
{
  "code": 0,
  "msg": "用例编码已存在",
  "data": null
}

// 模板用例不存在 (HTTP 200)
{
  "code": -4,
  "msg": "模板用例不存在",
  "data": null
}

// 权限不足 (HTTP 200)
{
  "code": -2,
  "msg": "权限不足，无法创建测试用例",
  "data": null
}

// 参数验证失败 (HTTP 200)
{
  "code": -3,
  "msg": "用例名称不能为空",
  "data": null
}

// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

## 业务逻辑说明

### 创建流程
1. **参数校验**: 验证必填字段和参数格式
2. **接口验证**: 检查接口是否存在且状态为active
3. **权限检查**: 验证用户是否有用例管理权限
4. **用例编码处理**: 检查编码唯一性或自动生成
5. **模板验证**: 如果提供模板ID，验证模板用例存在
6. **设置默认值**: 设置优先级、严重程度等默认值
7. **创建用例**: 向TestCases表插入新记录
8. **返回结果**: 返回新创建的用例基本信息

### 权限规则
1. **创建者权限**: 可以管理自己创建的接口的用例
2. **项目成员权限**: 项目成员可以管理用例
3. **用例管理权限**: 需要用例管理权限

### 用例编码生成规则
- 格式: `TC-API-{api_id}-{序列号}`
- 序列号: 该接口下已存在用例数量 + 1
- 示例: `TC-API-101-001`, `TC-API-101-002`

### 默认值设置
- **优先级**: `P2` (中优先级)
- **严重程度**: `medium` (中)
- **是否启用**: `true`
- **是否模板**: `false`
- **版本号**: `1.0`

## 安全特性

### 1. 认证要求
- 使用`@GlobalInterceptor(checkLogin = true)`进行认证
- 必须提供有效的认证令牌

### 2. 权限控制
- 验证用户是否有用例管理权限
- 只能为有权限的接口创建用例

### 3. 参数验证
- 必填字段验证
- 字段长度限制
- 枚举值有效性验证
- 用例编码格式验证

### 4. 业务规则验证
- 接口存在性和状态检查
- 用例编码唯一性检查
- 模板用例存在性检查

## 错误处理

### 错误码说明
| 错误码 | 含义 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| 0 | 业务逻辑失败 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 200 |
| -3 | 参数校验失败 | 200 |
| -4 | 资源不存在 | 200 |
| -5 | 服务器内部异常 | 500 |

### 常见错误场景
1. **接口不存在**: 指定的接口ID不存在
2. **接口已禁用**: 接口状态不是active
3. **用例编码已存在**: 同一接口下用例编码重复
4. **模板用例不存在**: 指定的模板ID不存在
5. **权限不足**: 用户没有用例管理权限
6. **参数验证失败**: 必填字段为空或格式错误

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有用例管理权限
3. **接口状态**: 只能为状态为active的接口创建用例
4. **用例编码**: 在同一个接口内必须保持唯一
5. **模板功能**: 支持基于模板快速创建用例
6. **JSON字段**: 复杂的JSON字段会进行格式验证

## 性能优化

### 1. 索引优化
- 接口ID索引
- 用例编码索引
- 创建人索引
- 删除状态索引

### 2. 查询优化
- 使用索引进行唯一性检查
- 批量操作优化

### 3. 缓存策略
- 接口信息可以缓存
- 模板用例可以缓存

## 测试

可以使用提供的 `test_create_test_case_api.bat` 脚本进行接口测试，或者使用 Postman 等工具进行测试。

测试前请确保：
1. 应用已启动
2. 数据库中有测试数据
3. 有有效的认证令牌
4. 有用例管理权限

## 后续扩展建议

1. **批量创建**: 支持批量创建测试用例
2. **用例复制**: 支持复制现有用例
3. **模板管理**: 增强模板用例管理功能
4. **用例导入**: 支持从文件导入用例
5. **用例导出**: 支持导出用例到文件
6. **用例版本**: 支持用例版本管理
7. **用例关联**: 支持用例之间的关联关系
8. **用例统计**: 提供用例统计信息
9. **用例搜索**: 支持用例搜索功能
10. **用例标签**: 增强标签管理功能
