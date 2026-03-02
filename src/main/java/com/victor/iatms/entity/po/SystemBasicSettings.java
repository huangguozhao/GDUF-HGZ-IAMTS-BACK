package com.victor.iatms.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统基本设置实体类
 */
@Data
public class SystemBasicSettings {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 系统名称
     */
    private String systemName;
    
    /**
     * 系统版本
     */
    private String systemVersion;
    
    /**
     * 系统ID
     */
    private String systemId;
    
    /**
     * 系统描述
     */
    private String systemDescription;
    
    /**
     * 部署模式: private-私有化部署, cloud-云端部署, hybrid-混合部署
     */
    private String deploymentMode;
    
    /**
     * 主题色: blue-蓝色, green-绿色, purple-紫色, orange-橙色
     */
    private String themeColor;
    
    /**
     * 自定义主题色
     */
    private String customColor;
    
    /**
     * 布局: sidebar-侧边布局, top-顶部布局, right-右侧布局
     */
    private String layout;
    
    /**
     * 表格密度: compact-紧凑, standard-标准, loose-宽松
     */
    private String tableDensity;
    
    /**
     * 语言: zh-CN-简体中文, en-US-English, zh-TW-繁體中文
     */
    private String language;
    
    /**
     * 主题模式: light-浅色, dark-深色, auto-跟随系统
     */
    private String themeMode;
    
    /**
     * 是否启用动画
     */
    private Boolean animationEnabled;
    
    /**
     * 默认首页
     */
    private String defaultHomepage;
    
    /**
     * 默认项目视图: list-列表, card-卡片, tree-树形
     */
    private String defaultProjectView;
    
    /**
     * 默认分页大小
     */
    private Integer defaultPageSize;
    
    /**
     * 时间格式
     */
    private String timeFormat;
    
    /**
     * 是否启用自动保存
     */
    private Boolean autoSave;
    
    /**
     * 是否显示欢迎页
     */
    private Boolean showWelcomePage;
    
    /**
     * 是否记住标签页
     */
    private Boolean rememberTabs;
    
    /**
     * 会话超时时间(分钟)
     */
    private Integer sessionTimeout;
    
    /**
     * 登录失败锁定次数
     */
    private Integer maxFailures;
    
    /**
     * 账户锁定时长(分钟)
     */
    private Integer lockoutDuration;
    
    /**
     * 是否启用双因素认证
     */
    private Boolean twoFactorAuth;
    
    /**
     * 登录IP限制: off-关闭, whitelist-白名单
     */
    private String ipRestriction;
    
    /**
     * 密码策略
     */
    private String passwordPolicy;
    
    /**
     * 数据保留周期(天)
     */
    private String dataRetention;
    
    /**
     * 备份策略: daily-每日, weekly-每周, monthly-每月
     */
    private String backupStrategy;
    
    /**
     * 日志级别: debug, info, warn, error
     */
    private String logLevel;
    
    /**
     * 日志存储路径
     */
    private String logPath;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

