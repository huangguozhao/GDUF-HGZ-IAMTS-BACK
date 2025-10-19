-- 检查数据库表是否存在
-- 在MySQL客户端中运行以下命令

USE iatmsdb_dev;

-- 检查 TestCaseResults 表是否存在
SHOW TABLES LIKE 'TestCaseResults';

-- 如果表存在，检查表结构
DESC TestCaseResults;

-- 检查是否有数据
SELECT COUNT(*) FROM TestCaseResults;

-- 检查其他相关表
SHOW TABLES LIKE 'TestCases';
SHOW TABLES LIKE 'Modules';
SHOW TABLES LIKE 'Projects';
SHOW TABLES LIKE 'Apis';
SHOW TABLES LIKE 'TestSuites';
SHOW TABLES LIKE 'TestReportSummaries';

