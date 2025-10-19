# 密码重置请求接口测试

## 接口信息
- **请求路径**: `POST /auth/password/reset-request`
- **请求头**: `Content-Type: application/json`

## 测试用例

### 1. 通过邮箱请求重置（成功）
```bash
curl -X POST http://localhost:8080/auth/password/reset-request \
  -H "Content-Type: application/json" \
  -d '{
    "account": "admin@example.com",
    "channel": "email"
  }'
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "重置验证码已发送，请查收",
  "data": {
    "reset_token_id": "req_1734567890123_ABC12345"
  }
}
```

### 2. 通过手机号请求重置（成功）
```bash
curl -X POST http://localhost:8080/auth/password/reset-request \
  -H "Content-Type: application/json" \
  -d '{
    "account": "13800138000",
    "channel": "sms"
  }'
```

### 3. 账号不存在
```bash
curl -X POST http://localhost:8080/auth/password/reset-request \
  -H "Content-Type: application/json" \
  -d '{
    "account": "nonexistent@example.com",
    "channel": "email"
  }'
```

**预期响应**:
```json
{
  "code": -4,
  "msg": "该邮箱/手机号未注册",
  "data": null
}
```

### 4. 不支持的渠道
```bash
curl -X POST http://localhost:8080/auth/password/reset-request \
  -H "Content-Type: application/json" \
  -d '{
    "account": "admin@example.com",
    "channel": "wechat"
  }'
```

**预期响应**:
```json
{
  "code": -3,
  "msg": "不支持的验证码发送渠道",
  "data": null
}
```

### 5. 参数缺失
```bash
curl -X POST http://localhost:8080/auth/password/reset-request \
  -H "Content-Type: application/json" \
  -d '{
    "account": "admin@example.com"
  }'
```

**预期响应**:
```json
{
  "code": -3,
  "msg": "发送渠道不能为空",
  "data": null
}
```

## 功能特性

1. **用户验证**: 支持通过邮箱或手机号查找用户
2. **状态检查**: 只有激活状态的用户才能重置密码
3. **频率限制**: 1分钟内只能发送一次验证码
4. **验证码存储**: 验证码存储在Redis中，有效期15分钟
5. **多渠道支持**: 支持邮箱和短信两种发送渠道
6. **安全保护**: 验证码不会在响应中返回

## 测试用户

| 邮箱 | 手机号 | 状态 | 说明 |
|------|--------|------|------|
| admin@example.com | 13800138000 | active | 系统管理员 |
| test@example.com | 13800138001 | active | 普通用户 |
| pending@example.com | 13800138002 | pending | 待审核用户 |
| disabled@example.com | 13800138003 | inactive | 禁用用户 |
