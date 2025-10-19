package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 测试结果详情DTO
 */
@Data
public class TestResultDetailDTO {

    /**
     * 结果基本信息
     */
    @JsonProperty("result_info")
    private TestResultInfoDTO resultInfo;

    /**
     * 执行上下文信息
     */
    @JsonProperty("execution_context")
    private ExecutionContextDTO executionContext;

    /**
     * 测试步骤执行详情
     */
    @JsonProperty("test_steps")
    private List<TestStepDTO> testSteps;

    /**
     * 断言结果详情
     */
    private List<AssertionDTO> assertions;

    /**
     * 附件信息
     */
    private List<ArtifactDTO> artifacts;

    /**
     * 环境配置信息
     */
    private EnvironmentInfoDTO environment;

    /**
     * 性能指标
     */
    private PerformanceDTO performance;
}

