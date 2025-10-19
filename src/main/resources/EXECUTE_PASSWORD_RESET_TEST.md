# 执行密码重置接口测试指南

## 接口信息

**请求路径**: `/auth/password/reset`  
**请求方式**: `POST`  
**Content-Type**: `application/json`

## 测试步骤

### 1. 先请求密码重置验证码

```bash
POST /auth/password/reset-request
Content-Type: application/json

{
  "account": "admin@example.com",
  "channel": "email"
}
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "重置验证码已发送，请查收",
  "data": {
    "reset_token_id": "req_abc123def456"
  }
}
```

### 2. 执行密码重置

```bash
POST /auth/password/reset
Content-Type: application/json

{
  "account": "admin@example.com",
  "verification_code": "889977",
  "new_password": "MyNewSecurePassword123!"
}
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "密码重置成功",
  "data": null
}
```

## 测试用例

### 用例1: 正常密码重置

**请求**:
```json
{
  "account": "admin@example.com",
  "verification_code": "889977",
  "new_password": "MyNewSecurePassword123!"
}
```

**预期响应**: `code: 1, msg: "密码重置成功"`

### 用例2: 验证码错误

**请求**:
```json
{
  "account": "admin@example.com",
  "verification_code": "000000",
  "new_password": "MyNewSecurePassword123!"
}
```

**预期响应**: `code: 0, msg: "验证码错误或已失效"`

### 用例3: 账号不存在

**请求**:
```json
{
  "account": "nonexistent@example.com",
  "verification_code": "889977",
  "new_password": "MyNewSecurePassword123!"
}
```

**预期响应**: `code: -4, msg: "该邮箱/手机号未注册"`

### 用例4: 密码强度不符合要求

**请求**:
```json
{
  "account": "admin@example.com",
  "verification_code": "889977",
  "new_password": "123456"
}
```

**预期响应**: `code: -3, msg: "密码必须包含大小写字母、数字和特殊字符，且长度至少为8位"`

### 用例5: 验证码已失效

**请求**:
```json
{
  "account": "admin@example.com",
  "verification_code": "889977",
  "new_password": "MyNewSecurePassword123!"
}
```

**预期响应**: `code: 0, msg: "验证码错误或已失效"`

## 密码强度要求

密码必须满足以下条件：
- 长度至少8位
- 包含小写字母 (a-z)
- 包含大写字母 (A-Z)
- 包含数字 (0-9)
- 包含特殊字符 (@$!%*?&)

## 错误码说明

| 错误码 | 含义 | 处理方式 |
|--------|------|----------|
| 1 | 成功 | - |
| 0 | 业务逻辑失败 | 展示msg给用户 |
| -3 | 参数校验失败 | 提示用户检查输入 |
| -4 | 资源不存在 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 提示用户"系统繁忙，请稍后再试" |

## 注意事项

1. **验证码有效期**: 15分钟
2. **验证码使用**: 验证码使用后会自动失效，防止重放攻击
3. **密码加密**: 新密码会使用BCrypt进行加密存储
4. **用户状态**: 只有状态为"active"的用户才能重置密码
5. **账号验证**: 支持邮箱和手机号两种账号类型

## 完整测试流程

1. 确保数据库中有测试用户数据
2. 调用密码重置请求接口获取验证码
3. 检查邮箱/短信是否收到验证码
4. 使用收到的验证码调用执行密码重置接口
5. 验证密码是否成功更新
6. 尝试使用新密码登录验证

## 数据库验证

密码重置成功后，可以查询数据库验证：

```sql
SELECT user_id, email, password, updated_at 
FROM Users 
WHERE email = 'admin@example.com';
```

`password`字段应该是BCrypt加密后的值，`updated_at`字段应该是最新的时间戳。
