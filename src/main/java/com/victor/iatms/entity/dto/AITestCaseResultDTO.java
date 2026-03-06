package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AITestCaseResultDTO {

    private Long generationId;

    private String status;

    private List<GeneratedTestCaseDTO> cases;

    private Integer caseCount;

    private String errorMessage;

    private Integer promptTokens;

    private Integer completionTokens;
}
