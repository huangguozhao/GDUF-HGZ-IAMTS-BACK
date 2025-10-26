-- ==================================================================================
-- TestCaseResults表结构升级脚本
-- 版本: 2.0
-- 日期: 2024-10-26
-- 说明: 为TestCaseResults表添加更多字段，支持更详细的测试结果分析和追踪
-- ==================================================================================

-- 1. 添加用例关联字段
ALTER TABLE TestCaseResults 
ADD COLUMN case_id INT NULL COMMENT '关联TestCases表的用例ID',
ADD INDEX idx_case_id_new (case_id);

-- 2. 添加用例基本信息字段（冗余存储，提高查询性能）
ALTER TABLE TestCaseResults 
ADD COLUMN case_code VARCHAR(50) COMMENT '用例编码',
ADD COLUMN case_name VARCHAR(255) COMMENT '用例名称',
ADD COLUMN module_name VARCHAR(255) COMMENT '模块名称',
ADD COLUMN api_name VARCHAR(255) COMMENT '接口名称',
ADD COLUMN suite_name VARCHAR(255) COMMENT '测试套件名称',
ADD COLUMN package_name VARCHAR(500) COMMENT '包名/命名空间';

-- 3. 添加Allure相关字段（支持BDD和测试管理）
ALTER TABLE TestCaseResults 
ADD COLUMN epic_name VARCHAR(255) COMMENT 'Epic名称',
ADD COLUMN feature_name VARCHAR(255) COMMENT 'Feature名称',
ADD COLUMN story_name VARCHAR(255) COMMENT 'Story名称';

-- 4. 添加测试分类字段
ALTER TABLE TestCaseResults 
ADD COLUMN test_layer ENUM('UNIT', 'INTEGRATION', 'API', 'E2E', 'PERFORMANCE', 'SECURITY') DEFAULT 'API' COMMENT '测试层级',
ADD COLUMN test_type ENUM('POSITIVE', 'NEGATIVE', 'BOUNDARY', 'SECURITY', 'PERFORMANCE', 'USABILITY') DEFAULT 'POSITIVE' COMMENT '测试类型',
ADD INDEX idx_test_layer (test_layer);

-- 5. 添加不稳定用例追踪字段
ALTER TABLE TestCaseResults 
ADD COLUMN flaky_count INT DEFAULT 0 COMMENT '不稳定次数',
ADD COLUMN last_flaky_time DATETIME COMMENT '最后一次不稳定时间';

-- 6. 添加JSON扩展字段（支持灵活扩展）
ALTER TABLE TestCaseResults 
ADD COLUMN history_trend JSON COMMENT '历史趋势数据',
ADD COLUMN custom_labels JSON COMMENT '自定义标签';

-- 7. 添加问题分析字段
ALTER TABLE TestCaseResults 
ADD COLUMN root_cause_analysis TEXT COMMENT '根因分析',
ADD COLUMN impact_assessment ENUM('HIGH', 'MEDIUM', 'LOW') COMMENT '影响评估';

-- 8. 添加复测相关字段
ALTER TABLE TestCaseResults 
ADD COLUMN retest_result ENUM('PASSED', 'FAILED', 'NOT_RETESTED') DEFAULT 'NOT_RETESTED' COMMENT '复测结果',
ADD COLUMN retest_notes TEXT COMMENT '复测备注';

-- 9. 添加新索引以优化查询性能
ALTER TABLE TestCaseResults 
ADD INDEX idx_suite_name (suite_name),
ADD INDEX idx_package_name (package_name(100)),
ADD INDEX idx_epic_name (epic_name);

-- 10. 更新表注释
ALTER TABLE TestCaseResults COMMENT = '测试用例结果表 - 存储测试执行的详细结果和分析信息';

-- ==================================================================================
-- 数据迁移脚本（可选）
-- 如果需要从现有数据中填充新字段，可以取消下面的注释并根据实际情况修改
-- ==================================================================================

/*
-- 从TestCases表回填基本信息
UPDATE TestCaseResults tcr
LEFT JOIN TestCases tc ON tcr.ref_id = tc.case_id AND tcr.task_type = 'test_case'
SET 
    tcr.case_id = tc.case_id,
    tcr.case_code = tc.case_code,
    tcr.case_name = tc.name
WHERE tcr.case_id IS NULL AND tc.case_id IS NOT NULL;

-- 初始化默认值
UPDATE TestCaseResults 
SET 
    test_layer = 'API',
    test_type = 'POSITIVE',
    flaky_count = 0,
    retest_result = 'NOT_RETESTED'
WHERE test_layer IS NULL;
*/

-- ==================================================================================
-- 验证脚本
-- ==================================================================================

-- 查看表结构
-- DESC TestCaseResults;

-- 查看新增字段的数据分布
-- SELECT 
--     test_layer,
--     test_type,
--     retest_result,
--     COUNT(*) as count
-- FROM TestCaseResults
-- WHERE is_deleted = FALSE
-- GROUP BY test_layer, test_type, retest_result;

-- ==================================================================================
-- 完成
-- ==================================================================================

