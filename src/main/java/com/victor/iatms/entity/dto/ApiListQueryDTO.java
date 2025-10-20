package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 接口列表查询DTO
 */
@Data
public class ApiListQueryDTO {

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 请求方法过滤
     */
    private String method;

    /**
     * 接口状态过滤
     */
    private String status;

    /**
     * 标签过滤
     */
    private List<String> tags;

    /**
     * 认证类型过滤
     */
    private String authType;

    /**
     * 关键字搜索
     */
    private String searchKeyword;

    /**
     * 是否包含已删除的接口
     */
    private Boolean includeDeleted;

    /**
     * 是否包含统计信息
     */
    private Boolean includeStatistics;

    /**
     * 排序字段
     */
    private String sortBy;

    /**
     * 排序顺序
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize;
    
    /**
     * 分页偏移量
     */
    private Integer offset;
}
