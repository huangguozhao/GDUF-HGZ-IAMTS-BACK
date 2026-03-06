package com.victor.iatms.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class PreConditionsDTO {

    private String description;

    private List<VariableDependencyDTO> requiredVariables;

    private Integer executionOrder;

    private List<String> dependencies;

    private EnvironmentRequirements environmentRequirements;

    @Data
    public static class EnvironmentRequirements {
        private Boolean authServiceAvailable;
        private Boolean databaseAccessible;
        private String requiredEnv;
        private List<String> requiredServices;
    }
}
