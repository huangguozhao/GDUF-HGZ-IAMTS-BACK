package com.victor.iatms.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Web请求日志切面
 * 用于记录所有Controller层的请求和响应信息
 */
@Aspect
@Component  // 临时注释掉，排查问题
public class WebLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 定义切点：拦截所有Controller包下的所有方法
     */
    @Pointcut("execution(public * com.victor.iatms.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 环绕通知：记录请求和响应的详细信息
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 构建请求日志信息
        RequestLog requestLog = buildRequestLog(joinPoint, request);
        
        // 打印请求信息
        printRequestLog(requestLog);
        
        Object result = null;
        Throwable exception = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 构建响应日志信息
            ResponseLog responseLog = buildResponseLog(result, exception, executionTime);
            
            // 打印响应信息
            printResponseLog(responseLog, requestLog.getRequestId());
        }
        
        return result;
    }

    /**
     * 构建请求日志信息
     */
    private RequestLog buildRequestLog(JoinPoint joinPoint, HttpServletRequest request) {
        RequestLog requestLog = new RequestLog();
        
        // 生成请求ID（用于关联请求和响应）
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        requestLog.setRequestId(requestId);
        
        // 请求基本信息
        requestLog.setUrl(request.getRequestURL().toString());
        requestLog.setUri(request.getRequestURI());
        requestLog.setHttpMethod(request.getMethod());
        requestLog.setIp(getIpAddress(request));
        requestLog.setClassMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        
        // 请求参数
        requestLog.setArgs(getRequestArgs(joinPoint));
        
        // 请求头
        requestLog.setHeaders(getRequestHeaders(request));
        
        // 查询参数
        requestLog.setQueryParams(getQueryParams(request));
        
        // User-Agent
        requestLog.setUserAgent(request.getHeader("User-Agent"));
        
        // Content-Type
        requestLog.setContentType(request.getContentType());
        
        return requestLog;
    }

    /**
     * 构建响应日志信息
     */
    private ResponseLog buildResponseLog(Object result, Throwable exception, long executionTime) {
        ResponseLog responseLog = new ResponseLog();
        
        responseLog.setExecutionTime(executionTime);
        
        if (exception != null) {
            responseLog.setSuccess(false);
            responseLog.setException(exception.getClass().getName());
            responseLog.setExceptionMessage(exception.getMessage());
        } else {
            responseLog.setSuccess(true);
            responseLog.setResult(result);
        }
        
        return responseLog;
    }

    /**
     * 打印请求日志
     */
    private void printRequestLog(RequestLog requestLog) {
        logger.info("================ HTTP Request Start ================");
        logger.info("Request ID    : {}", requestLog.getRequestId());
        logger.info("URL           : {}", requestLog.getUrl());
        logger.info("URI           : {}", requestLog.getUri());
        logger.info("HTTP Method   : {}", requestLog.getHttpMethod());
        logger.info("IP Address    : {}", requestLog.getIp());
        logger.info("Class Method  : {}", requestLog.getClassMethod());
        logger.info("Content-Type  : {}", requestLog.getContentType());
        
        // 打印请求头（只打印重要的）
        if (requestLog.getHeaders() != null && !requestLog.getHeaders().isEmpty()) {
            logger.info("Request Headers:");
            requestLog.getHeaders().forEach((key, value) -> 
                logger.info("  {} : {}", key, value));
        }
        
        // 打印查询参数
        if (requestLog.getQueryParams() != null && !requestLog.getQueryParams().isEmpty()) {
            logger.info("Query Parameters:");
            requestLog.getQueryParams().forEach((key, value) -> 
                logger.info("  {} : {}", key, value));
        }
        
        // 打印方法参数
        if (requestLog.getArgs() != null && !requestLog.getArgs().isEmpty()) {
            logger.info("Method Arguments:");
            requestLog.getArgs().forEach((key, value) -> 
                logger.info("  {} : {}", key, value));
        }
        
        logger.info("User-Agent    : {}", requestLog.getUserAgent());
        logger.info("====================================================");
    }

    /**
     * 打印响应日志
     */
    private void printResponseLog(ResponseLog responseLog, String requestId) {
        logger.info("================ HTTP Response Start ===============");
        logger.info("Request ID    : {}", requestId);
        logger.info("Success       : {}", responseLog.isSuccess());
        logger.info("Execution Time: {} ms", responseLog.getExecutionTime());
        
        if (responseLog.isSuccess()) {
            // 成功响应
            try {
                String resultJson = objectMapper.writeValueAsString(responseLog.getResult());
                // 如果结果太长，截断显示
                if (resultJson.length() > 1000) {
                    logger.info("Response      : {} ... (truncated, total {} chars)", 
                        resultJson.substring(0, 1000), resultJson.length());
                } else {
                    logger.info("Response      : {}", resultJson);
                }
            } catch (Exception e) {
                logger.info("Response      : {}", responseLog.getResult());
            }
        } else {
            // 异常响应
            logger.error("Exception Type: {}", responseLog.getException());
            logger.error("Exception Msg : {}", responseLog.getExceptionMessage());
        }
        
        logger.info("====================================================");
    }

    /**
     * 获取请求参数
     */
    private Map<String, Object> getRequestArgs(JoinPoint joinPoint) {
        Map<String, Object> args = new LinkedHashMap<>();
        
        String[] paramNames = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];
            
            // 过滤掉一些不需要记录的参数类型
            if (value instanceof HttpServletRequest ||
                value instanceof jakarta.servlet.http.HttpServletResponse ||
                value instanceof org.springframework.ui.Model ||
                value instanceof org.springframework.validation.BindingResult) {
                continue;
            }
            
            // 对MultipartFile特殊处理
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                args.put(paramNames[i], String.format("MultipartFile[name=%s, size=%d bytes]", 
                    file.getOriginalFilename(), file.getSize()));
            } else if (value instanceof MultipartFile[]) {
                MultipartFile[] files = (MultipartFile[]) value;
                args.put(paramNames[i], String.format("MultipartFile[count=%d]", files.length));
            } else {
                try {
                    // 尝试转换为JSON字符串
                    String jsonValue = objectMapper.writeValueAsString(value);
                    // 如果太长，截断
                    if (jsonValue.length() > 500) {
                        args.put(paramNames[i], jsonValue.substring(0, 500) + "... (truncated)");
                    } else {
                        args.put(paramNames[i], jsonValue);
                    }
                } catch (Exception e) {
                    args.put(paramNames[i], value != null ? value.toString() : "null");
                }
            }
        }
        
        return args;
    }

    /**
     * 获取请求头（只获取重要的）
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        
        // 只记录一些重要的请求头
        String[] importantHeaders = {
            "Authorization", "Content-Type", "Accept", 
            "Origin", "Referer", "X-Requested-With"
        };
        
        for (String headerName : importantHeaders) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                // 对Authorization进行脱敏处理
                if ("Authorization".equals(headerName) && headerValue.startsWith("Bearer ")) {
                    String token = headerValue.substring(7);
                    if (token.length() > 20) {
                        headers.put(headerName, "Bearer " + token.substring(0, 10) + "..." + token.substring(token.length() - 10));
                    } else {
                        headers.put(headerName, headerValue);
                    }
                } else {
                    headers.put(headerName, headerValue);
                }
            }
        }
        
        return headers;
    }

    /**
     * 获取查询参数
     */
    private Map<String, String> getQueryParams(HttpServletRequest request) {
        Map<String, String> params = new LinkedHashMap<>();
        
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(entry.getKey(), values.length == 1 ? values[0] : Arrays.toString(values));
            }
        }
        
        return params;
    }

    /**
     * 获取真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 请求日志对象
     */
    private static class RequestLog {
        private String requestId;
        private String url;
        private String uri;
        private String httpMethod;
        private String ip;
        private String classMethod;
        private Map<String, Object> args;
        private Map<String, String> headers;
        private Map<String, String> queryParams;
        private String userAgent;
        private String contentType;

        // Getters and Setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
        public String getHttpMethod() { return httpMethod; }
        public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getClassMethod() { return classMethod; }
        public void setClassMethod(String classMethod) { this.classMethod = classMethod; }
        public Map<String, Object> getArgs() { return args; }
        public void setArgs(Map<String, Object> args) { this.args = args; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
        public Map<String, String> getQueryParams() { return queryParams; }
        public void setQueryParams(Map<String, String> queryParams) { this.queryParams = queryParams; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }

    /**
     * 响应日志对象
     */
    private static class ResponseLog {
        private boolean success;
        private Object result;
        private String exception;
        private String exceptionMessage;
        private long executionTime;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        public String getException() { return exception; }
        public void setException(String exception) { this.exception = exception; }
        public String getExceptionMessage() { return exceptionMessage; }
        public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    }
}

