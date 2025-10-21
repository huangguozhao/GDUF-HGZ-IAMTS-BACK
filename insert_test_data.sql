-- 插入测试数据
-- 用于测试测试结果查询接口

USE iatmsdb_dev;

-- 插入测试结果数据
INSERT INTO TestCaseResults (
    report_id, execution_id, task_type, ref_id, full_name, status, duration,
    start_time, end_time, failure_message, failure_type, error_code,
    environment, browser, os, device, severity, priority, retry_count, flaky
) VALUES 
-- 测试数据1：成功的测试用例
(
    1001, 30001, 'test_case', 101, 
    '用户管理模块/用户登录接口/用户登录-成功场景', 
    'passed', 1245,
    '2024-09-16 10:30:00', '2024-09-16 10:30:01',
    NULL, NULL, NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'critical', 'P0', 0, false
),
-- 测试数据2：失败的测试用例
(
    1001, 30002, 'test_case', 102,
    '用户管理模块/用户注册接口/用户注册-邮箱已存在',
    'failed', 856,
    '2024-09-16 10:30:02', '2024-09-16 10:30:03',
    '预期状态码为409，实际返回200', 'assertion_error', 'ERR_001',
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'normal', 'P1', 1, false
),
-- 测试数据3：中断的测试用例
(
    1001, 30003, 'test_case', 103,
    '商品管理模块/商品查询接口/查询所有商品',
    'broken', 2340,
    '2024-09-16 10:31:00', '2024-09-16 10:31:02',
    '网络连接超时', 'timeout_error', 'ERR_002',
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'critical', 'P0', 2, true
),
-- 测试数据4：跳过的测试用例
(
    1001, 30004, 'test_case', 104,
    '订单管理模块/订单创建接口/创建订单-成功',
    'skipped', 0,
    '2024-09-16 10:32:00', '2024-09-16 10:32:00',
    '前置条件未满足', 'precondition_failed', NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'normal', 'P2', 0, false
),
-- 测试数据5：成功的模块测试
(
    1002, 30005, 'module', 1,
    '用户管理模块',
    'passed', 3560,
    '2024-09-16 10:33:00', '2024-09-16 10:33:04',
    NULL, NULL, NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'normal', 'P1', 0, false
),
-- 测试数据6：失败的项目测试
(
    1003, 30006, 'project', 1,
    '电商平台项目',
    'failed', 15678,
    '2024-09-16 10:35:00', '2024-09-16 10:35:16',
    '部分用例执行失败', 'partial_failure', NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'critical', 'P0', 0, false
),
-- 测试数据7：成功的API监控
(
    1004, 30007, 'api_monitor', 201,
    'API健康检查',
    'passed', 234,
    '2024-09-16 10:40:00', '2024-09-16 10:40:00',
    NULL, NULL, NULL,
    'production', 'Automated Monitor', 'Linux', 'Server',
    'normal', 'P2', 0, false
),
-- 测试数据8：失败的测试套件
(
    1005, 30008, 'test_suite', 301,
    '用户模块回归测试套件',
    'failed', 8920,
    '2024-09-16 10:45:00', '2024-09-16 10:45:09',
    '5个用例中有2个失败', 'suite_partial_failure', NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'high', 'P1', 0, false
),
-- 测试数据9：快速通过的用例
(
    1001, 30009, 'test_case', 105,
    '工具类/时间格式化/标准格式',
    'passed', 45,
    '2024-09-16 10:50:00', '2024-09-16 10:50:00',
    NULL, NULL, NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'low', 'P3', 0, false
),
-- 测试数据10：慢查询用例
(
    1001, 30010, 'test_case', 106,
    '报表管理/数据导出/导出大量数据',
    'passed', 25678,
    '2024-09-16 11:00:00', '2024-09-16 11:00:26',
    NULL, NULL, NULL,
    'test', 'Chrome 115', 'Windows 10', 'Desktop',
    'normal', 'P2', 0, false
);

-- 验证数据插入
SELECT COUNT(*) AS total_count FROM TestCaseResults;
SELECT 
    status, 
    COUNT(*) AS count 
FROM TestCaseResults 
WHERE is_deleted = FALSE 
GROUP BY status;

SELECT '测试数据插入成功！共插入10条记录' AS message;

-- 查看插入的数据
SELECT 
    result_id,
    task_type,
    ref_id,
    full_name,
    status,
    duration,
    start_time
FROM TestCaseResults
ORDER BY result_id;



