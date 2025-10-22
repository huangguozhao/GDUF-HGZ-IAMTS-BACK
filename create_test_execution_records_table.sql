-- 创建测试执行记录表
-- 用于记录手动或自动的测试执行历史

DROP TABLE IF EXISTS TestExecutionRecords;

CREATE TABLE TestExecutionRecords (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID，自增主键',
    
    -- 执行范围信息
    execution_scope ENUM('api', 'module', 'project', 'test_suite', 'test_case') NOT NULL COMMENT '执行范围类型',
    ref_id INT NOT NULL COMMENT '根据execution_scope关联对应表的ID',
    scope_name VARCHAR(255) NOT NULL COMMENT '执行范围的名称',
    
    -- 执行信息
    executed_by INT NOT NULL COMMENT '执行人ID，关联Users表',
    execution_type ENUM('manual', 'scheduled', 'triggered') NOT NULL DEFAULT 'manual' COMMENT '执行类型',
    environment VARCHAR(50) NOT NULL COMMENT '测试环境',
    
    -- 执行状态
    status ENUM('running', 'completed', 'failed', 'cancelled') NOT NULL DEFAULT 'running' COMMENT '执行状态',
    
    -- 时间信息
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NULL COMMENT '结束时间',
    duration_seconds INT DEFAULT 0 COMMENT '执行耗时(秒)',
    
    -- 统计信息
    total_cases INT DEFAULT 0 COMMENT '总用例数',
    executed_cases INT DEFAULT 0 COMMENT '已执行用例数',
    passed_cases INT DEFAULT 0 COMMENT '通过用例数',
    failed_cases INT DEFAULT 0 COMMENT '失败用例数',
    skipped_cases INT DEFAULT 0 COMMENT '跳过用例数',
    success_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '成功率',
    
    -- 执行配置
    browser VARCHAR(50) COMMENT '浏览器类型',
    app_version VARCHAR(50) COMMENT '应用版本',
    execution_config JSON COMMENT '执行配置信息',
    
    -- 结果信息
    report_url VARCHAR(500) COMMENT '报告访问地址',
    log_file_path VARCHAR(500) COMMENT '日志文件路径',
    error_message TEXT COMMENT '错误信息',
    
    -- 触发信息（如果是定时任务触发的）
    triggered_task_id BIGINT NULL COMMENT '触发任务ID，关联ScheduledTasks表',
    
    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted_by INT NULL COMMENT '删除人ID',
    
    -- 索引优化
    INDEX idx_execution_scope_ref (execution_scope, ref_id),
    INDEX idx_executed_by (executed_by),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    INDEX idx_environment (environment),
    INDEX idx_triggered_task_id (triggered_task_id),
    INDEX idx_created_at (created_at)
) COMMENT='测试执行记录表，记录手动或自动的测试执行历史';

-- 验证表创建
SELECT 
    TABLE_NAME as '表名',
    TABLE_COMMENT as '表注释',
    TABLE_ROWS as '行数'
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'TestExecutionRecords';

-- 查看表结构
DESCRIBE TestExecutionRecords;

-- 查看索引
SHOW INDEX FROM TestExecutionRecords;

