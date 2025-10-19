package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * 添加项目请求DTO
 */
@Data
public class AddProjectDTO {
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目描述
     */
    private String description;
}
