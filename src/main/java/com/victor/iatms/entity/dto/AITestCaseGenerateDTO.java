package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AITestCaseGenerateDTO {

    private Integer apiId;

    private List<Integer> apiIds;

    private String requirement;

    private String generationType;

    private Integer maxCases = 5;

    private List<String> testScenarios;

    private Boolean includeNegative = true;

    private Boolean includeBoundary = true;

    private String priority;

    private Boolean includeSecurity = false;
}
