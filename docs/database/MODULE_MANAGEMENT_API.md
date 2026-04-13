# 项目模块管理接口文档

## 接口概述

项目模块管理接口提供了获取指定项目下模块列表的功能，支持树形结构和平铺结构两种展示方式。

## 接口详情

### 获取模块列表

**请求路径**: `/projects/{project_id}/modules`  
**请求方式**: `GET`  
**接口描述**: 获取指定项目的模块列表，支持树形结构和平铺结构

#### 请求参数

**路径参数**:
- `project_id` (number, 必须): 项目ID

**查询参数**:
- `structure` (string, 可选): 返回结构，可选值：`tree`(树形)、`flat`(平铺)，默认：`flat`
- `status` (string, 可选): 模块状态过滤，可选值：`active`、`inactive`、`archived`，默认：`active`
- `include_deleted` (boolean, 可选): 是否包含已删除的模块，默认：`false`
- `include_statistics` (boolean, 可选): 是否包含统计信息（接口数、用例数），默认：`false`
- `search_keyword` (string, 可选): 关键字搜索（模块名称、描述）
- `sort_by` (string, 可选): 排序字段，可选值：`sort_order`、`name`、`created_at`，默认：`sort_order`
- `sort_order` (string, 可选): 排序顺序，可选值：`asc`、`desc`，默认：`asc`

#### 请求示例

```bash
# 获取项目1的模块列表（默认平铺结构）
GET /projects/1/modules

# 获取项目1的模块列表（树形结构）
GET /projects/1/modules?structure=tree

# 获取项目1的模块列表（包含统计信息）
GET /projects/1/modules?include_statistics=true

# 获取项目1的模块列表（按名称排序）
GET /projects/1/modules?sort_by=name&sort_order=asc

# 获取项目1的模块列表（搜索关键字）
GET /projects/1/modules?search_keyword=用户
```

#### 响应数据

**成功响应** (HTTP 200):

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "project_id": 1,
    "project_name": "电商平台项目",
    "total_modules": 8,
    "modules": [
      {
        "module_id": 1,
        "module_code": "USER_MGMT",
        "project_id": 1,
        "parent_module_id": null,
        "name": "用户管理模块",
        "description": "用户相关的功能模块",
        "sort_order": 1,
        "status": "active",
        "owner_info": {
          "user_id": 123,
          "name": "张三",
          "avatar_url": "/avatars/zhangsan.jpg"
        },
        "tags": ["核心模块", "用户相关"],
        "created_by": 1,
        "creator_name": "系统管理员",
        "created_at": "2024-01-15T10:30:00.000Z",
        "updated_at": "2024-09-10T16:45:00.000Z",
        "is_deleted": false,
        "api_count": 25,
        "case_count": 120,
        "level": 1,
        "path": "用户管理模块"
      }
    ]
  }
}
```

**失败响应**:

```json
// 项目不存在 (HTTP 404)
{
  "code": -4,
  "msg": "项目不存在",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看模块列表",
  "data": null
}

// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

## 数据结构说明

### ModuleDTO 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| module_id | Integer | 模块ID |
| module_code | String | 模块编码 |
| project_id | Integer | 项目ID |
| parent_module_id | Integer | 父模块ID（树形结构使用） |
| name | String | 模块名称 |
| description | String | 模块描述 |
| sort_order | Integer | 排序顺序 |
| status | String | 模块状态 |
| owner_info | Object | 负责人信息 |
| tags | Array | 标签列表 |
| created_by | Integer | 创建人ID |
| creator_name | String | 创建人姓名 |
| created_at | String | 创建时间 |
| updated_at | String | 更新时间 |
| is_deleted | Boolean | 是否已删除 |
| api_count | Integer | 接口数量（统计信息） |
| case_count | Integer | 用例数量（统计信息） |
| level | Integer | 层级（平铺结构使用） |
| path | String | 路径（平铺结构使用） |
| children | Array | 子模块列表（树形结构使用） |

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有访问该项目的权限
3. **性能考虑**: 
   - 统计信息查询可能较慢，建议根据需要开启
   - 树形结构处理需要注意性能，避免递归查询导致的性能问题
4. **数据完整性**: 模块编码在项目内必须唯一
5. **排序功能**: 支持模块的拖拽排序功能，通过 `sort_order` 字段实现

## 错误码说明

| 错误码 | 含义 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 403 |
| -3 | 参数校验失败 | 400 |
| -4 | 资源不存在 | 404 |
| -5 | 服务器内部异常 | 500 |

## 测试

可以使用提供的 `test_module_apis.bat` 脚本进行接口测试，或者使用 Postman 等工具进行测试。

测试前请确保：
1. 应用已启动
2. 数据库中有测试数据
3. 有有效的认证令牌
