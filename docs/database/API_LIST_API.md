# 获取接口列表接口文档

## 接口概述

获取接口列表接口提供了查询指定模块下接口列表的功能，支持多种过滤和排序条件，包含分页和统计信息。

## 接口详情

### 获取接口列表

**请求路径**: `/modules/{module_id}/apis`  
**请求方式**: `GET`  
**接口描述**: 获取指定模块下的接口列表

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`

**路径参数**:
- `module_id` (number, 必须): 模块ID

**查询参数**:
- `method` (string, 可选): 请求方法过滤，可选值: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD`, `OPTIONS`
- `status` (string, 可选): 接口状态过滤，可选值: `active`, `inactive`, `deprecated`，默认: `active`
- `tags` (string[], 可选): 标签过滤，支持多个标签
- `auth_type` (string, 可选): 认证类型过滤，可选值: `none`, `basic`, `bearer`, `api_key`, `oauth2`
- `search_keyword` (string, 可选): 关键字搜索（接口名称、描述、路径）
- `include_deleted` (boolean, 可选): 是否包含已删除的接口，默认: `false`
- `include_statistics` (boolean, 可选): 是否包含统计信息，默认: `false`
- `sort_by` (string, 可选): 排序字段，可选值: `name`, `method`, `path`, `created_at`, `updated_at`，默认: `created_at`
- `sort_order` (string, 可选): 排序顺序，可选值: `asc`, `desc`，默认: `desc`
- `page` (number, 可选): 页码，默认为 `1`
- `page_size` (number, 可选): 每页条数，默认为 `20`，最大 `100`

#### 请求示例

```bash
# 获取接口列表
curl -X GET "http://localhost:8080/modules/1/apis" \
  -H "Authorization: Bearer your_token_here"

# 按方法过滤
curl -X GET "http://localhost:8080/modules/1/apis?method=GET" \
  -H "Authorization: Bearer your_token_here"

# 按状态过滤
curl -X GET "http://localhost:8080/modules/1/apis?status=active" \
  -H "Authorization: Bearer your_token_here"

# 按标签过滤
curl -X GET "http://localhost:8080/modules/1/apis?tags=重要&tags=核心" \
  -H "Authorization: Bearer your_token_here"

# 按认证类型过滤
curl -X GET "http://localhost:8080/modules/1/apis?auth_type=bearer" \
  -H "Authorization: Bearer your_token_here"

# 关键字搜索
curl -X GET "http://localhost:8080/modules/1/apis?search_keyword=user" \
  -H "Authorization: Bearer your_token_here"

# 包含统计信息
curl -X GET "http://localhost:8080/modules/1/apis?include_statistics=true" \
  -H "Authorization: Bearer your_token_here"

# 包含已删除
curl -X GET "http://localhost:8080/modules/1/apis?include_deleted=true" \
  -H "Authorization: Bearer your_token_here"

# 按名称排序
curl -X GET "http://localhost:8080/modules/1/apis?sort_by=name&sort_order=asc" \
  -H "Authorization: Bearer your_token_here"

# 分页查询
curl -X GET "http://localhost:8080/modules/1/apis?page=1&page_size=10" \
  -H "Authorization: Bearer your_token_here"

# 组合条件
curl -X GET "http://localhost:8080/modules/1/apis?method=GET&status=active&tags=重要&include_statistics=true&sort_by=name&sort_order=asc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
```

#### 响应数据

**成功响应** (HTTP 200):

```json
{
  "code": 1,
  "msg": "查询接口列表成功",
  "data": {
    "total": 25,
    "items": [
      {
        "api_id": 101,
        "api_code": "GET_USER_INFO",
        "module_id": 5,
        "name": "获取用户信息",
        "method": "GET",
        "path": "/api/v1/users/{userId}",
        "full_url": "https://api.example.com/api/v1/users/{userId}",
        "description": "根据用户ID获取用户详细信息",
        "status": "active",
        "version": "1.0",
        "auth_type": "bearer",
        "tags": ["用户管理", "核心接口"],
        "request_body_type": null,
        "response_body_type": "json",
        "timeout_seconds": 30,
        "precondition_count": 2,
        "test_case_count": 8,
        "creator_info": {
          "user_id": 123,
          "name": "张三",
          "avatar_url": "/avatars/zhangsan.jpg"
        },
        "created_at": "2024-01-15T10:30:00.000Z",
        "updated_at": "2024-09-10T16:45:00.000Z"
      },
      {
        "api_id": 102,
        "api_code": "CREATE_USER",
        "module_id": 5,
        "name": "创建用户",
        "method": "POST",
        "path": "/api/v1/users",
        "full_url": "https://api.example.com/api/v1/users",
        "description": "创建新用户账号",
        "status": "active",
        "version": "1.0",
        "auth_type": "bearer",
        "tags": ["用户管理", "核心接口"],
        "request_body_type": "json",
        "response_body_type": "json",
        "timeout_seconds": 30,
        "precondition_count": 1,
        "test_case_count": 6,
        "creator_info": {
          "user_id": 123,
          "name": "张三",
          "avatar_url": "/avatars/zhangsan.jpg"
        },
        "created_at": "2024-01-16T14:20:00.000Z",
        "updated_at": "2024-08-20T11:15:00.000Z"
      }
    ],
    "page": 1,
    "page_size": 20,
    "summary": {
      "total_apis": 25,
      "by_method": {
        "GET": 10,
        "POST": 8,
        "PUT": 4,
        "DELETE": 3
      },
      "by_status": {
        "active": 20,
        "deprecated": 5
      },
      "by_auth_type": {
        "bearer": 18,
        "none": 5,
        "api_key": 2
      }
    }
  }
}
```

**失败响应**:

```json
// 模块不存在 (HTTP 200)
{
  "code": -4,
  "msg": "模块不存在",
  "data": null
}

// 模块已被删除 (HTTP 200)
{
  "code": 0,
  "msg": "模块已被删除",
  "data": null
}

// 权限不足 (HTTP 200)
{
  "code": -2,
  "msg": "权限不足，无法查看接口列表",
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

### 查询流程
1. **参数校验**: 验证模块ID和查询参数
2. **模块存在性检查**: 检查模块是否存在
3. **删除状态检查**: 检查模块是否已被删除
4. **权限验证**: 检查用户是否有模块访问权限
5. **设置默认值**: 设置查询参数的默认值
6. **构建查询条件**: 根据查询参数构建WHERE条件
7. **执行查询**: 查询接口列表和总数
8. **统计信息**: 如果需要，查询接口统计摘要
9. **返回结果**: 返回分页的接口列表和统计信息

### 权限规则
1. **创建者权限**: 可以访问自己创建的模块
2. **项目成员权限**: 项目成员可以访问模块
3. **模块访问权限**: 需要模块访问权限

### 过滤条件
1. **方法过滤**: 按请求方法过滤
2. **状态过滤**: 按接口状态过滤
3. **标签过滤**: 按标签过滤（支持多个标签）
4. **认证类型过滤**: 按认证类型过滤
5. **关键字搜索**: 在接口名称、描述、路径中搜索
6. **删除状态**: 是否包含已删除的接口

### 排序支持
1. **排序字段**: 名称、方法、路径、创建时间、更新时间
2. **排序顺序**: 升序、降序
3. **默认排序**: 按创建时间降序

### 分页支持
1. **页码**: 从1开始
2. **每页条数**: 默认20，最大100
3. **总数**: 返回符合条件的数据总条数

### 统计信息
1. **总接口数**: 符合条件接口的总数
2. **按方法统计**: 各请求方法的接口数量
3. **按状态统计**: 各状态的接口数量
4. **按认证类型统计**: 各认证类型的接口数量

## 安全特性

### 1. 认证要求
- 使用`@GlobalInterceptor(checkLogin = true)`进行认证
- 必须提供有效的认证令牌

### 2. 权限控制
- 验证用户是否有模块访问权限
- 只能访问有权限的模块

### 3. 数据过滤
- 默认过滤已删除的接口
- 支持包含已删除接口的查询

### 4. 参数验证
- 分页参数范围限制
- 排序字段有效性验证
- 查询参数格式验证

## 错误处理

### 错误码说明
| 错误码 | 含义 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| 0 | 业务逻辑失败 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 200 |
| -3 | 参数校验失败 | 400 |
| -4 | 资源不存在 | 200 |
| -5 | 服务器内部异常 | 500 |

### 常见错误场景
1. **模块不存在**: 指定的模块ID不存在
2. **模块已删除**: 模块已经被软删除
3. **权限不足**: 用户没有模块访问权限
4. **认证失败**: 未提供Token或Token无效/过期
5. **参数错误**: 查询参数格式错误或超出范围

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有模块访问权限
3. **分页限制**: 每页最大100条记录
4. **统计信息**: 统计信息查询可能较慢，建议根据需要开启
5. **标签过滤**: 支持多个标签的AND条件过滤
6. **关键字搜索**: 在接口名称、描述、路径中进行模糊搜索

## 性能优化

### 1. 索引优化
- 模块ID索引
- 接口状态索引
- 请求方法索引
- 认证类型索引
- 创建时间索引

### 2. 查询优化
- 使用LIMIT和OFFSET进行分页
- 条件查询使用索引
- 统计信息使用聚合查询

### 3. 缓存策略
- 统计信息可以缓存
- 接口列表可以缓存（根据更新频率）

## 测试

可以使用提供的 `test_api_list_api.bat` 脚本进行接口测试，或者使用 Postman 等工具进行测试。

测试前请确保：
1. 应用已启动
2. 数据库中有测试数据
3. 有有效的认证令牌
4. 有模块访问权限

## 后续扩展建议

1. **高级搜索**: 支持更复杂的搜索条件
2. **导出功能**: 支持接口列表导出
3. **批量操作**: 支持批量接口操作
4. **实时统计**: 实时更新统计信息
5. **缓存优化**: 添加查询结果缓存
6. **搜索优化**: 使用全文搜索引擎
7. **权限细化**: 更细粒度的访问权限控制
8. **审计日志**: 记录查询操作日志
