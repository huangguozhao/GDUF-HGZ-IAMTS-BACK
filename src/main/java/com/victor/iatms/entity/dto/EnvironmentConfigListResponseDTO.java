package com.victor.iatms.entity.dto;

import lombok.Data;
import java.util.List;

/**
 * 环境配置列表响应DTO
 */
@Data
public class EnvironmentConfigListResponseDTO {
    
    private Long total;
    private List<EnvironmentConfigDTO> items;
    private Integer page;
    private Integer pageSize;
}

