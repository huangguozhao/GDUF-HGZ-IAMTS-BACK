package com.victor.iatms.entity.query;

import lombok.Data;

/**
 * 环境配置查询参数
 */
@Data
public class EnvironmentConfigQuery {
    
    private String envType;
    private String status;
    private String searchKeyword;
    private Boolean isDefault;
    private String sortBy;
    private String sortOrder;
    private Integer page;
    private Integer pageSize;
    private Integer offset;
}

