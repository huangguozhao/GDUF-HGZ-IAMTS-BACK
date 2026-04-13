# 修改模块信息接口文档

## 接口概述

修改模块信息接口提供了安全更新指定模块信息的功能，支持部分更新，包含完整的业务逻辑验证、权限检查和循环引用检测。

## 接口详情

### 修改模块信息

**请求路径**: `/modules/{module_id}`  
**请求方式**: `PUT`  
**接口描述**: 更新指定模块的信息

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`
- `Content-Type` (string, 必须): 固定为 `application/json`

**路径参数**:
- `module_id` (number, 必须): 要更新的模块ID

**请求体**:
- `module_code` (string, 可选): 模块编码，项目内唯一
- `name` (string, 可选): 模块名称
- `description` (string, 可选): 模块描述
- `parent_module_id` (number, 可选): 父模块ID
- `sort_order` (number, 可选): 排序顺序
- `status` (string, 可选): 模块状态，可选值: `active`, `inactive`, `archived`
- `owner_id` (number, 可选): 模块负责人ID
- `tags` (string[], 可选): 标签信息

#### 请求示例

```bash
# 修改模块名称
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"name": "支付管理模块-新版"}'

# 修改模块描述
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"description": "更新后的支付模块描述，包含新功能"}'

# 修改模块状态
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"status": "active"}'

# 修改模块负责人
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"owner_id": 789}'

# 修改模块标签
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"tags": ["核心模块", "支付", "财务", "重要"]}'

# 修改模块编码
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"module_code": "PAYMENT_NEW"}'

# 修改父模块
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"parent_module_id": 5}'

# 修改排序顺序
curl -X PUT "http://localhost:8080/modules/1" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json" \
  -d '{"sort_order": 2}'
```

#### 响应数据

**成功响应** (HTTP 200):

```json
{
  "code": 1,
  "msg": "模块信息更新成功",
  "data": {
    "module_id": 10,
    "module_code": "PAYMENT_NEW",
    "project_id": 1,
    "parent_module_id": 5,
    "name": "支付管理模块-新版",
    "description": "更新后的支付模块描述，包含新功能",
    "sort_order": 2,
    "status": "active",
    "owner_info": {
      "user_id": 789,
      "name": "王五",
      "avatar_url": "/avatars/wangwu.jpg"
    },
    "tags": ["核心模块", "支付", "财务", "重要"],
    "created_by": 123,
    "creator_name": "张三",
    "updated_by": 456,
    "updater_name": "李四",
    "created_at": "2024-01-15T10:30:00.000Z",
    "updated_at": "2024-09-16T14:25:00.000Z",
    "is_deleted": false
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
  "msg": "模块已被删除，无法编辑",
  "data": null
}

// 模块编码已存在 (HTTP 200)
{
  "code": 0,
  "msg": "模块编码已被其他模块使用",
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
  "msg": "权限不足，无法编辑模块信息",
  "data": null
}

// 参数验证失败 (HTTP 200)
{
  "code": -3,
  "msg": "模块名称不能为空",
  "data": null
}

// 循环引用错误 (HTTP 200)
{
  "code": 0,
  "msg": "不能将模块设置为自己的父模块",
  "data": null
}

// 不能修改系统模块 (HTTP 200)
{
  "code": -2,
  "msg": "不能修改系统模块",
  "data": null
}
```

## 业务逻辑说明

### 修改流程
1. **参数校验**: 验证模块ID、修改信息和更新人ID
2. **模块存在性检查**: 检查模块是否存在
3. **删除状态检查**: 检查模块是否已被删除
4. **系统模块检查**: 检查是否为系统模块（不允许修改）
5. **权限验证**: 检查用户是否有修改权限
6. **模块编码唯一性验证**: 检查新的模块编码是否已存在
7. **父模块验证**: 检查父模块是否存在且属于同一项目
8. **循环引用检测**: 检查是否形成循环引用
9. **负责人验证**: 检查负责人是否存在且有效
10. **状态验证**: 检查模块状态是否有效
11. **执行更新**: 更新模块信息
12. **返回结果**: 返回更新后的模块信息

### 权限规则
1. **创建者权限**: 可以修改自己创建的模块
2. **管理员权限**: 管理员可以修改任何模块（待实现）
3. **系统模块保护**: 系统模块不允许修改

### 系统模块识别
- 模块编码以 `SYS_` 开头
- 模块名称包含"系统"关键字

### 循环引用检测
检查模块的父级链中是否包含当前模块，防止形成循环引用：
- 不能将模块设置为自己的父模块
- 不能将模块设置为自己的子模块的父模块
- 递归检查整个父级链

### 部分更新支持
接口支持部分更新，只修改请求体中提供的字段：
- 未提供的字段保持原值不变
- 提供null值的字段会被更新为null
- 提供空字符串的字段会被更新为空字符串

## 安全特性

### 1. 认证要求
- 使用`@GlobalInterceptor(checkLogin = true)`进行认证
- 必须提供有效的认证令牌

### 2. 权限控制
- 验证用户是否有修改权限
- 系统模块受到特殊保护
- 只能修改有权限的模块

### 3. 业务规则验证
- 模块编码唯一性检查
- 父模块存在性和项目一致性检查
- 循环引用检测
- 负责人有效性检查
- 状态有效性检查

### 4. 参数验证
- 模块名称长度限制
- 模块编码格式验证
- 描述长度限制
- 必填字段验证

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
3. **权限不足**: 用户没有修改权限
4. **系统模块**: 系统模块不允许修改
5. **模块编码已存在**: 新的模块编码已被其他模块使用
6. **父模块不存在**: 指定的父模块不存在
7. **循环引用**: 检测到循环引用
8. **负责人不存在**: 指定的负责人不存在或未激活
9. **状态无效**: 模块状态值无效
10. **参数验证失败**: 输入参数不符合要求

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有模块修改权限
3. **部分更新**: 支持部分更新，只修改提供的字段
4. **循环引用**: 更新父模块时会检查循环引用
5. **系统保护**: 系统模块不允许修改
6. **审计日志**: 修改操作会被记录（待实现）

## 循环引用检测算法

```javascript
// 伪代码示例：检测循环引用
async function checkCircularReference(moduleId, parentModuleId) {
  if (!parentModuleId) return false; // 没有父模块，无循环引用
  
  if (moduleId === parentModuleId) {
    throw new Error('不能将模块设置为自己的父模块');
  }
  
  // 检查父模块的父级链中是否包含当前模块
  let currentParentId = parentModuleId;
  const visited = new Set([moduleId]);
  
  while (currentParentId) {
    if (visited.has(currentParentId)) {
      throw new Error('检测到循环引用');
    }
    visited.add(currentParentId);
    
    const parentModule = await Module.findByPk(currentParentId);
    if (!parentModule) break;
    
    currentParentId = parentModule.parent_module_id;
  }
  
  return false;
}
```

## 测试

可以使用提供的 `test_update_module_api.bat` 脚本进行接口测试，或者使用 Postman 等工具进行测试。

测试前请确保：
1. 应用已启动
2. 数据库中有测试数据
3. 有有效的认证令牌
4. 有模块修改权限

## 后续扩展建议

1. **审计日志**: 实现完整的审计日志记录
2. **权限细化**: 更细粒度的修改权限控制
3. **批量修改**: 支持批量修改多个模块
4. **修改历史**: 提供模块修改历史记录
5. **版本控制**: 支持模块版本管理
6. **字段级权限**: 不同字段的修改权限控制
