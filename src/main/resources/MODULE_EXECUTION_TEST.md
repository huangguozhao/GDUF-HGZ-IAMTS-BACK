# 模块执行测试功能测试说明

## 测试环境准备

### 1. 数据库准备

确保以下表已创建并包含测试数据：

```sql
-- 确保有测试模块数据
INSERT INTO Modules (module_code, project_id, name, description, status, created_by) 
VALUES ('USER_MGMT', 1, '用户管理模块', '用户相关功能模块', 'active', 1);

-- 确保有测试接口数据
INSERT INTO Apis (api_code, module_id, name, method, path, status, created_by)
VALUES 
('LOGIN', 1, '用户登录接口', 'POST', '/api/auth/login', 'active', 1),
('REGISTER', 1, '用户注册接口', 'POST', '/api/auth/register', 'active', 1);

-- 确保有测试用例数据
INSERT INTO TestCases (case_code, api_id, name, priority, severity, is_enabled, created_by)
VALUES 
('TC-LOGIN-001', 1, '正常登录测试', 'P0', 'critical', TRUE, 1),
('TC-LOGIN-002', 1, '错误密码登录测试', 'P1', 'high', TRUE, 1),
('TC-REGISTER-001', 2, '正常注册测试', 'P0', 'critical', TRUE, 1);
```

### 2. 认证准备

确保有有效的用户Token用于测试：

```bash
# 先登录获取Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "your_password"
  }'
```

## 接口测试

### 1. 同步执行模块测试

**测试用例1: 正常执行**

```bash
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "environment": "test",
    "base_url": "https://test-api.example.com",
    "timeout": 30,
    "async": false,
    "concurrency": 5,
    "case_filter": {
      "priority": ["P0", "P1"],
      "enabled_only": true
    }
  }'
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "模块测试执行完成",
  "data": {
    "execution_id": 20001,
    "module_id": 1,
    "module_name": "用户管理模块",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:30:15.000Z",
    "total_duration": 15000,
    "total_cases": 3,
    "passed": 2,
    "failed": 1,
    "skipped": 0,
    "broken": 0,
    "success_rate": 66.7,
    "report_id": 6001,
    "summary_url": "/api/reports/6001/summary"
  }
}
```

**测试用例2: 模块不存在**

```bash
curl -X POST http://localhost:8080/api/modules/999/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "environment": "test",
    "async": false
  }'
```

**预期响应**:
```json
{
  "code": -4,
  "msg": "模块不存在",
  "data": null
}
```

**测试用例3: 并发数超限**

```bash
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "environment": "test",
    "concurrency": 100,
    "async": false
  }'
```

**预期响应**:
```json
{
  "code": -3,
  "msg": "并发数不能超过50",
  "data": null
}
```

### 2. 异步执行模块测试

**测试用例1: 正常异步执行**

```bash
curl -X POST http://localhost:8080/api/modules/1/execute-async \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "environment": "test",
    "base_url": "https://test-api.example.com",
    "timeout": 60,
    "async": true,
    "concurrency": 10,
    "case_filter": {
      "priority": ["P0"],
      "tags": ["冒烟测试"],
      "enabled_only": true
    },
    "variables": {
      "env": "test",
      "version": "1.2.0"
    }
  }'
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "模块测试执行任务已提交",
  "data": {
    "task_id": "module_task_abc123def456",
    "module_id": 1,
    "module_name": "用户管理模块",
    "total_cases": 3,
    "filtered_cases": 1,
    "status": "queued",
    "concurrency": 10,
    "estimated_duration": 2,
    "queue_position": 1,
    "monitor_url": "/api/tasks/module_task_abc123def456/status",
    "report_url": "/api/reports/module/1/executions/latest"
  }
}
```

### 3. 查询任务状态

**测试用例1: 查询任务状态**

```bash
curl -X GET http://localhost:8080/api/tasks/module_task_abc123def456/status \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "task_id": "module_task_abc123def456",
    "module_id": 1,
    "module_name": "用户管理模块",
    "status": "running",
    "total_cases": 1,
    "passed": 0,
    "failed": 0,
    "skipped": 0,
    "broken": 0,
    "success_rate": 0.0,
    "monitor_url": "/api/tasks/module_task_abc123def456/status"
  }
}
```

**测试用例2: 任务不存在**

```bash
curl -X GET http://localhost:8080/api/tasks/invalid_task_id/status \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": -4,
  "msg": "任务不存在",
  "data": null
}
```

### 4. 取消任务执行

**测试用例1: 取消任务**

```bash
curl -X POST http://localhost:8080/api/tasks/module_task_abc123def456/cancel \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 1,
  "msg": "任务取消成功",
  "data": true
}
```

**测试用例2: 取消已完成的任务**

```bash
curl -X POST http://localhost:8080/api/tasks/completed_task_id/cancel \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应**:
```json
{
  "code": 0,
  "msg": "任务取消失败",
  "data": false
}
```

## 权限测试

### 1. 无Token测试

```bash
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "async": false
  }'
```

**预期响应**: HTTP 401 或相应的认证失败响应

### 2. 无效Token测试

```bash
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token" \
  -d '{
    "environment": "test",
    "async": false
  }'
```

**预期响应**: HTTP 401 或相应的认证失败响应

### 3. 权限不足测试

使用没有 `module:execute` 权限的用户Token测试：

```bash
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer USER_WITHOUT_PERMISSION_TOKEN" \
  -d '{
    "environment": "test",
    "async": false
  }'
```

**预期响应**: HTTP 403 或相应的权限不足响应

## 性能测试

### 1. 大量用例测试

准备包含大量测试用例的模块，测试系统性能：

```bash
# 创建包含100个测试用例的模块进行测试
curl -X POST http://localhost:8080/api/modules/LARGE_MODULE_ID/execute-async \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "environment": "test",
    "concurrency": 20,
    "async": true
  }'
```

### 2. 高并发测试

使用工具如Apache Bench或JMeter进行高并发测试：

```bash
ab -n 100 -c 10 -H "Authorization: Bearer YOUR_TOKEN" \
   -H "Content-Type: application/json" \
   -p test_data.json \
   http://localhost:8080/api/modules/1/execute-async
```

## 边界条件测试

### 1. 参数边界测试

```bash
# 最小并发数
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "concurrency": 1,
    "async": false
  }'

# 最大并发数
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "concurrency": 50,
    "async": false
  }'

# 超时时间测试
curl -X POST http://localhost:8080/api/modules/1/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "timeout": 1,
    "async": false
  }'
```

### 2. 空数据测试

```bash
# 无测试用例的模块
curl -X POST http://localhost:8080/api/modules/EMPTY_MODULE_ID/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "environment": "test",
    "async": false
  }'
```

## 测试检查点

1. **功能正确性**: 验证所有接口功能是否按预期工作
2. **错误处理**: 验证各种错误情况的处理是否正确
3. **权限控制**: 验证权限控制是否有效
4. **数据一致性**: 验证执行结果是否正确记录到数据库
5. **性能表现**: 验证系统在高负载下的表现
6. **并发安全**: 验证并发执行时的数据安全性
7. **资源清理**: 验证任务完成后资源是否正确清理

## 注意事项

1. 测试前确保数据库连接正常
2. 测试时注意观察系统资源使用情况
3. 长时间运行的测试建议使用异步模式
4. 测试完成后及时清理测试数据
5. 注意测试环境的网络连接稳定性
