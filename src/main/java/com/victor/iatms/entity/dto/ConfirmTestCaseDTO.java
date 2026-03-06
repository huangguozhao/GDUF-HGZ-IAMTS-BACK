package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConfirmTestCaseDTO {

    private Long generationId;

    private Integer apiId;

    private List<GeneratedTestCaseDTO> cases;
}
