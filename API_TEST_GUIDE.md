# TestExecutionController 接口测试指南

## 📋 测试概述

本文档提供了 `TestExecutionController` 所有接口的测试方法，包含 **29个接口**，涵盖5大模块：
1. 测试用例执行（7个接口）
2. 模块执行（4个接口）
3. 项目执行（4个接口）
4. 接口执行（4个接口）
5. 测试套件执行（4个接口）
6. 测试结果查询（6个接口）

## 🚀 快速测试

### 方法1：使用测试脚本（推荐）

#### Windows系统：
```bash
# 双击运行或在命令行执行
test_all_apis.bat
```

#### Linux/Mac系统：
```bash
# 添加执行权限
chmod +x test_all_apis.sh

# 运行测试
./test_all_apis.sh
```

### 方法2：使用curl手动测试

确保应用已启动（http://localhost:8080），然后执行以下命令。

## 📝 详细接口测试

### 1. 测试用例执行相关接口（7个）

#### 1.1 执行单个测试用例
```bash
curl -X POST "http://localhost:8080/api/test-cases/1/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "timeout": 30000
  }'
```

#### 1.2 异步执行测试用例
```bash
curl -X POST "http://localhost:8080/api/test-cases/1/execute-async" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "async": true
  }'
```

#### 1.3 查询任务状态
```bash
curl -X GET "http://localhost:8080/api/tasks/test-task-id-123/status"
```

#### 1.4 取消任务执行
```bash
curl -X POST "http://localhost:8080/api/tasks/test-task-id-123/cancel" \
  -H "Content-Type: application/json" \
  -d '{}'
```

#### 1.5 获取执行结果详情
```bash
curl -X GET "http://localhost:8080/api/test-results/1"
```

#### 1.6 获取执行日志
```bash
curl -X GET "http://localhost:8080/api/test-results/1/logs"
```

#### 1.7 生成测试报告
```bash
curl -X POST "http://localhost:8080/api/test-results/1/report" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

### 2. 模块执行相关接口（4个）

#### 2.1 执行模块测试（同步）
```bash
curl -X POST "http://localhost:8080/api/modules/1/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 5
  }'
```

#### 2.2 异步执行模块测试
```bash
curl -X POST "http://localhost:8080/api/modules/1/execute-async" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "async": true
  }'
```

#### 2.3 查询模块任务状态
```bash
curl -X GET "http://localhost:8080/api/module-tasks/module-task-123/status"
```

#### 2.4 取消模块任务执行
```bash
curl -X POST "http://localhost:8080/api/module-tasks/module-task-123/cancel" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

### 3. 项目执行相关接口（4个）

#### 3.1 执行项目测试
```bash
curl -X POST "http://localhost:8080/api/projects/1/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 10
  }'
```

#### 3.2 异步执行项目测试
```bash
curl -X POST "http://localhost:8080/api/projects/1/execute-async" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "async": true
  }'
```

#### 3.3 查询项目任务状态
```bash
curl -X GET "http://localhost:8080/api/project-tasks/project-task-123/status"
```

#### 3.4 取消项目任务执行
```bash
curl -X POST "http://localhost:8080/api/project-tasks/project-task-123/cancel" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

### 4. 接口执行相关接口（4个）

#### 4.1 执行接口测试
```bash
curl -X POST "http://localhost:8080/api/apis/1/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 3
  }'
```

#### 4.2 异步执行接口测试
```bash
curl -X POST "http://localhost:8080/api/apis/1/execute-async" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "async": true
  }'
```

#### 4.3 查询接口任务状态
```bash
curl -X GET "http://localhost:8080/api/api-tasks/api-task-123/status"
```

#### 4.4 取消接口任务执行
```bash
curl -X POST "http://localhost:8080/api/api-tasks/api-task-123/cancel" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

### 5. 测试套件执行相关接口（4个）

#### 5.1 执行测试套件
```bash
curl -X POST "http://localhost:8080/api/test-suites/1/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 5
  }'
```

#### 5.2 异步执行测试套件
```bash
curl -X POST "http://localhost:8080/api/test-suites/1/execute-async" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "async": true
  }'
```

#### 5.3 查询测试套件任务状态
```bash
curl -X GET "http://localhost:8080/api/suite-tasks/suite-task-123/status"
```

#### 5.4 取消测试套件任务执行
```bash
curl -X POST "http://localhost:8080/api/suite-tasks/suite-task-123/cancel" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

### 6. 测试结果查询相关接口（6个）

#### 6.1 获取测试结果列表（无参数）
```bash
curl -X GET "http://localhost:8080/api/test-results"
```

**预期响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 0,
    "items": [],
    "page": 1,
    "page_size": 20,
    "summary": {
      "total_count": 0,
      "passed": 0,
      "failed": 0,
      "broken": 0,
      "skipped": 0,
      "unknown": 0,
      "success_rate": 0.00,
      "avg_duration": 0
    }
  }
}
```

#### 6.2 获取测试结果列表（带分页）
```bash
curl -X GET "http://localhost:8080/api/test-results?page=1&page_size=10"
```

#### 6.3 查询失败的测试结果
```bash
curl -X GET "http://localhost:8080/api/test-results?status=failed&page=1&page_size=20"
```

#### 6.4 按任务类型查询
```bash
curl -X GET "http://localhost:8080/api/test-results?task_type=test_case&ref_id=101"
```

#### 6.5 复杂条件查询
```bash
curl -X GET "http://localhost:8080/api/test-results?task_type=test_case&status=passed&priority=P0,P1&environment=test&sort_by=start_time&sort_order=desc"
```

#### 6.6 关键字搜索
```bash
curl -X GET "http://localhost:8080/api/test-results?search_keyword=登录&page=1&page_size=10"
```

---

## 📊 测试检查清单

使用以下清单确保所有接口测试完成：

### 测试用例执行模块
- [ ] 执行单个测试用例
- [ ] 异步执行测试用例
- [ ] 查询任务状态
- [ ] 取消任务执行
- [ ] 获取执行结果详情
- [ ] 获取执行日志
- [ ] 生成测试报告

### 模块执行模块
- [ ] 执行模块测试（同步）
- [ ] 异步执行模块测试
- [ ] 查询模块任务状态
- [ ] 取消模块任务执行

### 项目执行模块
- [ ] 执行项目测试
- [ ] 异步执行项目测试
- [ ] 查询项目任务状态
- [ ] 取消项目任务执行

### 接口执行模块
- [ ] 执行接口测试
- [ ] 异步执行接口测试
- [ ] 查询接口任务状态
- [ ] 取消接口任务执行

### 测试套件执行模块
- [ ] 执行测试套件
- [ ] 异步执行测试套件
- [ ] 查询测试套件任务状态
- [ ] 取消测试套件任务执行

### 测试结果查询模块
- [ ] 获取测试结果列表（无参数）
- [ ] 获取测试结果列表（带分页）
- [ ] 查询失败的测试结果
- [ ] 按任务类型查询
- [ ] 复杂条件查询
- [ ] 关键字搜索

---

## ⚙️ 测试前准备

### 1. 确保应用已启动
```bash
# 使用Maven启动
mvn spring-boot:run

# 或在IDE中运行 IatmsApplication.java
```

### 2. 确认拦截器已关闭
检查 `GlobalOperationAspect.java` 中的配置：
```java
boolean ENABLE_INTERCEPTOR = false; // 应该是 false
```

### 3. 确认端口正确
默认端口：`8080`
如果修改了端口，请在测试脚本或命令中修改 `BASE_URL`

---

## 🔍 测试结果分析

### HTTP状态码说明

| 状态码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | 接口正常工作 |
| 400 | 参数错误 | 检查请求参数格式 |
| 404 | 资源不存在 | 检查ID是否存在 |
| 500 | 服务器错误 | 查看控制台日志 |

### 业务状态码说明

响应体中的 `code` 字段：

| Code | 说明 | 示例 |
|------|------|------|
| 1 | 成功 | 正常业务响应 |
| 0 | 业务失败 | 资源不存在等 |
| -1 | 认证失败 | Token无效 |
| -2 | 权限不足 | 无权访问 |
| -3 | 参数错误 | 参数校验失败 |
| -5 | 服务器异常 | 系统错误 |

---

## 🐛 常见问题

### Q1: 所有接口返回404
**原因：** 应用未启动或端口错误
**解决：**
```bash
# 检查应用是否运行
jps | grep Iatms

# 检查端口
netstat -ano | findstr 8080
```

### Q2: 接口返回 "认证失败"
**原因：** 拦截器未关闭
**解决：** 检查 `GlobalOperationAspect.java` 中 `ENABLE_INTERCEPTOR = false`

### Q3: 接口返回 "资源不存在"
**原因：** 测试数据不存在
**解决：** 
- 修改测试脚本中的ID（如将 `case_id=1` 改为实际存在的ID）
- 或在数据库中插入测试数据

### Q4: curl命令不可用
**原因：** Windows系统未安装curl
**解决：**
- 下载安装 Git Bash（自带curl）
- 或使用 Postman 测试
- 或使用测试脚本（.bat文件）

---

## 📈 测试报告示例

运行测试脚本后，会生成如下报告：

```
======================================
TestExecutionController 接口测试
======================================

========== 测试用例执行相关接口 ==========

[测试 1] 执行单个测试用例
请求: POST http://localhost:8080/api/test-cases/1/execute
✓ 成功 (HTTP 200)
响应: {"code":1,"msg":"用例执行完成","data":{...}}

[测试 2] 异步执行测试用例
请求: POST http://localhost:8080/api/test-cases/1/execute-async
✓ 成功 (HTTP 200)
响应: {"code":1,"msg":"用例执行任务已提交","data":{...}}

... (省略其他测试结果)

======================================
测试结果统计
======================================
总计: 29 个接口
成功: 25 个
失败: 4 个
成功率: 86.21%

✓ 部分接口测试通过，请检查失败的接口！
```

---

## 🎯 下一步

测试完成后：

1. ✅ 记录测试结果
2. ✅ 修复失败的接口
3. ✅ 查看Web日志（如果启用了WebLogAspect）
4. ✅ 准备集成测试数据
5. ✅ 编写单元测试

---

## 📚 相关文档

- [测试结果列表接口文档](src/main/resources/TEST_RESULTS_LIST_API.md)
- [Web日志AOP使用文档](src/main/resources/WEB_LOG_ASPECT_USAGE.md)
- [接口文档汇总](src/main/resources/)

---

## 💡 提示

- 建议先测试测试结果查询接口（GET请求），因为它们不会修改数据
- 如果接口返回"资源不存在"，可以先插入测试数据
- 使用Web日志AOP可以查看详细的请求和响应信息
- 生产环境测试前务必恢复拦截器配置

