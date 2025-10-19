# 获取当前用户信息接口测试指南

## 接口信息

**请求路径**: `/auth/me`  
**请求方式**: `GET`  
**Content-Type**: `application/json`  
**认证要求**: 需要Bearer Token认证

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

### 2. 使用Token获取当前用户信息

```bash
GET /auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
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
    "last_login_time": "2025-09-16T10:30:00.000Z",
    "created_at": "2024-01-15T10:30:00.000Z"
  }
}
```

## 测试用例

### 用例1: 正常获取用户信息

**请求**:
```bash
GET /auth/me
Authorization: Bearer <valid_token>
```

**预期响应**: `code: 1, msg: "success"`，包含完整的用户信息

### 用例2: 未提供Token

**请求**:
```bash
GET /auth/me
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例3: Token无效

**请求**:
```bash
GET /auth/me
Authorization: Bearer invalid_token
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例4: Token过期

**请求**:
```bash
GET /auth/me
Authorization: Bearer <expired_token>
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例5: 用户不存在

**请求**:
```bash
GET /auth/me
Authorization: Bearer <valid_token_for_deleted_user>
```

**预期响应**: `HTTP 200, code: -4, msg: "用户不存在"`

### 用例6: 用户状态异常

**请求**:
```bash
GET /auth/me
Authorization: Bearer <valid_token_for_inactive_user>
```

**预期响应**: `HTTP 200, code: -2, msg: "用户状态异常，无法获取信息"`

## 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| user_id | number | 用户ID |
| name | string | 用户姓名 |
| email | string | 用户邮箱 |
| avatar_url | string\|null | 用户头像URL |
| phone | string\|null | 用户手机号 |
| department_id | number\|null | 部门ID |
| employee_id | string\|null | 员工工号 |
| position | string\|null | 职位信息 |
| description | string\|null | 备注/描述 |
| status | string | 账户状态 |
| last_login_time | string\|null | 最后登录时间 |
| created_at | string | 账户创建时间 |

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理方式 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 安全特性

1. **JWT认证**: 使用JWT Token进行身份验证
2. **Token验证**: 验证Token的有效性和过期时间
3. **用户状态检查**: 只有状态为"active"的用户才能获取信息
4. **敏感信息过滤**: 不返回密码等敏感字段
5. **拦截器保护**: 使用拦截器统一处理认证逻辑

## 注意事项

1. **Token格式**: 必须在Authorization头中使用"Bearer "前缀
2. **Token有效期**: Token有24小时有效期
3. **用户状态**: 只有状态为"active"的用户才能正常获取信息
4. **时间格式**: 所有时间字段使用ISO 8601格式
5. **空值处理**: 某些字段可能为null，前端需要做好空值处理

## 完整测试流程

1. 确保数据库中有测试用户数据
2. 调用登录接口获取Token
3. 使用Token调用获取当前用户信息接口
4. 验证返回的用户信息是否正确
5. 测试各种异常情况（无效Token、过期Token等）

## 数据库验证

可以通过以下SQL查询验证用户信息：

```sql
SELECT 
    user_id, name, email, avatar_url, phone, 
    department_id, employee_id, position, description, 
    status, last_login_time, created_at
FROM Users 
WHERE user_id = 1 AND is_deleted = FALSE;
```

## 相关接口

- [用户登录接口](./README_AUTH.md)
- [密码重置请求接口](./PASSWORD_RESET_TEST.md)
- [执行密码重置接口](./EXECUTE_PASSWORD_RESET_TEST.md)
