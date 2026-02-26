-- =====================================================
-- 项目访问日志和活动日志表
-- 用于支持"最近编辑项目"功能
-- =====================================================

-- 项目访问日志表
CREATE TABLE IF NOT EXISTS ProjectAccessLogs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    project_id INT NOT NULL COMMENT '项目ID',
    user_id INT NOT NULL COMMENT '访问用户ID',
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    access_type VARCHAR(50) DEFAULT 'view' COMMENT '访问类型: view(查看), edit(编辑), execute(执行)',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    is_deleted TINYINT(1) DEFAULT FALSE COMMENT '是否删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted_at DATETIME COMMENT '删除时间',
    deleted_by INT COMMENT '删除人ID',
    INDEX idx_project_user (project_id, user_id),
    INDEX idx_access_time (access_time),
    INDEX idx_user_time (user_id, access_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目访问日志表';

-- 项目活动日志表
CREATE TABLE IF NOT EXISTS ProjectActivityLogs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    project_id INT NOT NULL COMMENT '项目ID',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型: create(创建), update(更新), delete(删除), execute(执行), share(分享)',
    activity_description VARCHAR(500) COMMENT '活动描述',
    activity_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '活动发生时间',
    activity_user_id INT COMMENT '操作用户ID',
    activity_user_name VARCHAR(100) COMMENT '操作用户姓名',
    target_type VARCHAR(50) COMMENT '目标类型: module(模块), api(接口), case(用例), report(报告)',
    target_id INT COMMENT '目标ID',
    target_name VARCHAR(255) COMMENT '目标名称',
    changes_json TEXT COMMENT '变更内容JSON',
    is_deleted TINYINT(1) DEFAULT FALSE COMMENT '是否删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted_at DATETIME COMMENT '删除时间',
    deleted_by INT COMMENT '删除人ID',
    INDEX idx_project_timestamp (project_id, activity_timestamp),
    INDEX idx_user_timestamp (activity_user_id, activity_timestamp),
    INDEX idx_activity_type (activity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目活动日志表';

-- 初始化一些示例数据（可选）
-- 为现有项目添加一些默认访问记录，确保首页能显示项目
-- INSERT INTO ProjectAccessLogs (project_id, user_id, access_type, access_time)
-- SELECT project_id, creator_id, 'view', updated_at
-- FROM Projects 
-- WHERE is_deleted = FALSE AND creator_id IS NOT NULL;

