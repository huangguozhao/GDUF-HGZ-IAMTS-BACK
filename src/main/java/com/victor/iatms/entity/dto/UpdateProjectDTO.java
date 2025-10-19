package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 编辑项目请求DTO
 */
@Data
public class UpdateProjectDTO {
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
}
