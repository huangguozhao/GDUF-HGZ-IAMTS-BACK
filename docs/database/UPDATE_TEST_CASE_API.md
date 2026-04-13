# 修改测试用例API文档

## 接口概述

修改测试用例API提供了更新指定测试用例信息的功能，支持部分更新、完整的参数验证和业务规则检查。

## 接口详情

### 修改测试用例

**请求路径**: `/testcases/{case_id}`  
**请求方式**: `PUT`  
**接口描述**: 更新指定的测试用例信息

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`
- `Content-Type` (string, 必须): 固定为 `application/json`

**路径参数**:
- `case_id` (number, 必须): 要更新的测试用例ID

**请求体**:
- `case_code` (string, 可选): 用例编码
- `name` (string, 可选): 用例名称
- `description` (string, 可选): 用例描述
- `priority` (string, 可选): 优先级。可选: `P0`, `P1`, `P2`, `P3`
- `severity` (string, 可选): 严重程度。可选: `critical`, `high`, `medium`, `low`
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
- `is_enabled` (boolean, 可选): 是否启用
- `is_template` (boolean, 可选): 是否为模板用例
- `template_id` (number, 可选): 模板用例ID
- `version` (string, 可选): 版本号

#### 请求示例

```bash
# 基本用例更新
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "用户登录-成功场景-更新",
    "description": "更新后的测试用例描述",
    "priority": "P0",
    "severity": "critical",
    "tags": ["冒烟测试", "登录功能", "重要"],
    "is_enabled": true
  }'

# 部分更新
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "用户登录-成功场景-部分更新",
    "priority": "P1"
  }'

# 更新用例编码
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "case_code": "TC-API-101-UPDATED-001",
    "name": "用户登录-更新编码"
  }'

# 更新模板
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "template_id": 1002,
    "name": "用户登录-更新模板"
  }'

# 完整配置更新
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "用户登录-完整配置更新",
    "description": "包含完整配置的测试用例更新",
    "priority": "P0",
    "severity": "critical",
    "tags": ["冒烟测试", "登录功能", "核心功能"],
    "request_override": {
      "request_body": {
        "username": "updated_user",
        "password": "NewPassword123!"
      }
    },
    "expected_http_status": 200,
    "expected_response_schema": {
      "type": "object",
      "properties": {
        "code": { "type": "number" },
        "msg": { "type": "string" },
        "data": { 
          "type": "object",
          "properties": {
            "token": { "type": "string" }
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
      },
      {
        "type": "response_time",
        "max_time_ms": 1000
      }
    ],
    "is_enabled": true
  }'

# 更新为模板用例
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "更新为模板用例",
    "description": "更新为模板用例",
    "priority": "P1",
    "severity": "high",
    "is_template": true,
    "is_enabled": true
  }'

# 禁用用例
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "禁用用例",
    "description": "禁用测试用例",
    "priority": "P3",
    "severity": "low",
    "is_enabled": false
  }'

# 更新版本号
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "更新版本号用例",
    "description": "更新版本号",
    "version": "2.0"
  }'

# 更新JSON字段
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "更新JSON字段用例",
    "tags": ["新标签1", "新标签2"],
    "pre_conditions": [
      {
        "type": "auth",
        "config": {
          "token": "test_token"
        }
      }
    ],
    "test_steps": [
      {
        "step": 1,
        "action": "发送请求",
        "expected": "成功响应"
      }
    ],
    "assertions": [
      {
        "type": "status_code",
        "expected": 200
      }
    ]
  }'

# 更新预期响应
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "更新预期响应用例",
    "expected_http_status": 201,
    "expected_response_body": "{\"code\": 1, \"msg\": \"success\", \"data\": {\"id\": 123}}",
    "expected_response_schema": {
      "type": "object",
      "properties": {
        "code": { "type": "number" },
        "msg": { "type": "string" },
        "data": { "type": "object" }
      }
    }
  }'

# 更新提取器和验证器
curl -X PUT "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "更新提取器和验证器用例",
    "extractors": [
      {
        "name": "user_id",
        "path": "$.data.id",
        "type": "json_path"
      }
    ],
    "validators": [
      {
        "name": "response_time",
        "type": "response_time",
        "max_time_ms": 2000
      }
    ]
  }'
```

#### 响应数据

**成功响应** (HTTP 200):

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

**失败响应**:

```json
// 用例不存在 (HTTP 200)
{
  "code": -4,
  "msg": "测试用例不存在",
  "data": null
}

// 用例已被删除 (HTTP 200)
{
  "code": 0,
  "msg": "测试用例已被删除，无法编辑",
  "data": null
}

// 用例编码已存在 (HTTP 200)
{
  "code": 0,
  "msg": "用例编码已被其他用例使用",
  "data": null
}

// 模板用例不存在 (HTTP 200)
{
  "code": 0,
  "msg": "模板用例不存在或不是有效的模板",
  "data": null
}

// 权限不足 (HTTP 200)
{
  "code": -2,
  "msg": "权限不足，无法更新测试用例",
  "data": null
}

// 参数验证失败 (HTTP 200)
{
  "code": -3,
  "msg": "优先级值无效",
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

### 更新流程
1. **参数校验**: 验证必填字段和字段格式
2. **用例验证**: 检查用例是否存在且未被删除
3. **权限检查**: 验证用户是否有用例管理权限
4. **用例编码处理**: 验证唯一性（排除当前用例）
5. **模板验证**: 如果提供模板ID，验证模板存在
6. **枚举字段验证**: 验证优先级和严重程度
7. **执行更新**: 更新TestCases表中对应记录的字段
8. **返回结果**: 返回更新后的用例基本信息

### 部分更新特性
- 只更新请求体中提供的字段
- 未提供的字段保持原值不变
- 支持JSON字段的完整替换

### 权限规则
1. **创建者权限**: 可以更新自己创建的用例
2. **项目成员权限**: 项目成员可以更新用例
3. **用例管理权限**: 需要用例管理权限

### 用例编码更新规则
- 格式验证：只能包含大写字母、数字、下划线和中划线
- 长度限制：不能超过50个字符
- 唯一性检查：在同一接口下必须唯一（排除当前用例）

### 模板功能更新
- 支持更新模板用例ID
- 验证模板用例存在且有效
- 支持模板配置的继承和覆盖

## 安全特性

### 1. 认证要求
- 使用`@GlobalInterceptor(checkLogin = true)`进行认证
- 必须提供有效的认证令牌

### 2. 权限控制
- 验证用户是否有用例管理权限
- 只能在有权限的用例上执行更新操作

### 3. 参数验证
- 字段长度限制
- 枚举值有效性验证
- 用例编码格式验证

### 4. 业务规则验证
- 用例存在性和状态检查
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
1. **用例不存在**: 指定的用例ID不存在
2. **用例已被删除**: 用例状态为已删除
3. **用例编码已存在**: 同一接口下用例编码重复
4. **模板用例不存在**: 指定的模板ID不存在
5. **权限不足**: 用户没有用例管理权限
6. **参数验证失败**: 字段格式错误或超出范围

## 数据验证

### 字段验证
- `name`: 可选，长度不超过255个字符
- `description`: 可选，长度不超过1000个字符
- `case_code`: 可选，格式验证和唯一性检查
- `priority`: 可选，必须是P0、P1、P2、P3之一
- `severity`: 可选，必须是critical、high、medium、low之一

### JSON字段验证
- `tags`: 字符串数组格式
- `pre_conditions`: 对象数组格式
- `test_steps`: 对象数组格式
- `request_override`: 对象格式
- `expected_response_schema`: 对象格式
- `assertions`: 对象数组格式
- `extractors`: 对象数组格式
- `validators`: 对象数组格式

## 性能优化

### 1. 索引优化
- 用例ID索引
- 用例编码索引
- 接口ID索引

### 2. 查询优化
- 使用索引进行唯一性检查
- 批量更新支持

### 3. 缓存策略
- 用例信息可以缓存
- 模板用例可以缓存

## 测试

可以使用提供的 `test_update_test_case_api.bat` 脚本进行接口测试，或者使用 Postman 等工具进行测试。

测试前请确保：
1. 应用已启动
2. 数据库中有测试数据
3. 有有效的认证令牌
4. 有用例管理权限

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有用例管理权限
3. **部分更新**: 支持部分更新，只修改请求体中提供的字段
4. **用例编码**: 在同一个接口内必须保持唯一
5. **JSON字段**: 复杂的JSON字段会进行格式验证
6. **版本控制**: 支持版本号更新

## 后续扩展建议

1. **版本历史**: 支持用例版本历史功能
2. **批量更新**: 支持批量更新测试用例
3. **变更追踪**: 提供变更历史查询功能
4. **回滚功能**: 支持用例配置回滚
5. **变更通知**: 支持变更通知功能
6. **审计日志**: 增强审计日志记录
7. **权限细化**: 支持更细粒度的权限控制
8. **字段级权限**: 支持字段级别的更新权限
9. **审批流程**: 支持重要变更的审批流程
10. **变更影响分析**: 提供变更影响分析功能
11. **自动化测试**: 集成自动化测试功能
12. **性能监控**: 提供更新操作性能监控
13. **数据备份**: 支持更新前的数据备份
14. **变更统计**: 提供变更统计信息
15. **变更报告**: 生成变更报告功能