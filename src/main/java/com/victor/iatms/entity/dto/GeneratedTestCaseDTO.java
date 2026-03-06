package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GeneratedTestCaseDTO {

    private String name;

    private String description;

    private String testType;

    private String priority;

    private String severity;

    private List<String> tags;

    private PreConditionsDTO preConditions;

    private List<TestStepDTO> testSteps;

    private Integer expectedHttpStatus;

    private Object expectedResponseBody;

    private List<AssertionDTO> assertions;

    private String requestOverride;

    private String extractors;

    private Boolean selected = true;

    @Data
    public static class TestStepDTO {
        private Integer step;
        private String action;
        private String expected;
    }

    @Data
    public static class AssertionDTO {
        private String type;
        private String path;
        private Object expected;
        private String description;
    }
}
