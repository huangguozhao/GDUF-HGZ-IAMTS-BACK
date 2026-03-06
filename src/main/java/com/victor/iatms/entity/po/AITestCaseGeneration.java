package com.victor.iatms.entity.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AITestCaseGeneration {

    private Long generationId;

    private Integer projectId;

    private Integer apiId;

    private Integer userId;

    private String generationType;

    private String inputContext;

    private String generationConfig;

    private String status;

    private String generatedCases;

    private Integer caseCount;

    private Integer promptTokens;

    private Integer completionTokens;

    private BigDecimal totalCost;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private String errorMessage;
}
