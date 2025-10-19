# 获取当前用户信息接口文档

## 接口概述

**接口名称**: 获取当前用户信息  
**接口路径**: `/auth/me`  
**请求方式**: `GET`  
**接口描述**: 获取当前已认证登录用户的详细信息。此接口需要认证。

## 请求参数

### 请求头
```
Authorization: Bearer {token}
Content-Type: application/json
```

### 请求体参数
无

## 响应数据

### 成功响应

**HTTP状态码**: `200`

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "user_id": 123,
    "name": "张无忌",
    "email": "zhangwuji@mingjiao.org",
    "avatar_url": "https://example.com/avatars/zhangwuji.jpg",
    "phone": "13800138123",
    "department_id": 5,
    "employee_id": "MJ2024",
    "position": "教主",
    "description": "明教第三十四代教主",
    "status": "active",
    "last_login_time": "2025-09-16T08:15:47.000Z",
    "created_at": "2024-01-15T10:30:00.000Z"
  }
}
```

### 失败响应

#### Token无效或过期
```json
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

#### 用户不存在
```json
{
  "code": -4,
  "msg": "用户不存在",
  "data": null
}
```

#### 用户状态异常
```json
{
  "code": -2,
  "msg": "用户状态异常，无法获取信息",
  "data": null
}
```

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
| status | string | 账户状态 (`active`, `inactive`, `pending`) |
| last_login_time | string\|null | 最后登录时间 (ISO 8601) |
| created_at | string | 账户创建时间 (ISO 8601) |

## 业务逻辑

1. **Token验证**: 从Authorization头中提取JWT Token并验证有效性
2. **用户查询**: 根据Token中的用户ID查询用户信息
3. **状态检查**: 验证用户状态是否为"active"
4. **信息过滤**: 过滤敏感字段（如密码、删除标记等）
5. **数据返回**: 返回用户基本信息

## 认证机制

### JWT Token要求
- **格式**: `Bearer {token}`
- **有效期**: 24小时
- **包含信息**: 用户ID、邮箱、签发时间、过期时间

### 认证流程
1. 客户端在请求头中携带Token
2. JWT拦截器验证Token有效性
3. 从Token中提取用户ID
4. 查询用户信息并返回

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理建议 |
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

## 前置条件

1. 用户必须先通过登录接口获取有效的JWT Token
2. Token必须在有效期内
3. 用户状态必须为"active"

## 注意事项

1. **Token格式**: 必须在Authorization头中使用"Bearer "前缀
2. **Token有效期**: Token有24小时有效期
3. **用户状态**: 只有状态为"active"的用户才能正常获取信息
4. **时间格式**: 所有时间字段使用ISO 8601格式
5. **空值处理**: 某些字段可能为null，前端需要做好空值处理

## 相关接口

- [用户登录接口](./README_AUTH.md)
- [密码重置请求接口](./PASSWORD_RESET_TEST.md)
- [执行密码重置接口](./EXECUTE_PASSWORD_RESET_TEST.md)

## 技术实现

### 拦截器配置
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/auth/me")
                .excludePathPatterns("/auth/login", "/auth/password/reset-request", "/auth/password/reset");
    }
}
```

### JWT验证流程
1. 提取Authorization头
2. 验证Token格式
3. 验证Token有效性
4. 提取用户ID
5. 设置到请求属性中

### 数据库查询
```sql
SELECT 
    user_id, name, email, avatar_url, phone, 
    department_id, employee_id, position, description, 
    status, last_login_time, created_at
FROM Users 
WHERE user_id = ? AND is_deleted = FALSE
```
