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
- **请求参数**:
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
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
    "message": "获取成功",
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
- **请求参数**:
  ```json
  {
    "email": "user@example.com",
    "type": "email"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "重置验证码已发送，请查收",
    "data": {
      "reset_token_id": "1234567890"
    }
  }
  ```

#### 3.1.4 执行密码重置
- **接口地址**: `/auth/password/reset`
- **请求方法**: POST
- **请求参数**:
  ```json
  {
    "email": "user@example.com",
    "new_password": "new_password123",
    "code": "123456",
    "reset_token_id": "1234567890"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "密码重置成功",
    "data": null
  }
  ```

### 3.2 用户管理接口 (UserController)

#### 3.2.1 获取用户列表
- **接口地址**: `/users`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `name`: 用户姓名模糊查询（可选）
  - `email`: 用户邮箱模糊查询（可选）
  - `status`: 状态筛选（active/pending/inactive，可选）
  - `position`: 角色/职位筛选（可选）
  - `start_date`: 创建时间开始日期（可选）
  - `end_date`: 创建时间结束日期（可选）
  - `page`: 页码（默认1，可选）
  - `page_size`: 每页记录数（默认10，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "id": 1,
          "name": "admin",
          "email": "admin@example.com",
          "phone": "13800138000",
          "avatar_url": "",
          "department_id": 1,
          "employee_id": "EMP001",
          "position": "admin",
          "description": "系统管理员",
          "status": "active",
          "created_at": "2025-12-01 10:00:00",
          "updated_at": "2025-12-01 10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.2.2 搜索用户
- **接口地址**: `/users/search`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `keyword`: 搜索关键词（用户名或邮箱模糊查询，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": [
      {
        "id": 1,
        "name": "admin",
        "email": "admin@example.com",
        "status": "active"
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
    "name": "testuser",
    "email": "testuser@example.com",
    "password": "password123",
    "phone": "13800138000",
    "avatar_url": "",
    "department_id": 1,
    "employee_id": "EMP002",
    "position": "tester",
    "description": "测试用户",
    "status": "active"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "用户创建成功",
    "data": 2
  }
  ```

#### 3.2.4 更新用户信息
- **接口地址**: `/users/{userId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "name": "updateduser",
    "email": "updateduser@example.com",
    "phone": "13900139000",
    "avatar_url": "",
    "department_id": 1,
    "employee_id": "EMP002",
    "position": "developer",
    "description": "更新后的测试用户",
    "role_ids": [2, 3]
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
    "project_role": "tester",
    "permission_level": "write",
    "additional_roles": "{}",
    "custom_permissions": "{}",
    "notes": "测试权限分配"
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

#### 3.2.9 获取用户项目列表
- **接口地址**: `/users/{userId}/projects`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `status`: 状态筛选（active/inactive/removed，可选）
  - `project_role`: 项目角色筛选（可选）
  - `page`: 页码（默认1，可选）
  - `page_size`: 每页记录数（默认10，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "project_id": 1,
          "project_name": "测试项目",
          "project_role": "tester",
          "permission_level": "write",
          "status": "active",
          "created_at": "2025-12-01 10:00:00"
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
    "project_role": "developer",
    "permission_level": "write",
    "status": "active",
    "additional_roles": "{}",
    "custom_permissions": "{}",
    "notes": "更新权限信息"
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

### 3.6 角色管理接口 (RoleController)

#### 3.6.1 获取角色列表
- **接口地址**: `/roles`
- **请求方法**: GET
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  - `role_name`: 角色名称模糊查询（可选）
  - `is_super_admin`: 是否超级管理员（可选）
  - `include_deleted`: 是否包含已删除角色（可选）
  - `page`: 页码（默认1，可选）
  - `page_size`: 每页记录数（默认10，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "id": 1,
          "role_name": "admin",
          "description": "系统管理员",
          "is_super_admin": true,
          "created_at": "2025-12-01 10:00:00",
          "updated_at": "2025-12-01 10:00:00"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.6.2 创建角色
- **接口地址**: `/roles`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "role_name": "tester",
    "description": "测试人员角色",
    "is_super_admin": false
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "角色创建成功",
    "data": {
      "id": 2,
      "role_name": "tester",
      "description": "测试人员角色",
      "is_super_admin": false,
      "created_at": "2025-12-01 11:00:00",
      "updated_at": "2025-12-01 11:00:00"
    }
  }
  ```

#### 3.6.3 更新角色信息
- **接口地址**: `/roles/{roleId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "role_name": "senior_tester",
    "description": "高级测试人员角色",
    "is_super_admin": false
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "角色信息更新成功",
    "data": {
      "id": 2,
      "role_name": "senior_tester",
      "description": "高级测试人员角色",
      "is_super_admin": false,
      "created_at": "2025-12-01 11:00:00",
      "updated_at": "2025-12-01 12:00:00"
    }
  }
  ```

#### 3.6.4 删除角色
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

#### 3.6.5 为角色分配权限
- **接口地址**: `/roles/{roleId}/permissions`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "permission_ids": [1, 2, 3]
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "权限分配成功",
    "data": null
  }
  ```

### 3.7 权限管理接口 (PermissionController)

#### 3.7.1 获取权限列表
- **接口地址**: `/permissions`
- **请求方法**: GET
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  - `permission_name`: 权限名称模糊查询（可选）
  - `role_id`: 角色ID（可选，当提供时会标记is_assigned字段）
  - `include_deleted`: 是否包含已删除权限（可选）
  - `page`: 页码（默认1，可选）
  - `page_size`: 每页记录数（默认10，可选）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "items": [
        {
          "id": 1,
          "permission_name": "user:create",
          "description": "创建用户权限",
          "module": "用户管理",
          "created_at": "2025-12-01 10:00:00",
          "updated_at": "2025-12-01 10:00:00",
          "is_assigned": true
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

### 3.3 API管理接口 (ApiController)

#### 3.3.1 创建API
- **接口地址**: `/apis`
- **请求方法**: POST
- **认证要求**: 需要登录和`api:create`权限
- **请求参数**:
  ```json
  {
    "name": "测试API",
    "url": "http://api.example.com/test",
    "method": "GET",
    "project_id": 1,
    "environment": "test",
    "headers": {},
    "params": {},
    "body": {}
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "创建接口成功",
    "data": {
      "id": 1,
      "name": "测试API",
      "url": "http://api.example.com/test",
      "method": "GET",
      "project_id": 1,
      "environment": "test",
      "headers": {},
      "params": {},
      "body": {},
      "created_by": 1,
      "created_at": "2024-01-01 10:00:00"
    }
  }
  ```

#### 3.3.2 获取API列表
- **接口地址**: `/apis`
- **请求方法**: GET
- **认证要求**: 需要登录和`api:view`权限
- **请求参数**:
  - `project_id`: 项目ID
  - `page`: 页码
  - `page_size`: 每页条数
  - `name`: 接口名称（模糊查询）
  - `method`: 请求方法（GET, POST, PUT, DELETE）
  - `status`: 接口状态（active, inactive）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "list": [
        {
          "id": 1,
          "name": "测试API",
          "url": "http://api.example.com/test",
          "method": "GET",
          "project_id": 1,
          "environment": "test",
          "status": "active"
        }
      ],
      "total": 1,
      "page": 1,
      "pageSize": 10
    }
  }
  ```

#### 3.3.3 获取API详情
- **接口地址**: `/apis/{apiId}`
- **请求方法**: GET
- **认证要求**: 需要登录和`api:view`权限
- **请求参数**: 无（通过路径参数传递apiId）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "查询成功",
    "data": {
      "id": 1,
      "name": "测试API",
      "url": "http://api.example.com/test",
      "method": "GET",
      "project_id": 1,
      "environment": "test",
      "headers": {},
      "params": {},
      "body": {},
      "status": "active",
      "created_by": 1,
      "created_at": "2024-01-01 10:00:00",
      "updated_at": "2024-01-01 10:00:00"
    }
  }
  ```

#### 3.3.4 更新API
- **接口地址**: `/apis/{apiId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和`api:update`权限
- **请求参数**:
  ```json
  {
    "name": "更新后的API",
    "url": "http://api.example.com/updated",
    "method": "POST",
    "headers": {},
    "params": {},
    "body": {},
    "status": "active"
  }
  ```
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "更新接口成功",
    "data": {
      "id": 1,
      "name": "更新后的API",
      "url": "http://api.example.com/updated",
      "method": "POST",
      "project_id": 1,
      "environment": "test",
      "headers": {},
      "params": {},
      "body": {},
      "status": "active",
      "updated_at": "2024-01-01 11:00:00"
    }
  }
  ```

#### 3.3.5 删除API
- **接口地址**: `/apis/{apiId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和`api:delete`权限
- **请求参数**: 无（通过路径参数传递apiId）
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "接口删除成功",
    "data": null
  }
  ```

### 3.4 测试用例管理接口 (TestCaseController)

#### 3.4.1 创建测试用例
- **接口地址**: `/testcases`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "测试用例1",
    "description": "测试用例描述",
    "api_id": 1,
    "module_id": 1,
    "project_id": 1,
    "status": "active",
    "steps": [
      {
        "name": "步骤1",
        "type": "request",
        "data": {
          "url": "http://api.example.com/test",
          "method": "GET",
          "headers": {},
          "params": {}
        }
      }
    ]
  }
  ```

#### 3.4.2 修改测试用例
- **接口地址**: `/testcases/{caseId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "更新后的测试用例",
    "description": "更新后的测试用例描述",
    "api_id": 1,
    "module_id": 1,
    "project_id": 1,
    "status": "active",
    "steps": [
      {
        "name": "步骤1",
        "type": "request",
        "data": {
          "url": "http://api.example.com/test",
          "method": "GET",
          "headers": {},
          "params": {}
        }
      }
    ]
  }
  ```

#### 3.4.3 删除测试用例
- **接口地址**: `/testcases/{caseId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "测试用例删除成功",
    "data": null
  }
  ```

#### 3.4.4 获取测试用例列表
- **接口地址**: `/testcases`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `project_id`: 项目ID
  - `module_id`: 模块ID
  - `api_id`: 接口ID
  - `name`: 测试用例名称（模糊查询）
  - `status`: 状态
  - `page`: 页码
  - `pageSize`: 每页条数
  - `sort_by`: 排序字段
  - `sort_order`: 排序顺序

#### 3.4.5 复制测试用例
- **接口地址**: `/testcases/{caseId}/copy`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "复制的测试用例",
    "description": "复制的测试用例描述",
    "module_id": 1,
    "project_id": 1,
    "status": "active"
  }
  ```

### 3.5 测试执行接口 (TestExecutionController)

#### 3.5.1 执行测试用例（同步）
- **接口地址**: `/test-cases/{case_id}/execute`
- **请求方法**: POST
- **认证要求**: 需要登录和`testcase:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "skip_teardown": false
  }
  ```

#### 3.5.2 异步执行测试用例
- **接口地址**: `/test-cases/{case_id}/execute-async`
- **请求方法**: POST
- **认证要求**: 需要登录和`testcase:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "skip_teardown": false
  }
  ```

#### 3.5.3 查询任务状态
- **接口地址**: `/tasks/{task_id}/status`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限

#### 3.5.4 取消任务执行
- **接口地址**: `/tasks/{task_id}/cancel`
- **请求方法**: POST
- **认证要求**: 需要登录和`testcase:execute`权限

#### 3.5.5 获取执行结果详情
- **接口地址**: `/test-results/{execution_id}`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限

#### 3.5.6 获取执行日志
- **接口地址**: `/test-results/{execution_id}/logs`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限

#### 3.5.7 生成测试报告
- **接口地址**: `/test-results/{execution_id}/report`
- **请求方法**: POST
- **认证要求**: 需要登录和`testcase:view`权限

#### 3.5.8 执行模块测试（同步）
- **接口地址**: `/modules/{module_id}/execute`
- **请求方法**: POST
- **认证要求**: 需要登录和`module:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "sort_by": "priority",
    "sort_order": "desc"
  }
  ```

#### 3.5.9 异步执行模块测试
- **接口地址**: `/modules/{module_id}/execute-async`
- **请求方法**: POST
- **认证要求**: 需要登录和`module:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "sort_by": "priority",
    "sort_order": "desc"
  }
  ```

#### 3.5.10 查询模块任务状态
- **接口地址**: `/module-tasks/{task_id}/status`
- **请求方法**: GET
- **认证要求**: 需要登录和`module:view`权限

#### 3.5.11 取消模块任务执行
- **接口地址**: `/module-tasks/{task_id}/cancel`
- **请求方法**: POST
- **认证要求**: 需要登录和`module:execute`权限

#### 3.5.12 执行项目测试（同步）
- **接口地址**: `/projects/{project_id}/execute`
- **请求方法**: POST
- **认证要求**: 需要登录和`project:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "sort_by": "priority",
    "sort_order": "desc",
    "include_modules": [1, 2],
    "exclude_cases": [3, 4]
  }
  ```

#### 3.5.13 异步执行项目测试
- **接口地址**: `/projects/{project_id}/execute-async`
- **请求方法**: POST
- **认证要求**: 需要登录和`project:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "sort_by": "priority",
    "sort_order": "desc",
    "include_modules": [1, 2],
    "exclude_cases": [3, 4]
  }
  ```

#### 3.5.14 查询项目任务状态
- **接口地址**: `/project-tasks/{task_id}/status`
- **请求方法**: GET
- **认证要求**: 需要登录和`project:view`权限

#### 3.5.15 取消项目任务执行
- **接口地址**: `/project-tasks/{task_id}/cancel`
- **请求方法**: POST
- **认证要求**: 需要登录和`project:execute`权限

#### 3.5.16 执行接口测试（同步）
- **接口地址**: `/apis/{api_id}/execute`
- **请求方法**: POST
- **认证要求**: 需要登录和`api:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "sort_by": "priority",
    "sort_order": "desc"
  }
  ```

#### 3.5.17 异步执行接口测试
- **接口地址**: `/apis/{api_id}/execute-async`
- **请求方法**: POST
- **认证要求**: 需要登录和`api:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "sort_by": "priority",
    "sort_order": "desc"
  }
  ```

#### 3.5.18 查询接口任务状态
- **接口地址**: `/api-tasks/{task_id}/status`
- **请求方法**: GET
- **认证要求**: 需要登录和`api:view`权限

#### 3.5.19 取消接口任务执行
- **接口地址**: `/api-tasks/{task_id}/cancel`
- **请求方法**: POST
- **认证要求**: 需要登录和`api:execute`权限

#### 3.5.20 执行测试套件（同步）
- **接口地址**: `/test-suites/{suite_id}/execute`
- **请求方法**: POST
- **认证要求**: 需要登录和`suite:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "skip_dependent_cases": false
  }
  ```

#### 3.5.21 异步执行测试套件
- **接口地址**: `/test-suites/{suite_id}/execute-async`
- **请求方法**: POST
- **认证要求**: 需要登录和`suite:execute`权限
- **请求参数**:
  ```json
  {
    "environment": "test",
    "variables": {},
    "concurrent_count": 5,
    "skip_dependent_cases": false
  }
  ```

#### 3.5.22 查询测试套件任务状态
- **接口地址**: `/suite-tasks/{task_id}/status`
- **请求方法**: GET
- **认证要求**: 需要登录和`suite:view`权限

#### 3.5.23 取消测试套件任务执行
- **接口地址**: `/suite-tasks/{task_id}/cancel`
- **请求方法**: POST
- **认证要求**: 需要登录和`suite:execute`权限

#### 3.5.24 获取个人测试概况
- **接口地址**: `/dashboard/summary`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限
- **请求参数**:
  - `time_range`: 时间范围（默认：7d）
  - `include_recent_activity`: 是否包含最近活动
  - `include_pending_tasks`: 是否包含待处理任务
  - `include_quick_actions`: 是否包含快捷操作

#### 3.5.25 获取近七天测试执行情况
- **接口地址**: `/weekly-execution`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限
- **请求参数**:
  - `project_id`: 项目ID（可选）
  - `module_id`: 模块ID（可选）
  - `environment`: 环境（可选）
  - `include_daily_trend`: 是否包含每日趋势
  - `include_top_failures`: 是否包含失败最多的用例
  - `include_performance`: 是否包含性能数据

### 3.10 测试执行记录接口 (TestExecutionRecordController)

#### 3.10.1 分页查询测试执行记录
- **接口地址**: `/execution-records`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限
- **请求参数**:
  - `execution_scope`: 执行范围（可选）
  - `ref_id`: 关联ID（可选）
  - `executed_by`: 执行人ID（可选）
  - `execution_type`: 执行类型（可选）
  - `environment`: 环境（可选）
  - `status`: 状态（可选）
  - `start_time_begin`: 开始时间（可选）
  - `start_time_end`: 结束时间（可选）
  - `search_keyword`: 搜索关键词（可选）
  - `browser`: 浏览器（可选）
  - `app_version`: 应用版本（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序方式（可选）
  - `page`: 页码（可选）
  - `page_size`: 每页条数（可选）

#### 3.10.2 根据ID查询执行记录详情
- **接口地址**: `/execution-records/{recordId}`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限

#### 3.10.3 根据执行范围查询最近的执行记录
- **接口地址**: `/execution-records/scope/{executionScope}/{refId}`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限
- **请求参数**:
  - `limit`: 限制条数（可选）

#### 3.10.4 更新执行记录
- **接口地址**: `/execution-records/{recordId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和`testcase:execute`权限
- **请求参数**:
  ```json
  {
    "status": "passed",
    "comment": "测试通过",
    "tags": ["自动化", "回归测试"]
  }
  ```

#### 3.10.5 删除执行记录（软删除）
- **接口地址**: `/execution-records/{recordId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和`testcase:delete`权限

#### 3.10.6 批量删除执行记录
- **接口地址**: `/execution-records/batch`
- **请求方法**: DELETE
- **认证要求**: 需要登录和`testcase:delete`权限
- **请求参数**:
  ```json
  [1, 2, 3]
  ```

#### 3.10.7 获取执行记录统计信息
- **接口地址**: `/execution-records/statistics`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限
- **请求参数**:
  - `execution_scope`: 执行范围（可选）
  - `ref_id`: 关联ID（可选）
  - `executed_by`: 执行人ID（可选）
  - `environment`: 环境（可选）
  - `status`: 状态（可选）
  - `start_time_begin`: 开始时间（可选）
  - `start_time_end`: 结束时间（可选）

#### 3.10.8 根据执行人查询执行记录
- **接口地址**: `/execution-records/executor/{executedBy}`
- **请求方法**: GET
- **认证要求**: 需要登录和`testcase:view`权限
- **请求参数**:
  - `limit`: 限制条数（可选）

### 3.5 项目管理接口 (ProjectController)

#### 3.5.1 分页获取项目列表
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

#### 3.5.2 根据ID获取项目详情
- **接口地址**: `/projects/{projectId}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.5.3 根据项目编码获取项目详情
- **接口地址**: `/projects/code/{projectCode}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.5.4 获取项目统计数据
- **接口地址**: `/projects/{projectId}/statistics`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.5.5 编辑项目信息（简化版）
- **接口地址**: `/projects/{projectId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "项目名称",
    "description": "项目描述",
    "status": "active"
  }
  ```

#### 3.5.6 更新项目（完整版）
- **接口地址**: `/projects/{projectId}/full`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "projectId": 1,
    "name": "项目名称",
    "code": "PROJ001",
    "description": "项目描述",
    "status": "active",
    "createdBy": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedBy": 1,
    "updatedAt": "2024-01-01T00:00:00"
  }
  ```

#### 3.5.7 检查项目关联数据
- **接口地址**: `/projects/{projectId}/relations`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.5.8 分页获取最近编辑的项目
- **接口地址**: `/projects/recent-projects`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `limit`: 限制条数（可选）
  - `time_range`: 时间范围（可选）

#### 3.5.9 获取模块列表
- **接口地址**: `/projects/{projectId}/modules`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `structure`: 返回结构（tree/flat，可选）
  - `status`: 模块状态过滤（可选）
  - `include_deleted`: 是否包含已删除的模块（可选）
  - `include_statistics`: 是否包含统计信息（可选）
  - `search_keyword`: 关键字搜索（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）

#### 3.5.10 分页获取项目成员列表
- **接口地址**: `/projects/{projectId}/members`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `status`: 成员状态过滤（可选）
  - `permission_level`: 权限级别过滤（可选）
  - `project_role`: 项目角色过滤（可选）
  - `search_keyword`: 关键字搜索（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
  - `page`: 页码（可选）
  - `page_size`: 每页条数（可选）

#### 3.5.11 更新项目成员角色/权限
- **接口地址**: `/projects/{projectId}/members/{userId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "projectRole": "admin",
    "permissionLevel": "full"
  }
  ```

### 3.11 报告管理接口 (ReportController)

#### 3.11.1 获取报告列表
- **接口地址**: `/reports`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `project_id`: 项目ID
  - `start_time`: 开始时间
  - `end_time`: 结束时间
  - `page`: 页码
  - `pageSize`: 每页条数

#### 3.11.2 获取报告详情
- **接口地址**: `/reports/{reportId}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.11.3 导出报告
- **接口地址**: `/reports/{reportId}/export`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `export_format`: 导出格式
  - `include_details`: 是否包含详细的用例执行结果（可选）
  - `include_attachments`: 是否包含附件信息（链接）（可选）
  - `include_failure_details`: 是否包含失败详情（可选）
  - `timezone`: 时区设置（可选）

#### 3.11.4 根据项目ID查询报告列表
- **接口地址**: `/reports/project/{projectId}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.11.5 根据执行ID查询报告
- **接口地址**: `/reports/execution/{executionId}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.11.6 创建报告
- **接口地址**: `/reports`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "projectId": 1,
    "reportName": "测试报告",
    "reportType": "test_case_execution",
    "environment": "test",
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-01-01T01:00:00",
    "totalTestCases": 100,
    "passedTestCases": 80,
    "failedTestCases": 15,
    "skippedTestCases": 5,
    "successRate": 80.00,
    "status": "completed",
    "summary": "测试报告摘要",
    "createdBy": 1
  }
  ```

#### 3.11.7 更新报告
- **接口地址**: `/reports/{reportId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "reportName": "更新后的测试报告",
    "reportType": "test_case_execution",
    "environment": "test",
    "summary": "更新后的测试报告摘要",
    "status": "completed"
  }
  ```

#### 3.11.8 批量删除报告
- **接口地址**: `/reports/batch`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  [1, 2, 3]
  ```

#### 3.7.9 根据项目ID删除报告
- **接口地址**: `/reports/project/{projectId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录

#### 3.7.10 更新报告状态
- **接口地址**: `/reports/{reportId}/status`
- **请求方法**: PATCH
- **认证要求**: 需要登录
- **请求参数**:
  - `report_status`: 报告状态

#### 3.7.11 更新报告文件信息
- **接口地址**: `/reports/{reportId}/file`
- **请求方法**: PATCH
- **认证要求**: 需要登录
- **请求参数**:
  - `file_path`: 文件路径
  - `file_size`: 文件大小
  - `download_url`: 下载地址

#### 3.7.12 导出企业级测试报告
- **接口地址**: `/reports/{reportId}/export/enterprise`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `locale`: 语言环境（zh_CN/en_US，可选，默认zh_CN）

#### 3.7.13 导出ISO标准企业级测试报告
- **接口地址**: `/reports/{reportId}/export/iso`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `locale`: 语言环境（zh_CN/en_US，可选，默认zh_CN）

#### 3.7.14 导出Allure风格测试报告
- **接口地址**: `/reports/{reportId}/export/allure`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `locale`: 语言环境（zh_CN/en_US，可选，默认zh_CN）

#### 3.7.15 删除测试报告
- **接口地址**: `/reports/{reportId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **请求参数**:
  - `force`: 是否强制删除（物理删除）（可选）

### 3.8 模块管理接口 (ModuleController)

#### 3.8.1 添加模块
- **接口地址**: `/modules`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "模块名称",
    "description": "模块描述",
    "projectId": 1,
    "parentId": null,
    "sortOrder": 1
  }
  ```

#### 3.8.2 分页获取模块列表
- **接口地址**: `/modules/page`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `projectId`: 项目ID（可选）
  - `parentId`: 父模块ID（可选）
  - `name`: 模块名称（模糊查询，可选）
  - `status`: 模块状态（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）

#### 3.8.3 获取模块树形结构
- **接口地址**: `/modules/tree`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `projectId`: 项目ID（可选）
  - `parentId`: 父模块ID（可选）
  - `includeStatistics`: 是否包含统计信息（可选）

#### 3.8.4 获取模块层级路径
- **接口地址**: `/modules/{moduleId}/path`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.8.5 获取模块统计信息
- **接口地址**: `/modules/{moduleId}/statistics`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.8.6 获取模块接口列表
- **接口地址**: `/modules/{moduleId}/apis`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `method`: 请求方法过滤（可选）
  - `status`: 接口状态过滤（可选）
  - `tags`: 标签过滤（可选）
  - `auth_type`: 认证类型过滤（可选）
  - `search_keyword`: 关键字搜索（可选）
  - `include_deleted`: 是否包含已删除的接口（可选）
  - `include_statistics`: 是否包含统计信息（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
  - `page`: 页码（可选）
  - `page_size`: 每页条数（可选）

#### 3.8.7 根据ID获取模块详情
- **接口地址**: `/modules/{moduleId}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.8.8 更新模块
- **接口地址**: `/modules/{moduleId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "模块名称",
    "description": "模块描述",
    "parentId": null,
    "sortOrder": 1,
    "status": "active"
  }
  ```

#### 3.8.8 删除模块
- **接口地址**: `/modules/{moduleId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录

#### 3.8.9 批量删除模块
- **接口地址**: `/modules/batch`
- **请求方法**: DELETE
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  [1, 2, 3]
  ```

#### 3.8.10 获取所有顶级模块
- **接口地址**: `/modules/top`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `projectId`: 项目ID（可选）

### 3.9 用户管理接口 (UserController)

#### 3.9.1 分页查询用户列表
- **接口地址**: `/users`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `page`: 页码（可选，默认1）
  - `pageSize`: 每页条数（可选，默认10）
  - `name`: 用户名（模糊查询，可选）
  - `email`: 邮箱（模糊查询，可选）
  - `status`: 用户状态（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）

#### 3.9.2 根据用户名或邮箱模糊查询用户
- **接口地址**: `/users/search`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `keyword`: 搜索关键词（可选，同时匹配用户名和邮箱）

#### 3.9.3 创建用户
- **接口地址**: `/users`
- **请求方法**: POST
- **认证要求**: 需要管理员权限
- **请求参数**:
  ```json
  {
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role_ids": [1, 2]
  }
  ```

#### 3.9.4 更新用户信息
- **接口地址**: `/users/{userId}`
- **请求方法**: PUT
- **认证要求**: 需要管理员权限
- **请求参数**:
  ```json
  {
    "username": "updateduser",
    "email": "updated@example.com",
    "password": "newpassword123",
    "role_ids": [1]
  }
  ```

#### 3.9.5 更新用户状态
- **接口地址**: `/users/{userId}/status`
- **请求方法**: PUT
- **认证要求**: 需要管理员权限
- **请求参数**:
  ```json
  {
    "status": "active"
  }
  ```

#### 3.9.6 删除用户
- **接口地址**: `/users/{userId}`
- **请求方法**: DELETE
- **认证要求**: 需要管理员权限

#### 3.9.7 为用户分配项目
- **接口地址**: `/users/{userId}/projects`
- **请求方法**: POST
- **认证要求**: 需要管理员权限
- **请求参数**:
  ```json
  {
    "project_id": 1,
    "project_role": "member"
  }
  ```

#### 3.9.8 移除用户项目分配
- **接口地址**: `/users/{userId}/projects/{projectId}`
- **请求方法**: DELETE
- **认证要求**: 需要管理员权限

#### 3.9.9 分页获取用户项目列表
- **接口地址**: `/users/{userId}/projects`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `status`: 项目状态（可选）
  - `project_role`: 项目角色（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）

#### 3.9.10 更新用户项目成员信息
- **接口地址**: `/users/{userId}/projects/{projectId}`
- **请求方法**: PUT
- **认证要求**: 需要管理员权限
- **请求参数**:
  ```json
  {
    "project_role": "admin",
    "permission_level": "full"
  }
  ```

### 3.10 角色管理接口 (RoleController)

#### 3.10.1 获取角色列表
- **接口地址**: `/roles`
- **请求方法**: GET
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
  - `name`: 角色名称（模糊查询，可选）
  - `status`: 角色状态（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）

#### 3.10.2 创建角色
- **接口地址**: `/roles`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "name": "测试角色",
    "description": "角色描述",
    "permission_ids": [1, 2, 3]
  }
  ```

#### 3.10.3 更新角色
- **接口地址**: `/roles/{roleId}`
- **请求方法**: PUT
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "name": "更新后的角色",
    "description": "更新后的角色描述",
    "permission_ids": [1, 2, 3, 4]
  }
  ```

#### 3.10.4 删除角色
- **接口地址**: `/roles/{roleId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录和管理员权限

#### 3.10.5 分配角色权限
- **接口地址**: `/roles/{roleId}/permissions`
- **请求方法**: POST
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  ```json
  {
    "permission_ids": [1, 2, 3, 4]
  }
  ```

### 3.11 权限管理接口 (PermissionController)

#### 3.11.1 分页获取权限列表
- **接口地址**: `/permissions`
- **请求方法**: GET
- **认证要求**: 需要登录和管理员权限
- **请求参数**:
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）
  - `name`: 权限名称（模糊查询，可选）
  - `code`: 权限代码（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）

### 3.12 环境配置接口 (EnvironmentConfigController)

#### 3.12.1 创建环境配置
- **接口地址**: `/api/environments`
- **请求方法**: POST
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "测试环境",
    "key": "test",
    "base_url": "http://test-api.example.com",
    "variables": {
      "token": "test-token"
    },
    "project_id": 1,
    "env_type": "test",
    "status": "active",
    "is_default": false
  }
  ```

#### 3.12.2 获取环境配置列表
- **接口地址**: `/api/environments`
- **请求方法**: GET
- **认证要求**: 需要登录
- **请求参数**:
  - `env_type`: 环境类型（可选）
  - `status`: 状态（可选）
  - `search_keyword`: 搜索关键词（可选）
  - `is_default`: 是否默认环境（可选）
  - `sort_by`: 排序字段（可选）
  - `sort_order`: 排序顺序（可选）
  - `page`: 页码（可选，默认1）
  - `page_size`: 每页条数（可选，默认10）

#### 3.12.3 获取环境配置详情
- **接口地址**: `/api/environments/{envId}`
- **请求方法**: GET
- **认证要求**: 需要登录

#### 3.12.4 更新环境配置
- **接口地址**: `/api/environments/{envId}`
- **请求方法**: PUT
- **认证要求**: 需要登录
- **请求参数**:
  ```json
  {
    "name": "更新后的测试环境",
    "key": "test",
    "base_url": "http://updated-test-api.example.com",
    "variables": {
      "token": "new-test-token"
    },
    "env_type": "test",
    "status": "active",
    "is_default": false
  }
  ```

#### 3.12.5 删除环境配置
- **接口地址**: `/api/environments/{envId}`
- **请求方法**: DELETE
- **认证要求**: 需要登录

### 3.13 系统健康检查接口 (HealthCheckController)

#### 3.13.1 简单健康检查
- **接口地址**: `/health`
- **请求方法**: GET
- **认证要求**: 无
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "OK",
    "data": "System is running"
  }
  ```

#### 3.13.2 详细健康检查
- **接口地址**: `/health/detail`
- **请求方法**: GET
- **认证要求**: 无
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "Health check completed",
    "data": {
      "application": "running",
      "timestamp": 1734808800000,
      "database": "connected",
      "databaseUrl": "jdbc:mysql://localhost:3306/iatms"
    }
  }
  ```

#### 3.13.3 测试JSON响应
- **接口地址**: `/test/json`
- **请求方法**: GET
- **认证要求**: 无
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "success",
    "data": {
      "message": "JSON serialization works",
      "timestamp": "1734808800000"
    }
  }
  ```

### 3.14 测试接口 (TestController)

#### 3.14.1 简单测试接口
- **接口地址**: `/test`
- **请求方法**: GET
- **认证要求**: 无
- **响应示例**:
  ```json
  {
    "status": "success",
    "message": "",
    "data": null
  }
  ```

## 4. 错误处理

### 4.1 错误码分类

| 错误类型 | 描述 |
|---------|------|
| paramError | 参数错误 |
| authError | 认证错误 |
| businessError | 业务逻辑错误 |
| serverError | 服务器错误 |

### 4.2 错误示例

```json
{
  "status": "paramError",
  "message": "参数错误：用户名不能为空",
  "data": null
}
```

```json
{
  "status": "authError",
  "message": "认证失败：无效的Token",
  "data": null
}
```

## 5. 权限说明

系统采用基于角色的权限控制(RBAC)：

1. **管理员(Admin)**: 拥有所有权限
2. **项目负责人(ProjectManager)**: 管理项目下的所有资源
3. **测试人员(Tester)**: 执行测试、查看报告等
4. **普通用户(User)**: 基础权限

### 5.1 权限列表

| 权限标识 | 权限名称 | 描述 |
|---------|---------|------|
| api:create | API创建 | 创建新的API接口 |
| api:update | API更新 | 更新API接口信息 |
| api:delete | API删除 | 删除API接口 |
| api:view | API查看 | 查看API接口信息 |
| testcase:create | 测试用例创建 | 创建测试用例 |
| testcase:execute | 测试用例执行 | 执行测试用例 |
| project:manage | 项目管理 | 管理项目信息 |
| report:view | 报告查看 | 查看测试报告 |

## 6. 安全规范

### 6.1 数据安全
- 密码采用加盐哈希存储
- 敏感数据传输采用HTTPS
- 定期备份数据

### 6.2 访问控制
- 基于角色的权限控制
- 接口级别的权限验证
- 操作日志记录

## 7. 最佳实践

### 7.1 请求建议
- 使用POST请求传递复杂参数
- 合理设置请求超时时间
- 避免在URL中传递敏感信息

### 7.2 错误处理
- 正确处理所有返回状态码
- 记录详细的错误日志
- 给用户友好的错误提示

### 7.3 性能优化
- 合理使用缓存
- 减少不必要的请求
- 优化数据库查询

## 8. 变更记录

| 日期 | 版本 | 变更内容 |
|------|------|---------|
| 2024-01-01 | 1.0 | 初始版本 |
| 2024-01-15 | 1.1 | 添加异步执行接口 |
| 2024-02-01 | 1.2 | 更新报告导出功能 |

## 9. 附录

### 9.1 数据类型说明

| 类型 | 描述 | 示例 |
|------|------|------|
| String | 字符串 | "test" |
| Integer | 整数 | 123 |
| Long | 长整数 | 1234567890 |
| Boolean | 布尔值 | true/false |
| Object | 对象 | {"key": "value"} |
| Array | 数组 | [1, 2, 3] |

### 9.2 时间格式

所有时间字段采用ISO 8601格式：
- 示例: 2024-01-01T10:00:00Z

### 9.3 联系方式

如有问题，请联系：
- 邮箱: admin@iatms.com
- 电话: 010-12345678

---

**文档更新时间**: 2024年1月1日 10:00:00
**文档版本**: 1.0
**文档作者**: IATMS开发团队