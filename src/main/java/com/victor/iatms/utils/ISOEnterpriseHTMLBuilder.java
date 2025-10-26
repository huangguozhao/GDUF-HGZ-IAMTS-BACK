package com.victor.iatms.utils;

import com.victor.iatms.entity.dto.ISOEnterpriseReportDTO;
import java.time.format.DateTimeFormatter;

/**
 * ISO/IEC/IEEE 29119标准企业级报告HTML构建器
 * 使用专业的企业级模板
 * 
 * @author Victor
 * @since 2024-10-26
 */
public class ISOEnterpriseHTMLBuilder {
    
    private final StringBuilder html;
    private final ISOEnterpriseReportDTO reportData;
    private final String locale;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    
    public ISOEnterpriseHTMLBuilder(ISOEnterpriseReportDTO reportData, String locale) {
        this.html = new StringBuilder(150000); // 预分配150KB
        this.reportData = reportData;
        this.locale = locale != null ? locale : "zh_CN";
    }
    
    /**
     * 构建完整的HTML文档
     */
    public String build() {
        buildDoctype();
        buildHead();
        buildBody();
        return html.toString();
    }
    
    private void buildDoctype() {
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
    }
    
    private void buildHead() {
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>").append(escapeHtml(reportData.getReportTitle())).append(" - 企业级模板</title>\n");
        html.append("  <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n");
        buildStyles();
        html.append("</head>\n");
    }
    
    private void buildStyles() {
        html.append("  <style>\n");
        // 引入完整的CSS样式（与模板一致）
        html.append("    /* ==================== 全局样式 ==================== */\n");
        html.append("    * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("    body { font-family: 'Microsoft YaHei', 'Arial', sans-serif; background: #f5f7fa; color: #2c3e50; line-height: 1.8; padding: 20px; }\n");
        html.append("    .container { max-width: 1200px; margin: 0 auto; background: white; box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1); border-radius: 8px; overflow: hidden; }\n");
        
        // 报告头部样式
        html.append("    .report-header { background: linear-gradient(135deg, #1f3a93 0%, #2e5cb8 100%); color: white; padding: 40px 50px; position: relative; }\n");
        html.append("    .report-header::before { content: ''; position: absolute; top: 0; right: 0; width: 300px; height: 100%; background: url('data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><circle cx=\"50\" cy=\"50\" r=\"40\" fill=\"rgba(255,255,255,0.05)\"/></svg>'); opacity: 0.3; }\n");
        html.append("    .header-title { font-size: 32px; font-weight: bold; margin-bottom: 20px; position: relative; z-index: 1; }\n");
        html.append("    .header-meta { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; position: relative; z-index: 1; }\n");
        html.append("    .meta-item { display: flex; align-items: center; gap: 10px; padding: 10px 15px; background: rgba(255, 255, 255, 0.15); border-radius: 6px; backdrop-filter: blur(10px); }\n");
        html.append("    .meta-label { font-size: 14px; opacity: 0.9; }\n");
        html.append("    .meta-value { font-size: 16px; font-weight: 600; }\n");
        
        // 执行摘要样式
        html.append("    .executive-summary { padding: 40px 50px; background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%); border-bottom: 3px solid #1f3a93; }\n");
        html.append("    .section-title { font-size: 24px; font-weight: bold; color: #1f3a93; margin-bottom: 25px; padding-bottom: 10px; border-bottom: 3px solid #409eff; display: flex; align-items: center; gap: 10px; }\n");
        html.append("    .conclusion-banner { padding: 30px; border-radius: 12px; margin-bottom: 30px; display: flex; align-items: center; gap: 25px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); }\n");
        html.append("    .conclusion-banner.success { background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%); border-left: 6px solid #28a745; }\n");
        html.append("    .conclusion-banner.warning { background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%); border-left: 6px solid #ffc107; }\n");
        html.append("    .conclusion-banner.danger { background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%); border-left: 6px solid #dc3545; }\n");
        html.append("    .conclusion-icon { font-size: 64px; line-height: 1; }\n");
        html.append("    .conclusion-text h2 { font-size: 28px; margin-bottom: 10px; color: #2c3e50; }\n");
        html.append("    .conclusion-text p { font-size: 16px; color: #555; }\n");
        
        // KPI仪表盘
        html.append("    .kpi-dashboard { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 20px; margin-bottom: 30px; }\n");
        html.append("    .kpi-card { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); text-align: center; transition: all 0.3s; border-top: 4px solid #409eff; }\n");
        html.append("    .kpi-card:hover { transform: translateY(-5px); box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15); }\n");
        html.append("    .kpi-card.success { border-top-color: #67c23a; }\n");
        html.append("    .kpi-card.warning { border-top-color: #e6a23c; }\n");
        html.append("    .kpi-card.danger { border-top-color: #f56c6c; }\n");
        html.append("    .kpi-label { font-size: 14px; color: #909399; margin-bottom: 10px; }\n");
        html.append("    .kpi-value { font-size: 36px; font-weight: bold; color: #2c3e50; margin-bottom: 8px; font-family: 'Consolas', monospace; }\n");
        html.append("    .kpi-trend { font-size: 12px; padding: 4px 12px; border-radius: 20px; display: inline-block; }\n");
        html.append("    .kpi-trend.up { background: #f0f9ff; color: #67c23a; }\n");
        
        // 内容区域
        html.append("    .content-section { padding: 40px 50px; border-bottom: 1px solid #e4e7ed; }\n");
        
        // 表格样式
        html.append("    .data-table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); border-radius: 8px; overflow: hidden; }\n");
        html.append("    .data-table thead { background: #f5f7fa; }\n");
        html.append("    .data-table th { padding: 15px; text-align: left; font-weight: 600; color: #606266; border-bottom: 2px solid #e4e7ed; font-size: 14px; }\n");
        html.append("    .data-table td { padding: 12px 15px; border-bottom: 1px solid #ebeef5; font-size: 14px; color: #606266; }\n");
        html.append("    .data-table tbody tr:hover { background: #f5f7fa; }\n");
        html.append("    .data-table tbody tr:last-child td { border-bottom: none; }\n");
        
        // 徽章样式
        html.append("    .badge { display: inline-block; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 500; white-space: nowrap; }\n");
        html.append("    .badge-success { background: #f0f9ff; color: #67c23a; border: 1px solid #c6e2ff; }\n");
        html.append("    .badge-warning { background: #fdf6ec; color: #e6a23c; border: 1px solid #f5dab1; }\n");
        html.append("    .badge-danger { background: #fef0f0; color: #f56c6c; border: 1px solid #fde2e2; }\n");
        html.append("    .badge-info { background: #f4f4f5; color: #909399; border: 1px solid #e4e7ed; }\n");
        
        // 图表容器
        html.append("    .charts-container { margin: 30px 0; }\n");
        html.append("    .chart-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 20px; margin-bottom: 20px; }\n");
        html.append("    .chart-card { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }\n");
        html.append("    .chart-title { font-size: 18px; font-weight: 600; color: #2c3e50; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 2px solid #e4e7ed; }\n");
        html.append("    .chart-content { width: 100%; height: 400px; }\n");
        
        // 缺陷详情样式
        html.append("    .defect-card { background: white; border: 1px solid #e0e0e0; border-radius: 8px; padding: 25px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); transition: all 0.3s; }\n");
        html.append("    .defect-card:hover { box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1); }\n");
        html.append("    .defect-card.p0 { border-left: 5px solid #8b0000; }\n");
        html.append("    .defect-card.p1 { border-left: 5px solid #f56c6c; }\n");
        html.append("    .defect-card.p2 { border-left: 5px solid #e6a23c; }\n");
        html.append("    .defect-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; cursor: pointer; }\n");
        html.append("    .defect-title { font-size: 18px; font-weight: 600; color: #333; flex: 1; }\n");
        html.append("    .defect-content { display: none; margin-top: 20px; padding-top: 20px; border-top: 2px solid #f0f0f0; }\n");
        html.append("    .defect-content.expanded { display: block; }\n");
        html.append("    .error-box { background: #fff3f3; border-left: 4px solid #f56c6c; padding: 15px; border-radius: 4px; margin: 10px 0; }\n");
        html.append("    .error-message { color: #333; font-size: 14px; margin-bottom: 10px; line-height: 1.6; }\n");
        html.append("    .error-trace { background: #f8f9fa; border: 1px solid #ddd; padding: 12px; border-radius: 4px; font-family: 'Consolas', monospace; font-size: 12px; color: #666; max-height: 200px; overflow-y: auto; white-space: pre-wrap; word-wrap: break-word; }\n");
        html.append("    .expand-icon { transition: transform 0.3s; font-size: 20px; }\n");
        html.append("    .expand-icon.expanded { transform: rotate(180deg); }\n");
        
        // 页脚
        html.append("    .report-footer { background: #f8f9fa; padding: 30px 50px; text-align: center; color: #909399; font-size: 13px; border-top: 3px solid #1f3a93; }\n");
        html.append("    .report-footer p { margin: 5px 0; }\n");
        
        // 打印和响应式
        html.append("    @media print { body { background: white; padding: 0; } .container { box-shadow: none; max-width: 100%; } .chart-content { page-break-inside: avoid; } .defect-content { display: block !important; } }\n");
        html.append("    @media (max-width: 768px) { .report-header, .content-section { padding: 20px; } .header-title { font-size: 24px; } .kpi-dashboard { grid-template-columns: repeat(2, 1fr); } .chart-row { grid-template-columns: 1fr; } .chart-content { height: 300px; } }\n");
        
        // 水印
        html.append("    .watermark { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(-45deg); font-size: 80px; color: rgba(0, 0, 0, 0.03); font-weight: bold; pointer-events: none; z-index: 0; white-space: nowrap; }\n");
        
        html.append("  </style>\n");
    }
    
    private void buildBody() {
        html.append("<body>\n");
        html.append("  <!-- 水印 -->\n");
        html.append("  <div class=\"watermark\">ISO/IEC/IEEE 29119标准测试报告</div>\n\n");
        html.append("  <div class=\"container\">\n");
        
        // 第一部分：报告头信息
        buildReportHeader();
        
        // 第二部分：执行摘要
        buildExecutiveSummary();
        
        // 第三部分：测试范围
        buildTestScope();
        
        // 第四部分：测试环境
        buildTestEnvironment();
        
        // 第五部分：测试结果与度量
        buildTestResults();
        
        // 第六部分：详细缺陷信息
        buildDetailedDefects();
        
        // 第七部分：挑战与风险
        buildChallengesAndRisks();
        
        // 第八部分：结论与建议
        buildConclusionAndRecommendations();
        
        // 页脚
        buildFooter();
        
        html.append("  </div>\n\n");
        
        // JavaScript
        buildJavaScript();
        
        html.append("</body>\n");
        html.append("</html>");
    }
    
    /**
     * 第一部分：报告头信息
     */
    private void buildReportHeader() {
        html.append("    <!-- ==================== 第一部分：报告头信息 ==================== -->\n");
        html.append("    <div class=\"report-header\">\n");
        html.append("      <h1 class=\"header-title\">📊 ").append(escapeHtml(reportData.getReportTitle())).append("</h1>\n");
        html.append("      <div class=\"header-meta\">\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">报告编号:</span><span class=\"meta-value\">").append(escapeHtml(reportData.getReportNumber())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">项目名称:</span><span class=\"meta-value\">").append(escapeHtml(reportData.getProjectName())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">版本号:</span><span class=\"meta-value\">").append(escapeHtml(reportData.getVersion())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">测试周期:</span><span class=\"meta-value\">")
            .append(formatDate(reportData.getTestStartDate())).append(" ~ ").append(formatDate(reportData.getTestEndDate())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">报告日期:</span><span class=\"meta-value\">").append(formatDate(reportData.getReportDate())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">编写人:</span><span class=\"meta-value\">").append(escapeHtml(reportData.getTesterName())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">评审人:</span><span class=\"meta-value\">").append(escapeHtml(reportData.getReviewerName())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"meta-label\">报告状态:</span><span class=\"meta-value\">").append(formatReportStatus(reportData.getReportStatus())).append("</span></div>\n");
        html.append("      </div>\n");
        html.append("    </div>\n\n");
    }
    
    /**
     * 第二部分：执行摘要
     */
    private void buildExecutiveSummary() {
        html.append("    <!-- ==================== 第二部分：执行摘要 ==================== -->\n");
        html.append("    <div class=\"executive-summary\">\n");
        html.append("      <h2 class=\"section-title\">📋 执行摘要</h2>\n\n");
        
        // 核心结论横幅
        String conclusionClass = getConclusionClass(reportData.getConclusion());
        String conclusionIcon = getConclusionIcon(reportData.getConclusion());
        String conclusionTitle = getConclusionTitle(reportData.getConclusion());
        
        html.append("      <!-- 核心结论 -->\n");
        html.append("      <div class=\"conclusion-banner ").append(conclusionClass).append("\">\n");
        html.append("        <div class=\"conclusion-icon\">").append(conclusionIcon).append("</div>\n");
        html.append("        <div class=\"conclusion-text\">\n");
        html.append("          <h2>").append(conclusionTitle).append("</h2>\n");
        html.append("          <p>").append(escapeHtml(reportData.getDetailedConclusion())).append("</p>\n");
        html.append("        </div>\n");
        html.append("      </div>\n\n");
        
        // KPI仪表盘
        ISOEnterpriseReportDTO.KeyMetrics metrics = reportData.getKeyMetrics();
        if (metrics != null) {
            html.append("      <!-- KPI仪表盘 -->\n");
            html.append("      <h3 style=\"font-size: 18px; margin-bottom: 15px; color: #2c3e50;\">📊 关键指标一览</h3>\n");
            html.append("      <div class=\"kpi-dashboard\">\n");
            
            // 测试通过率
            String passRateClass = metrics.getTestPassRate().doubleValue() >= 95 ? "success" : (metrics.getTestPassRate().doubleValue() >= 85 ? "warning" : "danger");
            html.append("        <div class=\"kpi-card ").append(passRateClass).append("\">\n");
            html.append("          <div class=\"kpi-label\">测试通过率</div>\n");
            html.append("          <div class=\"kpi-value\">").append(metrics.getTestPassRate()).append("%</div>\n");
            html.append("          <div class=\"kpi-trend up\">目标: ≥").append(metrics.getTargetPassRate()).append("%</div>\n");
            html.append("        </div>\n");
            
            // 缺陷密度
            html.append("        <div class=\"kpi-card success\">\n");
            html.append("          <div class=\"kpi-label\">缺陷密度</div>\n");
            html.append("          <div class=\"kpi-value\">").append(metrics.getDefectDensity()).append("</div>\n");
            html.append("          <div class=\"kpi-trend up\">个/百用例</div>\n");
            html.append("        </div>\n");
            
            // 高优先级缺陷
            String criticalClass = metrics.getCriticalDefectCount() == 0 ? "success" : (metrics.getCriticalDefectCount() <= 2 ? "warning" : "danger");
            html.append("        <div class=\"kpi-card ").append(criticalClass).append("\">\n");
            html.append("          <div class=\"kpi-label\">高优先级缺陷</div>\n");
            html.append("          <div class=\"kpi-value\">").append(metrics.getCriticalDefectCount()).append("</div>\n");
            html.append("          <div class=\"kpi-trend up\">P0 + P1</div>\n");
            html.append("        </div>\n");
            
            // 缺陷修复率
            html.append("        <div class=\"kpi-card warning\">\n");
            html.append("          <div class=\"kpi-label\">缺陷修复率</div>\n");
            html.append("          <div class=\"kpi-value\">").append(metrics.getDefectFixRate()).append("%</div>\n");
            html.append("          <div class=\"kpi-trend up\">已修复缺陷</div>\n");
            html.append("        </div>\n");
            
            // 需求覆盖率
            html.append("        <div class=\"kpi-card success\">\n");
            html.append("          <div class=\"kpi-label\">需求覆盖率</div>\n");
            html.append("          <div class=\"kpi-value\">").append(metrics.getRequirementCoverage()).append("%</div>\n");
            html.append("          <div class=\"kpi-trend up\">已覆盖</div>\n");
            html.append("        </div>\n");
            
            // 测试效率
            html.append("        <div class=\"kpi-card\">\n");
            html.append("          <div class=\"kpi-label\">测试效率</div>\n");
            html.append("          <div class=\"kpi-value\">").append(metrics.getTestEfficiency()).append("</div>\n");
            html.append("          <div class=\"kpi-trend up\">用例/天</div>\n");
            html.append("        </div>\n");
            
            html.append("      </div>\n");
        }
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 第三部分：测试范围
     */
    private void buildTestScope() {
        ISOEnterpriseReportDTO.TestScope scope = reportData.getTestScope();
        if (scope == null) return;
        
        html.append("    <!-- ==================== 第三部分：测试范围 ==================== -->\n");
        html.append("    <div class=\"content-section\">\n");
        html.append("      <h2 class=\"section-title\">🎯 测试范围与背景</h2>\n\n");
        
        html.append("      <h3 style=\"font-size: 18px; margin-bottom: 15px; color: #2c3e50;\">测试目标</h3>\n");
        html.append("      <ul style=\"margin-left: 25px; line-height: 2;\">\n");
        if (scope.getTestObjectives() != null) {
            for (String objective : scope.getTestObjectives()) {
                html.append("        <li>").append(escapeHtml(objective)).append("</li>\n");
            }
        }
        html.append("      </ul>\n\n");
        
        html.append("      <h3 style=\"font-size: 18px; margin: 25px 0 15px; color: #2c3e50;\">测试范围</h3>\n");
        html.append("      <p>• 核心业务流程: ").append(escapeHtml(scope.getCoreBusinessProcesses())).append("</p>\n");
        if (scope.getTestTypes() != null && !scope.getTestTypes().isEmpty()) {
            html.append("      <p>• 测试类型: ").append(String.join("、", scope.getTestTypes())).append("</p>\n");
        }
        if (scope.getTestMethods() != null && !scope.getTestMethods().isEmpty()) {
            html.append("      <p>• 测试方法: ").append(String.join("、", scope.getTestMethods())).append("</p>\n");
        }
        html.append("      <p>• 覆盖模块: ").append(scope.getModuleCount()).append(" 个核心模块</p>\n");
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 第四部分：测试环境
     */
    private void buildTestEnvironment() {
        ISOEnterpriseReportDTO.TestEnvironment env = reportData.getTestEnvironment();
        if (env == null) return;
        
        html.append("    <!-- ==================== 第四部分：测试环境 ==================== -->\n");
        html.append("    <div class=\"content-section\">\n");
        html.append("      <h2 class=\"section-title\">💻 测试环境与配置</h2>\n\n");
        
        html.append("      <table class=\"data-table\">\n");
        html.append("        <thead><tr><th style=\"width: 20%;\">类型</th><th style=\"width: 30%;\">组件</th><th>版本/配置</th></tr></thead>\n");
        html.append("        <tbody>\n");
        html.append("          <tr><td><strong>环境</strong></td><td>环境名称</td><td>").append(escapeHtml(env.getEnvironmentName())).append(" (").append(escapeHtml(env.getEnvironmentType())).append(")</td></tr>\n");
        html.append("          <tr><td><strong>后端</strong></td><td>应用服务器</td><td>").append(escapeHtml(env.getBackendVersion())).append("</td></tr>\n");
        html.append("          <tr><td rowspan=\"2\"><strong>数据库</strong></td><td>数据库</td><td>").append(escapeHtml(env.getDatabaseInfo())).append("</td></tr>\n");
        html.append("          <tr><td>服务器</td><td>").append(escapeHtml(env.getServerAddress())).append("</td></tr>\n");
        if (env.getBrowserDeviceCoverage() != null && !env.getBrowserDeviceCoverage().isEmpty()) {
            html.append("          <tr><td><strong>浏览器</strong></td><td>覆盖范围</td><td>").append(String.join(", ", env.getBrowserDeviceCoverage())).append("</td></tr>\n");
        }
        html.append("        </tbody>\n");
        html.append("      </table>\n");
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 第五部分：测试结果与度量
     */
    private void buildTestResults() {
        html.append("    <!-- ==================== 第五部分：测试结果与度量 ==================== -->\n");
        html.append("    <div class=\"content-section\">\n");
        html.append("      <h2 class=\"section-title\">📊 测试结果与度量分析</h2>\n\n");
        
        // 测试执行概览
        html.append("      <h3 style=\"font-size: 18px; margin-bottom: 15px; color: #2c3e50;\">5.1 测试执行概览</h3>\n");
        html.append("      <table class=\"data-table\">\n");
        html.append("        <thead><tr><th>模块名称</th><th>总用例数</th><th>已执行</th><th>通过</th><th>失败</th><th>阻塞</th><th>跳过</th><th>通过率</th></tr></thead>\n");
        html.append("        <tbody>\n");
        
        if (reportData.getModuleResults() != null) {
            for (ISOEnterpriseReportDTO.ModuleResult module : reportData.getModuleResults()) {
                String passRateClass = module.getPassRate().doubleValue() >= 95 ? "badge-success" : (module.getPassRate().doubleValue() >= 85 ? "badge-warning" : "badge-danger");
                html.append("          <tr>\n");
                html.append("            <td>").append(escapeHtml(module.getModuleName())).append("</td>\n");
                html.append("            <td>").append(module.getTotalCases()).append("</td>\n");
                html.append("            <td>").append(module.getExecutedCases()).append("</td>\n");
                html.append("            <td>").append(module.getPassedCases()).append("</td>\n");
                html.append("            <td>").append(module.getFailedCases()).append("</td>\n");
                html.append("            <td>").append(module.getBrokenCases()).append("</td>\n");
                html.append("            <td>").append(module.getSkippedCases()).append("</td>\n");
                html.append("            <td><span class=\"").append(passRateClass).append("\">").append(module.getPassRate()).append("%</span></td>\n");
                html.append("          </tr>\n");
            }
        }
        
        html.append("        </tbody>\n");
        html.append("      </table>\n\n");
        
        // 图表区域
        html.append("      <div class=\"charts-container\">\n");
        html.append("        <div class=\"chart-row\">\n");
        html.append("          <div class=\"chart-card\"><div class=\"chart-title\">🎯 用例状态分布</div><div id=\"caseStatusChart\" class=\"chart-content\"></div></div>\n");
        html.append("          <div class=\"chart-card\"><div class=\"chart-title\">🐛 缺陷优先级分布</div><div id=\"defectSeverityChart\" class=\"chart-content\"></div></div>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"chart-row\">\n");
        html.append("          <div class=\"chart-card\"><div class=\"chart-title\">📊 测试通过率仪表盘</div><div id=\"gaugeChart\" class=\"chart-content\"></div></div>\n");
        html.append("          <div class=\"chart-card\"><div class=\"chart-title\">📈 缺陷趋势分析</div><div id=\"defectTrendChart\" class=\"chart-content\"></div></div>\n");
        html.append("        </div>\n");
        html.append("      </div>\n");
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 第六部分：详细缺陷信息
     */
    private void buildDetailedDefects() {
        html.append("    <!-- ==================== 第六部分：详细缺陷信息 ==================== -->\n");
        html.append("    <div class=\"content-section\">\n");
        html.append("      <h2 class=\"section-title\">🐛 详细缺陷信息</h2>\n\n");
        
        ISOEnterpriseReportDTO.DefectMetrics defectMetrics = reportData.getDefectMetrics();
        if (defectMetrics != null) {
            html.append("      <h3 style=\"font-size: 18px; margin-bottom: 15px; color: #2c3e50;\">6.1 缺陷统计概览</h3>\n");
            html.append("      <p style=\"font-size: 16px; margin-bottom: 15px;\">总缺陷数: <strong>").append(defectMetrics.getTotalDefects()).append("</strong></p>\n");
            html.append("      <p>• P0 阻塞: ").append(defectMetrics.getP0Count()).append(" ❌</p>\n");
            html.append("      <p>• P1 重要: ").append(defectMetrics.getP1Count()).append(" 🔴</p>\n");
            html.append("      <p>• P2 一般: ").append(defectMetrics.getP2Count()).append(" 🟠</p>\n");
            html.append("      <p>• P3 轻微: ").append(defectMetrics.getP3Count()).append(" 🟡</p>\n\n");
        }
        
        // 详细缺陷列表（可展开）
        if (reportData.getDetailedDefects() != null && !reportData.getDetailedDefects().isEmpty()) {
            html.append("      <h3 style=\"font-size: 18px; margin: 25px 0 15px; color: #2c3e50;\">6.2 详细缺陷信息（点击展开）</h3>\n");
            
            for (ISOEnterpriseReportDTO.DetailedDefect defect : reportData.getDetailedDefects()) {
                String priorityClass = defect.getPriority() != null ? defect.getPriority().toLowerCase() : "p3";
                
                html.append("      <div class=\"defect-card ").append(priorityClass).append("\">\n");
                html.append("        <div class=\"defect-header\" onclick=\"toggleDefect(").append(defect.getDefectIndex()).append(")\">\n");
                html.append("          <div class=\"defect-title\">[").append(defect.getDefectIndex()).append("] ").append(escapeHtml(defect.getCaseName())).append("</div>\n");
                html.append("          <div style=\"display: flex; gap: 8px; align-items: center;\">\n");
                html.append("            <span class=\"badge\" style=\"background: ").append(getPriorityColor(defect.getPriority())).append("; color: white;\">").append(defect.getPriority()).append("</span>\n");
                html.append("            <span class=\"expand-icon\" id=\"icon-").append(defect.getDefectIndex()).append("\">▼</span>\n");
                html.append("          </div>\n");
                html.append("        </div>\n");
                
                // 可展开内容
                html.append("        <div class=\"defect-content\" id=\"defect-").append(defect.getDefectIndex()).append("\">\n");
                
                // 错误详情
                if (defect.getErrorMessage() != null || defect.getStackTrace() != null) {
                    html.append("          <div class=\"error-box\">\n");
                    html.append("            <div style=\"font-weight: bold; color: #f56c6c; margin-bottom: 8px; font-size: 14px;\">🔍 错误详情</div>\n");
                    if (defect.getErrorMessage() != null) {
                        html.append("            <div class=\"error-message\">").append(escapeHtml(defect.getErrorMessage())).append("</div>\n");
                    }
                    if (defect.getStackTrace() != null && !defect.getStackTrace().trim().isEmpty()) {
                        html.append("            <div style=\"margin-top: 10px;\"><strong>堆栈跟踪:</strong></div>\n");
                        html.append("            <div class=\"error-trace\">").append(escapeHtml(defect.getStackTrace())).append("</div>\n");
                    }
                    html.append("          </div>\n");
                }
                
                // 根因分析
                if (defect.getRootCauseAnalysis() != null) {
                    html.append("          <div style=\"margin-top: 15px;\">\n");
                    html.append("            <div style=\"font-weight: bold; color: #606266; margin-bottom: 8px;\">💡 根因分析</div>\n");
                    html.append("            <p>").append(escapeHtml(defect.getRootCauseAnalysis())).append("</p>\n");
                    html.append("          </div>\n");
                }
                
                // 建议措施
                if (defect.getSuggestedActions() != null) {
                    html.append("          <div style=\"margin-top: 15px;\">\n");
                    html.append("            <div style=\"font-weight: bold; color: #606266; margin-bottom: 8px;\">🎯 建议措施</div>\n");
                    html.append("            <pre style=\"white-space: pre-wrap; font-family: inherit; margin: 0;\">").append(escapeHtml(defect.getSuggestedActions())).append("</pre>\n");
                    html.append("          </div>\n");
                }
                
                html.append("        </div>\n");
                html.append("      </div>\n");
            }
        } else {
            html.append("      <p style=\"text-align: center; padding: 40px; color: #67c23a; font-size: 16px;\">✅ 太棒了！所有测试用例均已通过</p>\n");
        }
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 第七部分：挑战与风险
     */
    private void buildChallengesAndRisks() {
        html.append("    <!-- ==================== 第七部分：挑战与风险 ==================== -->\n");
        html.append("    <div class=\"content-section\">\n");
        html.append("      <h2 class=\"section-title\">⚠️ 测试过程中的挑战与风险</h2>\n\n");
        
        // 已遇到的挑战
        if (reportData.getChallenges() != null && !reportData.getChallenges().isEmpty()) {
            html.append("      <h3 style=\"font-size: 18px; margin-bottom: 15px; color: #2c3e50;\">7.1 已遇到的挑战</h3>\n");
            html.append("      <ul style=\"margin-left: 25px; line-height: 2;\">\n");
            for (ISOEnterpriseReportDTO.Challenge challenge : reportData.getChallenges()) {
                html.append("        <li><strong>").append(escapeHtml(challenge.getTitle())).append(":</strong> ")
                    .append(escapeHtml(challenge.getDescription()))
                    .append(" <em>(缓解措施: ").append(escapeHtml(challenge.getMitigation())).append(")</em></li>\n");
            }
            html.append("      </ul>\n\n");
        }
        
        // 风险矩阵
        if (reportData.getRiskMatrix() != null && !reportData.getRiskMatrix().isEmpty()) {
            html.append("      <h3 style=\"font-size: 18px; margin: 25px 0 15px; color: #2c3e50;\">7.2 潜在风险识别</h3>\n");
            html.append("      <table class=\"data-table\">\n");
            html.append("        <thead><tr><th>风险项</th><th>发生概率</th><th>影响程度</th><th>风险等级</th><th>缓解措施</th></tr></thead>\n");
            html.append("        <tbody>\n");
            
            for (ISOEnterpriseReportDTO.RiskItem risk : reportData.getRiskMatrix()) {
                html.append("          <tr>\n");
                html.append("            <td>").append(escapeHtml(risk.getRiskName())).append("</td>\n");
                html.append("            <td>").append(formatProbability(risk.getProbability(), risk.getProbabilityPercent())).append("</td>\n");
                html.append("            <td>").append(formatImpact(risk.getImpact())).append("</td>\n");
                html.append("            <td><span class=\"badge\" style=\"background: ").append(getRiskColor(risk.getRiskLevel())).append("; color: white;\">")
                    .append(risk.getRiskIcon()).append(" ").append(formatRiskLevel(risk.getRiskLevel())).append("</span></td>\n");
                html.append("            <td>").append(escapeHtml(risk.getMitigation())).append("</td>\n");
                html.append("          </tr>\n");
            }
            
            html.append("        </tbody>\n");
            html.append("      </table>\n");
        }
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 第八部分：结论与建议
     */
    private void buildConclusionAndRecommendations() {
        html.append("    <!-- ==================== 第八部分：结论与建议 ==================== -->\n");
        html.append("    <div class=\"content-section\" style=\"border-bottom: none;\">\n");
        html.append("      <h2 class=\"section-title\">✅ 结论与建议</h2>\n\n");
        
        // 总体结论
        ISOEnterpriseReportDTO.OverallConclusion conclusion = reportData.getOverallConclusion();
        if (conclusion != null) {
            String conclusionClass = getConclusionClass(reportData.getConclusion());
            String conclusionIcon = getConclusionIcon(reportData.getConclusion());
            
            html.append("      <!-- 总体结论 -->\n");
            html.append("      <div class=\"conclusion-banner ").append(conclusionClass).append("\" style=\"margin-bottom: 30px;\">\n");
            html.append("        <div class=\"conclusion-icon\" style=\"font-size: 80px;\">").append(conclusionIcon).append("</div>\n");
            html.append("        <div class=\"conclusion-text\">\n");
            html.append("          <h2 style=\"font-size: 32px;\">").append(escapeHtml(conclusion.getTestConclusion())).append("</h2>\n");
            html.append("          <p style=\"font-size: 16px; margin-top: 15px;\">").append(escapeHtml(conclusion.getComprehensiveEvaluation())).append("</p>\n");
            html.append("        </div>\n");
            html.append("      </div>\n\n");
        }
        
        // 发布检查清单
        ISOEnterpriseReportDTO.ReleaseChecklist checklist = reportData.getReleaseChecklist();
        if (checklist != null) {
            html.append("      <h3 style=\"font-size: 18px; margin: 30px 0 15px; color: #2c3e50;\">8.1 发布建议清单</h3>\n");
            
            if (checklist.getMustFix() != null && !checklist.getMustFix().isEmpty()) {
                html.append("      <div style=\"background: #fef0f0; padding: 20px; border-radius: 8px; margin-bottom: 15px; border-left: 4px solid #f56c6c;\">\n");
                html.append("        <h4 style=\"color: #f56c6c; margin-bottom: 10px;\">🔴 必须修复 (Release前)</h4>\n");
                html.append("        <ul style=\"margin-left: 20px; line-height: 2.5;\">\n");
                for (ISOEnterpriseReportDTO.DefectItem item : checklist.getMustFix()) {
                    html.append("          <li><span class=\"badge\" style=\"background: #f56c6c; color: white;\">").append(item.getPriority()).append("</span> ")
                        .append(escapeHtml(item.getDescription())).append(" - ").append(escapeHtml(item.getImpact())).append("</li>\n");
                }
                html.append("        </ul>\n");
                html.append("      </div>\n");
            }
            
            if (checklist.getShouldFix() != null && !checklist.getShouldFix().isEmpty()) {
                html.append("      <div style=\"background: #fdf6ec; padding: 20px; border-radius: 8px; margin-bottom: 15px; border-left: 4px solid #e6a23c;\">\n");
                html.append("        <h4 style=\"color: #e6a23c; margin-bottom: 10px;\">🟡 建议修复 (Release后1周)</h4>\n");
                html.append("        <ul style=\"margin-left: 20px; line-height: 2.5;\">\n");
                for (ISOEnterpriseReportDTO.DefectItem item : checklist.getShouldFix()) {
                    html.append("          <li><span class=\"badge-warning\">").append(item.getPriority()).append("</span> ")
                        .append(escapeHtml(item.getDescription())).append(" - ").append(escapeHtml(item.getImpact())).append("</li>\n");
                }
                html.append("        </ul>\n");
                html.append("      </div>\n");
            }
            
            if (checklist.getSuggestedReleaseDate() != null) {
                html.append("      <p style=\"margin-top: 20px; font-size: 16px;\"><strong>📅 建议发布时间:</strong> ").append(escapeHtml(checklist.getSuggestedReleaseDate())).append("</p>\n");
            }
        }
        
        // 改进建议
        ISOEnterpriseReportDTO.ImprovementPlan plan = reportData.getImprovementPlan();
        if (plan != null) {
            html.append("      <h3 style=\"font-size: 18px; margin: 30px 0 15px; color: #2c3e50;\">8.2 后续改进建议</h3>\n");
            html.append("      <div style=\"background: #f0f9ff; padding: 25px; border-radius: 8px; line-height: 2;\">\n");
            
            if (plan.getShortTerm() != null && !plan.getShortTerm().isEmpty()) {
                html.append("        <p style=\"margin-bottom: 10px;\"><strong>🎯 短期改进 (1-2周)</strong></p>\n");
                html.append("        <ul style=\"margin-left: 25px; margin-bottom: 15px;\">\n");
                for (String item : plan.getShortTerm()) {
                    html.append("          <li>").append(escapeHtml(item)).append("</li>\n");
                }
                html.append("        </ul>\n");
            }
            
            if (plan.getMediumTerm() != null && !plan.getMediumTerm().isEmpty()) {
                html.append("        <p style=\"margin-bottom: 10px;\"><strong>🎯 中期改进 (1-2月)</strong></p>\n");
                html.append("        <ul style=\"margin-left: 25px; margin-bottom: 15px;\">\n");
                for (String item : plan.getMediumTerm()) {
                    html.append("          <li>").append(escapeHtml(item)).append("</li>\n");
                }
                html.append("        </ul>\n");
            }
            
            if (plan.getLongTerm() != null && !plan.getLongTerm().isEmpty()) {
                html.append("        <p style=\"margin-bottom: 10px;\"><strong>🎯 长期改进 (3-6月)</strong></p>\n");
                html.append("        <ul style=\"margin-left: 25px;\">\n");
                for (String item : plan.getLongTerm()) {
                    html.append("          <li>").append(escapeHtml(item)).append("</li>\n");
                }
                html.append("        </ul>\n");
            }
            
            html.append("      </div>\n");
        }
        
        html.append("    </div>\n\n");
    }
    
    /**
     * 页脚
     */
    private void buildFooter() {
        html.append("    <!-- ==================== 页脚 ==================== -->\n");
        html.append("    <div class=\"report-footer\">\n");
        html.append("      <p><strong>报告生成时间:</strong> ").append(formatDateTime(java.time.LocalDateTime.now())).append(" (GMT+8)</p>\n");
        html.append("      <p style=\"margin: 10px 0;\">接口自动化测试管理系统 (IATMS) - ISO/IEC/IEEE 29119标准企业级测试报告</p>\n");
        html.append("      <p style=\"font-size: 12px; color: #aaa;\">本报告由系统自动生成，数据真实有效。如有疑问，请联系测试团队。</p>\n");
        html.append("      <p style=\"margin-top: 15px; font-size: 11px; color: #ccc;\">© 2024 ").append(escapeHtml(reportData.getProjectName())).append(" 测试团队 | 保密文档 - 仅限内部使用</p>\n");
        html.append("    </div>\n");
    }
    
    /**
     * JavaScript - 图表和交互
     */
    private void buildJavaScript() {
        html.append("  <!-- JavaScript：图表初始化 -->\n");
        html.append("  <script>\n");
        html.append("    // 缺陷详情展开/收起\n");
        html.append("    function toggleDefect(index) {\n");
        html.append("      const content = document.getElementById('defect-' + index);\n");
        html.append("      const icon = document.getElementById('icon-' + index);\n");
        html.append("      content.classList.toggle('expanded');\n");
        html.append("      icon.classList.toggle('expanded');\n");
        html.append("    }\n\n");
        
        html.append("    // 等待页面加载完成\n");
        html.append("    window.addEventListener('load', function() {\n");
        
        // 图表1: 用例状态分布饼图
        buildCaseStatusChart();
        
        // 图表2: 缺陷优先级分布
        buildDefectSeverityChart();
        
        // 图表3: 测试通过率仪表盘
        buildGaugeChart();
        
        // 图表4: 缺陷趋势图
        buildDefectTrendChart();
        
        // 响应式调整
        html.append("      // 响应式调整\n");
        html.append("      window.addEventListener('resize', function() {\n");
        html.append("        caseStatusChart.resize();\n");
        html.append("        defectSeverityChart.resize();\n");
        html.append("        gaugeChart.resize();\n");
        html.append("        defectTrendChart.resize();\n");
        html.append("      });\n\n");
        
        html.append("    });\n");
        html.append("  </script>\n");
    }
    
    private void buildCaseStatusChart() {
        html.append("      // 1. 用例状态分布饼图\n");
        html.append("      const caseStatusChart = echarts.init(document.getElementById('caseStatusChart'));\n");
        html.append("      caseStatusChart.setOption({\n");
        html.append("        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },\n");
        html.append("        legend: { orient: 'vertical', right: 10, top: 'center' },\n");
        html.append("        series: [{\n");
        html.append("          name: '用例状态',\n");
        html.append("          type: 'pie',\n");
        html.append("          radius: ['40%', '70%'],\n");
        html.append("          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },\n");
        html.append("          label: { show: true, formatter: '{b}: {c}' },\n");
        html.append("          data: [\n");
        html.append("            { value: ").append(reportData.getPassedCases()).append(", name: '通过', itemStyle: { color: '#67c23a' } },\n");
        html.append("            { value: ").append(reportData.getFailedCases()).append(", name: '失败', itemStyle: { color: '#f56c6c' } },\n");
        html.append("            { value: ").append(reportData.getBrokenCases()).append(", name: '阻塞', itemStyle: { color: '#8b0000' } },\n");
        html.append("            { value: ").append(reportData.getSkippedCases()).append(", name: '跳过', itemStyle: { color: '#909399' } }\n");
        html.append("          ]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
    }
    
    private void buildDefectSeverityChart() {
        ISOEnterpriseReportDTO.DefectMetrics metrics = reportData.getDefectMetrics();
        if (metrics == null) return;
        
        html.append("      // 2. 缺陷优先级分布\n");
        html.append("      const defectSeverityChart = echarts.init(document.getElementById('defectSeverityChart'));\n");
        html.append("      defectSeverityChart.setOption({\n");
        html.append("        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },\n");
        html.append("        legend: { orient: 'vertical', right: 10, top: 'center' },\n");
        html.append("        series: [{\n");
        html.append("          name: '缺陷优先级',\n");
        html.append("          type: 'pie',\n");
        html.append("          radius: ['40%', '70%'],\n");
        html.append("          label: { show: true, formatter: '{b}: {c}' },\n");
        html.append("          data: [\n");
        html.append("            { value: ").append(metrics.getP0Count()).append(", name: 'P0 (阻塞)', itemStyle: { color: '#8b0000' } },\n");
        html.append("            { value: ").append(metrics.getP1Count()).append(", name: 'P1 (严重)', itemStyle: { color: '#f56c6c' } },\n");
        html.append("            { value: ").append(metrics.getP2Count()).append(", name: 'P2 (一般)', itemStyle: { color: '#e6a23c' } },\n");
        html.append("            { value: ").append(metrics.getP3Count()).append(", name: 'P3 (轻微)', itemStyle: { color: '#409eff' } }\n");
        html.append("          ]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
    }
    
    private void buildGaugeChart() {
        double passRate = reportData.getSuccessRate() != null ? reportData.getSuccessRate().doubleValue() : 0;
        
        html.append("      // 3. 测试通过率仪表盘\n");
        html.append("      const gaugeChart = echarts.init(document.getElementById('gaugeChart'));\n");
        html.append("      gaugeChart.setOption({\n");
        html.append("        series: [{\n");
        html.append("          type: 'gauge',\n");
        html.append("          startAngle: 180,\n");
        html.append("          endAngle: 0,\n");
        html.append("          min: 0,\n");
        html.append("          max: 100,\n");
        html.append("          splitNumber: 10,\n");
        html.append("          axisLine: { lineStyle: { width: 30, color: [[0.6, '#f56c6c'], [0.85, '#e6a23c'], [1, '#67c23a']] } },\n");
        html.append("          pointer: { show: true, length: '70%', width: 6 },\n");
        html.append("          axisTick: { length: 12, lineStyle: { color: 'auto', width: 2 } },\n");
        html.append("          splitLine: { length: 20, lineStyle: { color: 'auto', width: 3 } },\n");
        html.append("          axisLabel: { distance: 30, color: '#999', fontSize: 14 },\n");
        html.append("          title: { offsetCenter: [0, '80%'], fontSize: 16, color: '#999' },\n");
        html.append("          detail: { valueAnimation: true, formatter: '{value}%', color: 'inherit', fontSize: 50, offsetCenter: [0, '50%'] },\n");
        html.append("          data: [{ value: ").append(String.format("%.1f", passRate)).append(", name: '通过率' }]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
    }
    
    private void buildDefectTrendChart() {
        if (reportData.getDefectTrends() == null || reportData.getDefectTrends().isEmpty()) {
            html.append("      // 4. 缺陷趋势图（无数据）\n");
            html.append("      const defectTrendChart = echarts.init(document.getElementById('defectTrendChart'));\n");
            html.append("      defectTrendChart.setOption({ title: { text: '暂无趋势数据', left: 'center', top: 'center', textStyle: { color: '#999' } } });\n\n");
            return;
        }
        
        StringBuilder dates = new StringBuilder();
        StringBuilder newDefects = new StringBuilder();
        StringBuilder unresolvedDefects = new StringBuilder();
        
        for (int i = 0; i < reportData.getDefectTrends().size(); i++) {
            ISOEnterpriseReportDTO.DefectTrend trend = reportData.getDefectTrends().get(i);
            if (i > 0) {
                dates.append(", ");
                newDefects.append(", ");
                unresolvedDefects.append(", ");
            }
            dates.append("'").append(trend.getDate()).append("'");
            newDefects.append(trend.getNewDefects());
            unresolvedDefects.append(trend.getUnresolvedDefects());
        }
        
        html.append("      // 4. 缺陷趋势图\n");
        html.append("      const defectTrendChart = echarts.init(document.getElementById('defectTrendChart'));\n");
        html.append("      defectTrendChart.setOption({\n");
        html.append("        tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },\n");
        html.append("        legend: { data: ['新增缺陷', '累计未解决'], bottom: 10 },\n");
        html.append("        grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },\n");
        html.append("        xAxis: { type: 'category', data: [").append(dates).append("] },\n");
        html.append("        yAxis: [{ type: 'value', name: '缺陷数' }, { type: 'value', name: '累计数' }],\n");
        html.append("        series: [\n");
        html.append("          { name: '新增缺陷', type: 'bar', data: [").append(newDefects).append("], itemStyle: { color: '#f56c6c' } },\n");
        html.append("          { name: '累计未解决', type: 'line', yAxisIndex: 1, data: [").append(unresolvedDefects).append("], itemStyle: { color: '#e6a23c' }, lineStyle: { width: 3 } }\n");
        html.append("        ]\n");
        html.append("      });\n\n");
    }
    
    // ==================== 辅助方法 ====================
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_FORMATTER);
    }
    
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    private String formatReportStatus(String status) {
        if (status == null) return "草稿";
        switch (status.toLowerCase()) {
            case "approved": return "✅ 已批准";
            case "under_review": return "🔍 评审中";
            case "draft": return "📝 草稿";
            default: return status;
        }
    }
    
    private String getConclusionClass(String conclusion) {
        if (conclusion == null) return "success";
        switch (conclusion) {
            case "pass_recommend": return "success";
            case "pass_with_risk": return "warning";
            case "not_pass": return "danger";
            default: return "success";
        }
    }
    
    private String getConclusionIcon(String conclusion) {
        if (conclusion == null) return "✅";
        switch (conclusion) {
            case "pass_recommend": return "✅";
            case "pass_with_risk": return "⚠️";
            case "not_pass": return "❌";
            default: return "✅";
        }
    }
    
    private String getConclusionTitle(String conclusion) {
        if (conclusion == null) return "测试结论";
        switch (conclusion) {
            case "pass_recommend": return "测试通过，建议发布";
            case "pass_with_risk": return "有风险通过，谨慎发布";
            case "not_pass": return "测试不通过，不建议发布";
            default: return "测试结论";
        }
    }
    
    private String getPriorityColor(String priority) {
        if (priority == null) return "#909399";
        switch (priority.toUpperCase()) {
            case "P0": return "#8b0000";
            case "P1": return "#f56c6c";
            case "P2": return "#e6a23c";
            case "P3": return "#409eff";
            default: return "#909399";
        }
    }
    
    private String getRiskColor(String riskLevel) {
        if (riskLevel == null) return "#909399";
        switch (riskLevel.toLowerCase()) {
            case "high": return "#f56c6c";
            case "medium_high": return "#e6a23c";
            case "medium": return "#ffc107";
            case "low": return "#67c23a";
            default: return "#909399";
        }
    }
    
    private String formatProbability(String prob, Integer percent) {
        String text = "";
        switch (prob != null ? prob.toLowerCase() : "") {
            case "low": text = "低"; break;
            case "medium": text = "中"; break;
            case "high": text = "高"; break;
            default: text = prob; break;
        }
        if (percent != null) {
            text += " (" + percent + "%)";
        }
        return text;
    }
    
    private String formatImpact(String impact) {
        if (impact == null) return "";
        switch (impact.toLowerCase()) {
            case "low": return "低";
            case "medium": return "中";
            case "high": return "高";
            case "critical": return "极高";
            default: return impact;
        }
    }
    
    private String formatRiskLevel(String level) {
        if (level == null) return "";
        switch (level.toLowerCase()) {
            case "low": return "低";
            case "medium": return "中";
            case "medium_high": return "中高";
            case "high": return "高";
            default: return level;
        }
    }
}
