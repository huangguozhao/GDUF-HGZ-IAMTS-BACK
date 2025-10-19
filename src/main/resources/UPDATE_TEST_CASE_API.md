# 编辑测试用例接口文档

## 接口概述

**接口名称**: 编辑测试用例  
**接口路径**: `/apis/{api_id}/test-cases/{case_id}`  
**请求方式**: `PUT`  
**接口描述**: 更新指定接口下的测试用例信息。此接口需要认证，且需要用例管理权限。

## 请求参数

### 请求头
```
Authorization: Bearer {token}
Content-Type: application/json
```

### 路径参数
| 参数名 | 类型 | 是否必须 | 说明 |
|--------|------|----------|------|
| api_id | number | 必须 | 接口ID |
| case_id | number | 必须 | 要更新的用例ID |

### 请求体
**参数格式**: `application/json`

| 参数名 | 类型 | 是否必须 | 说明 | 示例值 |
|--------|------|----------|------|--------|
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

## 响应数据

### 成功响应

**HTTP状态码**: `200`

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

### 失败响应

#### 接口不存在
```json
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}
```

#### 测试用例不存在
```json
{
  "code": -4,
  "msg": "测试用例不存在",
  "data": null
}
```

#### 用例编码冲突
```json
{
  "code": 0,
  "msg": "用例编码已被其他用例使用",
  "data": null
}
```

#### 权限不足
```json
{
  "code": -2,
  "msg": "权限不足，无法更新测试用例",
  "data": null
}
```

#### 参数验证失败
```json
{
  "code": -3,
  "msg": "优先级值无效",
  "data": null
}
```

#### 模板用例不存在
```json
{
  "code": 0,
  "msg": "模板用例不存在",
  "data": null
}
```

## 响应字段说明

### 成功响应数据 (data)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| case_id | number | 用例ID |
| case_code | string | 用例编码 |
| api_id | number | 接口ID |
| name | string | 用例名称 |
| priority | string | 优先级 |
| severity | string | 严重程度 |
| is_enabled | boolean | 是否启用 |
| updated_at | string | 更新时间 (ISO 8601) |

## 业务逻辑

1. **认证与授权**: 验证Token和用户权限（testcase:update权限）
2. **验证接口**: 根据api_id检查接口是否存在且状态为active
3. **验证用例**: 根据case_id检查用例是否存在且属于指定接口
4. **参数校验**:
   - 如果提供case_code，检查其在同一接口下是否唯一（排除当前用例）
   - 如果提供template_id，验证模板用例存在
   - 验证枚举字段值的有效性
5. **更新用例**: 更新TestCases表中对应记录的字段：
   - 只更新请求体中提供的字段
   - 设置updated_by为当前操作者的用户ID
   - updated_at由数据库自动更新
6. **返回结果**: 返回更新后的用例基本信息

## 部分更新特性

### 更新策略
- **只更新提供的字段**: 请求体中未提供的字段保持不变
- **空值处理**: 空字符串会被转换为null值
- **数组清空**: 空数组会清空对应字段

### 示例

#### 只更新名称
```json
{
  "name": "新的用例名称"
}
```

#### 更新多个字段
```json
{
  "name": "更新的名称",
  "priority": "P1",
  "severity": "high",
  "is_enabled": false
}
```

#### 清空字段
```json
{
  "description": "",
  "tags": []
}
```

## 枚举值说明

### 优先级 (priority)
- `P0`: 最高优先级
- `P1`: 高优先级
- `P2`: 中优先级
- `P3`: 低优先级

### 严重程度 (severity)
- `critical`: 严重
- `high`: 高
- `medium`: 中
- `low`: 低

## JSON字段格式

### 标签 (tags)
```json
["冒烟测试", "登录功能", "回归测试"]
```

### 前置条件 (pre_conditions)
```json
[
  {
    "type": "setup",
    "description": "准备测试数据",
    "action": "创建测试用户"
  }
]
```

### 测试步骤 (test_steps)
```json
[
  {
    "step": 1,
    "action": "发送登录请求",
    "expected": "返回成功响应"
  },
  {
    "step": 2,
    "action": "验证响应状态码",
    "expected": "状态码为200"
  }
]
```

### 请求参数覆盖 (request_override)
```json
{
  "request_body": {
    "username": "testuser",
    "password": "Password123!"
  },
  "request_headers": {
    "Content-Type": "application/json"
  }
}
```

### 预期响应Schema (expected_response_schema)
```json
{
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
}
```

### 断言规则 (assertions)
```json
[
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
]
```

### 响应提取规则 (extractors)
```json
[
  {
    "name": "token",
    "expression": "$.data.token",
    "type": "string"
  },
  {
    "name": "user_id",
    "expression": "$.data.user.user_id",
    "type": "number"
  }
]
```

### 验证器配置 (validators)
```json
[
  {
    "type": "json_schema",
    "schema": {
      "type": "object",
      "required": ["code", "msg", "data"]
    }
  },
  {
    "type": "response_time",
    "max_time": 1000
  }
]
```

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理建议 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| 0 | 业务逻辑失败 | 200 | 展示msg给用户 |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -3 | 参数校验失败 | 400 | 提示用户检查输入 |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 权限要求

### 必需权限
- `testcase:update`: 更新测试用例权限

### 资源访问权限
- 需要对应API的访问权限
- 通过`@GlobalInterceptor`注解自动校验

## 注意事项

1. **部分更新**: 此接口支持部分更新，只修改请求体中提供的字段
2. **编码唯一性**: 在同一接口下，用例编码必须唯一
3. **JSON字段**: 复杂配置字段需要以JSON字符串形式传递
4. **空值处理**: 空字符串会被转换为null值
5. **权限校验**: 需要testcase:update权限和对应API的访问权限
6. **版本管理**: 支持版本号更新，便于版本控制

## 相关接口

- [分页获取接口相关用例列表](./TEST_CASE_LIST_API.md)
- [添加测试用例接口](./CREATE_TEST_CASE_API.md)
- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_API.md)

## TODO: 操作日志记录

**注意**: 根据接口文档要求，更新操作应该记录操作日志，便于审计和追踪变更历史。此功能暂未实现，需要在后续版本中添加。

### 建议实现的功能

1. **操作日志记录**
   - 在更新成功后记录操作日志
   - 记录变更的字段和变更前后的值
   - 记录操作人、操作时间等信息

2. **日志表设计**
   - 使用提供的Logs表结构
   - 记录操作类型为"update"
   - 记录目标类型为"testcase"
   - 记录目标ID为用例ID

3. **审计功能**
   - 支持日志查询和审计功能
   - 支持变更历史追踪
   - 支持操作人追踪

4. **实现建议**
   - 在TestCaseServiceImpl中添加日志记录逻辑
   - 使用AOP或事件机制记录操作日志
   - 考虑性能影响，异步记录日志
