# 删除测试用例API文档

## 接口概述

删除测试用例API提供了删除指定测试用例的功能，采用软删除方式，包含完整的业务规则检查和安全验证。

## 接口详情

### 删除测试用例

**请求路径**: `/testcases/{case_id}`  
**请求方式**: `DELETE`  
**接口描述**: 删除指定的测试用例（软删除）

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`

**路径参数**:
- `case_id` (number, 必须): 要删除的测试用例ID

**请求体**: 无

#### 请求示例

```bash
# 基本删除请求
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here"

# 删除不存在的用例
curl -X DELETE "http://localhost:8080/testcases/9999" \
  -H "Authorization: Bearer your_token_here"

# 删除已删除的用例
curl -X DELETE "http://localhost:8080/testcases/1002" \
  -H "Authorization: Bearer your_token_here"

# 删除模板用例
curl -X DELETE "http://localhost:8080/testcases/1004" \
  -H "Authorization: Bearer your_token_here"

# 删除系统用例
curl -X DELETE "http://localhost:8080/testcases/1005" \
  -H "Authorization: Bearer your_token_here"

# 删除正在被使用的用例
curl -X DELETE "http://localhost:8080/testcases/1006" \
  -H "Authorization: Bearer your_token_here"

# 无认证令牌
curl -X DELETE "http://localhost:8080/testcases/1001"

# 错误的请求方法
curl -X GET "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here"

# 错误的请求路径
curl -X DELETE "http://localhost:8080/testcase/1001" \
  -H "Authorization: Bearer your_token_here"

# 无效的用例ID
curl -X DELETE "http://localhost:8080/testcases/abc" \
  -H "Authorization: Bearer your_token_here"

# 负数用例ID
curl -X DELETE "http://localhost:8080/testcases/-1" \
  -H "Authorization: Bearer your_token_here"

# 零用例ID
curl -X DELETE "http://localhost:8080/testcases/0" \
  -H "Authorization: Bearer your_token_here"

# 大数值用例ID
curl -X DELETE "http://localhost:8080/testcases/999999999" \
  -H "Authorization: Bearer your_token_here"

# 过期令牌
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer expired_token_here"

# 无效令牌
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer invalid_token_here"

# 空令牌
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer "

# 无Bearer前缀
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: your_token_here"

# 错误的Authorization头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Basic dXNlcjpwYXNz"

# 多个Authorization头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Authorization: Bearer another_token_here"

# Content-Type头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Content-Type: application/json"

# Accept头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "Accept: application/json"

# User-Agent头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "User-Agent: TestClient/1.0"

# X-Requested-With头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "X-Requested-With: XMLHttpRequest"

# 自定义头
curl -X DELETE "http://localhost:8080/testcases/1001" \
  -H "Authorization: Bearer your_token_here" \
  -H "X-Custom-Header: custom_value"

# 并发删除
curl -X DELETE "http://localhost:8080/testcases/1007" \
  -H "Authorization: Bearer your_token_here" &
curl -X DELETE "http://localhost:8080/testcases/1007" \
  -H "Authorization: Bearer your_token_here" &
wait

# 批量删除测试
for i in {1008..1010}; do
  curl -X DELETE "http://localhost:8080/testcases/$i" \
    -H "Authorization: Bearer your_token_here"
done

# 压力测试
for i in {1..10}; do
  curl -X DELETE "http://localhost:8080/testcases/1011" \
    -H "Authorization: Bearer your_token_here"
done

# 边界值测试
curl -X DELETE "http://localhost:8080/testcases/1" \
  -H "Authorization: Bearer your_token_here"
curl -X DELETE "http://localhost:8080/testcases/2147483647" \
  -H "Authorization: Bearer your_token_here"

# 特殊字符测试
curl -X DELETE "http://localhost:8080/testcases/1001%20" \
  -H "Authorization: Bearer your_token_here"
curl -X DELETE "http://localhost:8080/testcases/1001+" \
  -H "Authorization: Bearer your_token_here"
```

#### 响应数据

**成功响应** (HTTP 200):

```json
{
  "code": 1,
  "msg": "测试用例删除成功",
  "data": null
}
```

**失败响应**:

```json
// 用例不存在 (HTTP 200)
{
  "code": -4,
  "msg": "测试用例不存在",
  "data": null
}

// 用例已被删除 (HTTP 200)
{
  "code": 0,
  "msg": "测试用例已被删除",
  "data": null
}

// 权限不足 (HTTP 200)
{
  "code": -2,
  "msg": "权限不足，无法删除测试用例",
  "data": null
}

// 模板用例 (HTTP 200)
{
  "code": 0,
  "msg": "模板用例不能被删除",
  "data": null
}

// 系统用例 (HTTP 200)
{
  "code": 0,
  "msg": "不能删除系统用例",
  "data": null
}

// 用例正在被使用 (HTTP 200)
{
  "code": 0,
  "msg": "用例正在被测试计划使用，无法删除",
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

### 删除流程
1. **参数校验**: 验证用例ID是否有效
2. **用例验证**: 检查用例是否存在且未被删除
3. **权限检查**: 验证用户是否有用例管理权限
4. **业务规则检查**:
   - 检查是否为模板用例
   - 检查是否为系统用例
   - 检查用例是否正在被使用
5. **执行软删除**: 设置删除标记和相关字段
6. **记录操作日志**: 记录删除操作信息

### 软删除实现
- 设置 `is_deleted` 字段为 `TRUE`
- 设置 `deleted_at` 字段为当前时间
- 设置 `deleted_by` 字段为当前操作者的用户ID
- 设置 `is_enabled` 字段为 `FALSE`

### 权限规则
1. **创建者权限**: 可以删除自己创建的用例
2. **项目成员权限**: 项目成员可以删除用例
3. **用例管理权限**: 需要用例管理权限

### 业务规则保护
1. **模板用例保护**: 模板用例不能被删除
2. **系统用例保护**: 系统内置用例不能被删除
3. **使用中保护**: 正在被测试计划使用的用例不能被删除

## 安全特性

### 1. 认证要求
- 使用`@GlobalInterceptor(checkLogin = true)`进行认证
- 必须提供有效的认证令牌

### 2. 权限控制
- 验证用户是否有用例管理权限
- 只能在有权限的用例上执行删除操作

### 3. 业务规则保护
- 模板用例保护
- 系统用例保护
- 使用中保护

### 4. 软删除机制
- 数据不会物理删除
- 保留删除记录和操作人信息
- 支持数据恢复和审计

## 错误处理

### 错误码说明
| 错误码 | 含义 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| 0 | 业务逻辑失败 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 200 |
| -3 | 参数校验失败 | 200 |
| -4 | 资源不存在 | 200 |
| -5 | 服务器内部异常 | 500 |

### 常见错误场景
1. **用例不存在**: 指定的用例ID不存在
2. **用例已被删除**: 用例状态为已删除
3. **权限不足**: 用户没有用例管理权限
4. **模板用例**: 用例是模板用例，不能删除
5. **系统用例**: 用例是系统内置用例，不能删除
6. **用例正在被使用**: 用例正在被测试计划使用

## 软删除详解

### 实现原理
- 使用软删除机制，数据不会物理删除
- 通过设置删除标记来标识已删除状态
- 保留删除记录和操作人信息

### 软删除SQL
```sql
UPDATE TestCases
SET is_deleted = TRUE,
    deleted_at = NOW(),
    deleted_by = #{deletedBy},
    is_enabled = FALSE
WHERE case_id = #{caseId}
  AND is_deleted = FALSE
```

### 软删除优势
1. **数据安全**: 数据不会物理丢失
2. **审计追踪**: 保留删除记录和操作人信息
3. **数据恢复**: 支持数据恢复功能
4. **关联保护**: 避免破坏数据关联关系

## 业务规则详解

### 模板用例保护
```java
// 检查是否为模板用例
if (testCase.getIsTemplate()) {
    throw new IllegalArgumentException("模板用例不能被删除");
}
```

### 系统用例保护
```java
// 检查是否为系统用例
if (isSystemTestCase(testCase)) {
    throw new IllegalArgumentException("不能删除系统用例");
}
```

### 使用中保护
```java
// 检查用例是否正在被使用
if (isTestCaseInUse(caseId)) {
    throw new IllegalArgumentException("用例正在被测试计划使用，无法删除");
}
```

## 权限控制详解

### 权限检查逻辑
```java
// 检查权限（需要用例管理权限）
if (!hasTestCaseManagePermission(testCase, currentUserId)) {
    throw new IllegalArgumentException("权限不足，无法删除测试用例");
}
```

### 权限规则
1. **创建者权限**: 可以删除自己创建的用例
2. **项目成员权限**: 项目成员可以删除用例
3. **用例管理权限**: 需要用例管理权限

## 测试建议

1. **功能测试**：
   - 测试正常删除流程
   - 测试各种业务规则保护
   - 测试权限控制

2. **异常测试**：
   - 测试不存在的用例
   - 测试已删除的用例
   - 测试模板用例和系统用例

3. **权限测试**：
   - 测试权限不足
   - 测试认证失败
   - 测试不同权限级别

4. **边界测试**：
   - 测试边界值
   - 测试特殊字符
   - 测试并发操作

## 性能考虑

### 1. 索引优化
- 用例ID索引
- 删除状态索引
- 创建人索引

### 2. 查询优化
- 使用索引进行存在性检查
- 避免全表扫描

### 3. 并发控制
- 使用数据库锁防止并发删除
- 事务控制确保数据一致性

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有用例管理权限
3. **软删除**: 采用软删除方式，数据不会物理删除
4. **业务规则**: 模板用例、系统用例、使用中的用例不能删除
5. **操作日志**: 删除操作会记录审计日志
6. **数据恢复**: 支持数据恢复功能（需要额外实现）

## 后续扩展建议

1. **回收站功能**: 提供用例回收站功能，支持恢复已删除的用例
2. **批量删除**: 支持批量删除测试用例
3. **删除确认**: 提供删除确认机制
4. **删除审批**: 支持重要用例删除的审批流程
5. **删除统计**: 提供删除统计信息
6. **删除报告**: 生成删除报告功能
7. **删除通知**: 支持删除通知功能
8. **删除分析**: 提供删除分析功能
9. **删除优化**: 提供删除优化建议
10. **删除监控**: 提供删除操作监控
11. **删除备份**: 支持删除前的数据备份
12. **删除回滚**: 支持删除操作回滚
13. **删除审计**: 增强删除审计功能
14. **删除权限**: 支持更细粒度的删除权限控制
15. **删除策略**: 支持不同的删除策略
16. **删除计划**: 支持定时删除计划
17. **删除规则**: 支持自定义删除规则
18. **删除模板**: 支持删除操作模板
19. **删除向导**: 提供删除操作向导
20. **删除帮助**: 提供删除操作帮助
