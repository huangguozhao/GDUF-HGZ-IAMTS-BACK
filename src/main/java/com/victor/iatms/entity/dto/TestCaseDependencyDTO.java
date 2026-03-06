package com.victor.iatms.entity.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TestCaseDependencyDTO {

    private Integer caseId;

    private String caseCode;

    private String caseName;

    private Integer executionOrder;

    private List<Integer> dependsOn;

    private List<String> requiredVariables;

    private List<String> providedVariables;

    private DependencyLevel dependencyLevel;

    public enum DependencyLevel {
        INDEPENDENT,
        DEPENDENT,
        PROVIDER
    }

    @Data
    public static class DependencyGraph {
        private List<TestCaseDependencyDTO> nodes;
        private List<DependencyEdge> edges;
        private List<List<Integer>> executionLayers;
    }

    @Data
    public static class DependencyEdge {
        private Integer fromCaseId;
        private Integer toCaseId;
        private List<String> variables;
    }
}
