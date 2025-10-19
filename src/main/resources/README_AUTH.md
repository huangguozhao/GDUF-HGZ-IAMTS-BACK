# 用户认证接口使用说明

## 接口信息

### 用户登录接口

**请求路径**: `POST /auth/login`

**请求头**: `Content-Type: application/json`

**请求参数**:
```json
{
  "email": "admin@example.com",
  "password": "123456"
}
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "登录成功",
  "data": {
    "user": {
      "userId": 1,
      "name": "管理员",
      "email": "admin@example.com",
      "avatarUrl": "https://example.com/avatars/admin.jpg",
      "phone": "13800138000",
      "departmentId": 1,
      "employeeId": "EMP001",
      "position": "系统管理员",
      "description": "系统初始管理员账户",
      "status": "active",
      "lastLoginTime": "2025-01-16T10:30:00"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

## 测试用户数据

系统已预置以下测试用户（密码都是 `123456`）：

| 邮箱 | 姓名 | 状态 | 说明 |
|------|------|------|------|
| admin@example.com | 管理员 | active | 系统管理员 |
| test@example.com | 测试用户 | active | 普通用户 |
| pending@example.com | 待审核用户 | pending | 待审核状态 |
| disabled@example.com | 禁用用户 | inactive | 已禁用状态 |

## 错误响应示例

### 邮箱或密码错误
```json
{
  "code": -1,
  "msg": "邮箱或密码错误",
  "data": null
}
```

### 账户待审核
```json
{
  "code": 0,
  "msg": "账户待审核，请联系管理员",
  "data": null
}
```

### 账户已禁用
```json
{
  "code": 0,
  "msg": "账户已被禁用，请联系管理员",
  "data": null
}
```

## 数据库初始化

请先执行 `src/main/resources/sql/init_user_data.sql` 文件来初始化测试用户数据。

## JWT Token 使用

登录成功后，客户端需要将返回的 `token` 保存起来，并在后续请求的请求头中携带：

```
Authorization: Bearer <your_token>
```

## 技术栈

- Spring Boot 3.x
- MyBatis 3.0.3
- JWT (jjwt 0.12.3)
- BCrypt 密码加密
- MySQL 8.0
- Lombok
