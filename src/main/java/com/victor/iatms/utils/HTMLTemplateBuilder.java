package com.victor.iatms.utils;

import com.victor.iatms.entity.dto.ReportExportResponseDTO;

/**
 * HTML模板构建器
 * 用于生成测试报告的HTML页面
 * 
 * @author Victor
 * @since 2024-10-26
 */
public class HTMLTemplateBuilder {
    
    private final StringBuilder html;
    private final ReportExportResponseDTO exportData;
    
    public HTMLTemplateBuilder(ReportExportResponseDTO exportData) {
        this.html = new StringBuilder(50000); // 预分配50KB
        this.exportData = exportData;
    }
    
    
    /**
     * 构建完整的HTML文档
     * 
     * @return HTML字符串
     */
    public String build() {
        buildDocumentStart();
        buildHead();
        buildBodyStart();
        buildHeader();
        buildSummaryCards();
        buildChartsSection();
        buildFailedCasesSection();
        buildBasicInfoTable();
        buildStatisticsDetails();
        buildAllTestCasesTable();
        buildExecutionInfo();
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
        html.append("  <title>").append(ReportFormatter.escapeHtml(exportData.getReportSummary().getReportName())).append(" - 测试报告</title>\n");
        html.append("  <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n");
        
        // 内联CSS样式
        buildStyles();
        
        html.append("</head>\n");
    }
    
    /**
     * 构建CSS样式
     */
    private void buildStyles() {
        html.append("  <style>\n");
        html.append("    * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("    body { font-family: 'Microsoft YaHei', 'Segoe UI', Tahoma, Arial, sans-serif; background-color: #f5f7fa; color: #333; line-height: 1.6; }\n");
        html.append("    .container { max-width: 1400px; margin: 0 auto; background-color: #fff; box-shadow: 0 2px 12px rgba(0,0,0,0.1); }\n");
        
        // 头部样式
        html.append("    .header { background: linear-gradient(135deg, #409eff 0%, #1e80ff 100%); color: white; padding: 40px 30px; text-align: center; }\n");
        html.append("    .header h1 { font-size: 32px; margin-bottom: 15px; font-weight: 600; }\n");
        html.append("    .header .meta { display: flex; justify-content: center; flex-wrap: wrap; gap: 30px; font-size: 14px; opacity: 0.95; }\n");
        html.append("    .header .meta-item { display: flex; align-items: center; gap: 8px; }\n");
        html.append("    .header .meta-item .icon { font-size: 18px; }\n");
        
        // 概览卡片样式
        html.append("    .summary-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 20px; padding: 30px; background: #f5f7fa; }\n");
        html.append("    .summary-card { background: white; border-radius: 8px; padding: 25px 20px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: transform 0.3s, box-shadow 0.3s; }\n");
        html.append("    .summary-card:hover { transform: translateY(-5px); box-shadow: 0 4px 16px rgba(0,0,0,0.12); }\n");
        html.append("    .summary-card .icon { font-size: 36px; margin-bottom: 10px; }\n");
        html.append("    .summary-card .label { font-size: 14px; color: #909399; margin-bottom: 8px; }\n");
        html.append("    .summary-card .value { font-size: 32px; font-weight: bold; }\n");
        html.append("    .summary-card.passed .value { color: #67c23a; }\n");
        html.append("    .summary-card.failed .value { color: #f56c6c; }\n");
        html.append("    .summary-card.broken .value { color: #e6a23c; }\n");
        html.append("    .summary-card.skipped .value { color: #909399; }\n");
        html.append("    .summary-card.total .value { color: #409eff; }\n");
        html.append("    .summary-card.rate .value { color: #67c23a; }\n");
        
        // 图表区域样式
        html.append("    .charts-section { padding: 30px; }\n");
        html.append("    .section-title { font-size: 24px; font-weight: 600; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 2px solid #409eff; color: #303133; }\n");
        html.append("    .chart-row { display: flex; gap: 30px; margin-bottom: 30px; }\n");
        html.append("    .chart-container { flex: 1; background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }\n");
        html.append("    .chart-title { font-size: 16px; font-weight: 600; margin-bottom: 15px; text-align: center; color: #606266; }\n");
        html.append("    .chart { width: 100%; height: 350px; }\n");
        
        // 表格样式
        html.append("    .info-section { padding: 30px; }\n");
        html.append("    .info-table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }\n");
        html.append("    .info-table th { background-color: #409eff; color: white; padding: 15px; text-align: left; font-weight: 600; font-size: 14px; }\n");
        html.append("    .info-table td { padding: 15px; border-bottom: 1px solid #ebeef5; font-size: 14px; color: #606266; }\n");
        html.append("    .info-table tr:last-child td { border-bottom: none; }\n");
        html.append("    .info-table tr:nth-child(even) { background-color: #fafafa; }\n");
        html.append("    .info-table td:first-child { font-weight: 600; color: #303133; width: 200px; }\n");
        
        // 统计详情样式
        html.append("    .stats-detail { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-top: 20px; }\n");
        html.append("    .stats-card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }\n");
        html.append("    .stats-card-title { font-size: 14px; color: #909399; margin-bottom: 10px; }\n");
        html.append("    .stats-card-value { font-size: 28px; font-weight: bold; margin-bottom: 10px; }\n");
        html.append("    .stats-card-percent { font-size: 14px; color: #606266; }\n");
        html.append("    .progress-bar { width: 100%; height: 30px; background: #ebeef5; border-radius: 15px; overflow: hidden; margin: 20px 0; }\n");
        html.append("    .progress-fill { height: 100%; background: linear-gradient(90deg, #67c23a 0%, #85ce61 100%); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 14px; transition: width 1s ease; }\n");
        
        // 失败用例样式
        html.append("    .failure-section { margin-top: 20px; }\n");
        html.append("    .failure-card { background: white; border: 1px solid #f56c6c; border-radius: 8px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(245,108,108,0.1); }\n");
        html.append("    .failure-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 2px solid #fef0f0; }\n");
        html.append("    .failure-title { font-size: 18px; font-weight: 600; color: #303133; flex: 1; }\n");
        html.append("    .failure-badge { padding: 5px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; text-transform: uppercase; }\n");
        html.append("    .failure-badge.failed { background-color: #fef0f0; color: #f56c6c; }\n");
        html.append("    .failure-badge.broken { background-color: #fdf6ec; color: #e6a23c; }\n");
        html.append("    .failure-meta { display: flex; gap: 20px; margin-bottom: 15px; font-size: 13px; color: #909399; flex-wrap: wrap; }\n");
        html.append("    .failure-meta-item { display: flex; align-items: center; gap: 5px; }\n");
        html.append("    .failure-meta-item strong { color: #606266; }\n");
        html.append("    .error-box { background: #fef0f0; border-left: 4px solid #f56c6c; padding: 15px; border-radius: 4px; margin-bottom: 15px; }\n");
        html.append("    .error-title { font-weight: bold; color: #f56c6c; margin-bottom: 8px; font-size: 14px; }\n");
        html.append("    .error-message { color: #303133; font-size: 14px; line-height: 1.6; margin-bottom: 10px; }\n");
        html.append("    .error-type { display: inline-block; background: #f56c6c; color: white; padding: 2px 8px; border-radius: 3px; font-size: 12px; margin-bottom: 10px; }\n");
        html.append("    .error-trace { background: #fff; border: 1px solid #ebeef5; padding: 12px; border-radius: 4px; font-family: 'Consolas', 'Monaco', 'Courier New', monospace; font-size: 12px; color: #606266; white-space: pre-wrap; word-wrap: break-word; max-height: 300px; overflow-y: auto; line-height: 1.5; }\n");
        html.append("    .no-failures { text-align: center; padding: 40px; color: #67c23a; font-size: 16px; }\n");
        html.append("    .no-failures .icon { font-size: 48px; margin-bottom: 10px; }\n");
        
        // 页脚样式
        html.append("    .footer { background: #f5f7fa; padding: 30px; text-align: center; color: #909399; font-size: 13px; border-top: 1px solid #ebeef5; }\n");
        html.append("    .footer p { margin: 5px 0; }\n");
        html.append("    .footer .copyright { font-weight: 600; color: #606266; }\n");
        
        // 响应式设计
        html.append("    @media (max-width: 1200px) {\n");
        html.append("      .summary-cards { grid-template-columns: repeat(3, 1fr); }\n");
        html.append("      .chart-row { flex-direction: column; }\n");
        html.append("    }\n");
        html.append("    @media (max-width: 768px) {\n");
        html.append("      .summary-cards { grid-template-columns: repeat(2, 1fr); gap: 15px; padding: 20px; }\n");
        html.append("      .header { padding: 30px 20px; }\n");
        html.append("      .header h1 { font-size: 24px; }\n");
        html.append("      .header .meta { font-size: 12px; gap: 15px; }\n");
        html.append("      .charts-section, .info-section { padding: 20px; }\n");
        html.append("      .chart { height: 250px; }\n");
        html.append("      .info-table th, .info-table td { padding: 10px; font-size: 12px; }\n");
        html.append("    }\n");
        
        // 打印样式
        html.append("    @media print {\n");
        html.append("      body { background: white; }\n");
        html.append("      .container { box-shadow: none; }\n");
        html.append("      .summary-card:hover { transform: none; }\n");
        html.append("      .chart { page-break-inside: avoid; }\n");
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
     * 构建头部区域
     */
    private void buildHeader() {
        html.append("    <div class=\"header\">\n");
        html.append("      <h1>📊 ").append(ReportFormatter.escapeHtml(exportData.getReportSummary().getReportName())).append("</h1>\n");
        html.append("      <div class=\"meta\">\n");
        html.append("        <div class=\"meta-item\"><span class=\"icon\">🎯</span><span>").append(ReportFormatter.escapeHtml(exportData.getReportSummary().getProjectName())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"icon\">📝</span><span>").append(ReportFormatter.formatReportType(exportData.getReportSummary().getReportType())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"icon\">🌐</span><span>").append(ReportFormatter.formatEnvironment(exportData.getReportSummary().getEnvironment())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"icon\">🕐</span><span>").append(ReportFormatter.formatDateTime(exportData.getReportSummary().getStartTime())).append("</span></div>\n");
        html.append("        <div class=\"meta-item\"><span class=\"icon\">⏱</span><span>").append(ReportFormatter.formatDuration(exportData.getReportSummary().getDuration())).append("</span></div>\n");
        html.append("      </div>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建概览卡片
     */
    private void buildSummaryCards() {
        html.append("    <div class=\"summary-cards\">\n");
        
        // 通过
        html.append("      <div class=\"summary-card passed\">\n");
        html.append("        <div class=\"icon\">✅</div>\n");
        html.append("        <div class=\"label\">通过</div>\n");
        html.append("        <div class=\"value\">").append(exportData.getReportSummary().getPassedCases()).append("</div>\n");
        html.append("      </div>\n");
        
        // 失败
        html.append("      <div class=\"summary-card failed\">\n");
        html.append("        <div class=\"icon\">❌</div>\n");
        html.append("        <div class=\"label\">失败</div>\n");
        html.append("        <div class=\"value\">").append(exportData.getReportSummary().getFailedCases()).append("</div>\n");
        html.append("      </div>\n");
        
        // 异常
        html.append("      <div class=\"summary-card broken\">\n");
        html.append("        <div class=\"icon\">⚠️</div>\n");
        html.append("        <div class=\"label\">异常</div>\n");
        html.append("        <div class=\"value\">").append(exportData.getReportSummary().getBrokenCases()).append("</div>\n");
        html.append("      </div>\n");
        
        // 跳过
        html.append("      <div class=\"summary-card skipped\">\n");
        html.append("        <div class=\"icon\">⊘</div>\n");
        html.append("        <div class=\"label\">跳过</div>\n");
        html.append("        <div class=\"value\">").append(exportData.getReportSummary().getSkippedCases()).append("</div>\n");
        html.append("      </div>\n");
        
        // 总数
        html.append("      <div class=\"summary-card total\">\n");
        html.append("        <div class=\"icon\">📋</div>\n");
        html.append("        <div class=\"label\">总用例数</div>\n");
        html.append("        <div class=\"value\">").append(exportData.getReportSummary().getTotalCases()).append("</div>\n");
        html.append("      </div>\n");
        
        // 成功率
        html.append("      <div class=\"summary-card rate\">\n");
        html.append("        <div class=\"icon\">📈</div>\n");
        html.append("        <div class=\"label\">成功率</div>\n");
        html.append("        <div class=\"value\">").append(ReportFormatter.formatPercentage(exportData.getReportSummary().getSuccessRate())).append("</div>\n");
        html.append("      </div>\n");
        
        html.append("    </div>\n");
    }
    
    /**
     * 构建图表区域
     */
    private void buildChartsSection() {
        html.append("    <div class=\"charts-section\">\n");
        html.append("      <h2 class=\"section-title\">📊 数据可视化</h2>\n");
        html.append("      <div class=\"chart-row\">\n");
        
        // 饼图
        html.append("        <div class=\"chart-container\">\n");
        html.append("          <div class=\"chart-title\">测试用例分布</div>\n");
        html.append("          <div id=\"pieChart\" class=\"chart\"></div>\n");
        html.append("        </div>\n");
        
        // 仪表盘
        html.append("        <div class=\"chart-container\">\n");
        html.append("          <div class=\"chart-title\">成功率</div>\n");
        html.append("          <div id=\"gaugeChart\" class=\"chart\"></div>\n");
        html.append("        </div>\n");
        
        // 柱状图
        html.append("        <div class=\"chart-container\">\n");
        html.append("          <div class=\"chart-title\">测试结果统计</div>\n");
        html.append("          <div id=\"barChart\" class=\"chart\"></div>\n");
        html.append("        </div>\n");
        
        html.append("      </div>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建失败用例详情部分
     */
    private void buildFailedCasesSection() {
        if (exportData.getTestResults() == null || exportData.getTestResults().isEmpty()) {
            return;
        }
        
        // 筛选失败和异常的用例
        java.util.List<ReportExportResponseDTO.TestCaseResultDTO> failedCases = 
            exportData.getTestResults().stream()
                .filter(r -> "failed".equalsIgnoreCase(r.getStatus()) || "broken".equalsIgnoreCase(r.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        
        html.append("    <div class=\"info-section\">\n");
        html.append("      <h2 class=\"section-title\">❌ 失败用例详情</h2>\n");
        
        if (failedCases.isEmpty()) {
            html.append("      <div class=\"no-failures\">\n");
            html.append("        <div class=\"icon\">✅</div>\n");
            html.append("        <div>太棒了！所有测试用例均已通过</div>\n");
            html.append("      </div>\n");
        } else {
            html.append("      <div class=\"failure-section\">\n");
            
            for (ReportExportResponseDTO.TestCaseResultDTO failedCase : failedCases) {
                html.append("        <div class=\"failure-card\">\n");
                
                // 失败用例头部
                html.append("          <div class=\"failure-header\">\n");
                html.append("            <div class=\"failure-title\">\n");
                html.append("              ").append(ReportFormatter.escapeHtml(failedCase.getCaseName() != null ? failedCase.getCaseName() : "未命名用例")).append("\n");
                html.append("            </div>\n");
                String badgeClass = "failed".equalsIgnoreCase(failedCase.getStatus()) ? "failed" : "broken";
                html.append("            <span class=\"failure-badge ").append(badgeClass).append("\">").append(failedCase.getStatus() != null ? failedCase.getStatus().toUpperCase() : "FAILED").append("</span>\n");
                html.append("          </div>\n");
                
                // 元数据信息
                html.append("          <div class=\"failure-meta\">\n");
                if (failedCase.getCaseCode() != null) {
                    html.append("            <div class=\"failure-meta-item\"><strong>用例编号:</strong> ").append(ReportFormatter.escapeHtml(failedCase.getCaseCode())).append("</div>\n");
                }
                if (failedCase.getPriority() != null) {
                    html.append("            <div class=\"failure-meta-item\"><strong>优先级:</strong> ").append(failedCase.getPriority()).append("</div>\n");
                }
                if (failedCase.getSeverity() != null) {
                    html.append("            <div class=\"failure-meta-item\"><strong>严重程度:</strong> ").append(failedCase.getSeverity()).append("</div>\n");
                }
                if (failedCase.getDuration() != null) {
                    html.append("            <div class=\"failure-meta-item\"><strong>执行耗时:</strong> ").append(ReportFormatter.formatDuration(failedCase.getDuration())).append("</div>\n");
                }
                if (failedCase.getStartTime() != null) {
                    html.append("            <div class=\"failure-meta-item\"><strong>开始时间:</strong> ").append(ReportFormatter.formatDateTime(failedCase.getStartTime())).append("</div>\n");
                }
                if (failedCase.getRetryCount() != null && failedCase.getRetryCount() > 0) {
                    html.append("            <div class=\"failure-meta-item\"><strong>重试次数:</strong> ").append(failedCase.getRetryCount()).append("</div>\n");
                }
                if (Boolean.TRUE.equals(failedCase.getFlaky())) {
                    html.append("            <div class=\"failure-meta-item\" style=\"color: #e6a23c;\"><strong>⚠️ 不稳定用例</strong></div>\n");
                }
                html.append("          </div>\n");
                
                // 错误信息
                if (failedCase.getFailureMessage() != null || failedCase.getFailureType() != null || failedCase.getFailureTrace() != null) {
                    html.append("          <div class=\"error-box\">\n");
                    html.append("            <div class=\"error-title\">🔍 错误详情</div>\n");
                    
                    if (failedCase.getFailureType() != null) {
                        html.append("            <div class=\"error-type\">").append(ReportFormatter.escapeHtml(failedCase.getFailureType())).append("</div>\n");
                    }
                    
                    if (failedCase.getFailureMessage() != null) {
                        html.append("            <div class=\"error-message\">").append(ReportFormatter.escapeHtml(failedCase.getFailureMessage())).append("</div>\n");
                    }
                    
                    if (failedCase.getFailureTrace() != null && !failedCase.getFailureTrace().trim().isEmpty()) {
                        html.append("            <div style=\"margin-top: 10px; font-weight: bold; color: #606266; font-size: 13px;\">堆栈跟踪:</div>\n");
                        html.append("            <div class=\"error-trace\">").append(ReportFormatter.escapeHtml(failedCase.getFailureTrace())).append("</div>\n");
                    }
                    
                    html.append("          </div>\n");
                }
                
                // 环境信息
                if (failedCase.getEnvironment() != null || failedCase.getBrowser() != null || failedCase.getOs() != null || failedCase.getDevice() != null) {
                    html.append("          <div style=\"margin-top: 15px; padding-top: 15px; border-top: 1px solid #ebeef5;\">\n");
                    html.append("            <div style=\"font-weight: bold; color: #606266; margin-bottom: 8px; font-size: 13px;\">🖥️ 执行环境</div>\n");
                    html.append("            <div class=\"failure-meta\">\n");
                    if (failedCase.getEnvironment() != null) {
                        html.append("              <div class=\"failure-meta-item\"><strong>环境:</strong> ").append(ReportFormatter.escapeHtml(failedCase.getEnvironment())).append("</div>\n");
                    }
                    if (failedCase.getBrowser() != null) {
                        html.append("              <div class=\"failure-meta-item\"><strong>浏览器:</strong> ").append(ReportFormatter.escapeHtml(failedCase.getBrowser())).append("</div>\n");
                    }
                    if (failedCase.getOs() != null) {
                        html.append("              <div class=\"failure-meta-item\"><strong>操作系统:</strong> ").append(ReportFormatter.escapeHtml(failedCase.getOs())).append("</div>\n");
                    }
                    if (failedCase.getDevice() != null) {
                        html.append("              <div class=\"failure-meta-item\"><strong>设备:</strong> ").append(ReportFormatter.escapeHtml(failedCase.getDevice())).append("</div>\n");
                    }
                    html.append("            </div>\n");
                    html.append("          </div>\n");
                }
                
                // 标签
                if (failedCase.getTags() != null && !failedCase.getTags().isEmpty()) {
                    html.append("          <div style=\"margin-top: 15px;\">\n");
                    html.append("            <div style=\"font-weight: bold; color: #606266; margin-bottom: 8px; font-size: 13px;\">🏷️ 标签</div>\n");
                    html.append("            <div style=\"display: flex; gap: 8px; flex-wrap: wrap;\">\n");
                    for (String tag : failedCase.getTags()) {
                        html.append("              <span style=\"background: #ecf5ff; color: #409eff; padding: 4px 10px; border-radius: 4px; font-size: 12px;\">").append(ReportFormatter.escapeHtml(tag)).append("</span>\n");
                    }
                    html.append("            </div>\n");
                    html.append("          </div>\n");
                }
                
                html.append("        </div>\n");
            }
            
            html.append("      </div>\n");
        }
        
        html.append("    </div>\n");
    }
    
    /**
     * 构建基本信息表格
     */
    private void buildBasicInfoTable() {
        html.append("    <div class=\"info-section\">\n");
        html.append("      <h2 class=\"section-title\">📋 基本信息</h2>\n");
        html.append("      <table class=\"info-table\">\n");
        html.append("        <tr><td>报告ID</td><td>").append(exportData.getReportSummary().getReportId()).append("</td></tr>\n");
        html.append("        <tr><td>报告名称</td><td>").append(ReportFormatter.escapeHtml(exportData.getReportSummary().getReportName())).append("</td></tr>\n");
        html.append("        <tr><td>项目名称</td><td>").append(ReportFormatter.escapeHtml(exportData.getReportSummary().getProjectName())).append("</td></tr>\n");
        html.append("        <tr><td>报告类型</td><td>").append(ReportFormatter.formatReportType(exportData.getReportSummary().getReportType())).append("</td></tr>\n");
        html.append("        <tr><td>测试环境</td><td>").append(ReportFormatter.formatEnvironment(exportData.getReportSummary().getEnvironment())).append("</td></tr>\n");
        html.append("        <tr><td>开始时间</td><td>").append(ReportFormatter.formatDateTime(exportData.getReportSummary().getStartTime())).append("</td></tr>\n");
        html.append("        <tr><td>结束时间</td><td>").append(ReportFormatter.formatDateTime(exportData.getReportSummary().getEndTime())).append("</td></tr>\n");
        html.append("        <tr><td>执行耗时</td><td>").append(ReportFormatter.formatDuration(exportData.getReportSummary().getDuration())).append("</td></tr>\n");
        html.append("      </table>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建统计详情
     */
    private void buildStatisticsDetails() {
        html.append("    <div class=\"info-section\">\n");
        html.append("      <h2 class=\"section-title\">📈 测试统计详情</h2>\n");
        
        // 成功率进度条
        double successRate = exportData.getReportSummary().getSuccessRate().doubleValue();
        String progressColor = ReportFormatter.getSuccessRateColor(successRate);
        html.append("      <div class=\"progress-bar\">\n");
        html.append("        <div class=\"progress-fill\" style=\"width: ").append(successRate).append("%; background: ").append(progressColor).append(";\">").append(ReportFormatter.formatPercentage(successRate)).append("</div>\n");
        html.append("      </div>\n");
        
        // 统计卡片
        html.append("      <div class=\"stats-detail\">\n");
        
        int total = exportData.getReportSummary().getTotalCases();
        
        html.append("        <div class=\"stats-card\">\n");
        html.append("          <div class=\"stats-card-title\">通过用例</div>\n");
        html.append("          <div class=\"stats-card-value\" style=\"color: #67c23a;\">").append(exportData.getReportSummary().getPassedCases()).append("</div>\n");
        html.append("          <div class=\"stats-card-percent\">占比: ").append(ReportFormatter.formatPercentage(ReportFormatter.calculatePercentage(exportData.getReportSummary().getPassedCases(), total))).append("</div>\n");
        html.append("        </div>\n");
        
        html.append("        <div class=\"stats-card\">\n");
        html.append("          <div class=\"stats-card-title\">失败用例</div>\n");
        html.append("          <div class=\"stats-card-value\" style=\"color: #f56c6c;\">").append(exportData.getReportSummary().getFailedCases()).append("</div>\n");
        html.append("          <div class=\"stats-card-percent\">占比: ").append(ReportFormatter.formatPercentage(ReportFormatter.calculatePercentage(exportData.getReportSummary().getFailedCases(), total))).append("</div>\n");
        html.append("        </div>\n");
        
        html.append("        <div class=\"stats-card\">\n");
        html.append("          <div class=\"stats-card-title\">异常用例</div>\n");
        html.append("          <div class=\"stats-card-value\" style=\"color: #e6a23c;\">").append(exportData.getReportSummary().getBrokenCases()).append("</div>\n");
        html.append("          <div class=\"stats-card-percent\">占比: ").append(ReportFormatter.formatPercentage(ReportFormatter.calculatePercentage(exportData.getReportSummary().getBrokenCases(), total))).append("</div>\n");
        html.append("        </div>\n");
        
        html.append("        <div class=\"stats-card\">\n");
        html.append("          <div class=\"stats-card-title\">跳过用例</div>\n");
        html.append("          <div class=\"stats-card-value\" style=\"color: #909399;\">").append(exportData.getReportSummary().getSkippedCases()).append("</div>\n");
        html.append("          <div class=\"stats-card-percent\">占比: ").append(ReportFormatter.formatPercentage(ReportFormatter.calculatePercentage(exportData.getReportSummary().getSkippedCases(), total))).append("</div>\n");
        html.append("        </div>\n");
        
        html.append("      </div>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建所有测试用例表格
     */
    private void buildAllTestCasesTable() {
        if (exportData.getTestResults() == null || exportData.getTestResults().isEmpty()) {
            return;
        }
        
        html.append("    <div class=\"info-section\">\n");
        html.append("      <h2 class=\"section-title\">📝 所有测试用例</h2>\n");
        html.append("      <table class=\"info-table\">\n");
        html.append("        <thead>\n");
        html.append("          <tr>\n");
        html.append("            <th>用例编号</th>\n");
        html.append("            <th>用例名称</th>\n");
        html.append("            <th>状态</th>\n");
        html.append("            <th>优先级</th>\n");
        html.append("            <th>严重程度</th>\n");
        html.append("            <th>耗时</th>\n");
        html.append("            <th>开始时间</th>\n");
        html.append("          </tr>\n");
        html.append("        </thead>\n");
        html.append("        <tbody>\n");
        
        for (ReportExportResponseDTO.TestCaseResultDTO result : exportData.getTestResults()) {
            html.append("          <tr>\n");
            html.append("            <td>").append(result.getCaseCode() != null ? ReportFormatter.escapeHtml(result.getCaseCode()) : "N/A").append("</td>\n");
            html.append("            <td>").append(result.getCaseName() != null ? ReportFormatter.escapeHtml(result.getCaseName()) : "N/A").append("</td>\n");
            
            // 状态徽章
            String statusColor = "#909399";
            if ("passed".equalsIgnoreCase(result.getStatus())) {
                statusColor = "#67c23a";
            } else if ("failed".equalsIgnoreCase(result.getStatus())) {
                statusColor = "#f56c6c";
            } else if ("broken".equalsIgnoreCase(result.getStatus())) {
                statusColor = "#e6a23c";
            } else if ("skipped".equalsIgnoreCase(result.getStatus())) {
                statusColor = "#909399";
            }
            html.append("            <td><span style=\"background: ").append(statusColor).append("; color: white; padding: 4px 10px; border-radius: 4px; font-size: 11px; font-weight: bold;\">")
                .append(result.getStatus() != null ? result.getStatus().toUpperCase() : "UNKNOWN").append("</span></td>\n");
            
            html.append("            <td>").append(result.getPriority() != null ? result.getPriority() : "N/A").append("</td>\n");
            html.append("            <td>").append(result.getSeverity() != null ? result.getSeverity() : "N/A").append("</td>\n");
            html.append("            <td>").append(ReportFormatter.formatDuration(result.getDuration())).append("</td>\n");
            html.append("            <td>").append(result.getStartTime() != null ? ReportFormatter.formatDateTime(result.getStartTime()) : "N/A").append("</td>\n");
            html.append("          </tr>\n");
        }
        
        html.append("        </tbody>\n");
        html.append("      </table>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建执行信息
     */
    private void buildExecutionInfo() {
        html.append("    <div class=\"info-section\">\n");
        html.append("      <h2 class=\"section-title\">⚙️ 执行信息</h2>\n");
        html.append("      <table class=\"info-table\">\n");
        html.append("        <tr><td>开始时间</td><td>").append(ReportFormatter.formatDateTime(exportData.getReportSummary().getStartTime())).append("</td></tr>\n");
        html.append("        <tr><td>结束时间</td><td>").append(ReportFormatter.formatDateTime(exportData.getReportSummary().getEndTime())).append("</td></tr>\n");
        html.append("        <tr><td>执行耗时</td><td>").append(ReportFormatter.formatDuration(exportData.getReportSummary().getDuration())).append("</td></tr>\n");
        html.append("        <tr><td>测试环境</td><td>").append(ReportFormatter.formatEnvironment(exportData.getReportSummary().getEnvironment())).append("</td></tr>\n");
        html.append("      </table>\n");
        html.append("    </div>\n");
    }
    
    /**
     * 构建页脚
     */
    private void buildFooter() {
        html.append("    <div class=\"footer\">\n");
        html.append("      <p><strong>报告生成时间:</strong> ").append(ReportFormatter.formatDateTime(java.time.LocalDateTime.now())).append("</p>\n");
        html.append("      <p class=\"copyright\">IATMS - 接口自动化测试管理系统</p>\n");
        html.append("      <p>Powered by Spring Boot & MyBatis | © 2024 All Rights Reserved</p>\n");
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
     * 构建JavaScript代码（ECharts图表初始化）
     */
    private void buildJavaScript() {
        html.append("  <script>\n");
        html.append("    window.addEventListener('load', function() {\n");
        
        // 饼图
        html.append("      var pieChart = echarts.init(document.getElementById('pieChart'));\n");
        html.append("      pieChart.setOption({\n");
        html.append("        tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },\n");
        html.append("        legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { fontSize: 14 } },\n");
        html.append("        series: [{\n");
        html.append("          name: '测试用例',\n");
        html.append("          type: 'pie',\n");
        html.append("          radius: ['40%', '70%'],\n");
        html.append("          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },\n");
        html.append("          label: { show: true, formatter: '{b}: {c}' },\n");
        html.append("          emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },\n");
        html.append("          data: [\n");
        html.append("            { value: ").append(exportData.getReportSummary().getPassedCases()).append(", name: '通过', itemStyle: { color: '#67c23a' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getFailedCases()).append(", name: '失败', itemStyle: { color: '#f56c6c' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getBrokenCases()).append(", name: '异常', itemStyle: { color: '#e6a23c' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getSkippedCases()).append(", name: '跳过', itemStyle: { color: '#909399' } }\n");
        html.append("          ]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
        
        // 仪表盘
        double successRate = exportData.getReportSummary().getSuccessRate().doubleValue();
        String gaugeColor = ReportFormatter.getSuccessRateColor(successRate);
        html.append("      var gaugeChart = echarts.init(document.getElementById('gaugeChart'));\n");
        html.append("      gaugeChart.setOption({\n");
        html.append("        series: [{\n");
        html.append("          type: 'gauge',\n");
        html.append("          startAngle: 180,\n");
        html.append("          endAngle: 0,\n");
        html.append("          min: 0,\n");
        html.append("          max: 100,\n");
        html.append("          splitNumber: 10,\n");
        html.append("          axisLine: { lineStyle: { width: 30, color: [[0.6, '#f56c6c'], [0.8, '#e6a23c'], [1, '#67c23a']] } },\n");
        html.append("          pointer: { show: false },\n");
        html.append("          axisTick: { show: false },\n");
        html.append("          splitLine: { length: 15, lineStyle: { width: 2, color: '#fff' } },\n");
        html.append("          axisLabel: { distance: 25, color: '#999', fontSize: 12 },\n");
        html.append("          detail: { valueAnimation: true, formatter: '{value}%', color: '").append(gaugeColor).append("', fontSize: 50, offsetCenter: [0, '70%'] },\n");
        html.append("          data: [{ value: ").append(String.format("%.1f", successRate)).append(" }]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
        
        // 柱状图
        html.append("      var barChart = echarts.init(document.getElementById('barChart'));\n");
        html.append("      barChart.setOption({\n");
        html.append("        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },\n");
        html.append("        xAxis: { type: 'category', data: ['总用例数', '已执行', '通过', '失败', '异常', '跳过'], axisLabel: { fontSize: 12 } },\n");
        html.append("        yAxis: { type: 'value' },\n");
        html.append("        series: [{\n");
        html.append("          type: 'bar',\n");
        html.append("          barWidth: '40%',\n");
        html.append("          itemStyle: { borderRadius: [8, 8, 0, 0] },\n");
        html.append("          label: { show: true, position: 'top', fontWeight: 'bold' },\n");
        html.append("          data: [\n");
        html.append("            { value: ").append(exportData.getReportSummary().getTotalCases()).append(", itemStyle: { color: '#409eff' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getExecutedCases()).append(", itemStyle: { color: '#409eff' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getPassedCases()).append(", itemStyle: { color: '#67c23a' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getFailedCases()).append(", itemStyle: { color: '#f56c6c' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getBrokenCases()).append(", itemStyle: { color: '#e6a23c' } },\n");
        html.append("            { value: ").append(exportData.getReportSummary().getSkippedCases()).append(", itemStyle: { color: '#909399' } }\n");
        html.append("          ]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
        
        // 响应式调整
        html.append("      window.addEventListener('resize', function() {\n");
        html.append("        pieChart.resize();\n");
        html.append("        gaugeChart.resize();\n");
        html.append("        barChart.resize();\n");
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

