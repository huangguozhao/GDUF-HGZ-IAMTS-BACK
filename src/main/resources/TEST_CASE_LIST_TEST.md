# 分页获取接口相关用例列表接口测试指南

## 接口信息

**请求路径**: `/apis/{api_id}/test-cases`  
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

### 2. 使用Token获取测试用例列表

```bash
GET /apis/101/test-cases
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 8,
    "items": [
      {
        "case_id": 1001,
        "case_code": "TC-API-101-001",
        "api_id": 101,
        "name": "用户登录-成功场景",
        "description": "测试用户登录成功的情况",
        "priority": "P0",
        "severity": "high",
        "tags": ["冒烟测试", "登录功能"],
        "is_enabled": true,
        "is_template": false,
        "version": "1.0",
        "created_by": 123,
        "creator_name": "张三",
        "created_at": "2024-06-15T10:30:00.000Z",
        "updated_at": "2024-08-20T14:25:00.000Z"
      }
    ],
    "page": 1,
    "page_size": 10
  }
}
```

## 测试用例

### 用例1: 正常获取测试用例列表

**请求**:
```bash
GET /apis/101/test-cases
Authorization: Bearer <valid_token>
```

**预期响应**: `code: 1, msg: "success"`，包含分页的测试用例列表

### 用例2: 按优先级过滤

**请求**:
```bash
GET /apis/101/test-cases?priority=P0
Authorization: Bearer <valid_token>
```

**预期响应**: 只返回优先级为P0的测试用例

### 用例3: 按严重程度过滤

**请求**:
```bash
GET /apis/101/test-cases?severity=high
Authorization: Bearer <valid_token>
```

**预期响应**: 只返回严重程度为high的测试用例

### 用例4: 按启用状态过滤

**请求**:
```bash
GET /apis/101/test-cases?is_enabled=true
Authorization: Bearer <valid_token>
```

**预期响应**: 只返回启用的测试用例

### 用例5: 按模板状态过滤

**请求**:
```bash
GET /apis/101/test-cases?is_template=false
Authorization: Bearer <valid_token>
```

**预期响应**: 只返回非模板的测试用例

### 用例6: 按标签过滤

**请求**:
```bash
GET /apis/101/test-cases?tags=冒烟测试&tags=登录功能
Authorization: Bearer <valid_token>
```

**预期响应**: 返回包含指定标签的测试用例

### 用例7: 按创建人过滤

**请求**:
```bash
GET /apis/101/test-cases?created_by=123
Authorization: Bearer <valid_token>
```

**预期响应**: 只返回指定创建人的测试用例

### 用例8: 分页查询

**请求**:
```bash
GET /apis/101/test-cases?page=2&page_size=5
Authorization: Bearer <valid_token>
```

**预期响应**: 返回第2页，每页5条记录

### 用例9: 名称模糊查询

**请求**:
```bash
GET /apis/101/test-cases?name=登录
Authorization: Bearer <valid_token>
```

**预期响应**: 返回名称包含"登录"的测试用例

### 用例10: 组合查询

**请求**:
```bash
GET /apis/101/test-cases?priority=P0&severity=high&is_enabled=true&page=1&page_size=20
Authorization: Bearer <valid_token>
```

**预期响应**: 返回优先级为P0、严重程度为high、已启用的测试用例，第1页，每页20条

### 用例11: 接口不存在

**请求**:
```bash
GET /apis/999/test-cases
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: -4, msg: "接口不存在"`

### 用例12: 未提供Token

**请求**:
```bash
GET /apis/101/test-cases
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例13: Token无效

**请求**:
```bash
GET /apis/101/test-cases
Authorization: Bearer invalid_token
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

## 查询参数说明

| 参数名 | 类型 | 是否必须 | 说明 | 示例值 |
|--------|------|----------|------|--------|
| api_id | number | 必须 | 接口ID（路径参数） | 101 |
| name | string | 否 | 用例名称模糊查询 | "登录" |
| priority | string | 否 | 优先级过滤 | "P0", "P1", "P2", "P3" |
| severity | string | 否 | 严重程度过滤 | "critical", "high", "medium", "low" |
| is_enabled | boolean | 否 | 是否启用过滤 | true, false |
| is_template | boolean | 否 | 是否模板用例过滤 | true, false |
| tags | string[] | 否 | 标签过滤（支持多个） | ["冒烟测试", "登录功能"] |
| created_by | number | 否 | 创建人ID过滤 | 123 |
| page | number | 否 | 页码，默认为1 | 1 |
| page_size | number | 否 | 每页条数，默认为10，最大100 | 10 |

## 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | number | 符合条件的数据总条数 |
| items | object[] | 当前页的用例数据列表 |
| page | number | 当前页码 |
| page_size | number | 当前每页条数 |
| case_id | number | 用例ID |
| case_code | string | 用例编码 |
| api_id | number | 接口ID |
| name | string | 用例名称 |
| description | string | 用例描述 |
| priority | string | 优先级 |
| severity | string | 严重程度 |
| tags | string[] | 标签数组 |
| is_enabled | boolean | 是否启用 |
| is_template | boolean | 是否为模板用例 |
| version | string | 版本号 |
| created_by | number | 创建人ID |
| creator_name | string | 创建人姓名 |
| created_at | string | 创建时间 |
| updated_at | string | 更新时间 |

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理方式 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 注意事项

1. **分页限制**: page_size最大值为100
2. **接口状态**: 只有状态为"active"的接口才能查询用例
3. **标签查询**: 支持多个标签的OR查询
4. **时间格式**: 所有时间字段使用ISO 8601格式
5. **权限验证**: 需要有效的JWT Token

## 完整测试流程

1. 确保数据库中有测试数据（接口和测试用例）
2. 调用登录接口获取Token
3. 使用Token调用获取测试用例列表接口
4. 验证返回的分页数据是否正确
5. 测试各种过滤条件
6. 测试分页功能
7. 测试异常情况

## 数据库验证

可以通过以下SQL查询验证数据：

```sql
-- 查询接口信息
SELECT api_id, name, status FROM Apis WHERE api_id = 101 AND is_deleted = FALSE;

-- 查询测试用例
SELECT 
    tc.case_id, tc.case_code, tc.name, tc.priority, tc.severity,
    tc.is_enabled, tc.is_template, u.name as creator_name
FROM TestCases tc
LEFT JOIN Users u ON tc.created_by = u.user_id
WHERE tc.api_id = 101 AND tc.is_deleted = FALSE;
```

## 相关接口

- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_TEST.md)
