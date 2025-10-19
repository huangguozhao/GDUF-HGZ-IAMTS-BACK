# 项目执行管理模块 API 文档

## 概述

项目执行管理模块提供了完整的项目管理功能，包括项目的增删改查、分页查询、排序等功能。

## 接口列表

### 1. 分页获取项目列表

**接口地址**: `GET /api/projects`

**接口描述**: 分页查询项目列表，支持按名称搜索和多种排序方式

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| name | string | 否 | - | 项目名称模糊查询 |
| creator_id | integer | 否 | - | 创建人ID过滤 |
| include_deleted | boolean | 否 | false | 是否包含已删除的项目 |
| sort_by | string | 否 | created_at | 排序字段：name, created_at, updated_at |
| sort_order | string | 否 | desc | 排序顺序：asc, desc |
| page | integer | 否 | 1 | 页码 |
| page_size | integer | 否 | 10 | 每页条数，最大100 |

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 15,
    "items": [
      {
        "projectId": 1,
        "name": "电商平台项目",
        "description": "新一代电商平台开发项目",
        "creatorInfo": {
          "userId": 123,
          "name": "张管理员",
          "avatarUrl": "/avatars/zhang.jpg"
        },
        "createdAt": "2024-01-15T10:30:00.000Z",
        "updatedAt": "2024-09-10T16:45:00.000Z",
        "isDeleted": false,
        "deletedAt": null
      }
    ],
    "page": 1,
    "pageSize": 10
  }
}
```

### 2. 根据ID获取项目详情

**接口地址**: `GET /api/projects/{projectId}`

**接口描述**: 根据项目ID获取项目详细信息

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | integer | 是 | 项目ID |

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "projectId": 1,
    "projectCode": "PROJ-001",
    "name": "电商平台项目",
    "description": "新一代电商平台开发项目",
    "status": "active",
    "version": "1.0",
    "createdBy": 123,
    "updatedBy": null,
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-09-10T16:45:00.000Z",
    "isDeleted": false,
    "deletedAt": null,
    "deletedBy": null
  }
}
```

### 3. 根据项目编码获取项目详情

**接口地址**: `GET /api/projects/code/{projectCode}`

**接口描述**: 根据项目编码获取项目详细信息

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectCode | string | 是 | 项目编码 |

### 4. 创建项目

**接口地址**: `POST /api/projects`

**接口描述**: 创建新项目

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
{
  "projectCode": "PROJ-001",
  "name": "电商平台项目",
  "description": "新一代电商平台开发项目",
  "status": "active",
  "version": "1.0",
  "createdBy": 123
}
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "创建项目成功",
  "data": 1
}
```

### 5. 更新项目

**接口地址**: `PUT /api/projects/{projectId}`

**接口描述**: 更新项目信息

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | integer | 是 | 项目ID |

**请求体**:
```json
{
  "name": "更新后的项目名称",
  "description": "更新后的项目描述",
  "status": "active",
  "version": "1.1"
}
```

### 6. 删除项目

**接口地址**: `DELETE /api/projects/{projectId}`

**接口描述**: 逻辑删除项目

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | integer | 是 | 项目ID |

### 7. 检查项目编码是否存在

**接口地址**: `GET /api/projects/check-code`

**接口描述**: 检查项目编码是否已存在

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| project_code | string | 是 | 项目编码 |
| exclude_id | integer | 否 | 排除的项目ID（用于更新时检查） |

**响应示例**:
```json
{
  "code": 1,
  "msg": "检查完成",
  "data": false
}
```

## 错误码说明

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| 0 | 业务逻辑失败 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 403 |
| -3 | 参数校验失败 | 400 |
| -4 | 资源不存在 | 404 |
| -5 | 服务器内部异常 | 500 |

## 注意事项

1. 所有接口都需要认证，需要在请求头中携带有效的Token
2. 分页查询的page_size最大值为100
3. 项目编码在系统中必须唯一
4. 删除操作为逻辑删除，不会物理删除数据
5. 排序字段支持：name, created_at, updated_at
6. 排序顺序支持：asc, desc

## 数据库表结构

项目信息存储在`Projects`表中，主要字段包括：
- project_id: 项目ID（主键）
- project_code: 项目编码（唯一）
- name: 项目名称
- description: 项目描述
- status: 项目状态（active, inactive, archived）
- version: 版本号
- created_by: 创建人ID
- updated_by: 更新人ID
- created_at: 创建时间
- updated_at: 更新时间
- is_deleted: 是否删除
- deleted_at: 删除时间
- deleted_by: 删除人ID
