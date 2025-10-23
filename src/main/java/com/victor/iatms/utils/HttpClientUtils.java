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
            // 连接被拒绝 - 被测系统未启动或端口不可达
            String errorMsg = String.format("连接被拒绝: 无法连接到 %s，请检查被测系统是否启动", url);
            log.error(errorMsg, e);
            return new HttpResponseResult(-1, null, null, errorMsg);
        } catch (java.net.SocketTimeoutException e) {
            // 请求超时 - 被测系统响应缓慢或网络延迟
            String errorMsg = String.format("请求超时: 等待响应超过%d秒，请检查被测系统性能或网络状态", timeout);
            log.error(errorMsg, e);
            return new HttpResponseResult(-2, null, null, errorMsg);
        } catch (java.net.UnknownHostException e) {
            // 未知主机 - 域名无法解析或主机不存在
            String errorMsg = String.format("未知主机: 无法解析主机名 '%s'，请检查URL配置", e.getMessage());
            log.error(errorMsg, e);
            return new HttpResponseResult(-3, null, null, errorMsg);
        } catch (java.net.MalformedURLException e) {
            // URL格式错误
            String errorMsg = String.format("URL格式错误: %s", e.getMessage());
            log.error(errorMsg, e);
            return new HttpResponseResult(-4, null, null, errorMsg);
        } catch (java.net.ProtocolException e) {
            // 协议错误 - HTTP方法不支持等
            String errorMsg = String.format("协议错误: %s", e.getMessage());
            log.error(errorMsg, e);
            return new HttpResponseResult(-5, null, null, errorMsg);
        } catch (javax.net.ssl.SSLException e) {
            // SSL证书错误
            String errorMsg = String.format("SSL证书错误: %s，请检查HTTPS配置", e.getMessage());
            log.error(errorMsg, e);
            return new HttpResponseResult(-6, null, null, errorMsg);
        } catch (IOException e) {
            // 其他IO错误
            String errorMsg = String.format("网络IO错误: %s", e.getMessage());
            log.error(errorMsg, e);
            return new HttpResponseResult(-7, null, null, errorMsg);
        } catch (Exception e) {
            // 其他未预期的错误
            String errorMsg = String.format("未知错误: %s", e.getMessage());
            log.error("发送HTTP请求时发生未预期的错误", e);
            return new HttpResponseResult(-99, null, null, errorMsg);
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
