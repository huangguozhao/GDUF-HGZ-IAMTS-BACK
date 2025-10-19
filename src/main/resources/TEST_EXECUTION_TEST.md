# 测试执行管理模块测试说明

## 测试环境准备

1. **启动应用**: 确保Spring Boot应用正常运行
2. **数据库准备**: 确保数据库中有测试用的接口和用例数据
3. **用户登录**: 获取有效的JWT Token
4. **测试数据**: 准备测试用例和接口数据

## 测试用例

### 1. 同步执行测试

#### 测试用例1: 正常执行测试用例
- **请求**: `POST /api/test-cases/{case_id}/execute`
- **参数**: 有效的用例ID和执行参数
- **预期结果**: HTTP 200, 返回执行结果

#### 测试用例2: 用例不存在
- **请求**: `POST /api/test-cases/99999/execute`
- **预期结果**: HTTP 404, code: -4

#### 测试用例3: 用例未启用
- **请求**: `POST /api/test-cases/{disabled_case_id}/execute`
- **预期结果**: HTTP 404, code: -4

#### 测试用例4: 接口不存在
- **请求**: `POST /api/test-cases/{case_id_with_invalid_api}/execute`
- **预期结果**: HTTP 400, code: 0

#### 测试用例5: 权限不足
- **请求**: 使用没有执行权限的用户Token
- **预期结果**: HTTP 403, code: -2

#### 测试用例6: 参数验证
- **请求**: 发送无效的JSON参数
- **预期结果**: HTTP 400, code: -3

### 2. 异步执行测试

#### 测试用例1: 异步执行成功
- **请求**: `POST /api/test-cases/{case_id}/execute-async`
- **预期结果**: HTTP 200, 返回任务信息

#### 测试用例2: 查询任务状态
- **请求**: `GET /api/tasks/{task_id}/status`
- **预期结果**: HTTP 200, 返回任务状态

#### 测试用例3: 任务不存在
- **请求**: `GET /api/tasks/invalid_task_id/status`
- **预期结果**: HTTP 404, code: -4

#### 测试用例4: 取消任务
- **请求**: `POST /api/tasks/{task_id}/cancel`
- **预期结果**: HTTP 200, 返回取消结果

### 3. 执行结果查询测试

#### 测试用例1: 获取执行结果
- **请求**: `GET /api/test-results/{execution_id}`
- **预期结果**: HTTP 200, 返回执行结果详情

#### 测试用例2: 执行记录不存在
- **请求**: `GET /api/test-results/99999`
- **预期结果**: HTTP 404, code: -4

#### 测试用例3: 获取执行日志
- **请求**: `GET /api/test-results/{execution_id}/logs`
- **预期结果**: HTTP 200, 返回执行日志

### 4. 报告生成测试

#### 测试用例1: 生成测试报告
- **请求**: `POST /api/test-results/{execution_id}/report`
- **预期结果**: HTTP 200, 返回报告ID

#### 测试用例2: 执行记录不存在
- **请求**: `POST /api/test-results/99999/report`
- **预期结果**: HTTP 404, code: -4

### 5. 功能验证测试

#### 测试用例1: HTTP请求执行
- **验证**: 测试用例能正确发送HTTP请求
- **检查点**: 请求方法、URL、请求头、请求体正确

#### 测试用例2: 断言执行
- **验证**: 断言规则能正确执行
- **检查点**: 断言结果正确，失败时记录错误信息

#### 测试用例3: 变量替换
- **验证**: 变量能正确替换
- **检查点**: URL和请求体中的变量被正确替换

#### 测试用例4: 环境配置
- **验证**: 不同环境的配置正确
- **检查点**: 基础URL根据环境正确设置

#### 测试用例5: 超时处理
- **验证**: 超时设置生效
- **检查点**: 超时后正确返回失败结果

#### 测试用例6: 错误处理
- **验证**: 各种异常情况正确处理
- **检查点**: 网络错误、JSON解析错误等被正确处理

## 测试数据准备

### 1. 测试用例数据

确保数据库中有以下测试用例：

```sql
-- 正常启用的测试用例
INSERT INTO TestCases (case_id, case_code, api_id, name, description, priority, severity, 
                      is_enabled, is_template, created_by, created_at, updated_at, is_deleted)
VALUES (1001, 'TC-001', 101, '用户登录-成功场景', '测试成功登录场景', 'P0', 'high', 
        TRUE, FALSE, 1, NOW(), NOW(), FALSE);

-- 已禁用的测试用例
INSERT INTO TestCases (case_id, case_code, api_id, name, description, priority, severity, 
                      is_enabled, is_template, created_by, created_at, updated_at, is_deleted)
VALUES (1002, 'TC-002', 101, '用户登录-密码错误', '测试密码错误场景', 'P1', 'medium', 
        FALSE, FALSE, 1, NOW(), NOW(), FALSE);
```

### 2. 接口数据

确保数据库中有对应的接口数据：

```sql
-- 正常启用的接口
INSERT INTO Apis (api_id, api_code, module_id, name, method, path, base_url, 
                  status, created_by, created_at, updated_at, is_deleted)
VALUES (101, 'API-001', 1, '用户登录接口', 'POST', '/api/login', 'https://api.example.com', 
        'active', 1, NOW(), NOW(), FALSE);

-- 已禁用的接口
INSERT INTO Apis (api_id, api_code, module_id, name, method, path, base_url, 
                  status, created_by, created_at, updated_at, is_deleted)
VALUES (102, 'API-002', 1, '用户注册接口', 'POST', '/api/register', 'https://api.example.com', 
        'inactive', 1, NOW(), NOW(), FALSE);
```

### 3. 用户权限数据

确保测试用户有相应的权限：

```sql
-- 用户权限配置
INSERT INTO UserRoles (user_id, role_id, created_at)
VALUES (1, 1, NOW());

-- 角色权限配置
INSERT INTO RolePermissions (role_id, permission_code, created_at)
VALUES (1, 'testcase:execute', NOW()),
       (1, 'testcase:view', NOW());
```

## 测试工具推荐

1. **Postman**: 用于API测试
2. **curl**: 命令行测试工具
3. **JUnit**: 单元测试框架
4. **Mockito**: Mock测试框架
5. **TestContainers**: 集成测试容器

## 测试检查点

### 1. 功能正确性
- 测试用例能正确执行
- HTTP请求发送正确
- 断言执行正确
- 变量替换正确
- 结果记录正确

### 2. 错误处理
- 异常情况正确处理
- 错误信息准确
- 错误码正确
- 日志记录完整

### 3. 权限控制
- 权限验证正确
- 资源访问控制正确
- 未授权访问被拒绝

### 4. 性能表现
- 执行时间合理
- 内存使用正常
- 并发执行稳定

### 5. 数据一致性
- 执行结果正确保存
- 报告数据准确
- 日志记录完整

## 性能测试

### 1. 并发执行测试
- 多个用户同时执行测试用例
- 验证系统稳定性
- 检查资源竞争问题

### 2. 大量数据测试
- 执行包含大量数据的测试用例
- 验证内存使用情况
- 检查执行性能

### 3. 长时间执行测试
- 执行耗时较长的测试用例
- 验证超时处理
- 检查资源释放

## 集成测试

### 1. 端到端测试
- 完整的测试执行流程
- 从用例执行到报告生成
- 验证整个流程的正确性

### 2. 数据库集成测试
- 验证数据正确保存
- 检查事务处理
- 验证数据一致性

### 3. 外部服务集成测试
- 验证HTTP请求发送
- 检查响应处理
- 验证错误处理

## 常见问题排查

### 1. 执行失败
- 检查用例是否存在且启用
- 检查关联接口是否存在且启用
- 检查网络连接
- 检查超时设置

### 2. 权限错误
- 检查用户是否登录
- 检查Token是否有效
- 检查用户权限配置
- 检查资源访问权限

### 3. 数据问题
- 检查数据库连接
- 检查数据完整性
- 检查事务处理
- 检查并发问题

### 4. 性能问题
- 检查内存使用
- 检查CPU使用
- 检查网络延迟
- 检查数据库性能

## 测试报告

### 1. 测试结果统计
- 测试用例总数
- 通过用例数
- 失败用例数
- 跳过用例数

### 2. 性能指标
- 平均执行时间
- 最大执行时间
- 最小执行时间
- 成功率

### 3. 问题汇总
- 发现的问题
- 问题严重程度
- 问题修复状态
- 改进建议

## 注意事项

1. 测试前确保环境准备充分
2. 测试过程中注意观察应用日志
3. 测试完成后清理测试数据
4. 建议使用不同的用户权限进行测试
5. 注意测试数据的隔离性
6. 验证异常情况的处理
7. 检查性能指标是否合理
8. 确保测试覆盖所有功能点
