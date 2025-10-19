-- 创建测试结果表
-- 在MySQL中运行此脚本

USE iatmsdb_dev;

-- 创建 TestCaseResults 表（如果不存在）
CREATE TABLE IF NOT EXISTS TestCaseResults (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID，自增主键',
    report_id BIGINT NULL COMMENT '报告ID',
    execution_id BIGINT NULL COMMENT '执行记录ID',
    task_type ENUM('test_suite', 'test_case', 'project', 'module', 'api_monitor') NOT NULL DEFAULT 'test_suite' COMMENT '任务类型',
    ref_id INT NOT NULL COMMENT '根据task_type关联对应表的ID',
    full_name VARCHAR(500) COMMENT 'Allure中的完整名称（包含路径）',
    status ENUM('passed', 'failed', 'broken', 'skipped', 'unknown') NOT NULL COMMENT '该用例的执行状态',
    duration BIGINT COMMENT '该用例执行耗时（毫秒）',
    start_time DATETIME NULL COMMENT '用例开始时间',
    end_time DATETIME NULL COMMENT '用例结束时间',
    failure_message TEXT COMMENT '失败信息',
    failure_trace TEXT COMMENT '失败堆栈跟踪',
    failure_type VARCHAR(100) COMMENT '失败类型',
    error_code VARCHAR(50) COMMENT '错误代码',
    steps_json JSON COMMENT '测试步骤执行详情',
    parameters_json JSON COMMENT '测试参数信息',
    attachments_json JSON COMMENT '附件信息',
    logs_link VARCHAR(500) COMMENT '该用例详细日志的链接',
    screenshot_link VARCHAR(500) COMMENT '截图链接',
    video_link VARCHAR(500) COMMENT '视频录制链接',
    environment VARCHAR(50) COMMENT '执行环境',
    browser VARCHAR(50) COMMENT '浏览器信息',
    os VARCHAR(50) COMMENT '操作系统',
    device VARCHAR(50) COMMENT '设备信息',
    tags_json JSON COMMENT '标签信息',
    severity ENUM('blocker', 'critical', 'normal', 'minor', 'trivial') COMMENT '严重程度',
    priority ENUM('P0', 'P1', 'P2', 'P3') COMMENT '优先级',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    flaky BOOLEAN DEFAULT false COMMENT '是否是不稳定用例',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted_by INT NULL COMMENT '删除人ID',
    
    -- 索引
    INDEX idx_report_id (report_id),
    INDEX idx_execution_id (execution_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_duration (duration),
    INDEX idx_environment (environment),
    INDEX idx_browser (browser),
    INDEX idx_is_deleted(is_deleted)
) COMMENT='测试结果表';

-- 验证表是否创建成功
SHOW TABLES LIKE 'TestCaseResults';
SELECT 'TestCaseResults table check completed!' AS message;

