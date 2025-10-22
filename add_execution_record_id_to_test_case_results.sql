-- ===================================================================
-- 数据库迁移脚本：在 TestCaseResults 表中添加 execution_record_id 字段
-- 用途：关联测试执行记录表（TestExecutionRecords）
-- 执行时间：2025-10-22
-- ===================================================================

-- 第一步：添加 execution_record_id 字段
ALTER TABLE TestCaseResults 
ADD COLUMN execution_record_id BIGINT NOT NULL COMMENT '测试执行记录ID，关联TestExecutionRecords表' AFTER result_id;

-- 第二步：为 execution_record_id 字段添加索引
CREATE INDEX idx_execution_record_id ON TestCaseResults(execution_record_id);

-- 第三步：验证字段是否添加成功
SELECT 
    COLUMN_NAME as '字段名',
    COLUMN_TYPE as '字段类型',
    IS_NULLABLE as '是否可空',
    COLUMN_COMMENT as '字段注释'
FROM 
    information_schema.COLUMNS
WHERE 
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'TestCaseResults'
    AND COLUMN_NAME = 'execution_record_id';

-- 第四步：验证索引是否创建成功
SHOW INDEX FROM TestCaseResults WHERE Key_name = 'idx_execution_record_id';

-- ===================================================================
-- 注意事项：
-- 1. 执行此脚本前请备份数据库
-- 2. 如果 TestCaseResults 表数据量很大，ALTER TABLE 操作可能需要较长时间
-- 3. 添加字段后，需要更新应用代码以正确设置 execution_record_id 的值
-- 4. 建议在低峰期执行此脚本
-- ===================================================================

-- 可选：如果需要回滚，执行以下语句
-- DROP INDEX idx_execution_record_id ON TestCaseResults;
-- ALTER TABLE TestCaseResults DROP COLUMN execution_record_id;

