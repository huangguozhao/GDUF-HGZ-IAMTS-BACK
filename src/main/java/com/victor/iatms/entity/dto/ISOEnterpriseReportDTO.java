package com.victor.iatms.entity.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ISO/IEC/IEEE 29119标准企业级测试报告DTO
 * 符合国际软件测试标准和ISTQB最佳实践
 * 
 * @author Victor
 * @since 2024-10-26
 */
@Data
public class ISOEnterpriseReportDTO {
    
    // ==================== 模块1: 报告头信息 (Document Header) ====================
    
    /** 报告标题 */
    private String reportTitle;
    
    /** 项目/产品名称 */
    private String projectName;
    
    /** 版本号 */
    private String version;
    
    /** 报告编号 (格式：TR-YYYYMMDD-0001) */
    private String reportNumber;
    
    /** 测试周期开始日期 */
    private LocalDateTime testStartDate;
    
    /** 测试周期结束日期 */
    private LocalDateTime testEndDate;
    
    /** 报告日期 */
    private LocalDateTime reportDate;
    
    /** 编写人 */
    private String testerName;
    
    /** 评审人 */
    private String reviewerName;
    
    /** 报告状态 (draft/under_review/approved) */
    private String reportStatus;
    
    // ==================== 模块2: 执行摘要 (Executive Summary) ====================
    
    /** 核心结论 (pass_recommend/pass_with_risk/not_pass) */
    private String conclusion;
    
    /** 详细结论说明 */
    private String detailedConclusion;
    
    /** 关键指标仪表盘 */
    private KeyMetrics keyMetrics;
    
    @Data
    public static class KeyMetrics {
        /** 测试通过率 */
        private BigDecimal testPassRate;
        
        /** 缺陷密度 */
        private BigDecimal defectDensity;
        
        /** 高优先级缺陷数 (P0 + P1) */
        private Integer criticalDefectCount;
        
        /** 缺陷修复率 */
        private BigDecimal defectFixRate;
        
        /** 需求覆盖率 */
        private BigDecimal requirementCoverage;
        
        /** 测试效率 (用例/天) */
        private BigDecimal testEfficiency;
        
        /** 目标通过率 */
        private BigDecimal targetPassRate = new BigDecimal("95.0");
        
        /** 目标缺陷密度 */
        private BigDecimal targetDefectDensity = new BigDecimal("5.0");
        
        /** 目标高优先级缺陷数 */
        private Integer targetCriticalDefects = 2;
        
        /** 目标修复率 */
        private BigDecimal targetFixRate = new BigDecimal("90.0");
    }
    
    // ==================== 模块3: 测试范围与背景 (Test Scope & Context) ====================
    
    /** 测试范围 */
    private TestScope testScope;
    
    @Data
    public static class TestScope {
        /** 核心业务流程 */
        private String coreBusinessProcesses;
        
        /** 测试类型列表 */
        private List<String> testTypes;
        
        /** 测试方法 */
        private List<String> testMethods;
        
        /** 覆盖模块数量 */
        private Integer moduleCount;
        
        /** 测试目标 */
        private List<String> testObjectives;
    }
    
    // ==================== 模块4: 测试环境与配置 (Test Environment) ====================
    
    /** 测试环境配置 */
    private TestEnvironment testEnvironment;
    
    @Data
    public static class TestEnvironment {
        /** 环境名称 */
        private String environmentName;
        
        /** 环境类型 */
        private String environmentType;
        
        /** 服务器地址 */
        private String serverAddress;
        
        /** 数据库信息 */
        private String databaseInfo;
        
        /** 后端版本 */
        private String backendVersion;
        
        /** 测试工具 */
        private List<String> testTools;
        
        /** 浏览器/设备覆盖 */
        private List<String> browserDeviceCoverage;
    }
    
    // ==================== 模块5: 测试结果与度量分析 (Test Results & Metrics) ====================
    
    /** 模块测试结果 */
    private List<ModuleResult> moduleResults;
    
    @Data
    public static class ModuleResult {
        /** 模块名称 */
        private String moduleName;
        
        /** 总用例数 */
        private Integer totalCases;
        
        /** 已执行数 */
        private Integer executedCases;
        
        /** 通过数 */
        private Integer passedCases;
        
        /** 失败数 */
        private Integer failedCases;
        
        /** 异常数 */
        private Integer brokenCases;
        
        /** 跳过数 */
        private Integer skippedCases;
        
        /** 通过率 */
        private BigDecimal passRate;
    }
    
    /** 测试覆盖率矩阵 (优先级 vs 状态) */
    private Map<String, Map<String, Integer>> coverageMatrix;
    
    /** 缺陷度量 */
    private DefectMetrics defectMetrics;
    
    @Data
    public static class DefectMetrics {
        /** 总缺陷数 */
        private Integer totalDefects;
        
        /** P0缺陷数 */
        private Integer p0Count;
        
        /** P1缺陷数 */
        private Integer p1Count;
        
        /** P2缺陷数 */
        private Integer p2Count;
        
        /** P3缺陷数 */
        private Integer p3Count;
        
        /** 缺陷发现率 */
        private BigDecimal defectDiscoveryRate;
        
        /** 缺陷遗留率 */
        private BigDecimal defectRemainRate;
    }
    
    // ==================== 模块6: 详细缺陷信息 (Defect Details) ====================
    
    /** 详细缺陷列表 */
    private List<DetailedDefect> detailedDefects;
    
    @Data
    public static class DetailedDefect {
        /** 缺陷序号 */
        private Integer defectIndex;
        
        /** 用例ID */
        private String caseId;
        
        /** 用例编号 */
        private String caseCode;
        
        /** 用例名称 */
        private String caseName;
        
        /** 优先级 */
        private String priority;
        
        /** 严重程度 */
        private String severity;
        
        /** 状态 */
        private String status;
        
        /** 发现时间 */
        private LocalDateTime discoveryTime;
        
        /** 测试人员 */
        private String testerName;
        
        /** 影响范围 */
        private String impactScope;
        
        /** 错误类型 */
        private String errorType;
        
        /** 错误消息 */
        private String errorMessage;
        
        /** 堆栈跟踪 */
        private String stackTrace;
        
        /** 前置条件 */
        private String preconditions;
        
        /** 复现步骤 */
        private String reproductionSteps;
        
        /** 预期结果 */
        private String expectedResult;
        
        /** 实际结果 */
        private String actualResult;
        
        /** 根因分析 */
        private String rootCauseAnalysis;
        
        /** 建议措施 */
        private String suggestedActions;
        
        /** 测试环境 */
        private String environment;
        
        /** 浏览器 */
        private String browser;
        
        /** 操作系统 */
        private String os;
        
        /** 设备 */
        private String device;
        
        /** 标签 */
        private List<String> tags;
        
        /** 执行耗时 */
        private Long duration;
        
        /** 重试次数 */
        private Integer retryCount;
        
        /** 是否不稳定 */
        private Boolean isFlaky;
    }
    
    // ==================== 模块7: 挑战与风险 (Challenges & Risks) ====================
    
    /** 已遇到的挑战 */
    private List<Challenge> challenges;
    
    @Data
    public static class Challenge {
        /** 挑战标题 */
        private String title;
        
        /** 挑战描述 */
        private String description;
        
        /** 缓解措施 */
        private String mitigation;
    }
    
    /** 风险评估矩阵 */
    private List<RiskItem> riskMatrix;
    
    @Data
    public static class RiskItem {
        /** 风险项 */
        private String riskName;
        
        /** 发生概率 (low/medium/high) */
        private String probability;
        
        /** 概率百分比 */
        private Integer probabilityPercent;
        
        /** 影响程度 (low/medium/high/critical) */
        private String impact;
        
        /** 风险等级 (low/medium/medium_high/high) */
        private String riskLevel;
        
        /** 风险等级图标 */
        private String riskIcon;
        
        /** 缓解措施 */
        private String mitigation;
    }
    
    /** 测试覆盖不足区域 */
    private List<UncoveredArea> uncoveredAreas;
    
    @Data
    public static class UncoveredArea {
        /** 区域名称 */
        private String areaName;
        
        /** 覆盖率 */
        private Integer coveragePercent;
        
        /** 建议 */
        private String recommendation;
    }
    
    // ==================== 模块8: 结论与建议 (Conclusion & Recommendations) ====================
    
    /** 总体结论详情 */
    private OverallConclusion overallConclusion;
    
    @Data
    public static class OverallConclusion {
        /** 测试结论 (通过/有风险通过/不通过) */
        private String testConclusion;
        
        /** 质量评估 (优秀/良好/一般/差) */
        private String qualityAssessment;
        
        /** 发布建议 (可以发布/谨慎发布/不建议发布) */
        private String releaseRecommendation;
        
        /** 风险等级 (低/中/高) */
        private String riskLevel;
        
        /** 综合评价 */
        private String comprehensiveEvaluation;
    }
    
    /** 发布检查清单 */
    private ReleaseChecklist releaseChecklist;
    
    @Data
    public static class ReleaseChecklist {
        /** 必须修复的缺陷 */
        private List<DefectItem> mustFix;
        
        /** 建议修复的缺陷 */
        private List<DefectItem> shouldFix;
        
        /** 可延后修复的缺陷 */
        private List<DefectItem> canDefer;
        
        /** 建议发布时间 */
        private String suggestedReleaseDate;
    }
    
    @Data
    public static class DefectItem {
        /** 优先级 */
        private String priority;
        
        /** 缺陷描述 */
        private String description;
        
        /** 影响说明 */
        private String impact;
    }
    
    /** 后续改进建议 */
    private ImprovementPlan improvementPlan;
    
    @Data
    public static class ImprovementPlan {
        /** 短期改进 (1-2周) */
        private List<String> shortTerm;
        
        /** 中期改进 (1-2月) */
        private List<String> mediumTerm;
        
        /** 长期改进 (3-6月) */
        private List<String> longTerm;
    }
    
    // ==================== 图表数据 ====================
    
    /** 缺陷趋势数据 */
    private List<DefectTrend> defectTrends;
    
    @Data
    public static class DefectTrend {
        /** 日期 */
        private String date;
        
        /** 新增缺陷 */
        private Integer newDefects;
        
        /** 已关闭缺陷 */
        private Integer closedDefects;
        
        /** 累计未解决缺陷 */
        private Integer unresolvedDefects;
    }
    
    /** 测试执行趋势数据 */
    private List<ExecutionTrend> executionTrends;
    
    @Data
    public static class ExecutionTrend {
        /** 日期 */
        private String date;
        
        /** 当日执行用例数 */
        private Integer dailyExecuted;
        
        /** 累计执行用例数 */
        private Integer cumulativeExecuted;
    }
    
    // ==================== 基础统计数据 (用于图表) ====================
    
    /** 总用例数 */
    private Integer totalCases;
    
    /** 已执行数 */
    private Integer executedCases;
    
    /** 通过数 */
    private Integer passedCases;
    
    /** 失败数 */
    private Integer failedCases;
    
    /** 异常数 */
    private Integer brokenCases;
    
    /** 跳过数 */
    private Integer skippedCases;
    
    /** 成功率 */
    private BigDecimal successRate;
    
    /** 测试持续时间 (毫秒) */
    private Long duration;
    
    /** 测试环境 */
    private String environment;
    
    /** 报告类型 */
    private String reportType;
}

