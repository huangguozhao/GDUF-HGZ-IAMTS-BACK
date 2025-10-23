# 网络错误处理改进说明

## 问题描述
在接口测试中，当请求不到或超时时，系统直接返回网络错误给前端，而不是作为测试失败的情况处理。这导致测试结果不准确，无法正确反映接口的真实状态。

## 解决方案

### 1. 改进 HttpClientUtils 错误处理
**文件**: `src/main/java/com/victor/iatms/utils/HttpClientUtils.java`

**改进内容**:
- 区分不同类型的网络错误
- 提供更详细的错误信息
- 针对不同异常类型返回具体的错误描述

**修改前**:
```java
} catch (IOException e) {
    log.error("HTTP请求失败: {}", e.getMessage(), e);
    return new HttpResponseResult(-1, null, null, e.getMessage());
}
```

**修改后**:
```java
} catch (java.net.ConnectException e) {
    log.error("连接失败: {}", e.getMessage(), e);
    return new HttpResponseResult(-1, null, null, "连接失败: " + e.getMessage());
} catch (java.net.SocketTimeoutException e) {
    log.error("请求超时: {}", e.getMessage(), e);
    return new HttpResponseResult(-1, null, null, "请求超时: " + e.getMessage());
} catch (java.net.UnknownHostException e) {
    log.error("未知主机: {}", e.getMessage(), e);
    return new HttpResponseResult(-1, null, null, "未知主机: " + e.getMessage());
} catch (IOException e) {
    log.error("HTTP请求失败: {}", e.getMessage(), e);
    return new HttpResponseResult(-1, null, null, "网络错误: " + e.getMessage());
}
```

### 2. 改进 TestCaseExecutor 网络错误处理
**文件**: `src/main/java/com/victor/iatms/utils/TestCaseExecutor.java`

**改进内容**:
- 在发送HTTP请求后立即检查网络错误
- 将网络错误作为测试失败处理，而不是异常
- 生成专门的网络错误日志

**新增逻辑**:
```java
// 4. 检查网络错误和超时情况
if (response.hasError()) {
    // 网络错误或超时，作为测试失败处理
    executionDTO.setExecutionStatus(ExecutionStatusEnum.FAILED.getCode());
    executionDTO.setFailureMessage("网络请求失败: " + response.getErrorMessage());
    executionDTO.setFailureType("NETWORK_ERROR");
    executionDTO.setHttpResponseStatus(-1);
    executionDTO.setHttpResponseBody(null);
    executionDTO.setHttpResponseHeaders(null);
    
    // 记录网络错误日志
    String networkErrorLog = generateNetworkErrorLog(executionDTO, requestInfo, response);
    executionDTO.setExecutionLogs(networkErrorLog);
    
    return executionDTO;
}
```

**新增方法**:
```java
/**
 * 生成网络错误日志
 */
private String generateNetworkErrorLog(TestCaseExecutionDTO executionDTO, HttpRequestInfo requestInfo, 
                                     HttpClientUtils.HttpResponseResult response) {
    StringBuilder logs = new StringBuilder();
    logs.append("=== 网络错误执行日志 ===\n");
    logs.append("用例ID: ").append(executionDTO.getCaseId()).append("\n");
    logs.append("用例名称: ").append(executionDTO.getName()).append("\n");
    logs.append("执行时间: ").append(executionDTO.getExecutionStartTime()).append("\n");
    logs.append("请求方法: ").append(requestInfo.getMethod()).append("\n");
    logs.append("请求URL: ").append(requestInfo.getUrl()).append("\n");
    logs.append("超时设置: ").append(requestInfo.getTimeout()).append("秒\n");
    logs.append("错误类型: 网络连接失败\n");
    logs.append("错误信息: ").append(response.getErrorMessage()).append("\n");
    logs.append("执行状态: ").append(executionDTO.getExecutionStatus()).append("\n");
    logs.append("失败原因: 无法连接到目标服务器或请求超时\n");
    
    return logs.toString();
}
```

### 3. 改进 TestExecutionServiceImpl 异常处理
**文件**: `src/main/java/com/victor/iatms/service/impl/TestExecutionServiceImpl.java`

**改进内容**:
- 将执行异常转换为测试失败结果，而不是抛出异常
- 确保前端接收到的是结构化的测试结果，而不是错误信息

**修改前**:
```java
} catch (Exception e) {
    log.error("执行测试用例失败: {}", e.getMessage(), e);
    // 更新执行记录为失败
    if (executionRecord != null) {
        updateExecutionRecordOnFailure(executionRecord, e.getMessage());
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
    }
    throw new RuntimeException("执行测试用例失败: " + e.getMessage());
}
```

**修改后**:
```java
} catch (Exception e) {
    log.error("执行测试用例失败: {}", e.getMessage(), e);
    // 更新执行记录为失败
    if (executionRecord != null) {
        updateExecutionRecordOnFailure(executionRecord, e.getMessage());
        testExecutionRecordMapper.updateExecutionRecord(executionRecord);
    }
    
    // 构建失败的执行结果，而不是直接抛出异常
    ExecutionResultDTO failureResult = new ExecutionResultDTO();
    failureResult.setExecutionId(generateExecutionId());
    failureResult.setCaseId(caseId);
    failureResult.setCaseName(executionDTO != null ? executionDTO.getName() : "未知用例");
    failureResult.setStatus(ExecutionStatusEnum.FAILED.getCode());
    failureResult.setFailureMessage("执行失败: " + e.getMessage());
    failureResult.setFailureType("EXECUTION_ERROR");
    failureResult.setStartTime(LocalDateTime.now());
    failureResult.setEndTime(LocalDateTime.now());
    failureResult.setDuration(0L);
    failureResult.setLogsLink("/api/test-results/" + failureResult.getExecutionId() + "/logs");
    
    return failureResult;
}
```

## 改进效果

### 1. 网络错误处理
- **连接失败**: 当目标服务器不可达时，测试标记为失败，错误类型为 `NETWORK_ERROR`
- **请求超时**: 当请求超过设定时间时，测试标记为失败，错误类型为 `NETWORK_ERROR`
- **未知主机**: 当DNS解析失败时，测试标记为失败，错误类型为 `NETWORK_ERROR`

### 2. 测试结果一致性
- 所有网络相关的错误都被统一处理为测试失败
- 前端接收到的始终是结构化的测试结果
- 测试报告能够正确统计网络错误导致的失败用例

### 3. 错误信息详细化
- 提供具体的错误类型和描述
- 记录详细的网络错误日志
- 便于问题排查和调试

## 测试场景

### 场景1: 服务器不可达
```
请求URL: http://unreachable-server.com/api/test
结果: 测试失败
状态: FAILED
错误类型: NETWORK_ERROR
错误信息: 连接失败: Connection refused
```

### 场景2: 请求超时
```
请求URL: http://slow-server.com/api/test
超时设置: 5秒
结果: 测试失败
状态: FAILED
错误类型: NETWORK_ERROR
错误信息: 请求超时: Read timed out
```

### 场景3: DNS解析失败
```
请求URL: http://nonexistent-domain.com/api/test
结果: 测试失败
状态: FAILED
错误类型: NETWORK_ERROR
错误信息: 未知主机: nonexistent-domain.com
```

## 注意事项

1. **超时设置**: 建议根据接口特性合理设置超时时间，避免过短导致误报
2. **重试机制**: 对于网络不稳定的环境，可以考虑添加重试机制
3. **监控告警**: 网络错误率过高时，应该触发监控告警
4. **日志记录**: 网络错误日志应该被妥善保存，便于后续分析

---

**修改时间**: 2025-10-22
**状态**: ✅ 已完成
**影响范围**: 测试执行模块、HTTP客户端工具类
