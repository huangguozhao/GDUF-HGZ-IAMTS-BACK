# Web日志AOP使用文档

## 功能说明

`WebLogAspect` 是一个用于记录所有HTTP请求和响应详细信息的AOP切面，可以帮助你：
- 调试接口问题
- 监控接口性能
- 审计用户操作
- 分析接口调用情况

## 记录的信息

### 请求信息
- ✅ 请求ID（用于关联请求和响应）
- ✅ 完整URL
- ✅ URI路径
- ✅ HTTP方法（GET/POST/PUT/DELETE等）
- ✅ 客户端IP地址（支持代理穿透）
- ✅ 调用的类和方法
- ✅ Content-Type
- ✅ 重要的请求头（Authorization、Accept、Origin等）
- ✅ 查询参数（URL参数）
- ✅ 方法参数（Controller方法的参数）
- ✅ User-Agent

### 响应信息
- ✅ 请求ID（与请求关联）
- ✅ 是否成功
- ✅ 执行时间（毫秒）
- ✅ 响应结果（JSON格式）
- ✅ 异常信息（如果有）

## 日志示例

### 成功请求的日志

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
Query Parameters:
  page : 1
  page_size : 20
  status : failed
Method Arguments:
  taskType : "null"
  refId : "null"
  status : "failed"
  environment : "null"
  priority : "null"
  severity : "null"
  startTimeBegin : "null"
  startTimeEnd : "null"
  durationMin : "null"
  durationMax : "null"
  searchKeyword : "null"
  sortBy : "null"
  sortOrder : "null"
  page : "1"
  pageSize : "20"
User-Agent    : Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
====================================================

================ HTTP Response Start ===============
Request ID    : a1b2c3d4e5f6g7h8
Success       : true
Execution Time: 125 ms
Response      : {"code":1,"msg":"success","data":{"total":10,"items":[...],"page":1,"page_size":20,"summary":{...}}}
====================================================
```

### 异常请求的日志

```
================ HTTP Request Start ================
Request ID    : x1y2z3a4b5c6d7e8
URL           : http://localhost:8080/api/test-results
URI           : /api/test-results
HTTP Method   : GET
IP Address    : 127.0.0.1
Class Method  : com.victor.iatms.controller.TestExecutionController.getTestResults
...
====================================================

================ HTTP Response Start ===============
Request ID    : x1y2z3a4b5c6d7e8
Success       : false
Execution Time: 15 ms
Exception Type: com.victor.iatms.exception.BusinessException
Exception Msg : 参数验证失败
====================================================
```

## 配置选项

在 `application.yml` 或 `application-dev.yml` 中添加配置：

```yaml
# Web日志配置
web:
  log:
    # 是否启用Web日志（默认：true）
    enabled: true
    
    # 是否记录请求头（默认：true）
    log-headers: true
    
    # 是否记录查询参数（默认：true）
    log-params: true
    
    # 是否记录方法参数（默认：true）
    log-args: true
    
    # 是否记录响应结果（默认：true）
    log-response: true
    
    # 响应结果最大长度，超过则截断（默认：1000）
    max-response-length: 1000
    
    # 参数最大长度，超过则截断（默认：500）
    max-param-length: 500
```

## 配置示例

### 1. 开发环境配置（详细日志）

```yaml
# application-dev.yml
web:
  log:
    enabled: true
    log-headers: true
    log-params: true
    log-args: true
    log-response: true
    max-response-length: 2000
    max-param-length: 1000
```

### 2. 生产环境配置（简化日志）

```yaml
# application-prod.yml
web:
  log:
    enabled: true
    log-headers: false        # 不记录请求头（可能包含敏感信息）
    log-params: true
    log-args: false           # 不记录方法参数（减少日志量）
    log-response: false       # 不记录响应结果（减少日志量）
    max-response-length: 500
    max-param-length: 200
```

### 3. 测试环境配置（关闭日志）

```yaml
# application-test.yml
web:
  log:
    enabled: false  # 完全关闭Web日志
```

## 特殊处理

### 1. 敏感信息脱敏

- **Authorization头**：自动截断，只显示前10位和后10位
  ```
  Authorization : Bearer eyJhbGciOi...ZCI6IjEifQ
  ```

### 2. 文件上传处理

- **MultipartFile**：显示文件名和大小，不记录文件内容
  ```
  file : MultipartFile[name=test.xlsx, size=1024 bytes]
  ```

### 3. 长内容截断

- **响应结果**：超过配置的最大长度自动截断
  ```
  Response : {"code":1,"msg":"success","data":... (truncated, total 2500 chars)
  ```

- **方法参数**：超过配置的最大长度自动截断
  ```
  requestBody : {"field1":"value1","field2":"value2"... (truncated)
  ```

### 4. 特殊类型过滤

以下类型的参数不会被记录：
- `HttpServletRequest`
- `HttpServletResponse`
- `Model`
- `BindingResult`

## 性能影响

### 性能开销
- 每个请求增加约 **1-5ms** 的处理时间
- 主要开销在JSON序列化和日志写入

### 优化建议

1. **生产环境优化**
   ```yaml
   web:
     log:
       log-args: false        # 关闭方法参数记录
       log-response: false    # 关闭响应结果记录
       max-response-length: 200  # 减小最大长度
   ```

2. **异步日志**
   使用Logback的异步Appender：
   ```xml
   <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
       <appender-ref ref="FILE"/>
   </appender>
   ```

3. **日志级别控制**
   在 `logback-spring.xml` 中设置：
   ```xml
   <logger name="com.victor.iatms.aspect.WebLogAspect" level="INFO"/>
   ```

## 查看日志

### 1. 控制台查看

启动应用后，所有请求和响应日志会输出到控制台。

### 2. 文件查看

日志会写入到配置的日志文件中，默认路径：
```
logs/iatms.log
```

### 3. 日志分析

可以使用以下命令快速查找：

```bash
# 查找特定请求ID的日志
grep "a1b2c3d4e5f6g7h8" logs/iatms.log

# 查找失败的请求
grep "Success       : false" logs/iatms.log

# 查找执行时间超过1秒的请求
grep -E "Execution Time: [0-9]{4,}" logs/iatms.log

# 查看最近的请求
tail -f logs/iatms.log | grep "HTTP Request Start"
```

## 临时关闭日志

### 方法1：修改配置文件

```yaml
web:
  log:
    enabled: false
```

### 方法2：修改日志级别

```xml
<!-- logback-spring.xml -->
<logger name="com.victor.iatms.aspect.WebLogAspect" level="OFF"/>
```

### 方法3：注释切面Bean

在 `WebLogAspect.java` 类上注释 `@Component` 注解：

```java
// @Component  // 注释这行即可禁用
@Aspect
public class WebLogAspect {
    ...
}
```

## 常见问题

### Q1: 日志太多，如何减少？

**A**: 可以通过以下方式减少日志量：
- 设置 `log-args: false` 不记录方法参数
- 设置 `log-response: false` 不记录响应结果
- 减小 `max-response-length` 和 `max-param-length`
- 使用日志过滤器只记录特定接口

### Q2: 如何只记录特定接口的日志？

**A**: 修改切点表达式，例如：
```java
@Pointcut("execution(public * com.victor.iatms.controller.TestExecutionController.*(..))")
public void webLog() {
}
```

### Q3: 日志中包含敏感信息怎么办？

**A**: 
- Authorization头已自动脱敏
- 可以在配置中关闭请求头记录：`log-headers: false`
- 可以在代码中添加更多脱敏逻辑

### Q4: 生产环境是否建议开启？

**A**: 建议开启，但需要：
- 关闭详细参数记录（`log-args: false`）
- 关闭响应结果记录（`log-response: false`）
- 使用异步日志写入
- 定期清理旧日志文件

### Q5: 如何排查接口性能问题？

**A**: 
1. 查看 `Execution Time` 字段
2. 使用命令找出慢接口：
   ```bash
   grep -E "Execution Time: [0-9]{4,}" logs/iatms.log
   ```
3. 根据 Request ID 查找完整的请求日志

## 扩展功能

如果你需要更多功能，可以在 `WebLogAspect.java` 中添加：

1. **记录到数据库**
2. **发送到日志收集系统**（如ELK）
3. **实时监控告警**（执行时间超过阈值时告警）
4. **统计分析**（接口调用次数、平均响应时间等）

## 总结

✅ **优点**：
- 自动记录所有接口的请求和响应
- 支持灵活配置
- 性能影响小
- 便于调试和问题排查

⚠️ **注意**：
- 生产环境建议简化日志配置
- 注意日志文件大小，定期清理
- 敏感信息需要脱敏处理

🔧 **推荐配置**：
- 开发环境：全部开启，详细记录
- 测试环境：全部开启，简化记录
- 生产环境：开启，但关闭详细参数和响应

