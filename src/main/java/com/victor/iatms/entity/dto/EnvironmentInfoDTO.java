package com.victor.iatms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 环境信息DTO
 */
@Data
public class EnvironmentInfoDTO {

    /**
     * 浏览器信息
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备信息
     */
    private String device;

    /**
     * 屏幕分辨率
     */
    @JsonProperty("screen_resolution")
    private String screenResolution;

    /**
     * 语言
     */
    private String language;

    /**
     * 时区
     */
    private String timezone;
}

