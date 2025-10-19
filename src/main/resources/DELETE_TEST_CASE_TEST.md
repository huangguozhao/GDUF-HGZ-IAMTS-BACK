# 删除测试用例接口测试指南

## 接口信息

**请求路径**: `/apis/{api_id}/test-cases/{case_id}`  
**请求方式**: `DELETE`  
**Content-Type**: `application/json`  
**认证要求**: 需要Bearer Token认证和testcase:delete权限

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

### 2. 使用Token删除测试用例

```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**成功响应**:
```json
{
  "code": 1,
  "msg": "测试用例删除成功",
  "data": null
}
```

## 测试用例

### 用例1: 正常删除测试用例

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `code: 1, msg: "测试用例删除成功"`

### 用例2: 删除不存在的接口

**请求**:
```bash
DELETE /apis/999/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: -4, msg: "接口不存在"`

### 用例3: 删除不存在的测试用例

**请求**:
```bash
DELETE /apis/101/test-cases/9999
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: -4, msg: "测试用例不存在"`

### 用例4: 删除不属于指定接口的用例

**请求**:
```bash
DELETE /apis/101/test-cases/2001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: -4, msg: "测试用例不存在"`

### 用例5: 删除已被删除的测试用例

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: 0, msg: "测试用例已被删除"`

### 用例6: 删除正在被测试计划使用的用例

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: 0, msg: "用例正在被测试计划使用，无法删除"`

### 用例7: 删除正在执行中的用例

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: 0, msg: "用例正在执行中，无法删除"`

### 用例8: 删除系统内置模板用例

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: 0, msg: "系统内置模板用例不允许删除"`

### 用例9: 未提供Token

**请求**:
```bash
DELETE /apis/101/test-cases/1001
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例10: Token无效

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer invalid_token
```

**预期响应**: `HTTP 401, code: -1, msg: "认证失败，请重新登录"`

### 用例11: 权限不足

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <token_without_permission>
```

**预期响应**: `HTTP 403, code: -2, msg: "权限不足"`

### 用例12: 删除操作失败

**请求**:
```bash
DELETE /apis/101/test-cases/1001
Authorization: Bearer <valid_token>
```

**预期响应**: `HTTP 200, code: -5, msg: "测试用例删除失败"`

## 请求参数说明

| 参数名 | 类型 | 是否必须 | 说明 | 示例值 |
|--------|------|----------|------|--------|
| api_id | number | 必须 | 接口ID（路径参数） | 101 |
| case_id | number | 必须 | 用例ID（路径参数） | 1001 |

## 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | number | 业务状态码 |
| msg | string | 操作结果消息 |
| data | null | 删除操作无返回数据 |

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理方式 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| 0 | 业务逻辑失败 | 200 | 展示msg给用户 |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -3 | 参数校验失败 | 400 | 提示用户检查输入 |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 业务逻辑说明

1. **认证与授权**: 验证Token和用户权限（testcase:delete权限）
2. **验证接口**: 检查接口是否存在
3. **验证用例**: 检查用例是否存在且属于指定接口
4. **检查删除状态**: 检查用例是否已被删除
5. **业务逻辑检查**: 检查用例是否正在被使用或执行
6. **执行软删除**: 设置is_deleted=TRUE，记录删除时间和删除人
7. **返回结果**: 返回删除成功消息

## 软删除机制

### 删除字段
- `is_deleted`: 设置为TRUE
- `deleted_at`: 记录删除时间
- `deleted_by`: 记录删除人ID

### 软删除优势
1. **数据安全**: 数据不会真正丢失，可以恢复
2. **审计追踪**: 保留删除记录，便于审计
3. **关联完整性**: 避免破坏数据关联关系
4. **业务连续性**: 不影响正在进行的业务流程

## 业务规则检查

### 当前实现的检查
1. ✅ **接口存在性检查**: 验证接口是否存在
2. ✅ **用例存在性检查**: 验证用例是否存在且属于指定接口
3. ✅ **删除状态检查**: 检查用例是否已被删除

### TODO: 待实现的检查
1. **测试计划使用检查**: 检查用例是否正在被测试计划使用
2. **执行状态检查**: 检查用例是否正在执行中
3. **系统模板检查**: 检查是否为系统内置模板用例

### 实现建议
```java
// 检查用例是否正在被测试计划使用
Long planCount = testPlanCaseMapper.countByTestCaseId(testCase.getCaseId());
if (planCount > 0) {
    throw new RuntimeException("用例正在被测试计划使用，无法删除");
}

// 检查用例是否正在执行中
Long executionCount = testExecutionMapper.countRunningByTestCaseId(testCase.getCaseId());
if (executionCount > 0) {
    throw new RuntimeException("用例正在执行中，无法删除");
}

// 检查用例是否为系统内置模板用例
if (testCase.getIsTemplate() && isSystemBuiltInTemplate(testCase)) {
    throw new RuntimeException("系统内置模板用例不允许删除");
}
```

## 注意事项

1. **软删除**: 此接口执行软删除，数据不会真正从数据库中删除
2. **权限要求**: 需要testcase:delete权限和对应API的访问权限
3. **业务检查**: 删除前会检查用例是否正在被使用或执行
4. **数据完整性**: 软删除机制保证数据关联的完整性
5. **审计追踪**: 记录删除操作的相关信息

## 相关接口

- [分页获取接口相关用例列表](./TEST_CASE_LIST_TEST.md)
- [添加测试用例接口](./CREATE_TEST_CASE_TEST.md)
- [编辑测试用例接口](./UPDATE_TEST_CASE_TEST.md)
- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_TEST.md)

## TODO: 操作日志记录

**注意**: 根据接口文档要求，删除操作应该记录操作日志，便于审计和追踪变更历史。此功能暂未实现，需要在后续版本中添加。

### 建议实现的功能

1. **操作日志记录**
   - 在删除成功后记录操作日志
   - 记录删除的用例信息
   - 记录操作人、操作时间等信息

2. **日志表设计**
   - 使用提供的Logs表结构
   - 记录操作类型为"delete"
   - 记录目标类型为"testcase"
   - 记录目标ID为用例ID

3. **审计功能**
   - 支持日志查询和审计功能
   - 支持删除历史追踪
   - 支持操作人追踪

4. **实现建议**
   - 在TestCaseServiceImpl中添加日志记录逻辑
   - 使用AOP或事件机制记录操作日志
   - 考虑性能影响，异步记录日志

## 回收站功能建议

**注意**: 对于重要的用例，可以考虑实现回收站功能，允许在一定时间内恢复已删除的用例。

### 回收站功能设计
1. **回收站列表**: 显示已删除的用例列表
2. **恢复功能**: 允许恢复已删除的用例
3. **彻底删除**: 支持彻底删除回收站中的用例
4. **自动清理**: 支持自动清理过期的删除记录

### 实现建议
1. **回收站表**: 创建专门的回收站表存储删除记录
2. **恢复接口**: 提供恢复用例的接口
3. **清理任务**: 实现定时清理任务
4. **权限控制**: 回收站功能需要特殊权限
