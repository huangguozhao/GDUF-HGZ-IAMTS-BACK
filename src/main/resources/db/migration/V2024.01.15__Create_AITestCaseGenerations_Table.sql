-- AI测试用例生成记录表
CREATE TABLE IF NOT EXISTS AITestCaseGenerations (
    generation_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '生成记录ID',
    
    -- 关联信息
    project_id INT COMMENT '项目ID',
    api_id INT COMMENT '接口ID（单个生成时）',
    user_id INT NOT NULL COMMENT '请求用户ID',
    
    -- 生成配置
    generation_type VARCHAR(20) NOT NULL COMMENT '生成类型：single/batch/requirement',
    input_context TEXT COMMENT '输入上下文（需求描述/接口信息JSON）',
    generation_config JSON COMMENT '生成配置（用例数量、覆盖场景等）',
    
    -- 生成结果
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/processing/completed/failed',
    generated_cases JSON COMMENT '生成的用例数据（JSON数组）',
    case_count INT DEFAULT 0 COMMENT '生成用例数量',
    
    -- AI交互记录
    prompt_tokens INT COMMENT '提示词token数',
    completion_tokens INT COMMENT '完成token数',
    total_cost DECIMAL(10,6) COMMENT '调用成本',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME COMMENT '完成时间',
    error_message TEXT COMMENT '错误信息',
    
    INDEX idx_project_id (project_id),
    INDEX idx_api_id (api_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI测试用例生成记录表';
