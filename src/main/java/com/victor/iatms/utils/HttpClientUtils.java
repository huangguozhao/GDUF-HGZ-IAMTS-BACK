package com.victor.iatms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端工具类
 */
@Slf4j
@Component
public class HttpClientUtils {

    /**
     * 发送HTTP请求
     * @param method HTTP方法
     * @param url 请求URL
     * @param headers 请求头
     * @param body 请求体
     * @param timeout 超时时间（秒）
     * @return HTTP响应结果
     */
    public HttpResponseResult sendRequest(String method, String url, Map<String, String> headers, 
                                         String body, int timeout) {
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod(method.toUpperCase());
            
            // 设置超时时间
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(timeout));
            
            // 设置请求头
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // 设置请求体
            if (body != null && !body.isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            String responseBody = getResponseBody(connection);
            Map<String, String> responseHeaders = getResponseHeaders(connection);
            
            return new HttpResponseResult(responseCode, responseBody, responseHeaders, null);
            
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
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取响应体
     */
    private String getResponseBody(HttpURLConnection connection) throws IOException {
        BufferedReader reader;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }
        reader.close();
        
        return response.toString();
    }

    /**
     * 获取响应头
     */
    private Map<String, String> getResponseHeaders(HttpURLConnection connection) {
        Map<String, String> headers = new java.util.HashMap<>();
        for (Map.Entry<String, java.util.List<String>> entry : connection.getHeaderFields().entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                headers.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return headers;
    }

    /**
     * HTTP响应结果类
     */
    public static class HttpResponseResult {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;
        private final String errorMessage;

        public HttpResponseResult(int statusCode, String body, Map<String, String> headers, String errorMessage) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
            this.errorMessage = errorMessage;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean hasError() {
            return errorMessage != null;
        }
    }
}
