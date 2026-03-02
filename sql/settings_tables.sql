-- =============================================
-- 系统设置相关表结构
-- =============================================

-- 1. 系统基本设置表
CREATE TABLE IF NOT EXISTS `system_basic_settings` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `system_name` VARCHAR(100) DEFAULT '接口自动化管理系统' COMMENT '系统名称',
  `system_version` VARCHAR(50) DEFAULT 'V2.5.3' COMMENT '系统版本',
  `system_id` VARCHAR(100) DEFAULT '' COMMENT '系统ID',
  `system_description` TEXT COMMENT '系统描述',
  `deployment_mode` VARCHAR(20) DEFAULT 'private' COMMENT '部署模式: private-私有化部署, cloud-云端部署, hybrid-混合部署',
  `theme_color` VARCHAR(20) DEFAULT 'blue' COMMENT '主题色: blue-蓝色, green-绿色, purple-紫色, orange-橙色',
  `custom_color` VARCHAR(20) DEFAULT '#2C6FD1' COMMENT '自定义主题色',
  `layout` VARCHAR(20) DEFAULT 'sidebar' COMMENT '布局: sidebar-侧边布局, top-顶部布局, right-右侧布局',
  `table_density` VARCHAR(20) DEFAULT 'standard' COMMENT '表格密度: compact-紧凑, standard-标准, loose-宽松',
  `language` VARCHAR(20) DEFAULT 'zh-CN' COMMENT '语言: zh-CN-简体中文, en-US-English, zh-TW-繁體中文',
  `theme_mode` VARCHAR(20) DEFAULT 'light' COMMENT '主题模式: light-浅色, dark-深色, auto-跟随系统',
  `animation_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用动画',
  `default_homepage` VARCHAR(50) DEFAULT 'dashboard' COMMENT '默认首页',
  `default_project_view` VARCHAR(20) DEFAULT 'list' COMMENT '默认项目视图: list-列表, card-卡片, tree-树形',
  `default_page_size` INT DEFAULT 10 COMMENT '默认分页大小',
  `time_format` VARCHAR(50) DEFAULT 'YYYY-MM-DD HH:mm:ss' COMMENT '时间格式',
  `auto_save` TINYINT(1) DEFAULT 1 COMMENT '是否启用自动保存',
  `show_welcome_page` TINYINT(1) DEFAULT 1 COMMENT '是否显示欢迎页',
  `remember_tabs` TINYINT(1) DEFAULT 1 COMMENT '是否记住标签页',
  `session_timeout` INT DEFAULT 30 COMMENT '会话超时时间(分钟)',
  `max_failures` INT DEFAULT 5 COMMENT '登录失败锁定次数',
  `lockout_duration` INT DEFAULT 30 COMMENT '账户锁定时长(分钟)',
  `two_factor_auth` TINYINT(1) DEFAULT 1 COMMENT '是否启用双因素认证',
  `ip_restriction` VARCHAR(20) DEFAULT 'off' COMMENT '登录IP限制: off-关闭, whitelist-白名单',
  `password_policy` VARCHAR(200) DEFAULT '高强度 (至少8位,包含大小写字母、数字和特殊字符)' COMMENT '密码策略',
  `data_retention` VARCHAR(20) DEFAULT '30天' COMMENT '数据保留周期',
  `backup_strategy` VARCHAR(20) DEFAULT 'daily' COMMENT '备份策略: daily-每日, weekly-每周, monthly-每月',
  `log_level` VARCHAR(20) DEFAULT 'debug' COMMENT '日志级别: debug, info, warn, error',
  `log_path` VARCHAR(200) DEFAULT '/var/log/apiops/' COMMENT '日志存储路径',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统基本设置表';

-- 插入默认数据
INSERT INTO `system_basic_settings` (
  system_name, system_version, system_id, system_description, deployment_mode,
  theme_color, custom_color, layout, table_density, language, theme_mode,
  animation_enabled, default_homepage, default_project_view, default_page_size,
  time_format, auto_save, show_welcome_page, remember_tabs, session_timeout,
  max_failures, lockout_duration, two_factor_auth, ip_restriction, password_policy,
  data_retention, backup_strategy, log_level, log_path
) VALUES (
  '接口自动化管理系统', 'V2.5.3', 'API-OPS-202405-1234', '接口自动化管理系统是一个高效的API测试和管理工具', 'private',
  'blue', '#2C6FD1', 'sidebar', 'standard', 'zh-CN', 'light',
  1, 'dashboard', 'list', 10,
  'YYYY-MM-DD HH:mm:ss', 1, 1, 1, 30,
  5, 30, 1, 'off', '高强度 (至少8位,包含大小写字母、数字和特殊字符)',
  '30天', 'daily', 'debug', '/var/log/apiops/'
);

-- 2. 通知设置表
CREATE TABLE IF NOT EXISTS `notification_settings` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `email_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用邮件通知',
  `sms_enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用短信通知',
  `system_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用系统通知',
  `email_host` VARCHAR(100) DEFAULT '' COMMENT '邮件服务器主机',
  `email_port` INT DEFAULT 587 COMMENT '邮件服务器端口',
  `email_username` VARCHAR(100) DEFAULT '' COMMENT '邮件用户名',
  `email_password` VARCHAR(200) DEFAULT '' COMMENT '邮件密码',
  `email_ssl` TINYINT(1) DEFAULT 1 COMMENT '是否启用SSL',
  `email_from` VARCHAR(100) DEFAULT '' COMMENT '邮件发件人',
  `sms_provider` VARCHAR(20) DEFAULT 'aliyun' COMMENT '短信服务商: aliyun-阿里云, tencent-腾讯云, huawei-华为云',
  `sms_access_key` VARCHAR(200) DEFAULT '' COMMENT '短信AccessKey',
  `sms_secret_key` VARCHAR(200) DEFAULT '' COMMENT '短信SecretKey',
  `sms_sign_name` VARCHAR(50) DEFAULT '' COMMENT '短信签名',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知设置表';

-- 插入默认数据
INSERT INTO `notification_settings` (
  email_enabled, sms_enabled, system_enabled,
  email_host, email_port, email_username, email_password, email_ssl, email_from,
  sms_provider, sms_access_key, sms_secret_key, sms_sign_name
) VALUES (
  1, 0, 1,
  '', 587, '', '', 1, '',
  'aliyun', '', '', ''
);

-- 3. 集成设置表
CREATE TABLE IF NOT EXISTS `integration_settings` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `integration_type` VARCHAR(50) NOT NULL COMMENT '集成类型: api-接口集成, webhook-Webhook, jenkins-Jenkins, git-Git',
  `service_name` VARCHAR(100) DEFAULT '' COMMENT '集成服务名称',
  `enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用',
  `api_version` VARCHAR(20) DEFAULT 'v2.0' COMMENT 'API版本',
  `request_timeout` INT DEFAULT 30 COMMENT '请求超时时间(秒)',
  `max_retries` INT DEFAULT 3 COMMENT '最大重试次数',
  `auth_type` VARCHAR(20) DEFAULT 'none' COMMENT '认证类型: none-无, basic-Basic, bearer-Bearer Token, oauth2-OAuth2',
  `auth_token` VARCHAR(500) DEFAULT '' COMMENT '认证令牌',
  `username` VARCHAR(100) DEFAULT '' COMMENT '用户名(Basic认证用)',
  `password` VARCHAR(200) DEFAULT '' COMMENT '密码(Basic认证用)',
  `response_format` VARCHAR(20) DEFAULT 'json' COMMENT '响应格式: json, xml, text',
  `webhook_url` VARCHAR(500) DEFAULT '' COMMENT 'Webhook URL',
  `webhook_secret` VARCHAR(200) DEFAULT '' COMMENT 'Webhook密钥',
  `jenkins_url` VARCHAR(500) DEFAULT '' COMMENT 'Jenkins地址',
  `jenkins_username` VARCHAR(100) DEFAULT '' COMMENT 'Jenkins用户名',
  `jenkins_token` VARCHAR(200) DEFAULT '' COMMENT 'Jenkins API Token',
  `git_url` VARCHAR(500) DEFAULT '' COMMENT 'Git仓库地址',
  `git_branch` VARCHAR(100) DEFAULT 'main' COMMENT 'Git分支',
  `git_username` VARCHAR(100) DEFAULT '' COMMENT 'Git用户名',
  `git_token` VARCHAR(200) DEFAULT '' COMMENT 'Git Token',
  `extra_config` TEXT COMMENT '其他配置(JSON格式)',
  `description` VARCHAR(500) DEFAULT '' COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `uk_integration_type` (`integration_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集成设置表';

