-- 创建任务管理表
-- 用于存储待处理任务和高优先级问题

CREATE TABLE IF NOT EXISTS Tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    
    -- 任务基本信息
    task_type VARCHAR(50) NOT NULL DEFAULT 'test_case' COMMENT '任务类型: test_case(测试用例), bug_fix(缺陷修复), api_test(接口测试), performance(性能测试), manual(手动测试)',
    task_title VARCHAR(255) NOT NULL COMMENT '任务标题',
    task_description TEXT COMMENT '任务描述',
    
    -- 优先级设置
    priority INT DEFAULT 3 COMMENT '优先级: 1-紧急, 2-高, 3-中, 4-低',
    severity VARCHAR(20) DEFAULT 'medium' COMMENT '严重程度: critical, high, medium, low',
    
    -- 状态管理
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending(待处理), in_progress(进行中), completed(已完成), cancelled(已取消)',
    progress INT DEFAULT 0 COMMENT '进度百分比: 0-100',
    
    -- 截止日期
    due_date DATETIME COMMENT '截止日期',
    start_date DATETIME COMMENT '开始日期',
    completed_date DATETIME COMMENT '完成日期',
    
    -- 分配信息
    assignee_id INT COMMENT '被分配人ID',
    assignee_name VARCHAR(100) COMMENT '被分配人姓名',
    assigner_id INT COMMENT '分配人ID',
    assigner_name VARCHAR(100) COMMENT '分配人姓名',
    
    -- 关联信息
    project_id INT COMMENT '关联项目ID',
    project_name VARCHAR(255) COMMENT '关联项目名称',
    test_case_id INT COMMENT '关联测试用例ID',
    execution_id BIGINT COMMENT '关联执行记录ID',
    report_id BIGINT COMMENT '关联报告ID',
    
    -- 标签
    tags VARCHAR(500) COMMENT '标签, 多个用逗号分隔',
    
    -- 备注
    remarks TEXT COMMENT '备注',
    
    -- 软删除标记
    is_deleted TINYINT(1) DEFAULT FALSE COMMENT '是否删除',
    
    -- 创建和更新时间
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by INT COMMENT '创建人ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_by INT COMMENT '更新人ID',
    deleted_at DATETIME COMMENT '删除时间',
    deleted_by INT COMMENT '删除人ID',
    
    -- 索引
    INDEX idx_assignee (assignee_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_due_date (due_date),
    INDEX idx_project (project_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务管理表';

