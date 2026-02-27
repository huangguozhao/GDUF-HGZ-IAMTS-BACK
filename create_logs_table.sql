-- =====================================================
-- 用户操作日志表
-- 用于支持"最近活动"功能
-- =====================================================
CREATE TABLE IF NOT EXISTS Logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id INT NOT NULL COMMENT '操作用户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型: login(登录), logout(登出), create_project(创建项目), update_project(更新项目), delete_project(删除项目), execute_test(执行测试), create_case(创建用例), update_case(更新用例), delete_case(删除用例), create_task(创建任务), update_task(更新任务), generate_report(生成报告), share_project(分享项目)',
    target_id INT COMMENT '目标ID（项目ID/用例ID/任务ID等）',
    target_name VARCHAR(255) COMMENT '目标名称',
    target_type VARCHAR(50) COMMENT '目标类型: project(项目), case(用例), task(任务), report(报告)',
    description VARCHAR(500) COMMENT '操作描述',
    status VARCHAR(20) DEFAULT 'success' COMMENT '操作状态: success(成功), failed(失败)',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_path VARCHAR(255) COMMENT '请求路径',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    execution_time BIGINT COMMENT '执行耗时(毫秒)',
    error_message VARCHAR(1000) COMMENT '错误信息',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    is_deleted TINYINT(1) DEFAULT FALSE COMMENT '是否删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted_at DATETIME COMMENT '删除时间',
    INDEX idx_user_timestamp (user_id, timestamp),
    INDEX idx_operation_type (operation_type),
    INDEX idx_target (target_type, target_id),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作日志表';

