-- =====================================================
-- 定时测试任务功能数据库表
-- 执行此脚本创建所需的表结构
-- =====================================================

-- 如果表已存在，建议先删除（注意：这会删除所有数据）
-- DROP TABLE IF EXISTS ScheduledTaskExecutions;
-- DROP TABLE IF EXISTS ScheduledTestTasks;

-- =====================================================
-- 1. 定时测试任务定义表
-- =====================================================
CREATE TABLE IF NOT EXISTS ScheduledTestTasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    
    -- 基本信息
    task_name VARCHAR(255) NOT NULL COMMENT '任务名称',
    description TEXT COMMENT '任务描述',
    task_type ENUM('single_case', 'module', 'project', 'test_suite', 'api') NOT NULL COMMENT '任务类型',
    
    -- 执行目标
    target_id INT NOT NULL COMMENT '目标ID（取决于task_type）',
    target_name VARCHAR(255) NOT NULL COMMENT '目标名称',
    
    -- 调度配置
    trigger_type ENUM('cron', 'simple', 'daily', 'weekly', 'monthly') NOT NULL COMMENT '触发器类型',
    cron_expression VARCHAR(255) COMMENT 'Cron表达式（trigger_type=cron时必填）',
    simple_repeat_interval BIGINT COMMENT '重复间隔（毫秒，trigger_type=simple时使用）',
    simple_repeat_count INT DEFAULT -1 COMMENT '重复次数（-1表示无限）',
    daily_hour INT COMMENT '每日执行时刻-小时（0-23）',
    daily_minute INT COMMENT '每日执行时刻-分钟（0-59）',
    weekly_days VARCHAR(50) COMMENT '每周执行的天数（1-7，逗号分隔，1=周一）',
    monthly_day INT COMMENT '每月执行的日期（1-31）',
    
    -- 执行配置
    execution_environment ENUM('dev', 'test', 'prod', 'staging') NOT NULL DEFAULT 'test' COMMENT '执行环境',
    base_url VARCHAR(500) COMMENT '基础URL覆盖',
    timeout_seconds INT DEFAULT 30 COMMENT '超时时间（秒）',
    concurrency INT DEFAULT 1 COMMENT '并发数',
    execution_strategy ENUM('sequential', 'parallel', 'by_module', 'smart') DEFAULT 'sequential' COMMENT '执行策略',
    retry_enabled TINYINT(1) DEFAULT 0 COMMENT '是否启用重试',
    max_retry_attempts INT DEFAULT 0 COMMENT '最大重试次数',
    retry_delay_ms INT DEFAULT 1000 COMMENT '重试延迟（毫秒）',
    
    -- 通知配置
    notify_on_success TINYINT(1) DEFAULT 0 COMMENT '成功时通知',
    notify_on_failure TINYINT(1) DEFAULT 1 COMMENT '失败时通知',
    notification_recipients VARCHAR(500) COMMENT '通知接收者（邮箱，逗号分隔）',
    
    -- 执行限制
    skip_if_previous_failed TINYINT(1) DEFAULT 0 COMMENT '前次失败时跳过',
    max_duration_seconds INT COMMENT '最大执行时长（秒）',
    
    -- 状态信息
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    
    -- 执行统计
    total_executions INT DEFAULT 0 COMMENT '总执行次数',
    successful_executions INT DEFAULT 0 COMMENT '成功执行次数',
    failed_executions INT DEFAULT 0 COMMENT '失败执行次数',
    skipped_executions INT DEFAULT 0 COMMENT '跳过执行次数',
    
    -- 时间信息
    next_trigger_time DATETIME COMMENT '下次触发时间',
    last_execution_time DATETIME COMMENT '最后一次执行时间',
    last_execution_status ENUM('pending', 'running', 'success', 'failed', 'skipped') COMMENT '最后执行状态',
    
    -- 创建和修改信息
    created_by INT NOT NULL COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by INT COMMENT '最后修改人ID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    deleted_by INT COMMENT '删除人ID',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    
    -- 索引
    UNIQUE KEY uk_task_name (task_name, is_deleted),
    INDEX idx_task_type (task_type),
    INDEX idx_target_id (target_id),
    INDEX idx_is_enabled (is_enabled),
    INDEX idx_next_trigger_time (next_trigger_time),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时测试任务定义表';

-- =====================================================
-- 2. 定时任务执行记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS ScheduledTaskExecutions (
    execution_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '执行记录ID',
    
    -- 关联信息
    task_id BIGINT NOT NULL COMMENT '任务ID，关联ScheduledTestTasks',
    test_execution_record_id BIGINT COMMENT '测试执行记录ID，关联TestExecutionRecords.record_id',
    quartz_job_id VARCHAR(255) COMMENT 'Quartz任务ID',
    
    -- 执行状态
    status ENUM('pending', 'running', 'success', 'failed', 'skipped', 'timeout', 'cancelled') NOT NULL DEFAULT 'pending' COMMENT '执行状态',
    
    -- 时间信息
    scheduled_time DATETIME NOT NULL COMMENT '计划执行时间',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    duration_seconds INT COMMENT '执行耗时（秒）',
    delay_seconds INT COMMENT '延迟时间（秒，actual_start_time - scheduled_time）',
    
    -- 执行统计
    total_cases INT DEFAULT 0 COMMENT '总用例数',
    passed_cases INT DEFAULT 0 COMMENT '通过数',
    failed_cases INT DEFAULT 0 COMMENT '失败数',
    skipped_cases INT DEFAULT 0 COMMENT '跳过数',
    success_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '成功率',
    
    -- 错误信息
    error_message TEXT COMMENT '错误信息',
    stack_trace TEXT COMMENT '堆栈跟踪',
    
    -- 重试信息
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    is_retry TINYINT(1) DEFAULT 0 COMMENT '是否是重试',
    original_execution_id BIGINT COMMENT '原始执行ID（如果是重试）',
    
    -- 通知状态
    notification_sent TINYINT(1) DEFAULT 0 COMMENT '是否发送通知',
    notification_channels VARCHAR(100) COMMENT '通知渠道（email,webhook等）',
    notification_timestamp DATETIME COMMENT '通知时间',
    
    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    
    -- 索引
    INDEX idx_task_id (task_id),
    INDEX idx_test_execution_record_id (test_execution_record_id),
    INDEX idx_status (status),
    INDEX idx_scheduled_time (scheduled_time),
    INDEX idx_actual_start_time (actual_start_time),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (task_id) REFERENCES ScheduledTestTasks(task_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行记录表';

-- =====================================================
-- 3. 修改 TestExecutionRecords 表，添加关联字段
-- 注意：TestExecutionRecords 表的主键是 record_id，不是 execution_id
-- 该表已有 triggered_task_id 字段，这里我们添加一个额外的索引
-- =====================================================

-- 如果 scheduled_task_id 字段不存在，则添加（MySQL不支持IF NOT EXISTS，需要手动判断）
-- 请先执行 DESCRIBE TestExecutionRecords; 查看现有字段

-- 添加 scheduled_task_id 字段（如果字段已存在会报错，可以忽略）
-- ALTER TABLE TestExecutionRecords 
-- ADD COLUMN scheduled_task_id BIGINT NULL COMMENT '关联的定时任务ID' AFTER record_id;

-- 添加索引（如果索引已存在会报错，可以忽略）
-- ALTER TABLE TestExecutionRecords 
-- ADD INDEX idx_scheduled_task_id (scheduled_task_id);

-- =====================================================
-- 4. 创建任务查询视图（可选）
-- =====================================================
CREATE OR REPLACE VIEW v_scheduled_task_summary AS
SELECT 
    st.task_id,
    st.task_name,
    st.task_type,
    st.target_name,
    st.execution_environment,
    st.is_enabled,
    st.total_executions,
    st.successful_executions,
    st.failed_executions,
    st.skipped_executions,
    ROUND(
        CASE 
            WHEN st.total_executions > 0 
            THEN (st.successful_executions * 100.0 / st.total_executions) 
            ELSE 0 
        END, 2
    ) AS success_rate,
    st.next_trigger_time,
    st.last_execution_time,
    st.last_execution_status,
    st.created_at
FROM ScheduledTestTasks st
WHERE st.is_deleted = 0;

-- =====================================================
-- 5. 插入测试数据（可选）
-- =====================================================
-- INSERT INTO ScheduledTestTasks (task_name, description, task_type, target_id, target_name, trigger_type, cron_expression, execution_environment, created_by)
-- VALUES ('每日API测试', '每天早上9点执行API测试', 'project', 1, '测试项目', 'cron', '0 0 9 * * ?', 'test', 1);

