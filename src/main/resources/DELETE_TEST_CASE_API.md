# 删除测试用例接口文档

## 接口概述

**接口名称**: 删除测试用例  
**接口路径**: `/apis/{api_id}/test-cases/{case_id}`  
**请求方式**: `DELETE`  
**接口描述**: 删除指定接口下的测试用例（软删除）。此接口需要认证，且需要用例管理权限。

## 请求参数

### 请求头
```
Authorization: Bearer {token}
```

### 路径参数
| 参数名 | 类型 | 是否必须 | 说明 |
|--------|------|----------|------|
| api_id | number | 必须 | 接口ID |
| case_id | number | 必须 | 要删除的用例ID |

### 请求体
**无请求体**

## 响应数据

### 成功响应

**HTTP状态码**: `200`

```json
{
  "code": 1,
  "msg": "测试用例删除成功",
  "data": null
}
```

### 失败响应

#### 接口不存在
```json
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}
```

#### 测试用例不存在
```json
{
  "code": -4,
  "msg": "测试用例不存在",
  "data": null
}
```

#### 测试用例已被删除
```json
{
  "code": 0,
  "msg": "测试用例已被删除",
  "data": null
}
```

#### 用例正在被测试计划使用
```json
{
  "code": 0,
  "msg": "用例正在被测试计划使用，无法删除",
  "data": null
}
```

#### 用例正在执行中
```json
{
  "code": 0,
  "msg": "用例正在执行中，无法删除",
  "data": null
}
```

#### 系统内置模板用例不允许删除
```json
{
  "code": 0,
  "msg": "系统内置模板用例不允许删除",
  "data": null
}
```

#### 权限不足
```json
{
  "code": -2,
  "msg": "权限不足，无法删除测试用例",
  "data": null
}
```

## 响应字段说明

### 成功响应数据 (data)
删除操作无返回数据，`data` 字段为 `null`。

## 业务逻辑

1. **认证与授权**: 验证Token和用户权限（testcase:delete权限）
2. **验证接口**: 根据api_id检查接口是否存在
3. **验证用例**: 根据case_id检查用例是否存在且属于指定接口
4. **检查用例状态**: 检查用例是否已被删除
5. **重要的业务逻辑检查**:
   - 检查用例是否正在被测试计划使用
   - 检查用例是否正在执行中
   - 检查用例是否为系统内置模板用例
6. **执行软删除**: 更新TestCases表：
   - 设置is_deleted为TRUE
   - 设置deleted_at为当前时间
   - 设置deleted_by为当前操作者的用户ID
7. **返回结果**: 返回操作成功的消息

## 软删除机制

### 软删除字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| is_deleted | boolean | 是否删除，设置为TRUE |
| deleted_at | timestamp | 删除时间，设置为当前时间 |
| deleted_by | int | 删除人ID，设置为当前用户ID |

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

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理建议 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| 0 | 业务逻辑失败 | 200 | 展示msg给用户 |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -3 | 参数校验失败 | 400 | 提示用户检查输入 |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 权限要求

### 必需权限
- `testcase:delete`: 删除测试用例权限

### 资源访问权限
- 需要对应API的访问权限
- 通过`@GlobalInterceptor`注解自动校验

## 注意事项

1. **软删除**: 此接口执行软删除，数据不会真正从数据库中删除
2. **权限校验**: 需要testcase:delete权限和对应API的访问权限
3. **业务检查**: 删除前会检查用例是否正在被使用或执行
4. **数据完整性**: 软删除机制保证数据关联的完整性
5. **审计追踪**: 记录删除操作的相关信息

## 相关接口

- [分页获取接口相关用例列表](./TEST_CASE_LIST_API.md)
- [添加测试用例接口](./CREATE_TEST_CASE_API.md)
- [编辑测试用例接口](./UPDATE_TEST_CASE_API.md)
- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_API.md)

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

### 回收站表设计建议
```sql
CREATE TABLE TestCaseRecycleBin (
    recycle_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '回收站ID',
    case_id INT NOT NULL COMMENT '原用例ID',
    case_code VARCHAR(50) NOT NULL COMMENT '用例编码',
    api_id INT NOT NULL COMMENT '接口ID',
    name VARCHAR(255) NOT NULL COMMENT '用例名称',
    description TEXT COMMENT '用例描述',
    priority ENUM('P0', 'P1', 'P2', 'P3') COMMENT '优先级',
    severity ENUM('critical', 'high', 'medium', 'low') COMMENT '严重程度',
    tags JSON COMMENT '标签',
    is_template BOOLEAN COMMENT '是否为模板用例',
    version VARCHAR(20) COMMENT '版本号',
    created_by INT COMMENT '原创建人ID',
    deleted_by INT NOT NULL COMMENT '删除人ID',
    deleted_at TIMESTAMP NOT NULL COMMENT '删除时间',
    expire_at TIMESTAMP NOT NULL COMMENT '过期时间',
    restored_at TIMESTAMP NULL COMMENT '恢复时间',
    restored_by INT NULL COMMENT '恢复人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_case_id (case_id),
    INDEX idx_api_id (api_id),
    INDEX idx_deleted_by (deleted_by),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_expire_at (expire_at)
) COMMENT='测试用例回收站表';
```

### 回收站接口设计建议
1. **获取回收站列表**: `GET /recycle-bin/test-cases`
2. **恢复用例**: `POST /recycle-bin/test-cases/{recycle_id}/restore`
3. **彻底删除**: `DELETE /recycle-bin/test-cases/{recycle_id}`
4. **批量恢复**: `POST /recycle-bin/test-cases/batch-restore`
5. **批量彻底删除**: `DELETE /recycle-bin/test-cases/batch-delete`
