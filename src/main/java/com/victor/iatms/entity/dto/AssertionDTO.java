package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 断言结果DTO
 */
@Data
public class AssertionDTO {

    /**
     * 断言ID
     */
    @JsonProperty("assertion_id")
    private Integer assertionId;

    /**
     * 断言类型
     */
    private String type;

    /**
     * 断言表达式
     */
    private String expression;

    /**
     * 预期值
     */
    private Object expected;

    /**
     * 实际值
     */
    private Object actual;

    /**
     * 断言状态
     */
    private String status;

    /**
     * 断言消息
     */
    private String message;
}

