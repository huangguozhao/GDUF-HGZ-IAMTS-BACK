package com.victor.iatms.service.impl;

import com.victor.iatms.service.VariablePoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VariablePoolServiceImpl implements VariablePoolService {

    private static final String VARIABLE_PATTERN = "\\{\\{([^}]+)\\}\\}";
    private static final Pattern pattern = Pattern.compile(VARIABLE_PATTERN);

    private static final int MAX_POOL_SIZE = 100;
    private static final int POOL_EXPIRE_MINUTES = 60;

    private final Map<String, Map<String, Object>> variablePools = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> poolCreationTimes = new ConcurrentHashMap<>();

    @Override
    public void initializePool(String executionId) {
        cleanupExpiredPools();
        
        if (variablePools.size() >= MAX_POOL_SIZE) {
            String oldestKey = poolCreationTimes.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
            if (oldestKey != null) {
                clearPool(oldestKey);
            }
        }
        
        variablePools.put(executionId, new ConcurrentHashMap<>());
        poolCreationTimes.put(executionId, LocalDateTime.now());
        
        log.info("初始化变量池: executionId={}", executionId);
    }

    @Override
    public void setVariable(String executionId, String name, Object value) {
        Map<String, Object> pool = variablePools.get(executionId);
        if (pool == null) {
            log.warn("变量池不存在，自动创建: executionId={}", executionId);
            initializePool(executionId);
            pool = variablePools.get(executionId);
        }
        pool.put(name, value);
        log.debug("设置变量: executionId={}, name={}, value={}", executionId, name, value);
    }

    @Override
    public Object getVariable(String executionId, String name) {
        Map<String, Object> pool = variablePools.get(executionId);
        if (pool == null) {
            log.warn("变量池不存在: executionId={}", executionId);
            return null;
        }
        return pool.get(name);
    }

    @Override
    public Map<String, Object> getAllVariables(String executionId) {
        Map<String, Object> pool = variablePools.get(executionId);
        if (pool == null) {
            return new HashMap<>();
        }
        return new HashMap<>(pool);
    }

    @Override
    public void setVariables(String executionId, Map<String, Object> variables) {
        Map<String, Object> pool = variablePools.get(executionId);
        if (pool == null) {
            log.warn("变量池不存在，自动创建: executionId={}", executionId);
            initializePool(executionId);
            pool = variablePools.get(executionId);
        }
        if (variables != null) {
            pool.putAll(variables);
            log.debug("批量设置变量: executionId={}, count={}", executionId, variables.size());
        }
    }

    @Override
    public boolean hasVariable(String executionId, String name) {
        Map<String, Object> pool = variablePools.get(executionId);
        return pool != null && pool.containsKey(name);
    }

    @Override
    public void clearPool(String executionId) {
        variablePools.remove(executionId);
        poolCreationTimes.remove(executionId);
        log.info("清理变量池: executionId={}", executionId);
    }

    @Override
    public void cleanupExpiredPools() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(POOL_EXPIRE_MINUTES);
        
        List<String> expiredPools = new ArrayList<>();
        poolCreationTimes.forEach((executionId, creationTime) -> {
            if (creationTime.isBefore(expireTime)) {
                expiredPools.add(executionId);
            }
        });
        
        for (String executionId : expiredPools) {
            clearPool(executionId);
            log.info("清理过期变量池: executionId={}", executionId);
        }
    }

    @Override
    public String applyVariables(String executionId, String template) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        
        Map<String, Object> pool = variablePools.get(executionId);
        if (pool == null || pool.isEmpty()) {
            return template;
        }
        
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = pool.get(variableName);
            
            if (value != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(String.valueOf(value)));
            } else {
                log.warn("变量未找到: executionId={}, variableName={}", executionId, variableName);
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    @Override
    public Map<String, Object> getVariablesByPrefix(String executionId, String prefix) {
        Map<String, Object> pool = variablePools.get(executionId);
        if (pool == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        pool.forEach((key, value) -> {
            if (key.startsWith(prefix)) {
                result.put(key, value);
            }
        });
        
        return result;
    }
}
