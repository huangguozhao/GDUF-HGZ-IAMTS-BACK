# Web日志AOP快速开始

## 🚀 立即生效

创建完成后，**无需任何额外配置**，Web日志AOP已经自动生效！

所有Controller的请求和响应都会被自动记录。

## 📋 快速测试

### 1. 启动应用

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者在IDE中直接运行 IatmsApplication.java
```

### 2. 发送测试请求

```bash
# 测试测试结果列表接口
curl -X GET "http://localhost:8080/api/test-results?page=1&page_size=10"
```

### 3. 查看控制台日志

你会看到类似这样的输出：

```
================ HTTP Request Start ================
Request ID    : a1b2c3d4e5f6g7h8
URL           : http://localhost:8080/api/test-results
URI           : /api/test-results
HTTP Method   : GET
IP Address    : 127.0.0.1
Class Method  : com.victor.iatms.controller.TestExecutionController.getTestResults
Content-Type  : null
Query Parameters:
  page : 1
  page_size : 10
Method Arguments:
  taskType : "null"
  refId : "null"
  status : "null"
  ...
====================================================

================ HTTP Response Start ===============
Request ID    : a1b2c3d4e5f6g7h8
Success       : true
Execution Time: 125 ms
Response      : {"code":1,"msg":"success","data":{...}}
====================================================
```

## ⚙️ 常用配置

### 临时关闭日志（测试时）

在 `application-dev.yml` 中修改：

```yaml
web:
  log:
    enabled: false  # 关闭Web日志
```

### 简化日志输出

```yaml
web:
  log:
    enabled: true
    log-headers: false    # 不记录请求头
    log-params: true      # 记录查询参数
    log-args: false       # 不记录方法参数
    log-response: false   # 不记录响应结果
```

### 只看执行时间

```yaml
web:
  log:
    enabled: true
    log-headers: false
    log-params: false
    log-args: false
    log-response: false  # 这样只会显示基本信息和执行时间
```

## 🔍 日志查找技巧

### 1. 根据Request ID查找

```bash
# 查找特定请求的所有日志
grep "a1b2c3d4e5f6g7h8" logs/iatms.log
```

### 2. 查找慢接口

```bash
# 查找执行时间超过1秒的接口
grep -E "Execution Time: [0-9]{4,}" logs/iatms.log
```

### 3. 查找失败的请求

```bash
# 查找所有失败的请求
grep "Success       : false" logs/iatms.log
```

### 4. 实时监控

```bash
# 实时查看日志
tail -f logs/iatms.log
```

## 📊 日志示例对比

### 详细模式（开发环境推荐）

```yaml
web:
  log:
    enabled: true
    log-headers: true
    log-params: true
    log-args: true
    log-response: true
```

输出：
```
================ HTTP Request Start ================
Request ID    : a1b2c3d4e5f6g7h8
URL           : http://localhost:8080/api/test-results
URI           : /api/test-results
HTTP Method   : GET
IP Address    : 127.0.0.1
Class Method  : com.victor.iatms.controller.TestExecutionController.getTestResults
Content-Type  : null
Request Headers:
  Accept : application/json
  Authorization : Bearer eyJhbGciOi...ZCI6IjEifQ
Query Parameters:
  page : 1
  page_size : 10
  status : failed
Method Arguments:
  taskType : "null"
  status : "failed"
  page : "1"
  pageSize : "10"
  [... 更多参数 ...]
User-Agent    : Mozilla/5.0
====================================================

================ HTTP Response Start ===============
Request ID    : a1b2c3d4e5f6g7h8
Success       : true
Execution Time: 125 ms
Response      : {"code":1,"msg":"success","data":{"total":10,...}}
====================================================
```

### 简化模式（生产环境推荐）

```yaml
web:
  log:
    enabled: true
    log-headers: false
    log-params: true
    log-args: false
    log-response: false
```

输出：
```
================ HTTP Request Start ================
Request ID    : a1b2c3d4e5f6g7h8
URL           : http://localhost:8080/api/test-results
URI           : /api/test-results
HTTP Method   : GET
IP Address    : 127.0.0.1
Class Method  : com.victor.iatms.controller.TestExecutionController.getTestResults
Content-Type  : null
Query Parameters:
  page : 1
  page_size : 10
  status : failed
====================================================

================ HTTP Response Start ===============
Request ID    : a1b2c3d4e5f6g7h8
Success       : true
Execution Time: 125 ms
====================================================
```

## 💡 使用技巧

### 1. 调试接口问题

当接口出现问题时：
1. 从日志中找到 Request ID
2. 用 Request ID 查找完整的请求和响应日志
3. 检查参数、执行时间、异常信息

### 2. 性能优化

1. 查看 Execution Time 找出慢接口
2. 分析是否是数据库查询慢还是业务逻辑慢
3. 针对性优化

### 3. 排查生产问题

1. 用户反馈问题 → 获取请求时间
2. 在日志中查找该时间段的所有请求
3. 根据IP地址和URL路径定位具体请求
4. 分析请求参数和响应结果

## ⚠️ 注意事项

1. **日志文件大小**
   - 开发环境：日志会很多，定期清理
   - 生产环境：建议使用简化模式

2. **敏感信息**
   - Authorization头已自动脱敏
   - 如有其他敏感字段，需要手动添加脱敏逻辑

3. **性能影响**
   - 详细模式：每个请求约增加 3-5ms
   - 简化模式：每个请求约增加 1-2ms

4. **文件上传**
   - 文件内容不会被记录
   - 只记录文件名和大小

## 🎯 下一步

1. ✅ 启动应用测试
2. ✅ 发送几个请求查看日志
3. ✅ 根据需要调整配置
4. ✅ 在生产环境使用简化配置

## 📚 更多信息

详细文档请查看：`WEB_LOG_ASPECT_USAGE.md`

