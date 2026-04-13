# 创建模块接口文档

## 接口概述

创建模块接口提供了在指定项目下创建新模块的功能，支持创建根模块和子模块，包含完整的参数校验和业务逻辑验证。

## 接口详情

### 创建模块

**请求路径**: `/modules`  
**请求方式**: `POST`  
**接口描述**: 创建一个新的模块

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`
- `Content-Type` (string, 必须): 固定为 `application/json`

**请求体参数**:

| 参数名             | 类型     | 是否必须 | 备注                                                         |
| :----------------- | :------- | :------- | :----------------------------------------------------------- |
| `module_code`      | string   | 必须     | 模块编码，项目内唯一，只能包含大写字母、数字和下划线         |
| `project_id`       | number   | 必须     | 项目ID                                                       |
| `parent_module_id` | number   | 否       | 父模块ID，不提供则创建根模块                                 |
| `name`             | string   | 必须     | 模块名称                                                     |
| `description`      | string   | 否       | 模块描述                                                     |
| `sort_order`       | number   | 否       | 排序顺序，默认: `0`                                          |
| `status`           | string   | 否       | 模块状态，默认: `active`。可选: `active`, `inactive`, `archived` |
| `owner_id`         | number   | 否       | 模块负责人ID                                                 |
| `tags`             | string[] | 否       | 标签信息                                                     |

#### 请求示例

```bash
# 创建根模块
curl -X POST "http://localhost:8080/modules" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "module_code": "PAYMENT_MGMT",
    "project_id": 1,
    "name": "支付管理模块",
    "description": "支付相关的功能模块，包括支付、退款、对账等功能",
    "sort_order": 3,
    "status": "active",
    "owner_id": 456,
    "tags": ["核心模块", "支付相关", "财务"]
  }'

# 创建子模块
curl -X POST "http://localhost:8080/modules" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "module_code": "AUTH_SUB",
    "project_id": 1,
    "parent_module_id": 1,
    "name": "认证子模块",
    "description": "用户认证和授权子模块",
    "sort_order": 1,
    "status": "active",
    "owner_id": 123,
    "tags": ["认证", "安全"]
  }'
```

#### 响应数据

**成功响应** (HTTP 200):

```json
{
  "code": 1,
  "msg": "模块创建成功",
  "data": {
    "module_id": 10,
    "module_code": "PAYMENT_MGMT",
    "project_id": 1,
    "parent_module_id": null,
    "name": "支付管理模块",
    "description": "支付相关的功能模块，包括支付、退款、对账等功能",
    "sort_order": 3,
    "status": "active",
    "owner_id": 456,
    "owner_name": "李四",
    "tags": ["核心模块", "支付相关", "财务"],
    "created_by": 123,
    "creator_name": "张三",
    "created_at": "2024-09-16T10:30:00.000Z",
    "updated_at": "2024-09-16T10:30:00.000Z"
  }
}
```

**失败响应**:

```json
// 模块编码已存在 (HTTP 200)
{
  "code": 0,
  "msg": "模块编码已存在",
  "data": null
}

// 项目不存在 (HTTP 200)
{
  "code": 0,
  "msg": "指定的项目不存在",
  "data": null
}

// 父模块不存在 (HTTP 200)
{
  "code": 0,
  "msg": "指定的父模块不存在",
  "data": null
}

// 权限不足 (HTTP 200)
{
  "code": -2,
  "msg": "权限不足，无法创建模块",
  "data": null
}

// 参数验证失败 (HTTP 200)
{
  "code": -3,
  "msg": "模块编码不能为空",
  "data": null
}

// 模块编码格式错误 (HTTP 200)
{
  "code": -3,
  "msg": "模块编码只能包含大写字母、数字和下划线",
  "data": null
}

// 负责人不存在 (HTTP 200)
{
  "code": 0,
  "msg": "指定的负责人不存在",
  "data": null
}
```

## 业务逻辑说明

### 参数校验
1. **模块编码校验**：
   - 不能为空
   - 长度不能超过50个字符
   - 只能包含大写字母、数字和下划线
   - 在项目内必须唯一

2. **项目校验**：
   - 项目ID不能为空
   - 项目必须存在且未被删除

3. **父模块校验**（如果提供）：
   - 父模块必须存在且未被删除
   - 父模块必须属于同一个项目

4. **负责人校验**（如果提供）：
   - 负责人必须存在且未被删除

5. **模块名称校验**：
   - 不能为空
   - 长度不能超过255个字符

6. **模块描述校验**：
   - 长度不能超过1000个字符

7. **排序顺序校验**：
   - 不能为负数

8. **模块状态校验**：
   - 只能是active、inactive或archived

### 创建流程
1. 验证所有输入参数
2. 检查项目是否存在
3. 检查父模块是否存在（如果提供）
4. 检查负责人是否存在（如果提供）
5. 检查模块编码是否已存在
6. 创建模块记录
7. 返回创建的模块详情

## 数据结构说明

### CreateModuleResponseDTO 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| module_id | Integer | 模块ID |
| module_code | String | 模块编码 |
| project_id | Integer | 项目ID |
| parent_module_id | Integer | 父模块ID |
| name | String | 模块名称 |
| description | String | 模块描述 |
| sort_order | Integer | 排序顺序 |
| status | String | 模块状态 |
| owner_id | Integer | 模块负责人ID |
| owner_name | String | 模块负责人姓名 |
| tags | Array | 标签列表 |
| created_by | Integer | 创建人ID |
| creator_name | String | 创建人姓名 |
| created_at | String | 创建时间 |
| updated_at | String | 更新时间 |

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有模块管理权限
3. **编码规范**: 模块编码建议使用大写英文和下划线，便于识别和管理
4. **父子关系**: 创建子模块时，父模块必须属于同一个项目
5. **唯一性**: 模块编码在项目内必须保持唯一性
6. **循环引用**: 系统会验证父子模块关系的合理性，避免循环引用

## 错误码说明

| 错误码 | 含义 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| 0 | 业务逻辑失败 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 200 |
| -3 | 参数校验失败 | 200 |
| -4 | 资源不存在 | 404 |
| -5 | 服务器内部异常 | 500 |

## 测试

可以使用提供的 `test_create_module_api.bat` 脚本进行接口测试，或者使用 Postman 等工具进行测试。

测试前请确保：
1. 应用已启动
2. 数据库中有测试数据
3. 有有效的认证令牌
4. 有模块管理权限
