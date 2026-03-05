-- 添加 case_ids 字段到 ScheduledTestTasks 表
-- 用于存储多个用例ID的JSON数组

-- 添加 case_ids 字段
ALTER TABLE ScheduledTestTasks 
ADD COLUMN case_ids TEXT COMMENT '用例ID列表（JSON格式）' AFTER target_name;
