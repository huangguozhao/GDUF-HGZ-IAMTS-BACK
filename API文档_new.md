# IATMS II 接口文档

## 1. 项目概述

IATMS II 是一个接口自动化测试管理系统，提供了完整的接口测试解决方案，包括API管理、测试用例设计、测试执行、报告生成等功能。

## 2. 接口规范

### 2.1 基本信息
- **API版本**: 1.0
- **请求方法**: GET、POST、PUT、DELETE
- **数据格式**: JSON
- **字符编码**: UTF-8
- **内容类型**: application/json

### 2.2 响应格式

所有接口返回统一格式：

```json
{
  "status": "success",
  "message": "操作成功",
  "data": {}
}
```

#### 响应状态码
- `success`: 请求成功
- `paramError`: 参数错误
- `authError`: 认证错误
- `businessError`: 业务逻辑错误
- `serverError`: 服务器错误

### 2.3 认证机制

系统采用JWT Token认证机制：

1. 登录成功后获取Token
2. 在后续请求的Header中添加 `Authorization: Bearer {token}`
3. Token有效期一般为24小时

## 3. 接口分类

### 3.1 认证接口 (AuthController)

#### 3.1.1 用户登录
- **接口地址**: `/auth/login`
- **请求方法**: POST
- **认证要求**: 不需要
- **请求参数**:
  ```json
  {
    "email": "admin@example.com",
    "password": "123456"
  }
  ```
- **请求参数说明**:
  - `email`: 用户邮箱（必填，格式必须正确）
  - `password`: 用户密码（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "登录成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "userInfo": {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "role": "admin"
      }
    }
  }
  ```

#### 3.1.2 获取当前用户信息
- **接口地址**: `/auth/me`
- **请求方法**: GET
- **认证要求**: 需要登录
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "admin"
    }
  }
  ```

#### 3.1.3 密码重置请求
- **接口地址**: `/auth/password/reset-request`
- **请求方法**: POST
- **认证要求**: 不需要
- **请求参数**:
  ```json
  {
    "account": "user@example.com",
    "channel": "email"
  }
  ```
- **请求参数说明**:
  - `account`: 用户注册的邮箱或手机号（必填）
  - `channel`: 发送验证码的渠道（必填，只能是email或sms）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "重置验证码已发送，请查收",
    "data": {
      "reset_token_id": "reset_123456"
    }
  }
  ```

#### 3.1.4 执行密码重置
- **接口地址**: `/auth/password/reset`
- **请求方法**: POST
- **认证要求**: 不需要
- **请求参数**:
  ```json
  {
    "account": "user@example.com",
    "verificationCode": "123456",
    "newPassword": "newpassword123"
  }
  ```
- **请求参数说明**:
  - `account`: 请求重置时使用的邮箱或手机号（必填）
  - `verificationCode`: 从邮箱或短信中收到的验证码（必填，长度4-8位）
  - `newPassword`: 新的密码（必填，长度8-50位）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "密码重置成功",
    "data": null
  }
  ```

### 3.2 用户管理接口 (UserController)

#### 3.2.1 分页查询用户列表
- **接口地址**: `/users`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `name`: 用户姓名模糊查询（可选）
  - `email`: 用户邮箱模糊查询（可选）
  - `status`: 状态筛选（active/pending/inactive，可选）
  - `position`: 角色/职位筛选（可选）
  - `startDate`: 创建时间开始日期（可选）
  - `endDate`: 创建时间结束日期（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页记录数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "id": 1,
          "name": "管理员",
          "email": "admin@example.com",
          "status": "active",
          "position": "admin",
          "createdAt": "2025-01-01 10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.2.2 根据用户名或邮箱模糊查询用户
- **接口地址**: `/users/search`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `keyword`: 搜索关键词（用户名或邮箱，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": [
      {
        "id": 1,
        "name": "管理员",
        "email": "admin@example.com",
        "status": "active",
        "position": "admin"
      },
      {
        "id": 2,
        "name": "测试用户",
        "email": "test@example.com",
        "status": "active",
        "position": "user"
      }
    ]
  }
  ```

#### 3.2.3 添加用户
- **接口地址**: `/users`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "name": "新用户",
    "email": "newuser@example.com",
    "password": "password123",
    "phone": "13800138000",
    "avatarUrl": "http://example.com/avatar.jpg",
    "departmentId": 1,
    "employeeId": "EMP001",
    "position": "tester",
    "description": "测试人员",
    "status": "active"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户创建成功",
    "data": 3
  }
  ```

#### 3.2.4 更新用户信息
- **接口地址**: `/users/{userId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "name": "更新后的用户",
    "email": "updated@example.com",
    "phone": "13900139000",
    "avatarUrl": "http://example.com/updated-avatar.jpg",
    "departmentId": 2,
    "employeeId": "EMP002",
    "position": "senior_tester",
    "description": "高级测试人员",
    "roleIds": [1, 2]
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户信息更新成功",
    "data": null
  }
  ```

#### 3.2.5 更新用户状态
- **接口地址**: `/users/{userId}/status`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "status": "inactive"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户状态已更新",
    "data": null
  }
  ```

#### 3.2.6 删除用户
- **接口地址**: `/users/{userId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和管理员权限
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户删除成功",
    "data": null
  }
  ```

#### 3.2.7 为用户分配项目
- **接口地址**: `/users/{userId}/projects`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "project_id": 1,
    "project_role": "member",
    "permission_level": "full"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户已成功分配到项目",
    "data": {
      "member_id": 1
    }
  }
  ```

#### 3.2.8 移除用户项目分配
- **接口地址**: `/users/{userId}/projects/{projectId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和管理员权限
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户已从项目中成功移除",
    "data": null
  }
  ```

#### 3.2.9 分页获取用户项目列表
- **接口地址**: `/users/{userId}/projects`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `status`: 项目状态（可选）
  - `project_role`: 项目角色（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "projectId": 1,
          "projectName": "测试项目",
          "projectRole": "member",
          "permissionLevel": "full",
          "joinedAt": "2025-01-01 10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.2.10 更新用户项目成员信息
- **接口地址**: `/users/{userId}/projects/{projectId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "project_role": "admin",
    "permission_level": "full"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户项目成员信息更新成功",
    "data": null
  }
  ```

### 3.3 角色管理接口 (RoleController)

#### 3.3.1 分页查询角色列表
- **接口地址**: `/roles`
- **请求方法**: GET
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  - `role_name`: 角色名称模糊查询（可选）
  - `is_super_admin`: 是否超级管理员（可选）
  - `include_deleted`: 是否包含已删除角色（可选，默认false）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页记录数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "id": 1,
          "roleName": "admin",
          "description": "系统管理员",
          "isSuperAdmin": true,
          "createdAt": "2025-01-01 10:00:00",
          "updatedAt": "2025-01-01 10:00:00"
        },
        {
          "id": 2,
          "roleName": "user",
          "description": "普通用户",
          "isSuperAdmin": false,
          "createdAt": "2025-01-01 10:00:00",
          "updatedAt": "2025-01-01 10:00:00"
        }
      ],
      "total": 2,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.3.2 创建角色
- **接口地址**: `/roles`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "roleName": "test_role",
    "description": "测试角色",
    "isSuperAdmin": false
  }
  ```
- **请求参数说明**:
  - `roleName`: 角色名称（必填，唯一）
  - `description`: 角色描述（可选）
  - `isSuperAdmin`: 是否超级管理员（可选，默认false）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "角色创建成功",
    "data": {
      "id": 3,
      "roleName": "test_role",
      "description": "测试角色",
      "isSuperAdmin": false,
      "createdAt": "2025-01-01 10:00:00",
      "updatedAt": "2025-01-01 10:00:00"
    }
  }
  ```

#### 3.3.3 更新角色信息
- **接口地址**: `/roles/{roleId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "roleName": "updated_role",
    "description": "更新后的角色描述",
    "isSuperAdmin": false
  }
  ```
- **请求参数说明**:
  - `roleName`: 角色名称（可选）
  - `description`: 角色描述（可选）
  - `isSuperAdmin`: 是否超级管理员（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "角色信息更新成功",
    "data": {
      "id": 3,
      "roleName": "updated_role",
      "description": "更新后的角色描述",
      "isSuperAdmin": false,
      "createdAt": "2025-01-01 10:00:00",
      "updatedAt": "2025-01-02 10:00:00"
    }
  }
  ```

#### 3.3.4 删除角色
- **接口地址**: `/roles/{roleId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和管理员权限
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "角色删除成功",
    "data": null
  }
  ```

#### 3.3.5 为角色分配权限
- **接口地址**: `/roles/{roleId}/permissions`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "permissionIds": [1, 2, 3, 4]
  }
  ```
- **请求参数说明**:
  - `permissionIds`: 权限ID列表（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "权限分配成功",
    "data": null
  }
  ```

### 3.4 权限管理接口 (PermissionController)

#### 3.4.1 分页查询权限列表
- **接口地址**: `/permissions`
- **请求方法**: GET
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  - `permission_name`: 权限名称模糊查询（可选）
  - `role_id`: 角色ID（可选，当提供时会标记`is_assigned`字段）
  - `include_deleted`: 是否包含已删除权限（可选，默认false）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页记录数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "permissionId": 1,
          "permissionName": "read_users",
          "description": "查看用户权限",
          "roleCount": 2,
          "isAssigned": true,
          "createdAt": "2025-01-01 10:00:00",
          "updatedAt": "2025-01-01 10:00:00",
          "isDeleted": false,
          "deletedAt": null
        },
        {
          "permissionId": 2,
          "permissionName": "write_users",
          "description": "编辑用户权限",
          "roleCount": 1,
          "isAssigned": false,
          "createdAt": "2025-01-01 10:00:00",
          "updatedAt": "2025-01-01 10:00:00",
          "isDeleted": false,
          "deletedAt": null
        }
      ],
      "total": 2,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

### 3.5 项目管理接口 (ProjectController)

#### 3.5.1 分页查询项目列表
- **接口地址**: `/projects`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `name`: 项目名称（模糊查询，可选）
  - `creator_id`: 创建人ID（可选）
  - `include_deleted`: 是否包含已删除的项目（可选，默认false）
  - `sort_by`: 排序字段（可选，默认created_at）
  - `sort_order`: 排序顺序（可选，默认desc）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "items": [
        {
          "id": 1,
          "name": "测试项目",
          "description": "这是一个测试项目",
          "projectCode": "TEST_PROJ",
          "projectType": "API",
          "status": "ACTIVE",
          "creatorId": 1,
          "createdAt": "2025-01-01 10:00:00",
          "updatedAt": "2025-01-01 10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.5.2 获取项目详情
- **接口地址**: `/projects/{projectId}`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "id": 1,
      "name": "测试项目",
      "description": "这是一个测试项目",
      "projectCode": "TEST_PROJ",
      "projectType": "API",
      "status": "ACTIVE",
      "creatorId": 1,
      "createdAt": "2025-01-01 10:00:00",
      "updatedAt": "2025-01-01 10:00:00"
    }
  }
  ```

#### 3.5.3 根据项目编码获取项目详情
- **接口地址**: `/projects/code/{projectCode}`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `projectCode`: 项目编码（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "id": 1,
      "name": "测试项目",
      "description": "这是一个测试项目",
      "projectCode": "TEST_PROJ",
      "projectType": "API",
      "status": "ACTIVE",
      "creatorId": 1,
      "createdAt": "2025-01-01 10:00:00",
      "updatedAt": "2025-01-01 10:00:00"
    }
  }
  ```

#### 3.5.4 创建项目（简化版）
- **接口地址**: `/projects`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "新项目",
    "description": "这是一个新项目",
    "projectCode": "NEW_PROJ",
    "projectType": "API",
    "status": "ACTIVE",
    "avatarUrl": "http://example.com/avatar.jpg"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "项目创建成功",
    "data": {
      "projectId": 2,
      "projectCode": "NEW_PROJ"
    }
  }
  ```

#### 3.5.5 创建项目（完整版）
- **接口地址**: `/projects/create`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "完整项目",
    "description": "这是一个完整的项目",
    "projectCode": "FULL_PROJ",
    "projectType": "API",
    "status": "ACTIVE",
    "creatorId": 1,
    "avatarUrl": "http://example.com/avatar.jpg"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "创建项目成功",
    "data": 3
  }
  ```

#### 3.5.6 更新项目信息（简化版）
- **接口地址**: `/projects/{projectId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **请求参数**:
  ```json
  {
    "name": "更新后的项目名称",
    "description": "更新后的项目描述",
    "status": "INACTIVE",
    "avatarUrl": "http://example.com/new-avatar.jpg"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "项目信息更新成功",
    "data": {
      "projectId": 1,
      "projectCode": "TEST_PROJ"
    }
  }
  ```

#### 3.5.7 更新项目（完整版）
- **接口地址**: `/projects/{projectId}/full`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **请求参数**:
  ```json
  {
    "name": "更新后的完整项目",
    "description": "更新后的完整项目描述",
    "projectCode": "FULL_PROJ",
    "projectType": "API",
    "status": "ACTIVE",
    "creatorId": 1
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "更新项目成功",
    "data": true
  }
  ```

#### 3.5.8 删除项目（安全删除）
- **接口地址**: `/projects/{projectId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **请求参数**:
  - `force_delete`: 是否强制删除（忽略关联数据检查，可选，默认false）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "项目删除成功",
    "data": {
      "success": true,
      "projectId": 1,
      "message": "项目删除成功"
    }
  }
  ```

#### 3.5.9 简单删除项目（不检查关联数据）
- **接口地址**: `/projects/{projectId}/simple`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "项目删除成功",
    "data": true
  }
  ```

#### 3.5.10 检查项目关联数据
- **接口地址**: `/projects/{projectId}/relations`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "检查完成",
    "data": {
      "projectId": 1,
      "hasModules": false,
      "hasApis": false,
      "hasTestCases": false,
      "hasTestExecutions": false
    }
  }
  ```

#### 3.5.11 检查项目编码是否存在
- **接口地址**: `/projects/check-code`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `project_code`: 项目编码（必填）
  - `exclude_id`: 排除的项目ID（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "检查完成",
    "data": false
  }
  ```

#### 3.5.12 获取项目统计数据
- **接口地址**: `/projects/{projectId}/statistics`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询项目统计数据成功",
    "data": {
      "totalModules": 10,
      "totalApis": 100,
      "totalTestCases": 50,
      "totalTestExecutions": 200
    }
  }
  ```

#### 3.5.13 分页查询项目成员列表
- **接口地址**: `/projects/{projectId}/members`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **请求参数**:
  - `status`: 成员状态过滤（可选）
  - `permission_level`: 权限级别过滤（可选）
  - `project_role`: 项目角色过滤（可选）
  - `search_keyword`: 关键字搜索（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询项目成员成功",
    "data": {
      "items": [
        {
          "userId": 1,
          "name": "管理员",
          "email": "admin@example.com",
          "projectRole": "admin",
          "permissionLevel": "full",
          "status": "active",
          "joinedAt": "2025-01-01 10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.5.14 添加项目成员
- **接口地址**: `/projects/{projectId}/members`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **请求参数**:
  ```json
  {
    "userId": 2,
    "projectRole": "member",
    "permissionLevel": "full"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "成员已成功添加到项目",
    "data": {
      "userId": 2,
      "projectId": 1,
      "projectRole": "member",
      "permissionLevel": "full"
    }
  }
  ```

#### 3.5.15 更新项目成员角色/权限
- **接口地址**: `/projects/{projectId}/members/{userId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **路径参数**:
  - `projectId`: 项目ID（必填）
  - `userId`: 用户ID（必填）
- **请求参数**:
  ```json
  {
    "projectRole": "admin",
    "permissionLevel": "full"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "成员角色更新成功",
    "data": {
      "userId": 2,
      "projectId": 1,
      "projectRole": "admin",
      "permissionLevel": "full"
    }
  }
  ```

#### 3.5.16 获取项目模块列表
- **接口地址**: `/projects/{projectId}/modules`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `projectId`: 项目ID（必填）
- **请求参数**:
  - `structure`: 返回结构（tree/flat，可选）
  - `status`: 模块状态过滤（可选）
  - `include_deleted`: 是否包含已删除的模块（可选，默认false）
  - `include_statistics`: 是否包含统计信息（可选，默认false）
  - `search_keyword`: 关键字搜索（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询模块列表成功",
    "data": {
      "modules": [
        {
          "id": 1,
          "name": "用户模块",
          "description": "用户相关模块",
          "projectId": 1,
          "status": "ACTIVE",
          "parentId": null,
          "createdAt": "2025-01-01 10:00:00",
          "updatedAt": "2025-01-01 10:00:00"
        }
      ]
    }
  }
  ```

### 3.6 环境配置接口 (EnvironmentConfigController)

#### 3.6.1 创建环境配置
- **接口地址**: `/api/environments`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "envCode": "DEV",
    "envName": "开发环境",
    "envType": "development",
    "description": "开发测试环境",
    "baseUrl": "http://dev.example.com",
    "domain": "example.com",
    "protocol": "http",
    "port": 8080,
    "databaseConfig": {
      "host": "localhost",
      "port": 3306,
      "username": "dev_user",
      "password": "dev_pass",
      "database": "dev_db"
    },
    "externalServices": {
      "authService": "http://auth.dev.example.com"
    },
    "variables": {
      "api_version": "v1",
      "timeout": 30000
    },
    "authConfig": {
      "type": "jwt",
      "token": "dev_token"
    },
    "status": "active",
    "isDefault": false,
    "maintenanceMessage": null,
    "deploymentInfo": {
      "deployedAt": "2025-01-01T10:00:00Z",
      "deployedBy": "admin"
    },
    "deployedVersion": "1.0.0"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "创建环境配置成功",
    "data": {
      "envId": 1,
      "envCode": "DEV",
      "envName": "开发环境",
      "envType": "development",
      "description": "开发测试环境",
      "baseUrl": "http://dev.example.com",
      "domain": "example.com",
      "protocol": "http",
      "port": 8080,
      "status": "active",
      "isDefault": false,
      "createdAt": "2025-01-01T10:00:00Z",
      "updatedAt": "2025-01-01T10:00:00Z"
    }
  }
  ```

#### 3.6.2 查询环境配置详情
- **接口地址**: `/api/environments/{envId}`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `envId`: 环境配置ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询环境配置成功",
    "data": {
      "envId": 1,
      "envCode": "DEV",
      "envName": "开发环境",
      "envType": "development",
      "description": "开发测试环境",
      "baseUrl": "http://dev.example.com",
      "domain": "example.com",
      "protocol": "http",
      "port": 8080,
      "databaseConfig": {
        "host": "localhost",
        "port": 3306,
        "username": "dev_user",
        "password": "dev_pass",
        "database": "dev_db"
      },
      "externalServices": {
        "authService": "http://auth.dev.example.com"
      },
      "variables": {
        "api_version": "v1",
        "timeout": 30000
      },
      "authConfig": {
        "type": "jwt",
        "token": "dev_token"
      },
      "status": "active",
      "isDefault": false,
      "maintenanceMessage": null,
      "deploymentInfo": {
        "deployedAt": "2025-01-01T10:00:00Z",
        "deployedBy": "admin"
      },
      "deployedVersion": "1.0.0",
      "createdAt": "2025-01-01T10:00:00Z",
      "updatedAt": "2025-01-01T10:00:00Z"
    }
  }
  ```

#### 3.6.3 更新环境配置
- **接口地址**: `/api/environments/{envId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **路径参数**:
  - `envId`: 环境配置ID（必填）
- **请求参数**:
  ```json
  {
    "envName": "更新后的开发环境",
    "description": "更新后的开发测试环境",
    "baseUrl": "http://updated.dev.example.com",
    "port": 8081,
    "status": "active",
    "isDefault": true
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "更新环境配置成功",
    "data": {
      "envId": 1,
      "envCode": "DEV",
      "envName": "更新后的开发环境",
      "envType": "development",
      "description": "更新后的开发测试环境",
      "baseUrl": "http://updated.dev.example.com",
      "domain": "example.com",
      "protocol": "http",
      "port": 8081,
      "status": "active",
      "isDefault": true,
      "updatedAt": "2025-01-02T10:00:00Z"
    }
  }
  ```

#### 3.6.4 删除环境配置
- **接口地址**: `/api/environments/{envId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **路径参数**:
  - `envId`: 环境配置ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "删除环境配置成功",
    "data": null
  }
  ```

#### 3.6.5 分页查询环境配置列表
- **接口地址**: `/api/environments`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `envType`: 环境类型过滤（可选）
  - `status`: 状态过滤（可选）
  - `searchKeyword`: 关键字搜索（可选）
  - `isDefault`: 是否默认环境（可选）
  - `sortBy`: 排序字段（可选）
  - `sortOrder`: 排序顺序（可选）
  - `page`: 页码（可选，默认1）
  - `pageSize`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询环境配置列表成功",
    "data": {
      "items": [
        {
          "envId": 1,
          "envCode": "DEV",
          "envName": "开发环境",
          "envType": "development",
          "description": "开发测试环境",
          "baseUrl": "http://dev.example.com",
          "status": "active",
          "isDefault": true,
          "createdAt": "2025-01-01T10:00:00Z",
          "updatedAt": "2025-01-02T10:00:00Z"
        },
        {
          "envId": 2,
          "envCode": "TEST",
          "envName": "测试环境",
          "envType": "testing",
          "description": "集成测试环境",
          "baseUrl": "http://test.example.com",
          "status": "active",
          "isDefault": false,
          "createdAt": "2025-01-01T11:00:00Z",
          "updatedAt": "2025-01-01T11:00:00Z"
        }
      ],
      "total": 2,
      "page": 1,
      "pageSize": 10,
      "totalPages": 1
    }
  }
  ```

### 3.7 模块管理接口 (ModuleController)

#### 3.7.1 创建模块
- **接口地址**: `/modules`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "moduleCode": "USER_MOD",
    "projectId": 1,
    "parentModuleId": null,
    "name": "用户模块",
    "description": "用户管理相关模块",
    "sortOrder": 1,
    "status": "active",
    "ownerId": 1,
    "tags": ["用户", "认证", "权限"]
  }
  ```
- **请求参数说明**:
  - `moduleCode`: 模块编码（必填，项目内唯一）
  - `projectId`: 项目ID（必填）
  - `parentModuleId`: 父模块ID（可选，根模块为null）
  - `name`: 模块名称（必填）
  - `description`: 模块描述（可选）
  - `sortOrder`: 排序顺序（可选）
  - `status`: 模块状态（可选，默认active）
  - `ownerId`: 模块负责人ID（可选）
  - `tags`: 标签列表（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "模块创建成功",
    "data": {
      "moduleId": 1,
      "moduleCode": "USER_MOD",
      "name": "用户模块"
    }
  }
  ```

#### 3.7.2 删除模块
- **接口地址**: `/modules/{moduleId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **路径参数**:
  - `moduleId`: 模块ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "模块删除成功",
    "data": null
  }
  ```

#### 3.7.3 更新模块信息
- **接口地址**: `/modules/{moduleId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **路径参数**:
  - `moduleId`: 模块ID（必填）
- **请求参数**:
  ```json
  {
    "name": "更新后的用户模块",
    "description": "更新后的用户管理相关模块",
    "parentModuleId": 2,
    "sortOrder": 2,
    "status": "active",
    "ownerId": 2,
    "tags": ["用户", "认证", "权限", "更新"]
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "模块信息更新成功",
    "data": {
      "moduleId": 1,
      "moduleCode": "USER_MOD",
      "name": "更新后的用户模块"
    }
  }
  ```

#### 3.7.4 获取模块下的接口列表
- **接口地址**: `/modules/{moduleId}/apis`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `moduleId`: 模块ID（必填）
- **请求参数**:
  - `method`: 请求方法过滤（可选，如GET、POST等）
  - `status`: 接口状态过滤（可选）
  - `tags`: 标签过滤（可选，多个标签用逗号分隔）
  - `auth_type`: 认证类型过滤（可选）
  - `search_keyword`: 关键字搜索（可选）
  - `include_deleted`: 是否包含已删除的接口（可选，默认false）
  - `include_statistics`: 是否包含统计信息（可选，默认false）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询接口列表成功",
    "data": {
      "items": [
        {
          "id": 1,
          "name": "用户登录接口",
          "path": "/auth/login",
          "method": "POST",
          "moduleId": 1,
          "status": "active",
          "createdAt": "2025-01-01T10:00:00Z",
          "updatedAt": "2025-01-01T10:00:00Z"
        },
        {
          "id": 2,
          "name": "获取用户信息接口",
          "path": "/users/me",
          "method": "GET",
          "moduleId": 1,
          "status": "active",
          "createdAt": "2025-01-01T11:00:00Z",
          "updatedAt": "2025-01-01T11:00:00Z"
        }
      ],
      "total": 2,
      "page": 1,
      "pageSize": 10,
      "totalPages": 1
    }
  }
  ```

#### 3.7.5 获取模块统计数据
- **接口地址**: `/modules/{moduleId}/statistics`
- **请求方法**: GET
- **认证要求**: 需要登录
- **路径参数**:
  - `moduleId`: 模块ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询模块统计数据成功",
    "data": {
      "moduleId": 1,
      "totalApis": 2,
      "activeApis": 2,
      "deletedApis": 0,
      "totalTestCases": 5,
      "executedTestCases": 3,
      "passRate": 60.0,
      "totalTestExecutions": 10
    }
  }
  ```

### 3.8 API管理接口 (ApiController)

#### 3.8.1 创建接口
- **接口地址**: `/apis`
- **请求方法**: POST
- **认证要求**: 需要登录和api:create权限
- **请求参数**:
  ```json
  {
    "apiCode": "USER_LOGIN",
    "moduleId": 1,
    "name": "用户登录接口",
    "method": "POST",
    "path": "/api/auth/login",
    "baseUrl": "http://example.com",
    "requestParameters": [
      {
        "name": "username",
        "type": "string",
        "required": true,
        "description": "用户名",
        "example": "admin"
      },
      {
        "name": "password",
        "type": "string",
        "required": true,
        "description": "密码",
        "example": "password123"
      }
    ],
    "requestHeaders": [
      {
        "name": "Content-Type",
        "value": "application/json",
        "required": true
      }
    ],
    "requestBody": "{\"username\": \"admin\", \"password\": \"password123\"}",
    "requestBodyType": "json",
    "responseBodyType": "json",
    "description": "用户登录接口",
    "status": "active",
    "version": "v1",
    "authType": "none",
    "tags": ["认证", "用户"],
    "examples": [
      {
        "name": "登录示例",
        "request": {"username": "admin", "password": "password123"},
        "response": {"code": 200, "message": "登录成功"}
      }
    ],
    "timeoutSeconds": 30
  }
  ```
- **请求参数说明**:
  - `apiCode`: 接口编码（可选，不提供则自动生成）
  - `moduleId`: 模块ID（必填）
  - `name`: 接口名称（必填）
  - `method`: 请求方法（必填，如GET、POST、PUT、DELETE等）
  - `path`: 接口路径（必填）
  - `baseUrl`: 基础URL（可选）
  - `requestParameters`: 查询参数配置（可选）
  - `pathParameters`: 路径参数配置（可选）
  - `requestHeaders`: 请求头配置（可选）
  - `requestBody`: 请求体内容（可选）
  - `requestBodyType`: 请求体类型（可选，如json、xml、form、text）
  - `responseBodyType`: 响应体类型（可选，如json、xml、html、text）
  - `description`: 接口描述（可选）
  - `status`: 接口状态（可选，如draft、active、deprecated）
  - `version`: 版本号（可选）
  - `authType`: 认证类型（可选，如none、basic、bearer、apikey、oauth2）
  - `authConfig`: 认证配置（可选）
  - `tags`: 标签数组（可选）
  - `examples`: 请求示例（可选）
  - `timeoutSeconds`: 超时时间（秒，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "创建接口成功",
    "data": {
      "apiId": 1,
      "apiCode": "USER_LOGIN",
      "name": "用户登录接口",
      "method": "POST",
      "path": "/api/auth/login",
      "status": "active"
    }
  }
  ```

#### 3.8.2 更新接口
- **接口地址**: `/apis/{apiId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和api:update权限
- **路径参数**:
  - `apiId`: 接口ID（必填）
- **请求参数**:
  ```json
  {
    "name": "更新后的登录接口",
    "method": "POST",
    "path": "/api/v2/auth/login",
    "description": "更新后的用户登录接口",
    "status": "active",
    "version": "v2",
    "tags": ["认证", "用户", "v2"]
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "更新接口成功",
    "data": {
      "apiId": 1,
      "apiCode": "USER_LOGIN",
      "name": "更新后的登录接口",
      "method": "POST",
      "path": "/api/v2/auth/login",
      "status": "active",
      "version": "v2"
    }
  }
  ```

#### 3.8.3 根据ID查询接口
- **接口地址**: `/apis/{apiId}`
- **请求方法**: GET
- **认证要求**: 需要登录和api:view权限
- **路径参数**:
  - `apiId`: 接口ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "apiId": 1,
      "apiCode": "USER_LOGIN",
      "moduleId": 1,
      "name": "更新后的登录接口",
      "method": "POST",
      "path": "/api/v2/auth/login",
      "baseUrl": "http://example.com",
      "requestParameters": [
        {
          "name": "username",
          "type": "string",
          "required": true,
          "description": "用户名",
          "example": "admin"
        },
        {
          "name": "password",
          "type": "string",
          "required": true,
          "description": "密码",
          "example": "password123"
        }
      ],
      "requestHeaders": [
        {
          "name": "Content-Type",
          "value": "application/json",
          "required": true
        }
      ],
      "requestBody": "{\"username\": \"admin\", \"password\": \"password123\"}",
      "requestBodyType": "json",
      "responseBodyType": "json",
      "description": "更新后的用户登录接口",
      "status": "active",
      "version": "v2",
      "authType": "none",
      "tags": ["认证", "用户", "v2"],
      "createdBy": 1,
      "createdAt": "2025-01-01T10:00:00Z",
      "updatedAt": "2025-01-02T10:00:00Z"
    }
  }
  ```

#### 3.8.4 分页查询接口列表
- **接口地址**: `/apis`
- **请求方法**: GET
- **认证要求**: 需要登录和api:view权限
- **请求参数**:
  - `moduleId`: 模块ID（可选，过滤指定模块的接口）
  - `name`: 接口名称（可选，模糊查询）
  - `method`: 请求方法（可选，过滤指定请求方法的接口）
  - `status`: 接口状态（可选，如active、draft、deprecated）
  - `tags`: 标签（可选，多个标签用逗号分隔）
  - `path`: 接口路径（可选，模糊查询）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选，asc或desc）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "items": [
        {
          "apiId": 1,
          "apiCode": "USER_LOGIN",
          "name": "更新后的登录接口",
          "method": "POST",
          "path": "/api/v2/auth/login",
          "status": "active",
          "version": "v2",
          "moduleId": 1,
          "tags": ["认证", "用户", "v2"]
        },
        {
          "apiId": 2,
          "apiCode": "USER_GET_INFO",
          "name": "获取用户信息接口",
          "method": "GET",
          "path": "/api/users/{id}",
          "status": "active",
          "version": "v1",
          "moduleId": 1,
          "tags": ["用户", "查询"]
        }
      ],
      "total": 2,
      "page": 1,
      "pageSize": 10,
      "totalPages": 1
    }
  }
  ```

#### 3.8.5 删除接口
- **接口地址**: `/apis/{apiId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和api:delete权限
- **路径参数**:
  - `apiId`: 接口ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "接口删除成功",
    "data": null
  }
  ```

### 3.9 测试用例管理接口 (TestCaseController)

#### 3.9.1 创建测试用例
- **接口地址**: `/test-cases`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "apiId": 1,
    "caseCode": "TC_USER_LOGIN_001",
    "name": "用户登录测试用例",
    "testType": "functional",
    "priority": "P0",
    "severity": "critical",
    "description": "测试用户登录功能的基本流程",
    "preconditions": ["系统已正常启动", "数据库连接正常"],
    "testSteps": [
      {
        "step": 1,
        "description": "发送登录请求",
        "expectedResult": "返回登录成功的响应"
      }
    ],
    "assertions": [
      {
        "type": "statusCode",
        "expectedValue": 200
      },
      {
        "type": "jsonPath",
        "expression": "$.data.token",
        "expectedValue": "not null"
      }
    ],
    "environmentConfig": {
      "envId": 1,
      "variables": {
        "username": "admin",
        "password": "password123"
      }
    },
    "timeout": 30000,
    "status": "active",
    "tags": ["登录", "功能测试"],
    "version": "1.0"
  }
  ```
- **请求参数说明**:
  - `apiId`: 接口ID（必填）
  - `caseCode`: 用例编码（可选，不提供则自动生成）
  - `name`: 用例名称（必填）
  - `testType`: 测试类型（可选，如functional、performance、security等）
  - `priority`: 优先级（可选，如P0、P1、P2、P3）
  - `severity`: 严重程度（可选，如critical、high、medium、low）
  - `description`: 用例描述（可选）
  - `preconditions`: 前置条件（可选）
  - `testSteps`: 测试步骤（可选）
  - `assertions`: 断言规则（可选）
  - `environmentConfig`: 环境配置（可选）
  - `timeout`: 超时时间（毫秒，可选）
  - `status`: 用例状态（可选，如active、draft、deprecated）
  - `tags`: 标签列表（可选）
  - `version`: 版本号（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "测试用例创建成功",
    "data": {
      "caseId": 1,
      "caseCode": "TC_USER_LOGIN_001",
      "name": "用户登录测试用例",
      "apiId": 1,
      "status": "active"
    }
  }
  ```

#### 3.9.2 更新测试用例
- **接口地址**: `/test-cases/{caseId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **路径参数**:
  - `caseId`: 测试用例ID（必填）
- **请求参数**:
  ```json
  {
    "name": "更新后的用户登录测试用例",
    "priority": "P1",
    "severity": "high",
    "description": "更新后的测试用例描述",
    "testSteps": [
      {
        "step": 1,
        "description": "发送登录请求",
        "expectedResult": "返回登录成功的响应"
      },
      {
        "step": 2,
        "description": "验证用户信息是否正确",
        "expectedResult": "用户信息与预期一致"
      }
    ],
    "tags": ["登录", "功能测试", "更新"],
    "version": "1.1",
    "status": "active"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "测试用例更新成功",
    "data": {
      "caseId": 1,
      "caseCode": "TC_USER_LOGIN_001",
      "name": "更新后的用户登录测试用例",
      "apiId": 1,
      "status": "active",
      "version": "1.1"
    }
  }
  ```

#### 3.9.3 删除测试用例
- **接口地址**: `/test-cases/{caseId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **路径参数**:
  - `caseId`: 测试用例ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "测试用例删除成功",
    "data": null
  }
  ```

#### 3.9.4 分页查询测试用例列表
- **接口地址**: `/test-cases`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `apiId`: 接口ID（可选）
  - `moduleId`: 模块ID（可选）
  - `priority`: 优先级过滤（可选）
  - `severity`: 严重程度过滤（可选）
  - `testType`: 测试类型过滤（可选）
  - `status`: 状态过滤（可选）
  - `tags`: 标签过滤（可选，多个标签用逗号分隔）
  - `searchKeyword`: 关键字搜索（可选）
  - `sortBy`: 排序字段（可选）
  - `sortOrder`: 排序顺序（可选）
  - `page`: 页码（可选，默认1）
  - `pageSize`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询测试用例列表成功",
    "data": {
      "items": [
        {
          "caseId": 1,
          "caseCode": "TC_USER_LOGIN_001",
          "name": "更新后的用户登录测试用例",
          "apiId": 1,
          "priority": "P1",
          "severity": "high",
          "testType": "functional",
          "status": "active",
          "tags": ["登录", "功能测试", "更新"],
          "version": "1.1",
          "createdAt": "2025-01-01T10:00:00Z",
          "updatedAt": "2025-01-02T10:00:00Z"
        },
        {
          "caseId": 2,
          "caseCode": "TC_USER_GET_INFO_001",
          "name": "获取用户信息测试用例",
          "apiId": 2,
          "priority": "P1",
          "severity": "medium",
          "testType": "functional",
          "status": "active",
          "tags": ["用户信息", "功能测试"],
          "version": "1.0",
          "createdAt": "2025-01-01T11:00:00Z",
          "updatedAt": "2025-01-01T11:00:00Z"
        }
      ],
      "total": 2,
      "page": 1,
      "pageSize": 10,
      "totalPages": 1
    }
  }
  ```

#### 3.9.5 复制测试用例
- **接口地址**: `/test-cases/{caseId}/copy`
- **请求方法**: POST
- **认证要求**: 需要登录
- **路径参数**:
  - `caseId`: 测试用例ID（必填）
- **请求参数**:
  ```json
  {
    "apiId": 1,
    "caseCode": "TC_USER_LOGIN_002",
    "name": "用户登录测试用例（复制）",
    "suffix": "_COPY"
  }
  ```
- **请求参数说明**:
  - `apiId`: 目标接口ID（可选，不提供则使用原接口ID）
  - `caseCode`: 新用例编码（可选，不提供则自动生成）
  - `name`: 新用例名称（可选，不提供则在原名称后添加后缀）
  - `suffix`: 名称后缀（可选，默认"_COPY"）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "测试用例复制成功",
    "data": {
      "caseId": 3,
      "caseCode": "TC_USER_LOGIN_002",
      "name": "用户登录测试用例（复制）",
      "apiId": 1,
      "status": "active"
    }
  }
  ```

### 3.10 测试执行接口 (TestExecutionController)

#### 3.10.1 执行单个测试用例
- **接口地址**: `/test-cases/{case_id}/execute`
- **请求方法**: POST
- **认证要求**: 需要登录，需要`testcase:execute`权限
- **路径参数**:
  - `case_id`: 测试用例ID（必填）
- **请求参数**:
  ```json
  {
    "environment": "dev",
    "executionType": "manual",
    "baseUrl": "http://localhost:8080",
    "timeout": 30,
    "authOverride": {
      "token": "new_token"
    },
    "variables": {
      "username": "testuser",
      "password": "testpass"
    },
    "async": false
  }
  ```
- **请求参数说明**:
  - `environment`: 执行环境标识（可选）
  - `executionType`: 执行类型（manual：手动，scheduled：定时，triggered：触发，默认manual）
  - `baseUrl`: 覆盖接口的基础URL（可选）
  - `timeout`: 超时时间（秒，可选）
  - `authOverride`: 认证信息覆盖配置（可选）
  - `variables`: 执行变量，用于参数化测试（可选）
  - `async`: 是否异步执行（可选，默认false）
  - `callbackUrl`: 异步执行完成后的回调URL（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用例执行完成",
    "data": {
      "executionId": 1,
      "taskId": "task_1234567890",
      "caseId": 1,
      "status": "passed",
      "startTime": "2025-01-01T10:00:00Z",
      "endTime": "2025-01-01T10:00:05Z",
      "duration": 5000,
      "responseTime": 1500,
      "errorMessage": null,
      "stepResults": [
        {
          "stepIndex": 1,
          "stepName": "发送登录请求",
          "status": "passed",
          "actualResult": "HTTP/1.1 200 OK",
          "expectedResult": "HTTP/1.1 200 OK",
          "errorMessage": null,
          "duration": 1000
        }
      ]
    }
  }
  ```

#### 3.10.2 异步执行测试用例
- **接口地址**: `/test-cases/{case_id}/execute-async`
- **请求方法**: POST
- **认证要求**: 需要登录，需要`testcase:execute`权限
- **路径参数**:
  - `case_id`: 测试用例ID（必填）
- **请求参数**:
  ```json
  {
    "environment": "dev",
    "executionType": "manual",
    "baseUrl": "http://localhost:8080",
    "timeout": 30,
    "variables": {
      "username": "testuser",
      "password": "testpass"
    },
    "callbackUrl": "http://example.com/callback"
  }
  ```
- **请求参数说明**:
  - 同执行单个测试用例接口
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用例执行任务已提交",
    "data": {
      "executionId": 1,
      "taskId": "task_1234567890",
      "caseId": 1,
      "status": "pending",
      "startTime": "2025-01-01T10:00:00Z",
      "endTime": null,
      "duration": 0
    }
  }
  ```

#### 3.10.3 查询任务状态
- **接口地址**: `/tasks/{task_id}/status`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `task_id`: 任务ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "获取任务状态成功",
    "data": {
      "executionId": 1,
      "taskId": "task_1234567890",
      "caseId": 1,
      "status": "running",
      "startTime": "2025-01-01T10:00:00Z",
      "endTime": null,
      "duration": 2000
    }
  }
  ```

#### 3.10.4 取消任务执行
- **接口地址**: `/tasks/{task_id}/cancel`
- **请求方法**: POST
- **认证要求**: 需要登录，需要`testcase:execute`权限
- **路径参数**:
  - `task_id`: 任务ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "任务取消成功",
    "data": true
  }
  ```

#### 3.10.5 获取执行结果详情
- **接口地址**: `/test-results/{execution_id}`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `execution_id`: 执行记录ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "获取执行结果成功",
    "data": {
      "executionId": 1,
      "taskId": "task_1234567890",
      "caseId": 1,
      "status": "passed",
      "startTime": "2025-01-01T10:00:00Z",
      "endTime": "2025-01-01T10:00:05Z",
      "duration": 5000,
      "responseTime": 1500,
      "errorMessage": null,
      "stepResults": [
        {
          "stepIndex": 1,
          "stepName": "发送登录请求",
          "status": "passed",
          "actualResult": "HTTP/1.1 200 OK",
          "expectedResult": "HTTP/1.1 200 OK",
          "errorMessage": null,
          "duration": 1000
        },
        {
          "stepIndex": 2,
          "stepName": "验证响应状态码",
          "status": "passed",
          "actualResult": "200",
          "expectedResult": "200",
          "errorMessage": null,
          "duration": 500
        }
      ]
    }
  }
  ```

#### 3.10.6 获取执行日志
- **接口地址**: `/test-results/{execution_id}/logs`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `execution_id`: 执行记录ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "获取执行日志成功",
    "data": "[2025-01-01 10:00:00] INFO: 开始执行测试用例ID: 1\n[2025-01-01 10:00:01] INFO: 发送请求到: http://localhost:8080/api/login\n[2025-01-01 10:00:02] INFO: 收到响应: HTTP/1.1 200 OK\n[2025-01-01 10:00:05] INFO: 测试用例执行完成，状态: passed"
  }
  ```

#### 3.10.7 生成测试报告
- **接口地址**: `/test-results/{execution_id}/report`
- **请求方法**: POST
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `execution_id`: 执行记录ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "测试报告生成成功",
    "data": 123
  }
  ```

### 3.11 测试执行记录接口 (TestExecutionRecordController)

#### 3.11.1 分页查询测试执行记录
- **接口地址**: `/execution-records`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **请求参数**:
  - `execution_scope`: 执行范围（可选）
  - `ref_id`: 关联ID（可选）
  - `executed_by`: 执行人ID（可选）
  - `execution_type`: 执行类型（可选）
  - `environment`: 执行环境（可选）
  - `status`: 执行状态（可选）
  - `start_time_begin`: 开始时间（可选，ISO 8601格式）
  - `start_time_end`: 结束时间（可选，ISO 8601格式）
  - `search_keyword`: 搜索关键词（可选）
  - `browser`: 浏览器类型（可选）
  - `app_version`: 应用版本（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询执行记录成功",
    "data": {
      "items": [
        {
          "id": 1,
          "executionScope": "testcase",
          "refId": 1,
          "executedBy": 1,
          "executionType": "manual",
          "environment": "dev",
          "status": "completed",
          "startTime": "2025-01-01T10:00:00",
          "endTime": "2025-01-01T10:01:30",
          "durationSeconds": 90,
          "totalCases": 1,
          "executedCases": 1,
          "passedCases": 1,
          "failedCases": 0,
          "skippedCases": 0,
          "successRate": "100.00",
          "browser": null,
          "appVersion": null,
          "reportUrl": null,
          "logFilePath": null,
          "errorMessage": null
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.11.2 根据ID查询执行记录详情
- **接口地址**: `/execution-records/{recordId}`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `recordId`: 执行记录ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询执行记录详情成功",
    "data": {
      "id": 1,
      "executionScope": "testcase",
      "refId": 1,
      "executedBy": 1,
      "executionType": "manual",
      "environment": "dev",
      "status": "completed",
      "startTime": "2025-01-01T10:00:00",
      "endTime": "2025-01-01T10:01:30",
      "durationSeconds": 90,
      "totalCases": 1,
      "executedCases": 1,
      "passedCases": 1,
      "failedCases": 0,
      "skippedCases": 0,
      "successRate": "100.00",
      "browser": null,
      "appVersion": null,
      "reportUrl": null,
      "logFilePath": null,
      "errorMessage": null,
      "createdAt": "2025-01-01T10:00:00",
      "updatedAt": "2025-01-01T10:01:30"
    }
  }
  ```

#### 3.11.3 根据执行范围查询最近的执行记录
- **接口地址**: `/execution-records/scope/{executionScope}/{refId}`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `executionScope`: 执行范围（必填）
  - `refId`: 关联ID（必填）
- **请求参数**:
  - `limit`: 限制条数（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询最近执行记录成功",
    "data": [
      {
        "id": 1,
        "executionScope": "testcase",
        "refId": 1,
        "executedBy": 1,
        "executionType": "manual",
        "environment": "dev",
        "status": "completed",
        "startTime": "2025-01-01T10:00:00",
        "endTime": "2025-01-01T10:01:30",
        "durationSeconds": 90,
        "totalCases": 1,
        "executedCases": 1,
        "passedCases": 1,
        "failedCases": 0,
        "skippedCases": 0,
        "successRate": "100.00"
      }
    ]
  }
  ```

#### 3.11.4 更新执行记录
- **接口地址**: `/execution-records/{recordId}`
- **请求方法**: PUT
- **认证要求**: 需要登录，需要`testcase:execute`权限
- **路径参数**:
  - `recordId`: 执行记录ID（必填）
- **请求参数**:
  ```json
  {
    "status": "completed",
    "endTime": "2025-01-01T10:01:30",
    "durationSeconds": 90,
    "totalCases": 1,
    "executedCases": 1,
    "passedCases": 1,
    "failedCases": 0,
    "skippedCases": 0,
    "successRate": "100.00",
    "browser": "Chrome",
    "appVersion": "1.0.0",
    "reportUrl": "http://localhost:8080/reports/1",
    "logFilePath": "/logs/execution-1.log",
    "errorMessage": null
  }
  ```
- **请求参数说明**:
  - `status`: 执行状态（running, completed, failed, cancelled）
  - `endTime`: 结束时间
  - `durationSeconds`: 执行耗时（秒）
  - `totalCases`: 总用例数
  - `executedCases`: 已执行用例数
  - `passedCases`: 通过用例数
  - `failedCases`: 失败用例数
  - `skippedCases`: 跳过用例数
  - `successRate`: 成功率
  - `browser`: 浏览器类型
  - `appVersion`: 应用版本
  - `reportUrl`: 报告访问地址
  - `logFilePath`: 日志文件路径
  - `errorMessage`: 错误信息
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "更新执行记录成功",
    "data": {
      "id": 1,
      "executionScope": "testcase",
      "refId": 1,
      "executedBy": 1,
      "executionType": "manual",
      "environment": "dev",
      "status": "completed",
      "startTime": "2025-01-01T10:00:00",
      "endTime": "2025-01-01T10:01:30",
      "durationSeconds": 90,
      "totalCases": 1,
      "executedCases": 1,
      "passedCases": 1,
      "failedCases": 0,
      "skippedCases": 0,
      "successRate": "100.00",
      "browser": "Chrome",
      "appVersion": "1.0.0",
      "reportUrl": "http://localhost:8080/reports/1",
      "logFilePath": "/logs/execution-1.log",
      "errorMessage": null
    }
  }
  ```

#### 3.11.5 删除执行记录（软删除）
- **接口地址**: `/execution-records/{recordId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录，需要`testcase:delete`权限
- **路径参数**:
  - `recordId`: 执行记录ID（必填）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "删除执行记录成功",
    "data": true
  }
  ```

#### 3.11.6 批量删除执行记录
- **接口地址**: `/execution-records/batch`
- **请求方法**: DELETE
- **认证要求**: 需要登录，需要`testcase:delete`权限
- **请求参数**:
  ```json
  [1, 2, 3]
  ```
- **请求参数说明**:
  - 数组格式的执行记录ID列表
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "批量删除执行记录成功，共删除3条记录",
    "data": 3
  }
  ```

#### 3.11.7 获取执行记录统计信息
- **接口地址**: `/execution-records/statistics`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **请求参数**:
  - `execution_scope`: 执行范围（可选）
  - `ref_id`: 关联ID（可选）
  - `executed_by`: 执行人ID（可选）
  - `environment`: 执行环境（可选）
  - `status`: 执行状态（可选）
  - `start_time_begin`: 开始时间（可选，ISO 8601格式）
  - `start_time_end`: 结束时间（可选，ISO 8601格式）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "获取统计信息成功",
    "data": {
      "totalExecutions": 100,
      "passedExecutions": 85,
      "failedExecutions": 10,
      "cancelledExecutions": 5,
      "averageDuration": 30,
      "successRate": "85.00",
      "executionsByStatus": [
        {"status": "completed", "count": 85},
        {"status": "failed", "count": 10},
        {"status": "cancelled", "count": 5}
      ],
      "executionsByEnvironment": [
        {"environment": "dev", "count": 50},
        {"environment": "test", "count": 30},
        {"environment": "prod", "count": 20}
      ]
    }
  }
  ```

#### 3.11.8 根据执行人查询执行记录
- **接口地址**: `/execution-records/executor/{executedBy}`
- **请求方法**: GET
- **认证要求**: 需要登录，需要`testcase:view`权限
- **路径参数**:
  - `executedBy`: 执行人ID（必填）
- **请求参数**:
  - `limit`: 限制条数（可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询执行人的执行记录成功",
    "data": [
      {
        "id": 1,
        "executionScope": "testcase",
        "refId": 1,
        "executedBy": 1,
        "executionType": "manual",
        "environment": "dev",
        "status": "completed",
        "startTime": "2025-01-01T10:00:00",
        "endTime": "2025-01-01T10:01:30",
        "durationSeconds": 90,
        "totalCases": 1,
        "executedCases": 1,
        "passedCases": 1,
        "failedCases": 0,
        "skippedCases": 0,
        "successRate": "100.00"
      }
    ]
  }
  ```

### 3.12 报告管理接口 (ReportController)

### 3.13 系统健康检查接口 (HealthCheckController)

### 3.14 测试接口 (TestController)