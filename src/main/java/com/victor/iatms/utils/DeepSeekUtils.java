package com.victor.iatms.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.victor.iatms.config.DeepSeekConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * DeepSeek API调用工具类
 */
@Slf4j
@Component
public class DeepSeekUtils {

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(30000))
            .build();

    /**
     * 调用DeepSeek API进行对话
     * @param messages 消息列表
     * @return AI回复内容
     */
    public String chat(List<Map<String, String>> messages) {
        return chat(messages, null);
    }

    /**
     * 调用DeepSeek API进行对话
     * @param messages 消息列表
     * @param systemPrompt 系统提示词(可选)
     * @return AI回复内容
     */
    public String chat(List<Map<String, String>> messages, String systemPrompt) {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.put("max_tokens", deepSeekConfig.getMaxTokens());
            requestBody.put("temperature", 0.7);

            // 构建消息数组
            JSONArray messageArray = new JSONArray();

            // 添加系统提示词
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messageArray.add(systemMessage);
            }

            // 添加用户消息
            for (Map<String, String> message : messages) {
                JSONObject msg = new JSONObject();
                msg.put("role", message.get("role"));
                msg.put("content", message.get("content"));
                messageArray.add(msg);
            }

            requestBody.put("messages", messageArray);

            // 构建请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deepSeekConfig.getBaseUrl() + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + deepSeekConfig.getKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                    .timeout(Duration.ofMillis(deepSeekConfig.getTimeout()))
                    .build();

            // 发送请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析响应
            if (response.statusCode() == 200) {
                JSONObject responseJson = JSON.parseObject(response.body());
                JSONArray choices = responseJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    return message.getString("content");
                }
            } else {
                log.error("DeepSeek API调用失败: {}, {}", response.statusCode(), response.body());
            }

            return null;
        } catch (Exception e) {
            log.error("DeepSeek API调用异常: ", e);
            return null;
        }
    }

    /**
     * 简单的单轮对话
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String chat(String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);
        messages.add(message);
        return chat(messages);
    }

    /**
     * 简单的单轮对话(带系统提示)
     * @param systemPrompt 系统提示
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String chat(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);
        messages.add(message);
        return chat(messages, systemPrompt);
    }
}

