package com.victor.iatms.service;

import java.util.Map;

public interface VariablePoolService {

    void initializePool(String executionId);

    void setVariable(String executionId, String name, Object value);

    Object getVariable(String executionId, String name);

    Map<String, Object> getAllVariables(String executionId);

    void setVariables(String executionId, Map<String, Object> variables);

    boolean hasVariable(String executionId, String name);

    void clearPool(String executionId);

    void cleanupExpiredPools();

    String applyVariables(String executionId, String template);

    Map<String, Object> getVariablesByPrefix(String executionId, String prefix);
}
