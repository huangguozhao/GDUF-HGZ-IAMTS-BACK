package com.victor.iatms.utils;

import com.victor.iatms.entity.dto.EnterpriseReportDTO;
import com.victor.iatms.entity.dto.ReportExportResponseDTO;

/**
 * 企业级测试报告HTML构建器
 * 符合ISO/IEC/IEEE 29119标准和ISTQB最佳实践
 * 
 * 核心模块：
 * 1. 报告头信息（Document Header）
 * 2. 执行摘要（Executive Summary）- 关键指标仪表盘
 * 3. 测试结果与度量分析（Test Results & Metrics）
 * 
 * @author Victor
 * @since 2024-10-26
 */
public class EnterpriseHTMLBuilder {
    
    private final StringBuilder html;
    private final EnterpriseReportDTO enterpriseData;
    private final ReportExportResponseDTO exportData;
    
    public EnterpriseHTMLBuilder(EnterpriseReportDTO enterpriseData, ReportExportResponseDTO exportData) {
        this.html = new StringBuilder(100000); // 预分配100KB
        this.enterpriseData = enterpriseData;
        this.exportData = exportData;
    }
    
    /**
     * 构建完整的企业级HTML报告
     */
    public String build() {
        buildDocumentStart();
        buildHead();
        buildBodyStart();
        
        // 核心3个模块
        buildReportHeader();           // 模块1：报告头信息
        buildExecutiveSummary();       // 模块2：执行摘要
        buildTestResults();            // 模块3：测试结果与度量分析
        
        // 可展开的失败用例详情
        buildExpandableFailures();
        
        buildFooter();
        buildBodyEnd();
        buildDocumentEnd();
        
        return html.toString();
    }
    
    /**
     * 构建文档开始标签
     */
    private void buildDocumentStart() {
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
    }
    
    /**
     * 构建HEAD部分
     */
    private void buildHead() {
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n");
        html.append("  <title>").append(ReportFormatter.escapeHtml(enterpriseData.getReportTitle())).append("</title>\n");
        html.append("  <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n");
        
        // 企业级样式
        buildEnterpriseStyles();
        
        html.append("</head>\n");
    }
    
    /**
     * 构建企业级CSS样式
     */
    private void buildEnterpriseStyles() {
        html.append("  <style>\n");
        
        // 全局样式
        html.append("    * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("    body { font-family: 'Microsoft YaHei', 'Segoe UI', Arial, sans-serif; background-color: #f5f7fa; color: #2c3e50; line-height: 1.8; }\n");
        html.append("    .container { max-width: 1200px; margin: 0 auto; background-color: #fff; box-shadow: 0 2px 20px rgba(0,0,0,0.1); }\n");
        
        // 报告头部样式（深蓝色商务风格）
        html.append("    .report-header { background: linear-gradient(135deg, #1f3a93 0%, #2c5aa0 100%); color: white; padding: 40px 50px; position: relative; }\n");
        html.append("    .report-header::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 5px; background: linear-gradient(90deg, #ffd700 0%, #ffed4e 100%); }\n");
        html.append("    .header-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 30px; }\n");
        html.append("    .logo-section { display: flex; align-items: center; gap: 15px; }\n");
        html.append("    .logo { width: 60px; height: 60px; background: white; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: bold; color: #1f3a93; }\n");
        html.append("    .company-name { font-size: 16px; opacity: 0.9; }\n");
        html.append("    .report-meta { text-align: right; font-size: 13px; opacity: 0.9; }\n");
        html.append("    .report-meta div { margin-bottom: 5px; }\n");
        html.append("    .report-title-main { text-align: center; }\n");
        html.append("    .report-title-main h1 { font-size: 36px; font-weight: 600; margin-bottom: 15px; letter-spacing: 2px; }\n");
        html.append("    .report-subtitle { font-size: 16px; opacity: 0.95; display: flex; justify-content: center; gap: 30px; flex-wrap: wrap; }\n");
        html.append("    .report-subtitle span { display: inline-flex; align-items: center; gap: 8px; }\n");
        
        // 执行摘要样式
        html.append("    .executive-summary { padding: 40px 50px; background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%); }\n");
        html.append("    .section-title { font-size: 28px; font-weight: 600; color: #1f3a93; margin-bottom: 30px; padding-bottom: 15px; border-bottom: 3px solid #1f3a93; display: flex; align-items: center; gap: 15px; }\n");
        html.append("    .section-title::before { content: ''; width: 6px; height: 35px; background: linear-gradient(180deg, #1f3a93 0%, #409eff 100%); border-radius: 3px; }\n");
        
        // 结论横幅
        html.append("    .conclusion-banner { padding: 30px; border-radius: 12px; margin-bottom: 40px; display: flex; align-items: center; gap: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }\n");
        html.append("    .conclusion-banner.pass { background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%); border-left: 6px solid #28a745; }\n");
        html.append("    .conclusion-banner.risk { background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%); border-left: 6px solid #ffc107; }\n");
        html.append("    .conclusion-banner.fail { background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%); border-left: 6px solid #dc3545; }\n");
        html.append("    .conclusion-icon { font-size: 64px; line-height: 1; }\n");
        html.append("    .conclusion-text h2 { font-size: 26px; margin-bottom: 10px; color: #2c3e50; }\n");
        html.append("    .conclusion-text p { font-size: 15px; color: #495057; line-height: 1.6; }\n");
        
        // KPI仪表盘
        html.append("    .kpi-dashboard { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 20px; margin-bottom: 40px; }\n");
        html.append("    .kpi-card { background: white; border-radius: 10px; padding: 25px 20px; text-align: center; box-shadow: 0 3px 10px rgba(0,0,0,0.08); transition: all 0.3s; border-top: 4px solid #1f3a93; }\n");
        html.append("    .kpi-card:hover { transform: translateY(-5px); box-shadow: 0 6px 20px rgba(0,0,0,0.15); }\n");
        html.append("    .kpi-label { font-size: 13px; color: #6c757d; margin-bottom: 12px; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }\n");
        html.append("    .kpi-value { font-size: 38px; font-weight: bold; margin-bottom: 8px; }\n");
        html.append("    .kpi-value.success { color: #28a745; }\n");
        html.append("    .kpi-value.warning { color: #ffc107; }\n");
        html.append("    .kpi-value.danger { color: #dc3545; }\n");
        html.append("    .kpi-value.info { color: #17a2b8; }\n");
        html.append("    .kpi-trend { font-size: 12px; color: #6c757d; }\n");
        html.append("    .kpi-trend.up { color: #28a745; }\n");
        html.append("    .kpi-trend.down { color: #dc3545; }\n");
        
        // 图表区域
        html.append("    .charts-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 30px; margin: 30px 0; }\n");
        html.append("    .chart-card { background: white; border-radius: 10px; padding: 25px; box-shadow: 0 3px 10px rgba(0,0,0,0.08); }\n");
        html.append("    .chart-card-title { font-size: 18px; font-weight: 600; color: #2c3e50; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 2px solid #e9ecef; }\n");
        html.append("    .chart { width: 100%; height: 350px; }\n");
        
        // 可展开的失败用例
        html.append("    .failures-section { padding: 40px 50px; background: #fff; }\n");
        html.append("    .failure-accordion { margin-top: 20px; }\n");
        html.append("    .failure-item { border: 1px solid #dee2e6; border-radius: 8px; margin-bottom: 15px; overflow: hidden; transition: all 0.3s; }\n");
        html.append("    .failure-item:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.1); }\n");
        html.append("    .failure-header-bar { background: linear-gradient(135deg, #fff5f5 0%, #ffe5e5 100%); padding: 18px 25px; cursor: pointer; display: flex; justify-content: space-between; align-items: center; border-left: 5px solid #dc3545; }\n");
        html.append("    .failure-header-bar:hover { background: linear-gradient(135deg, #ffe5e5 0%, #ffd5d5 100%); }\n");
        html.append("    .failure-header-left { display: flex; align-items: center; gap: 15px; flex: 1; }\n");
        html.append("    .failure-number { background: #dc3545; color: white; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 14px; }\n");
        html.append("    .failure-title-text { font-size: 16px; font-weight: 600; color: #2c3e50; flex: 1; }\n");
        html.append("    .failure-badges { display: flex; gap: 10px; align-items: center; }\n");
        html.append("    .expand-icon { font-size: 20px; color: #6c757d; transition: transform 0.3s; }\n");
        html.append("    .expand-icon.expanded { transform: rotate(180deg); }\n");
        html.append("    .failure-content { max-height: 0; overflow: hidden; transition: max-height 0.4s ease-out; background: white; }\n");
        html.append("    .failure-content.expanded { max-height: 2000px; }\n");
        html.append("    .failure-body { padding: 25px; border-top: 1px solid #dee2e6; }\n");
        html.append("    .failure-meta-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-bottom: 20px; }\n");
        html.append("    .failure-meta-item { padding: 12px; background: #f8f9fa; border-radius: 6px; }\n");
        html.append("    .failure-meta-label { font-size: 12px; color: #6c757d; margin-bottom: 5px; font-weight: 600; text-transform: uppercase; }\n");
        html.append("    .failure-meta-value { font-size: 14px; color: #2c3e50; font-weight: 500; }\n");
        html.append("    .error-detail-box { background: #fff5f5; border: 1px solid #ffcdd2; border-radius: 8px; padding: 20px; margin-top: 20px; }\n");
        html.append("    .error-detail-title { font-size: 15px; font-weight: 600; color: #d32f2f; margin-bottom: 12px; display: flex; align-items: center; gap: 8px; }\n");
        html.append("    .error-type-badge { display: inline-block; background: #dc3545; color: white; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; margin-bottom: 12px; }\n");
        html.append("    .error-message-text { color: #2c3e50; font-size: 14px; line-height: 1.6; margin-bottom: 15px; padding: 12px; background: white; border-left: 4px solid #dc3545; border-radius: 4px; }\n");
        html.append("    .error-trace-box { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 6px; padding: 15px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace; font-size: 12px; color: #495057; white-space: pre-wrap; word-wrap: break-word; max-height: 300px; overflow-y: auto; line-height: 1.5; }\n");
        html.append("    .error-trace-title { font-size: 13px; font-weight: 600; color: #495057; margin-bottom: 10px; }\n");
        
        // 徽章样式
        html.append("    .badge { display: inline-block; padding: 5px 12px; border-radius: 4px; font-size: 12px; font-weight: 600; text-transform: uppercase; }\n");
        html.append("    .badge-p0 { background: #8b0000; color: white; }\n");
        html.append("    .badge-p1 { background: #dc3545; color: white; }\n");
        html.append("    .badge-p2 { background: #ffc107; color: #333; }\n");
        html.append("    .badge-p3 { background: #17a2b8; color: white; }\n");
        html.append("    .badge-critical { background: #721c24; color: white; }\n");
        html.append("    .badge-high { background: #dc3545; color: white; }\n");
        html.append("    .badge-medium { background: #ffc107; color: #333; }\n");
        html.append("    .badge-low { background: #28a745; color: white; }\n");
        html.append("    .badge-failed { background: #dc3545; color: white; }\n");
        html.append("    .badge-broken { background: #fd7e14; color: white; }\n");
        
        // 表格样式
        html.append("    .data-table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }\n");
        html.append("    .data-table thead { background: linear-gradient(135deg, #1f3a93 0%, #2c5aa0 100%); }\n");
        html.append("    .data-table th { color: white; padding: 15px; text-align: left; font-weight: 600; font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px; }\n");
        html.append("    .data-table td { padding: 15px; border-bottom: 1px solid #e9ecef; color: #495057; font-size: 14px; }\n");
        html.append("    .data-table tbody tr:hover { background-color: #f8f9fa; }\n");
        html.append("    .data-table tbody tr:last-child td { border-bottom: none; }\n");
        html.append("    .data-table .number-cell { text-align: right; font-weight: 500; }\n");
        
        // 页脚
        html.append("    .report-footer { background: #2c3e50; color: white; padding: 30px 50px; text-align: center; }\n");
        html.append("    .footer-content { font-size: 13px; opacity: 0.9; }\n");
        html.append("    .footer-content p { margin: 8px 0; }\n");
        html.append("    .footer-divider { height: 1px; background: rgba(255,255,255,0.2); margin: 15px 0; }\n");
        
        // 打印样式
        html.append("    @media print {\n");
        html.append("      body { background: white; }\n");
        html.append("      .container { box-shadow: none; }\n");
        html.append("      .kpi-card:hover, .failure-item:hover { transform: none; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n");
        html.append("      .failure-content { max-height: none !important; }\n");
        html.append("      .chart { page-break-inside: avoid; }\n");
        html.append("      .failure-item { page-break-inside: avoid; }\n");
        html.append("    }\n");
        
        // 响应式
        html.append("    @media (max-width: 768px) {\n");
        html.append("      .report-header, .executive-summary, .failures-section { padding: 30px 20px; }\n");
        html.append("      .kpi-dashboard { grid-template-columns: repeat(2, 1fr); gap: 15px; }\n");
        html.append("      .charts-grid { grid-template-columns: 1fr; }\n");
        html.append("      .chart { height: 250px; }\n");
        html.append("      .section-title { font-size: 22px; }\n");
        html.append("      .report-title-main h1 { font-size: 26px; }\n");
        html.append("    }\n");
        
        html.append("  </style>\n");
    }
    
    /**
     * 构建BODY开始标签
     */
    private void buildBodyStart() {
        html.append("<body>\n");
        html.append("  <div class=\"container\">\n");
    }
    
    /**
     * 模块1：构建报告头信息
     */
    private void buildReportHeader() {
        html.append("    <div class=\"report-header\">\n");
        
        // 顶部：LOGO和报告元数据
        html.append("      <div class=\"header-top\">\n");
        html.append("        <div class=\"logo-section\">\n");
        html.append("          <div class=\"logo\">IATMS</div>\n");
        html.append("          <div class=\"company-name\">接口自动化测试管理系统</div>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"report-meta\">\n");
        html.append("          <div><strong>报告编号:</strong> ").append(enterpriseData.getReportNumber() != null ? enterpriseData.getReportNumber() : "N/A").append("</div>\n");
        html.append("          <div><strong>报告日期:</strong> ").append(ReportFormatter.formatDateTime(enterpriseData.getReportDate())).append("</div>\n");
        html.append("          <div><strong>编写人:</strong> ").append(ReportFormatter.escapeHtml(enterpriseData.getTesterName())).append("</div>\n");
        if (enterpriseData.getReviewerName() != null) {
            html.append("          <div><strong>评审人:</strong> ").append(ReportFormatter.escapeHtml(enterpriseData.getReviewerName())).append("</div>\n");
        }
        html.append("        </div>\n");
        html.append("      </div>\n");
        
        // 报告标题
        html.append("      <div class=\"report-title-main\">\n");
        html.append("        <h1>").append(ReportFormatter.escapeHtml(enterpriseData.getReportTitle())).append("</h1>\n");
        html.append("        <div class=\"report-subtitle\">\n");
        html.append("          <span>📦 <strong>").append(ReportFormatter.escapeHtml(enterpriseData.getProjectName())).append("</strong></span>\n");
        html.append("          <span>🔖 <strong>版本:</strong> ").append(enterpriseData.getVersion() != null ? enterpriseData.getVersion() : "N/A").append("</span>\n");
        html.append("          <span>📅 <strong>测试周期:</strong> ").append(ReportFormatter.formatDateTime(enterpriseData.getTestStartDate())).append(" ~ ").append(ReportFormatter.formatDateTime(enterpriseData.getTestEndDate())).append("</span>\n");
        html.append("        </div>\n");
        html.append("      </div>\n");
        
        html.append("    </div>\n");
    }
    
    /**
     * 模块2：构建执行摘要
     */
    private void buildExecutiveSummary() {
        html.append("    <div class=\"executive-summary\">\n");
        html.append("      <h2 class=\"section-title\">执行摘要 Executive Summary</h2>\n");
        
        // 核心结论横幅
        buildConclusionBanner();
        
        // KPI仪表盘
        buildKPIDashboard();
        
        // 关键图表
        buildKeyCharts();
        
        html.append("    </div>\n");
    }
    
    /**
     * 构建结论横幅
     */
    private void buildConclusionBanner() {
        String conclusion = enterpriseData.getConclusion();
        String cssClass = "pass";
        String icon = "✅";
        
        if ("pass_with_risk".equals(conclusion)) {
            cssClass = "risk";
            icon = "⚠️";
        } else if ("not_pass".equals(conclusion)) {
            cssClass = "fail";
            icon = "❌";
        }
        
        html.append("      <div class=\"conclusion-banner ").append(cssClass).append("\">\n");
        html.append("        <div class=\"conclusion-icon\">").append(icon).append("</div>\n");
        html.append("        <div class=\"conclusion-text\">\n");
        html.append("          <h2>测试结论: ").append(getConclusionText(conclusion)).append("</h2>\n");
        html.append("          <p>").append(ReportFormatter.escapeHtml(enterpriseData.getDetailedConclusion())).append("</p>\n");
        html.append("        </div>\n");
        html.append("      </div>\n");
    }
    
    /**
     * 获取结论文本
     */
    private String getConclusionText(String conclusion) {
        if ("pass_recommend".equals(conclusion)) {
            return "通过 - 建议发布";
        } else if ("pass_with_risk".equals(conclusion)) {
            return "有风险通过 - 谨慎发布";
        } else if ("not_pass".equals(conclusion)) {
            return "不通过 - 不建议发布";
        }
        return "待评估";
    }
    
    /**
     * 构建KPI仪表盘
     */
    private void buildKPIDashboard() {
        EnterpriseReportDTO.KeyMetrics metrics = enterpriseData.getKeyMetrics();
        if (metrics == null) return;
        
        html.append("      <div class=\"kpi-dashboard\">\n");
        
        // KPI 1: 测试通过率
        html.append("        <div class=\"kpi-card\">\n");
        html.append("          <div class=\"kpi-label\">测试通过率</div>\n");
        String passRateClass = metrics.getTestPassRate() >= 95 ? "success" : (metrics.getTestPassRate() >= 85 ? "warning" : "danger");
        html.append("          <div class=\"kpi-value ").append(passRateClass).append("\">").append(String.format("%.1f%%", metrics.getTestPassRate())).append("</div>\n");
        html.append("          <div class=\"kpi-trend\">目标: ≥95%</div>\n");
        html.append("        </div>\n");
        
        // KPI 2: 缺陷密度
        html.append("        <div class=\"kpi-card\">\n");
        html.append("          <div class=\"kpi-label\">缺陷密度</div>\n");
        String densityClass = metrics.getDefectDensity() <= 5 ? "success" : (metrics.getDefectDensity() <= 10 ? "warning" : "danger");
        html.append("          <div class=\"kpi-value ").append(densityClass).append("\">").append(String.format("%.1f", metrics.getDefectDensity())).append("</div>\n");
        html.append("          <div class=\"kpi-trend\">个/百用例 (目标: ≤5)</div>\n");
        html.append("        </div>\n");
        
        // KPI 3: 高优先级缺陷
        html.append("        <div class=\"kpi-card\">\n");
        html.append("          <div class=\"kpi-label\">高优先级缺陷</div>\n");
        String criticalClass = metrics.getCriticalDefectCount() == 0 ? "success" : (metrics.getCriticalDefectCount() <= 2 ? "warning" : "danger");
        html.append("          <div class=\"kpi-value ").append(criticalClass).append("\">").append(metrics.getCriticalDefectCount()).append("</div>\n");
        html.append("          <div class=\"kpi-trend\">P0+P1 (目标: ≤2)</div>\n");
        html.append("        </div>\n");
        
        // KPI 4: 缺陷修复率
        html.append("        <div class=\"kpi-card\">\n");
        html.append("          <div class=\"kpi-label\">缺陷修复率</div>\n");
        String fixRateClass = metrics.getDefectFixRate() >= 90 ? "success" : (metrics.getDefectFixRate() >= 80 ? "warning" : "danger");
        html.append("          <div class=\"kpi-value ").append(fixRateClass).append("\">").append(String.format("%.1f%%", metrics.getDefectFixRate())).append("</div>\n");
        html.append("          <div class=\"kpi-trend\">目标: ≥90%</div>\n");
        html.append("        </div>\n");
        
        // KPI 5: 需求覆盖率
        html.append("        <div class=\"kpi-card\">\n");
        html.append("          <div class=\"kpi-label\">需求覆盖率</div>\n");
        String coverageClass = metrics.getRequirementCoverage() == 100 ? "success" : "warning";
        html.append("          <div class=\"kpi-value ").append(coverageClass).append("\">").append(String.format("%.0f%%", metrics.getRequirementCoverage())).append("</div>\n");
        html.append("          <div class=\"kpi-trend\">目标: 100%</div>\n");
        html.append("        </div>\n");
        
        // KPI 6: 测试效率
        html.append("        <div class=\"kpi-card\">\n");
        html.append("          <div class=\"kpi-label\">测试效率</div>\n");
        html.append("          <div class=\"kpi-value info\">").append(metrics.getTestEfficiency()).append("</div>\n");
        html.append("          <div class=\"kpi-trend\">用例/天</div>\n");
        html.append("        </div>\n");
        
        html.append("      </div>\n");
    }
    
    /**
     * 构建关键图表
     */
    private void buildKeyCharts() {
        html.append("      <div class=\"charts-grid\">\n");
        
        // 图表1: 测试通过率仪表盘
        html.append("        <div class=\"chart-card\">\n");
        html.append("          <div class=\"chart-card-title\">📊 测试通过率</div>\n");
        html.append("          <div id=\"passRateGauge\" class=\"chart\"></div>\n");
        html.append("        </div>\n");
        
        // 图表2: 缺陷严重程度分布
        html.append("        <div class=\"chart-card\">\n");
        html.append("          <div class=\"chart-card-title\">🐛 缺陷严重程度分布</div>\n");
        html.append("          <div id=\"defectPieChart\" class=\"chart\"></div>\n");
        html.append("        </div>\n");
        
        // 图表3: 缺陷趋势图
        html.append("        <div class=\"chart-card\" style=\"grid-column: span 2;\">\n");
        html.append("          <div class=\"chart-card-title\">📈 缺陷趋势分析</div>\n");
        html.append("          <div id=\"defectTrendChart\" class=\"chart\"></div>\n");
        html.append("        </div>\n");
        
        html.append("      </div>\n");
    }
    
    /**
     * 模块3：构建测试结果与度量分析
     */
    private void buildTestResults() {
        html.append("    <div class=\"executive-summary\">\n");
        html.append("      <h2 class=\"section-title\">测试结果与度量分析 Test Results & Metrics</h2>\n");
        
        // 模块测试结果表格
        buildModuleResultsTable();
        
        html.append("    </div>\n");
    }
    
    /**
     * 构建模块测试结果表格
     */
    private void buildModuleResultsTable() {
        if (enterpriseData.getModuleResults() == null || enterpriseData.getModuleResults().isEmpty()) {
            return;
        }
        
        html.append("      <table class=\"data-table\">\n");
        html.append("        <thead>\n");
        html.append("          <tr>\n");
        html.append("            <th>模块名称</th>\n");
        html.append("            <th class=\"number-cell\">总用例数</th>\n");
        html.append("            <th class=\"number-cell\">已执行</th>\n");
        html.append("            <th class=\"number-cell\">通过</th>\n");
        html.append("            <th class=\"number-cell\">失败</th>\n");
        html.append("            <th class=\"number-cell\">阻塞</th>\n");
        html.append("            <th class=\"number-cell\">跳过</th>\n");
        html.append("            <th class=\"number-cell\">通过率</th>\n");
        html.append("          </tr>\n");
        html.append("        </thead>\n");
        html.append("        <tbody>\n");
        
        int totalCases = 0, totalExecuted = 0, totalPassed = 0, totalFailed = 0, totalBlocked = 0, totalSkipped = 0;
        
        for (EnterpriseReportDTO.ModuleTestResult module : enterpriseData.getModuleResults()) {
            html.append("          <tr>\n");
            html.append("            <td>").append(ReportFormatter.escapeHtml(module.getModuleName())).append("</td>\n");
            html.append("            <td class=\"number-cell\">").append(module.getTotalCases()).append("</td>\n");
            html.append("            <td class=\"number-cell\">").append(module.getExecutedCases()).append("</td>\n");
            html.append("            <td class=\"number-cell\" style=\"color: #28a745; font-weight: 600;\">").append(module.getPassedCases()).append("</td>\n");
            html.append("            <td class=\"number-cell\" style=\"color: #dc3545; font-weight: 600;\">").append(module.getFailedCases()).append("</td>\n");
            html.append("            <td class=\"number-cell\" style=\"color: #8b0000; font-weight: 600;\">").append(module.getBlockedCases()).append("</td>\n");
            html.append("            <td class=\"number-cell\" style=\"color: #6c757d;\">").append(module.getSkippedCases()).append("</td>\n");
            
            String passRateColor = module.getPassRate() >= 95 ? "#28a745" : (module.getPassRate() >= 85 ? "#ffc107" : "#dc3545");
            html.append("            <td class=\"number-cell\" style=\"color: ").append(passRateColor).append("; font-weight: 700;\">").append(String.format("%.1f%%", module.getPassRate())).append("</td>\n");
            html.append("          </tr>\n");
            
            totalCases += module.getTotalCases();
            totalExecuted += module.getExecutedCases();
            totalPassed += module.getPassedCases();
            totalFailed += module.getFailedCases();
            totalBlocked += module.getBlockedCases();
            totalSkipped += module.getSkippedCases();
        }
        
        // 合计行
        double totalPassRate = totalExecuted > 0 ? (totalPassed * 100.0 / totalExecuted) : 0;
        html.append("          <tr style=\"background: #f8f9fa; font-weight: 700;\">\n");
        html.append("            <td><strong>合计</strong></td>\n");
        html.append("            <td class=\"number-cell\">").append(totalCases).append("</td>\n");
        html.append("            <td class=\"number-cell\">").append(totalExecuted).append("</td>\n");
        html.append("            <td class=\"number-cell\" style=\"color: #28a745;\">").append(totalPassed).append("</td>\n");
        html.append("            <td class=\"number-cell\" style=\"color: #dc3545;\">").append(totalFailed).append("</td>\n");
        html.append("            <td class=\"number-cell\" style=\"color: #8b0000;\">").append(totalBlocked).append("</td>\n");
        html.append("            <td class=\"number-cell\" style=\"color: #6c757d;\">").append(totalSkipped).append("</td>\n");
        html.append("            <td class=\"number-cell\" style=\"color: #1f3a93;\">").append(String.format("%.1f%%", totalPassRate)).append("</td>\n");
        html.append("          </tr>\n");
        
        html.append("        </tbody>\n");
        html.append("      </table>\n");
    }
    
    /**
     * 构建可展开的失败用例详情
     */
    private void buildExpandableFailures() {
        if (exportData.getTestResults() == null || exportData.getTestResults().isEmpty()) {
            return;
        }
        
        // 筛选失败和异常的用例
        java.util.List<ReportExportResponseDTO.TestCaseResultDTO> failedCases = 
            exportData.getTestResults().stream()
                .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        
        if (failedCases.isEmpty()) {
            return;
        }
        
        html.append("    <div class=\"failures-section\">\n");
        html.append("      <h2 class=\"section-title\">失败用例详细分析 Failure Analysis</h2>\n");
        html.append("      <p style=\"color: #6c757d; margin-bottom: 20px;\">共发现 <strong style=\"color: #dc3545;\">").append(failedCases.size()).append("</strong> 个失败/异常用例，点击展开查看详情</p>\n");
        
        html.append("      <div class=\"failure-accordion\">\n");
        
        int index = 1;
        for (ReportExportResponseDTO.TestCaseResultDTO failedCase : failedCases) {
            buildExpandableFailureItem(failedCase, index++);
        }
        
        html.append("      </div>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建单个可展开的失败用例项
     */
    private void buildExpandableFailureItem(ReportExportResponseDTO.TestCaseResultDTO failedCase, int index) {
        String itemId = "failure-" + index;
        
        html.append("        <div class=\"failure-item\">\n");
        
        // 可点击的头部
        html.append("          <div class=\"failure-header-bar\" onclick=\"toggleFailure('").append(itemId).append("')\">\n");
        html.append("            <div class=\"failure-header-left\">\n");
        html.append("              <div class=\"failure-number\">").append(index).append("</div>\n");
        html.append("              <div class=\"failure-title-text\">").append(ReportFormatter.escapeHtml(failedCase.getCaseName() != null ? failedCase.getCaseName() : "未命名用例")).append("</div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"failure-badges\">\n");
        
        // 优先级徽章
        if (failedCase.getPriority() != null) {
            html.append("              <span class=\"badge badge-").append(failedCase.getPriority().toLowerCase()).append("\">").append(failedCase.getPriority()).append("</span>\n");
        }
        
        // 严重程度徽章
        if (failedCase.getSeverity() != null) {
            html.append("              <span class=\"badge badge-").append(failedCase.getSeverity().toLowerCase()).append("\">").append(failedCase.getSeverity()).append("</span>\n");
        }
        
        // 状态徽章
        String statusBadge = "failed".equalsIgnoreCase(failedCase.getStatus()) ? "failed" : "broken";
        html.append("              <span class=\"badge badge-").append(statusBadge).append("\">").append(failedCase.getStatus() != null ? failedCase.getStatus().toUpperCase() : "FAILED").append("</span>\n");
        
        html.append("              <span class=\"expand-icon\" id=\"icon-").append(itemId).append("\">▼</span>\n");
        html.append("            </div>\n");
        html.append("          </div>\n");
        
        // 可展开的内容
        html.append("          <div class=\"failure-content\" id=\"content-").append(itemId).append("\">\n");
        html.append("            <div class=\"failure-body\">\n");
        
        // 元数据网格
        html.append("              <div class=\"failure-meta-grid\">\n");
        if (failedCase.getCaseCode() != null) {
            html.append("                <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">用例编号</div><div class=\"failure-meta-value\">").append(ReportFormatter.escapeHtml(failedCase.getCaseCode())).append("</div></div>\n");
        }
        if (failedCase.getStartTime() != null) {
            html.append("                <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">开始时间</div><div class=\"failure-meta-value\">").append(ReportFormatter.formatDateTime(failedCase.getStartTime())).append("</div></div>\n");
        }
        if (failedCase.getDuration() != null) {
            html.append("                <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">执行耗时</div><div class=\"failure-meta-value\">").append(ReportFormatter.formatDuration(failedCase.getDuration())).append("</div></div>\n");
        }
        if (failedCase.getRetryCount() != null && failedCase.getRetryCount() > 0) {
            html.append("                <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">重试次数</div><div class=\"failure-meta-value\">").append(failedCase.getRetryCount()).append(" 次</div></div>\n");
        }
        if (Boolean.TRUE.equals(failedCase.getFlaky())) {
            html.append("                <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">稳定性</div><div class=\"failure-meta-value\" style=\"color: #fd7e14;\">⚠️ 不稳定用例</div></div>\n");
        }
        html.append("              </div>\n");
        
        // 错误详情
        if (failedCase.getFailureMessage() != null || failedCase.getFailureType() != null || failedCase.getFailureTrace() != null) {
            html.append("              <div class=\"error-detail-box\">\n");
            html.append("                <div class=\"error-detail-title\">🔍 错误详情</div>\n");
            
            if (failedCase.getFailureType() != null) {
                html.append("                <div class=\"error-type-badge\">").append(ReportFormatter.escapeHtml(failedCase.getFailureType())).append("</div>\n");
            }
            
            if (failedCase.getFailureMessage() != null) {
                html.append("                <div class=\"error-message-text\">").append(ReportFormatter.escapeHtml(failedCase.getFailureMessage())).append("</div>\n");
            }
            
            if (failedCase.getFailureTrace() != null && !failedCase.getFailureTrace().trim().isEmpty()) {
                html.append("                <div class=\"error-trace-title\">堆栈跟踪 Stack Trace:</div>\n");
                html.append("                <div class=\"error-trace-box\">").append(ReportFormatter.escapeHtml(failedCase.getFailureTrace())).append("</div>\n");
            }
            
            html.append("              </div>\n");
        }
        
        // 环境信息
        if (failedCase.getEnvironment() != null || failedCase.getBrowser() != null || failedCase.getOs() != null) {
            html.append("              <div style=\"margin-top: 20px; padding-top: 20px; border-top: 1px solid #dee2e6;\">\n");
            html.append("                <div style=\"font-weight: 600; color: #495057; margin-bottom: 12px; font-size: 14px;\">🖥️ 执行环境</div>\n");
            html.append("                <div class=\"failure-meta-grid\">\n");
            if (failedCase.getEnvironment() != null) {
                html.append("                  <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">环境</div><div class=\"failure-meta-value\">").append(ReportFormatter.escapeHtml(failedCase.getEnvironment())).append("</div></div>\n");
            }
            if (failedCase.getBrowser() != null) {
                html.append("                  <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">浏览器</div><div class=\"failure-meta-value\">").append(ReportFormatter.escapeHtml(failedCase.getBrowser())).append("</div></div>\n");
            }
            if (failedCase.getOs() != null) {
                html.append("                  <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">操作系统</div><div class=\"failure-meta-value\">").append(ReportFormatter.escapeHtml(failedCase.getOs())).append("</div></div>\n");
            }
            if (failedCase.getDevice() != null) {
                html.append("                  <div class=\"failure-meta-item\"><div class=\"failure-meta-label\">设备</div><div class=\"failure-meta-value\">").append(ReportFormatter.escapeHtml(failedCase.getDevice())).append("</div></div>\n");
            }
            html.append("                </div>\n");
            html.append("              </div>\n");
        }
        
        html.append("            </div>\n");
        html.append("          </div>\n");
        
        html.append("        </div>\n");
    }
    
    /**
     * 构建页脚
     */
    private void buildFooter() {
        html.append("    <div class=\"report-footer\">\n");
        html.append("      <div class=\"footer-content\">\n");
        html.append("        <p><strong>报告生成时间:</strong> ").append(ReportFormatter.formatDateTime(java.time.LocalDateTime.now())).append("</p>\n");
        html.append("        <div class=\"footer-divider\"></div>\n");
        html.append("        <p><strong>IATMS - 接口自动化测试管理系统</strong></p>\n");
        html.append("        <p>符合ISO/IEC/IEEE 29119标准 | Powered by Spring Boot & MyBatis</p>\n");
        html.append("        <p>© 2024 All Rights Reserved | 企业级测试报告</p>\n");
        html.append("      </div>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建BODY结束标签和JavaScript
     */
    private void buildBodyEnd() {
        html.append("  </div>\n");
        
        // JavaScript代码
        buildJavaScript();
        
        html.append("</body>\n");
    }
    
    /**
     * 构建JavaScript代码
     */
    private void buildJavaScript() {
        html.append("  <script>\n");
        
        // 展开/折叠功能
        html.append("    function toggleFailure(id) {\n");
        html.append("      const content = document.getElementById('content-' + id);\n");
        html.append("      const icon = document.getElementById('icon-' + id);\n");
        html.append("      if (content.classList.contains('expanded')) {\n");
        html.append("        content.classList.remove('expanded');\n");
        html.append("        icon.classList.remove('expanded');\n");
        html.append("      } else {\n");
        html.append("        content.classList.add('expanded');\n");
        html.append("        icon.classList.add('expanded');\n");
        html.append("      }\n");
        html.append("    }\n\n");
        
        // ECharts图表初始化
        html.append("    window.addEventListener('load', function() {\n");
        
        // 图表1: 测试通过率仪表盘
        if (enterpriseData.getKeyMetrics() != null) {
            double passRate = enterpriseData.getKeyMetrics().getTestPassRate();
            String gaugeColor = passRate >= 95 ? "#28a745" : (passRate >= 85 ? "#ffc107" : "#dc3545");
            
            html.append("      const passRateGauge = echarts.init(document.getElementById('passRateGauge'));\n");
            html.append("      passRateGauge.setOption({\n");
            html.append("        series: [{\n");
            html.append("          type: 'gauge',\n");
            html.append("          startAngle: 180,\n");
            html.append("          endAngle: 0,\n");
            html.append("          min: 0,\n");
            html.append("          max: 100,\n");
            html.append("          splitNumber: 10,\n");
            html.append("          axisLine: { lineStyle: { width: 30, color: [[0.85, '#dc3545'], [0.95, '#ffc107'], [1, '#28a745']] } },\n");
            html.append("          pointer: { show: false },\n");
            html.append("          axisTick: { show: false },\n");
            html.append("          splitLine: { length: 15, lineStyle: { width: 2, color: '#fff' } },\n");
            html.append("          axisLabel: { distance: 25, color: '#666', fontSize: 12 },\n");
            html.append("          detail: { valueAnimation: true, formatter: '{value}%', color: '").append(gaugeColor).append("', fontSize: 50, offsetCenter: [0, '70%'], fontWeight: 'bold' },\n");
            html.append("          data: [{ value: ").append(String.format("%.1f", passRate)).append(" }]\n");
            html.append("        }]\n");
            html.append("      });\n\n");
        }
        
        // 图表2: 缺陷严重程度分布饼图
        if (enterpriseData.getDefectMetrics() != null) {
            EnterpriseReportDTO.DefectMetrics metrics = enterpriseData.getDefectMetrics();
            
            html.append("      const defectPieChart = echarts.init(document.getElementById('defectPieChart'));\n");
            html.append("      defectPieChart.setOption({\n");
            html.append("        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },\n");
            html.append("        legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { fontSize: 13 } },\n");
            html.append("        series: [{\n");
            html.append("          name: '缺陷分布',\n");
            html.append("          type: 'pie',\n");
            html.append("          radius: ['40%', '70%'],\n");
            html.append("          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },\n");
            html.append("          label: { show: true, formatter: '{b}: {c}' },\n");
            html.append("          emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },\n");
            html.append("          data: [\n");
            html.append("            { value: ").append(metrics.getP0Count()).append(", name: 'P0-阻塞', itemStyle: { color: '#8b0000' } },\n");
            html.append("            { value: ").append(metrics.getP1Count()).append(", name: 'P1-严重', itemStyle: { color: '#dc3545' } },\n");
            html.append("            { value: ").append(metrics.getP2Count()).append(", name: 'P2-一般', itemStyle: { color: '#ffc107' } },\n");
            html.append("            { value: ").append(metrics.getP3Count()).append(", name: 'P3-轻微', itemStyle: { color: '#17a2b8' } }\n");
            html.append("          ]\n");
            html.append("        }]\n");
            html.append("      });\n\n");
        }
        
        // 图表3: 缺陷趋势图
        if (enterpriseData.getDefectTrends() != null && !enterpriseData.getDefectTrends().isEmpty()) {
            java.util.List<EnterpriseReportDTO.DefectTrend> trends = enterpriseData.getDefectTrends();
            
            StringBuilder dates = new StringBuilder();
            StringBuilder newDefects = new StringBuilder();
            StringBuilder closedDefects = new StringBuilder();
            StringBuilder cumulative = new StringBuilder();
            
            for (int i = 0; i < trends.size(); i++) {
                EnterpriseReportDTO.DefectTrend trend = trends.get(i);
                if (i > 0) {
                    dates.append(", ");
                    newDefects.append(", ");
                    closedDefects.append(", ");
                    cumulative.append(", ");
                }
                dates.append("'").append(trend.getDate()).append("'");
                newDefects.append(trend.getNewDefects());
                closedDefects.append(trend.getClosedDefects());
                cumulative.append(trend.getCumulativeUnresolved());
            }
            
            html.append("      const defectTrendChart = echarts.init(document.getElementById('defectTrendChart'));\n");
            html.append("      defectTrendChart.setOption({\n");
            html.append("        tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },\n");
            html.append("        legend: { data: ['新增缺陷', '关闭缺陷', '累计未解决'], top: 10 },\n");
            html.append("        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },\n");
            html.append("        xAxis: { type: 'category', boundaryGap: false, data: [").append(dates).append("] },\n");
            html.append("        yAxis: [{ type: 'value', name: '缺陷数' }],\n");
            html.append("        series: [\n");
            html.append("          { name: '新增缺陷', type: 'bar', data: [").append(newDefects).append("], itemStyle: { color: '#dc3545' } },\n");
            html.append("          { name: '关闭缺陷', type: 'bar', data: [").append(closedDefects).append("], itemStyle: { color: '#28a745' } },\n");
            html.append("          { name: '累计未解决', type: 'line', data: [").append(cumulative).append("], itemStyle: { color: '#ffc107' }, lineStyle: { width: 3 } }\n");
            html.append("        ]\n");
            html.append("      });\n\n");
        }
        
        // 响应式调整
        html.append("      window.addEventListener('resize', function() {\n");
        html.append("        if (typeof passRateGauge !== 'undefined') passRateGauge.resize();\n");
        html.append("        if (typeof defectPieChart !== 'undefined') defectPieChart.resize();\n");
        html.append("        if (typeof defectTrendChart !== 'undefined') defectTrendChart.resize();\n");
        html.append("      });\n");
        
        html.append("    });\n");
        html.append("  </script>\n");
    }
    
    /**
     * 构建文档结束标签
     */
    private void buildDocumentEnd() {
        html.append("</html>");
    }
}

