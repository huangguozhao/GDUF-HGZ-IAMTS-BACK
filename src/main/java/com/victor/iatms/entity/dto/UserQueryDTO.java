package com.victor.iatms.entity.dto;

import lombok.Data;

/**
 * @description: 用户查询DTO
 * @author: victor
 * @date: 2025-12-07
 */
@Data
public class UserQueryDTO {

    /**
     * 用户姓名模糊查询
     */
    private String name;

    /**
     * 用户邮箱模糊查询
     */
    private String email;

    /**
     * 分页查询的页码，默认为 1
     */
    private Integer page = 1;

    /**
     * 分页查询的每页记录数，默认为 10
     */
    private Integer pageSize = 10;

    private Integer offset;

    /**
     * 状态筛选（active/pending/inactive）
     */
    private String status;

    /**
     * 角色/职位筛选
     */
    private String position;

    /**
     * 创建时间开始日期
     */
    private String startDate;

    /**
     * 创建时间结束日期
     */
    private String endDate;
}
