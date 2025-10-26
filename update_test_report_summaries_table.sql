-- ==================================================================================
-- TestReportSummaries表结构升级脚本
-- 版本: 2.0  
-- 日期: 2024-10-26
-- 说明: 为TestReportSummaries表添加ISO报告相关字段，支持企业级测试报告功能
-- ==================================================================================

-- 1. 添加报告配置ID
ALTER TABLE TestReportSummaries 
ADD COLUMN report_config_id INT NULL COMMENT '报告配置ID';

-- 2. 添加ISO标准指标数据
ALTER TABLE TestReportSummaries 
ADD COLUMN iso_metrics JSON COMMENT 'ISO标准指标数据 - 包含质量指标、性能指标等';

-- 3. 添加执行摘要
ALTER TABLE TestReportSummaries 
ADD COLUMN executive_summary TEXT COMMENT '执行摘要 - 面向管理层的简要总结';

-- 4. 添加结论建议
ALTER TABLE TestReportSummaries 
ADD COLUMN conclusion_recommendation TEXT COMMENT '结论建议 - 测试结论和改进建议';

-- 5. 添加风险评估数据
ALTER TABLE TestReportSummaries 
ADD COLUMN risk_assessment JSON COMMENT '风险评估数据 - 包含风险等级、影响分析等';

-- 6. 添加缺陷分析数据
ALTER TABLE TestReportSummaries 
ADD COLUMN defect_analysis JSON COMMENT '缺陷分析数据 - 缺陷分类、趋势分析等';

-- 7. 添加环境详细信息
ALTER TABLE TestReportSummaries 
ADD COLUMN environment_details JSON COMMENT '环境详细信息 - 软硬件环境、配置信息等';

-- 8. 添加测试范围详情
ALTER TABLE TestReportSummaries 
ADD COLUMN test_scope_details JSON COMMENT '测试范围详情 - 测试覆盖范围、测试策略等';

-- 9. 添加索引（如果不存在的话）
ALTER TABLE TestReportSummaries 
ADD INDEX IF NOT EXISTS idx_report_config_id (report_config_id);

-- ==================================================================================
-- 数据说明和使用示例
-- ==================================================================================

/*
1. iso_metrics 字段示例：
{
    "testCoverage": {
        "codeCoverage": 85.5,
        "requirementCoverage": 92.3,
        "apiCoverage": 88.7
    },
    "qualityMetrics": {
        "defectDensity": 0.12,
        "defectRemovalEfficiency": 95.3,
        "testEffectiveness": 89.2
    },
    "performanceMetrics": {
        "avgResponseTime": 245,
        "throughput": 1250,
        "errorRate": 0.02
    }
}

2. risk_assessment 字段示例：
{
    "overallRiskLevel": "MEDIUM",
    "risks": [
        {
            "category": "功能风险",
            "level": "HIGH",
            "description": "核心支付功能存在高危缺陷",
            "mitigation": "立即修复并回归测试"
        },
        {
            "category": "性能风险",
            "level": "MEDIUM",
            "description": "高并发场景下响应时间超标",
            "mitigation": "优化数据库查询和缓存策略"
        }
    ]
}

3. defect_analysis 字段示例：
{
    "summary": {
        "totalDefects": 45,
        "criticalDefects": 3,
        "majorDefects": 12,
        "minorDefects": 30
    },
    "distribution": {
        "byModule": {...},
        "byPriority": {...},
        "bySeverity": {...}
    },
    "trend": {
        "newDefects": 8,
        "fixedDefects": 15,
        "reopenedDefects": 2
    }
}

4. environment_details 字段示例：
{
    "hardware": {
        "server": "Dell PowerEdge R740",
        "cpu": "Intel Xeon Silver 4210",
        "memory": "64GB DDR4",
        "storage": "1TB SSD"
    },
    "software": {
        "os": "CentOS 7.9",
        "jdk": "OpenJDK 11.0.12",
        "database": "MySQL 8.0.26",
        "middleware": "Tomcat 9.0.50"
    },
    "network": {
        "bandwidth": "1Gbps",
        "latency": "< 5ms"
    }
}

5. test_scope_details 字段示例：
{
    "inScope": [
        "用户管理模块",
        "订单处理模块",
        "支付集成模块"
    ],
    "outOfScope": [
        "第三方服务",
        "历史数据迁移"
    ],
    "testTypes": [
        "功能测试",
        "接口测试",
        "性能测试",
        "安全测试"
    ],
    "testStrategy": "基于风险的测试方法，重点覆盖核心业务流程"
}
*/

-- ==================================================================================
-- 数据迁移脚本（可选）
-- 如果需要为现有报告初始化默认值，可以取消下面的注释
-- ==================================================================================

/*
-- 为现有报告添加默认的 ISO 指标结构
UPDATE TestReportSummaries 
SET iso_metrics = JSON_OBJECT(
    'testCoverage', JSON_OBJECT(
        'codeCoverage', 0,
        'requirementCoverage', 0,
        'apiCoverage', 0
    ),
    'qualityMetrics', JSON_OBJECT(
        'defectDensity', 0,
        'defectRemovalEfficiency', 0,
        'testEffectiveness', 0
    ),
    'performanceMetrics', JSON_OBJECT(
        'avgResponseTime', 0,
        'throughput', 0,
        'errorRate', 0
    )
)
WHERE iso_metrics IS NULL AND is_deleted = FALSE;

-- 为现有报告添加默认的风险评估
UPDATE TestReportSummaries 
SET risk_assessment = JSON_OBJECT(
    'overallRiskLevel', 'UNKNOWN',
    'risks', JSON_ARRAY()
)
WHERE risk_assessment IS NULL AND is_deleted = FALSE;

-- 为现有报告添加默认的缺陷分析
UPDATE TestReportSummaries 
SET defect_analysis = JSON_OBJECT(
    'summary', JSON_OBJECT(
        'totalDefects', 0,
        'criticalDefects', 0,
        'majorDefects', 0,
        'minorDefects', 0
    ),
    'distribution', JSON_OBJECT(),
    'trend', JSON_OBJECT(
        'newDefects', 0,
        'fixedDefects', 0,
        'reopenedDefects', 0
    )
)
WHERE defect_analysis IS NULL AND is_deleted = FALSE;
*/

-- ==================================================================================
-- 验证脚本
-- ==================================================================================

-- 查看表结构
-- DESC TestReportSummaries;

-- 查看新增字段
-- SELECT 
--     report_id,
--     report_name,
--     report_config_id,
--     CASE 
--         WHEN iso_metrics IS NOT NULL THEN 'YES'
--         ELSE 'NO'
--     END as has_iso_metrics,
--     CASE 
--         WHEN executive_summary IS NOT NULL THEN 'YES'
--         ELSE 'NO'
--     END as has_summary,
--     CASE 
--         WHEN risk_assessment IS NOT NULL THEN 'YES'
--         ELSE 'NO'
--     END as has_risk,
--     created_at
-- FROM TestReportSummaries
-- WHERE is_deleted = FALSE
-- ORDER BY created_at DESC
-- LIMIT 10;

-- 查看ISO字段使用统计
-- SELECT 
--     COUNT(*) as total_reports,
--     SUM(CASE WHEN iso_metrics IS NOT NULL THEN 1 ELSE 0 END) as with_iso_metrics,
--     SUM(CASE WHEN executive_summary IS NOT NULL THEN 1 ELSE 0 END) as with_summary,
--     SUM(CASE WHEN conclusion_recommendation IS NOT NULL THEN 1 ELSE 0 END) as with_conclusion,
--     SUM(CASE WHEN risk_assessment IS NOT NULL THEN 1 ELSE 0 END) as with_risk,
--     SUM(CASE WHEN defect_analysis IS NOT NULL THEN 1 ELSE 0 END) as with_defects,
--     SUM(CASE WHEN environment_details IS NOT NULL THEN 1 ELSE 0 END) as with_env,
--     SUM(CASE WHEN test_scope_details IS NOT NULL THEN 1 ELSE 0 END) as with_scope
-- FROM TestReportSummaries
-- WHERE is_deleted = FALSE;

-- ==================================================================================
-- 回滚脚本（如需回滚，请谨慎使用）
-- ==================================================================================

/*
-- 删除新增的字段
ALTER TABLE TestReportSummaries 
DROP COLUMN report_config_id,
DROP COLUMN iso_metrics,
DROP COLUMN executive_summary,
DROP COLUMN conclusion_recommendation,
DROP COLUMN risk_assessment,
DROP COLUMN defect_analysis,
DROP COLUMN environment_details,
DROP COLUMN test_scope_details;

-- 删除新增的索引
ALTER TABLE TestReportSummaries 
DROP INDEX IF EXISTS idx_report_config_id;
*/

-- ==================================================================================
-- 完成
-- ==================================================================================

SELECT 'TestReportSummaries表升级完成！' as status;

