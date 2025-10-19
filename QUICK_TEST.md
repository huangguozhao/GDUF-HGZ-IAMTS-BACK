# 🚀 TestExecutionController 接口快速测试

## 📋 概述

本项目提供了**4种测试方法**，帮助你快速测试 TestExecutionController 的所有29个接口。

## ⚡ 选择测试方法

### 方法1：自动化测试脚本 ⭐推荐

**优点**：一键运行，自动统计结果

#### Windows:
```bash
test_all_apis.bat
```

#### Linux/Mac:
```bash
chmod +x test_all_apis.sh
./test_all_apis.sh
```

**输出示例：**
```
======================================
TestExecutionController 接口测试
======================================

[测试 1] 执行单个测试用例
请求: POST http://localhost:8080/api/test-cases/1/execute
✓ 成功 (HTTP 200)

... (省略其他测试)

总计: 29 个接口
成功: 25 个
失败: 4 个
成功率: 86.21%
```

---

### 方法2：Postman Collection

**优点**：可视化界面，方便调试

#### 步骤：
1. 打开 Postman
2. 点击 "Import" → "Choose Files"
3. 选择 `TestExecutionController.postman_collection.json`
4. 导入成功后，可以看到所有29个接口
5. 点击 "Run" 按钮批量运行

**截图说明：**
- 接口分为6个文件夹（模块）
- 每个接口都配置好了请求方法、URL、参数
- 可以单独运行，也可以批量运行

---

### 方法3：手动curl测试

**优点**：灵活，适合单个接口测试

#### 快速测试最重要的接口：

```bash
# 1. 测试结果列表（最简单，建议优先测试）
curl -X GET "http://localhost:8080/api/test-results"

# 2. 带参数查询
curl -X GET "http://localhost:8080/api/test-results?page=1&page_size=10"

# 3. 执行测试用例
curl -X POST "http://localhost:8080/api/test-cases/1/execute" \
  -H "Content-Type: application/json" \
  -d '{"environment":"test","timeout":30000}'
```

完整命令请查看：`API_TEST_GUIDE.md`

---

### 方法4：浏览器直接访问（GET接口）

**优点**：最简单，无需工具

```
# 直接在浏览器打开
http://localhost:8080/api/test-results
http://localhost:8080/api/test-results?page=1&page_size=10
http://localhost:8080/api/test-results?status=failed
```

---

## 🎯 推荐测试流程

### 第一步：确认环境
```bash
# 1. 启动应用
mvn spring-boot:run

# 2. 确认拦截器已关闭
# 检查 GlobalOperationAspect.java 中：
# boolean ENABLE_INTERCEPTOR = false;

# 3. 等待应用完全启动
# 看到 "Started IatmsApplication in xxx seconds"
```

### 第二步：快速验证
```bash
# 使用浏览器访问最简单的接口
http://localhost:8080/api/test-results

# 预期看到 JSON 响应：
# {"code":1,"msg":"success","data":{...}}
```

### 第三步：全面测试
```bash
# 运行自动化测试脚本
test_all_apis.bat   # Windows
./test_all_apis.sh  # Linux/Mac
```

### 第四步：检查结果
- 查看控制台输出的测试统计
- 查看Web日志（如果启用了WebLogAspect）
- 记录失败的接口

---

## 📊 接口清单（29个）

### ✅ 优先测试这些（GET请求，不会修改数据）
- [ ] `GET /api/test-results` - 获取测试结果列表
- [ ] `GET /api/test-results?page=1&page_size=10` - 分页查询
- [ ] `GET /api/test-results?status=failed` - 按状态查询
- [ ] `GET /api/tasks/{task_id}/status` - 查询任务状态
- [ ] `GET /api/test-results/{execution_id}` - 获取执行结果
- [ ] `GET /api/test-results/{execution_id}/logs` - 获取执行日志

### 🔄 然后测试这些（POST请求，会执行操作）
- [ ] `POST /api/test-cases/{case_id}/execute` - 执行测试用例
- [ ] `POST /api/modules/{module_id}/execute` - 执行模块测试
- [ ] `POST /api/projects/{project_id}/execute` - 执行项目测试
- [ ] `POST /api/apis/{api_id}/execute` - 执行接口测试
- [ ] `POST /api/test-suites/{suite_id}/execute` - 执行测试套件

### 🚀 最后测试这些（异步和取消操作）
- [ ] `POST /api/test-cases/{case_id}/execute-async` - 异步执行
- [ ] `POST /api/tasks/{task_id}/cancel` - 取消任务
- [ ] 其他17个接口...

完整清单请查看：`API_TEST_GUIDE.md`

---

## 🔍 测试结果判断

### ✅ 成功标志
- HTTP状态码：200 或 201
- 响应体包含：`"code":1` 或 `"code":0`（业务失败但接口正常）
- 无异常抛出

### ❌ 失败标志
- HTTP状态码：404, 500
- 响应体包含：`"code":-1` 或其他负数
- 连接超时或拒绝

### ⚠️ 预期的"失败"（正常情况）
某些接口可能因为测试数据不存在而返回错误，这是正常的：
- 资源不存在（如ID不存在）
- 任务ID不存在
- 业务校验失败

**解决方法：**
1. 检查并修改测试脚本中的ID
2. 或在数据库中插入测试数据

---

## 🐛 常见问题快速解决

### 问题1：所有接口404
```bash
# 原因：应用未启动
# 解决：检查应用是否运行
jps | grep Iatms  # Linux
tasklist | findstr java  # Windows
```

### 问题2：认证失败
```bash
# 原因：拦截器未关闭
# 解决：修改 GlobalOperationAspect.java
boolean ENABLE_INTERCEPTOR = false;  # 确保是 false
```

### 问题3：资源不存在
```bash
# 原因：测试数据不存在
# 解决：修改测试脚本中的ID或插入测试数据
# 例如：将 case_id=1 改为实际存在的ID
```

### 问题4：curl不可用（Windows）
```bash
# 解决方案1：使用批处理脚本
test_all_apis.bat

# 解决方案2：安装Git Bash（自带curl）
# 解决方案3：使用Postman
```

---

## 📈 测试报告示例

### 成功的测试报告
```
======================================
测试结果统计
======================================
总计: 29 个接口
成功: 29 个
失败: 0 个
成功率: 100.00%

✓ 所有接口测试通过！
```

### 部分失败的测试报告
```
======================================
测试结果统计
======================================
总计: 29 个接口
成功: 25 个
失败: 4 个
成功率: 86.21%

失败的接口：
- POST /api/test-cases/1/execute (资源不存在)
- POST /api/modules/1/execute (资源不存在)
- POST /api/projects/1/execute (资源不存在)
- POST /api/apis/1/execute (资源不存在)

建议：插入测试数据或修改测试ID
```

---

## 📚 相关文档

| 文档 | 说明 |
|------|------|
| `API_TEST_GUIDE.md` | 详细的测试指南 |
| `test_all_apis.bat` | Windows测试脚本 |
| `test_all_apis.sh` | Linux/Mac测试脚本 |
| `TestExecutionController.postman_collection.json` | Postman集合 |
| `WEB_LOG_ASPECT_USAGE.md` | Web日志AOP文档 |
| `TEST_RESULTS_LIST_API.md` | 测试结果接口文档 |

---

## 💡 测试技巧

1. **先测试GET接口**：不会修改数据，最安全
2. **使用Web日志**：可以看到详细的请求和响应
3. **批量测试**：使用自动化脚本或Postman Runner
4. **单个调试**：使用curl或Postman单独测试
5. **查看控制台**：可以看到详细的错误信息

---

## 🎯 下一步

测试完成后：

1. ✅ 记录测试结果
2. ✅ 修复失败的接口（如果有）
3. ✅ 插入必要的测试数据
4. ✅ 编写单元测试
5. ✅ 准备生产环境部署

---

## ⚡ 一键测试（最快）

```bash
# Windows
test_all_apis.bat

# Linux/Mac
chmod +x test_all_apis.sh && ./test_all_apis.sh
```

就这么简单！🚀

---

## 📞 需要帮助？

- 查看详细文档：`API_TEST_GUIDE.md`
- 查看控制台日志
- 查看Web日志（logs/iatms.log）
- 检查应用是否启动
- 确认拦截器已关闭

祝测试顺利！✨

