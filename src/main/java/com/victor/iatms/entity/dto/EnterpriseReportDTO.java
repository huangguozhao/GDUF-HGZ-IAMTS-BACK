package com.victor.iatms.entity.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 企业级测试报告DTO
 * 符合ISO/IEC/IEEE 29119标准和ISTQB最佳实践
 * 
 * @author Victor
 * @since 2024-10-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseReportDTO {
    
    // ==================== 第一部分：报告头信息 ====================
    
    /**
     * 报告ID
     */
    private Long reportId;
    
    /**
     * 报告标题（格式：【项目名】版本号 报告类型测试报告）
     */
    private String reportTitle;
    
    /**
     * 项目/产品名称
     */
    private String projectName;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 报告编号（格式：TR-YYYYMMDD-0001）
     */
    private String reportNumber;
    
    /**
     * 测试周期开始日期
     */
    private LocalDateTime testStartDate;
    
    /**
     * 测试周期结束日期
     */
    private LocalDateTime testEndDate;
    
    /**
     * 报告生成日期
     */
    private LocalDateTime reportDate;
    
    /**
     * 编写人
     */
    private String testerName;
    
    /**
     * 评审人
     */
    private String reviewerName;
    
    /**
     * 报告状态（draft/under_review/approved）
     */
    private String reportStatus;
    
    /**
     * 公司/部门LOGO URL
     */
    private String logoUrl;
    
    // ==================== 第二部分：执行摘要 ====================
    
    /**
     * 核心结论（pass_recommend/pass_with_risk/not_pass）
     */
    private String conclusion;
    
    /**
     * 详细结论说明
     */
    private String detailedConclusion;
    
    /**
     * 关键指标
     */
    private KeyMetrics keyMetrics;
    
    /**
     * 风险评估
     */
    private RiskAssessment riskAssessment;
    
    /**
     * Top 3风险项
     */
    private List<TopRisk> topRisks;
    
    // ==================== 第三部分：测试范围与背景 ====================
    
    /**
     * 测试目标
     */
    private String testObjective;
    
    /**
     * 测试范围列表
     */
    private List<TestScopeItem> testScopes;
    
    /**
     * 测试类型
     */
    private List<TestType> testTypes;
    
    /**
     * 入口准则
     */
    private List<String> entryCriteria;
    
    /**
     * 出口准则
     */
    private List<String> exitCriteria;
    
    // ==================== 第四部分：测试环境与配置 ====================
    
    /**
     * 环境配置
     */
    private EnvironmentConfig environmentConfig;
    
    /**
     * 测试数据说明
     */
    private String testDataDescription;
    
    // ==================== 第五部分：测试结果与度量分析 ====================
    
    /**
     * 模块测试结果
     */
    private List<ModuleTestResult> moduleResults;
    
    /**
     * 每日测试进度
     */
    private List<DailyProgress> dailyProgress;
    
    /**
     * 缺陷度量指标
     */
    private DefectMetrics defectMetrics;
    
    /**
     * 缺陷趋势数据
     */
    private List<DefectTrend> defectTrends;
    
    /**
     * 性能测试结果（可选）
     */
    private PerformanceResult performanceResult;
    
    // ==================== 第六部分：详细的缺陷信息 ====================
    
    /**
     * 未解决的P0/P1缺陷
     */
    private List<DefectDetail> unresolvedCriticalDefects;
    
    /**
     * 代表性P2缺陷（Top 5）
     */
    private List<DefectDetail> topP2Defects;
    
    // ==================== 第七部分：挑战与风险 ====================
    
    /**
     * 测试过程中遇到的挑战
     */
    private List<Challenge> challenges;
    
    /**
     * 潜在风险识别
     */
    private List<RiskItem> potentialRisks;
    
    /**
     * 测试覆盖不足区域
     */
    private List<String> insufficientCoverageAreas;
    
    // ==================== 第八部分：结论与建议 ====================
    
    /**
     * 发布决策
     */
    private ReleaseDecision releaseDecision;
    
    /**
     * 后续行动计划
     */
    private ActionPlan actionPlan;
    
    /**
     * 测试团队建议
     */
    private List<String> teamRecommendations;
    
    // ==================== 内部类定义 ====================
    
    /**
     * 关键指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyMetrics {
        private Double testPassRate;           // 测试通过率
        private Double defectDensity;          // 缺陷密度（个/百用例）
        private Integer criticalDefectCount;   // 高优先级缺陷数（P0+P1）
        private Double defectFixRate;          // 缺陷修复率
        private Double requirementCoverage;    // 需求覆盖率
        private Integer testEfficiency;        // 测试效率（用例数/天）
        private String trendVsPrevious;        // 与上次对比趋势
    }
    
    /**
     * 风险评估
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessment {
        private String overallRiskLevel;       // 总体风险等级（low/medium/high/critical）
        private Map<String, Integer> riskMatrix; // 风险矩阵数据
    }
    
    /**
     * Top风险项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRisk {
        private String priority;               // P0/P1/P2
        private String title;                  // 风险标题
        private String impact;                 // 影响描述
        private String recommendation;         // 建议
    }
    
    /**
     * 测试范围项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestScopeItem {
        private String moduleName;             // 模块名称
        private Boolean isTested;              // 是否测试
        private String testType;               // 测试类型
        private String remark;                 // 备注
    }
    
    /**
     * 测试类型
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestType {
        private String typeName;               // 类型名称
        private Boolean isIncluded;            // 是否包含
        private String remark;                 // 备注
    }
    
    /**
     * 环境配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvironmentConfig {
        private List<EnvComponent> frontend;   // 前端环境
        private List<EnvComponent> backend;    // 后端环境
        private List<EnvComponent> hardware;   // 硬件配置
    }
    
    /**
     * 环境组件
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvComponent {
        private String componentType;          // 组件类型
        private String componentName;          // 组件名称
        private String version;                // 版本/配置
    }
    
    /**
     * 模块测试结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleTestResult {
        private String moduleName;             // 模块名称
        private Integer totalCases;            // 总用例数
        private Integer executedCases;         // 已执行
        private Integer passedCases;           // 通过
        private Integer failedCases;           // 失败
        private Integer blockedCases;          // 阻塞
        private Integer skippedCases;          // 跳过
        private Double passRate;               // 通过率
    }
    
    /**
     * 每日测试进度
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgress {
        private String date;                   // 日期
        private Integer plannedCases;          // 计划执行
        private Integer actualExecuted;        // 实际执行
        private Integer actualPassed;          // 实际通过
    }
    
    /**
     * 缺陷度量指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefectMetrics {
        private Integer totalDefects;          // 总缺陷数
        private Integer p0Count;               // P0数量
        private Integer p1Count;               // P1数量
        private Integer p2Count;               // P2数量
        private Integer p3Count;               // P3数量
        private Map<String, Integer> byModule; // 按模块分布
        private Map<String, Integer> byStatus; // 按状态分布
        private Double defectDiscoveryRate;    // 缺陷发现率（个/天）
        private Double avgFixCycle;            // 平均修复周期（天）
        private Double defectReopenRate;       // 缺陷重开率（%）
        private Double defectRemainRate;       // 缺陷遗留率（%）
    }
    
    /**
     * 缺陷趋势
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefectTrend {
        private String date;                   // 日期
        private Integer newDefects;            // 新增缺陷
        private Integer closedDefects;         // 关闭缺陷
        private Integer cumulativeUnresolved;  // 累计未解决
    }
    
    /**
     * 性能测试结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceResult {
        private Integer p50ResponseTime;       // P50响应时间(ms)
        private Integer p90ResponseTime;       // P90响应时间(ms)
        private Integer p95ResponseTime;       // P95响应时间(ms)
        private Integer p99ResponseTime;       // P99响应时间(ms)
        private List<ConcurrencyTest> concurrencyTests; // 并发测试数据
    }
    
    /**
     * 并发测试
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConcurrencyTest {
        private Integer concurrentUsers;       // 并发用户数
        private Integer avgResponseTime;       // 平均响应时间(ms)
    }
    
    /**
     * 缺陷详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefectDetail {
        private String defectId;               // 缺陷ID
        private String title;                  // 标题
        private String module;                 // 模块
        private String priority;               // 优先级
        private String severity;               // 严重程度
        private String status;                 // 状态
        private String assignee;               // 责任人
        private LocalDateTime discoveredDate;  // 发现日期
        private LocalDateTime expectedFixDate; // 预计修复日期
        private String description;            // 描述
        private String impact;                 // 影响
    }
    
    /**
     * 挑战
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Challenge {
        private String challengeType;          // 挑战类型
        private String problem;                // 具体问题
        private String impact;                 // 影响
        private String solution;               // 解决方案
        private String status;                 // 状态（resolved/in_progress/pending）
    }
    
    /**
     * 风险项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskItem {
        private String riskName;               // 风险名称
        private String probability;            // 发生概率（low/medium/high）
        private String impact;                 // 影响程度（low/medium/high/critical）
        private String riskLevel;              // 风险等级
        private String mitigation;             // 缓解措施
    }
    
    /**
     * 发布决策
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReleaseDecision {
        private String decision;               // 决策（recommend/risk_accept/not_recommend）
        private String summary;                // 决策摘要
        private List<DecisionCriteria> criteria; // 决策标准
    }
    
    /**
     * 决策标准
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecisionCriteria {
        private String criteriaName;           // 标准名称
        private String currentValue;           // 当前值
        private String targetValue;            // 目标值
        private Boolean isMet;                 // 是否达标
    }
    
    /**
     * 行动计划
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionPlan {
        private List<ActionItem> immediateActions;  // 立即行动
        private List<ActionItem> shortTermActions;  // 短期行动
        private List<ActionItem> longTermActions;   // 长期行动
    }
    
    /**
     * 行动项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionItem {
        private String priority;               // 优先级（P1/P2/P3）
        private String description;            // 描述
        private String assignee;               // 负责人
        private LocalDateTime dueDate;         // 截止日期
        private Boolean isCompleted;           // 是否完成
    }
}

