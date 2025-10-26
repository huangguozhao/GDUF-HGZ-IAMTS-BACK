package com.victor.iatms.utils;

import com.victor.iatms.entity.dto.AllureReportDTO;
import java.time.format.DateTimeFormatter;

/**
 * Allure风格测试报告HTML构建器
 * 面向技术人员的详细报告
 * 
 * @author Victor
 * @since 2024-10-26
 */
public class AllureHTMLBuilder {
    
    private final StringBuilder html;
    private final AllureReportDTO reportData;
    @SuppressWarnings("unused")
    private final String locale;
    @SuppressWarnings("unused")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @SuppressWarnings("unused")
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public AllureHTMLBuilder(AllureReportDTO reportData, String locale) {
        this.html = new StringBuilder(200000); // 预分配200KB
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
        html.append("  <title>Allure风格测试报告 - ").append(escapeHtml(reportData.getReportTitle())).append("</title>\n");
        html.append("  <script src=\"https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js\"></script>\n");
        buildStyles();
        html.append("</head>\n");
    }
    
    // 构建CSS样式 - 第一部分：全局和导航栏
    private void buildStyles() {
        html.append("  <style>\n");
        html.append("    * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("    body { font-family: 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif; background: #f5f7fa; color: #333; line-height: 1.6; overflow-x: hidden; }\n");
        
        // 顶部导航栏
        html.append("    .top-navbar { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 30px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); position: sticky; top: 0; z-index: 1000; display: flex; justify-content: space-between; align-items: center; }\n");
        html.append("    .navbar-brand { font-size: 20px; font-weight: bold; display: flex; align-items: center; gap: 10px; }\n");
        html.append("    .navbar-brand .logo { width: 32px; height: 32px; background: white; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-size: 18px; }\n");
        html.append("    .navbar-info { display: flex; gap: 30px; font-size: 14px; }\n");
        html.append("    .navbar-info-item { display: flex; align-items: center; gap: 8px; opacity: 0.95; }\n");
        
        // 主布局
        html.append("    .main-container { display: flex; min-height: calc(100vh - 62px); }\n");
        
        // 侧边栏
        html.append("    .sidebar { width: 260px; background: white; box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05); position: sticky; top: 62px; height: calc(100vh - 62px); overflow-y: auto; }\n");
        html.append("    .sidebar-nav { list-style: none; padding: 20px 0; }\n");
        html.append("    .sidebar-nav-item { padding: 12px 25px; cursor: pointer; transition: all 0.3s; display: flex; align-items: center; gap: 12px; font-size: 15px; border-left: 3px solid transparent; }\n");
        html.append("    .sidebar-nav-item:hover { background: #f5f7fa; }\n");
        html.append("    .sidebar-nav-item.active { background: #ecf5ff; border-left-color: #667eea; color: #667eea; font-weight: 600; }\n");
        html.append("    .sidebar-nav-icon { font-size: 18px; width: 24px; text-align: center; }\n");
        
        // 主内容区
        html.append("    .content-area { flex: 1; padding: 30px; overflow-y: auto; }\n");
        html.append("    .content-section { display: none; }\n");
        html.append("    .content-section.active { display: block; }\n");
        
        buildStylesPart2();
    }
    
    // 构建CSS样式 - 第二部分：概览和统计
    private void buildStylesPart2() {
        // 概览页面
        html.append("    .overview-header { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); margin-bottom: 30px; }\n");
        html.append("    .overview-title { font-size: 28px; font-weight: bold; color: #333; margin-bottom: 10px; }\n");
        html.append("    .overview-subtitle { font-size: 14px; color: #666; }\n");
        
        // 统计卡片
        html.append("    .stat-card { background: white; padding: 25px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); text-align: center; transition: all 0.3s; border-top: 4px solid #667eea; }\n");
        html.append("    .stat-card:hover { transform: translateY(-5px); box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15); }\n");
        html.append("    .stat-card.success { border-top-color: #10b981; }\n");
        html.append("    .stat-card.danger { border-top-color: #ef4444; }\n");
        html.append("    .stat-card.warning { border-top-color: #f59e0b; }\n");
        html.append("    .stat-icon { font-size: 40px; margin-bottom: 15px; }\n");
        html.append("    .stat-value { font-size: 36px; font-weight: bold; color: #333; margin-bottom: 8px; }\n");
        html.append("    .stat-label { font-size: 14px; color: #666; text-transform: uppercase; letter-spacing: 1px; }\n");
        
        // 图表和表格
        html.append("    .charts-section { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); margin-bottom: 30px; }\n");
        html.append("    .chart-container { width: 100%; height: 400px; margin-top: 20px; }\n");
        html.append("    .section-title { font-size: 20px; font-weight: 600; color: #333; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 2px solid #e5e7eb; }\n");
        html.append("    .stats-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; }\n");
        html.append("    .stats-table thead { background: #f9fafb; }\n");
        html.append("    .stats-table th { padding: 15px; text-align: center; font-size: 14px; font-weight: 600; color: #374151; border-bottom: 2px solid #e5e7eb; }\n");
        html.append("    .stats-table td { padding: 15px; text-align: center; font-size: 15px; color: #333; border-bottom: 1px solid #f3f4f6; }\n");
        html.append("    .stats-table td:first-child { text-align: left; font-weight: 500; }\n");
        html.append("    .stats-table tbody tr:hover { background: #f9fafb; }\n");
        html.append("    .pass-rate { font-weight: 600; color: #10b981; }\n");
        
        buildStylesPart3();
    }
    
    // 构建CSS样式 - 第三部分：测试套件和用例
    private void buildStylesPart3() {
        // 测试套件
        html.append("    .suites-container { background: white; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); overflow: hidden; }\n");
        html.append("    .suite-item { border-bottom: 1px solid #e5e7eb; }\n");
        html.append("    .suite-item:last-child { border-bottom: none; }\n");
        html.append("    .suite-header { padding: 20px 25px; cursor: pointer; display: flex; justify-content: space-between; align-items: center; background: #fafafa; transition: all 0.3s; }\n");
        html.append("    .suite-header:hover { background: #f5f7fa; }\n");
        html.append("    .suite-header.expanded { background: #ecf5ff; }\n");
        html.append("    .suite-info { display: flex; align-items: center; gap: 15px; flex: 1; }\n");
        html.append("    .suite-icon { font-size: 24px; }\n");
        html.append("    .suite-name { font-size: 16px; font-weight: 600; color: #333; }\n");
        html.append("    .suite-stats { display: flex; gap: 15px; font-size: 14px; }\n");
        html.append("    .suite-stat { padding: 4px 12px; border-radius: 12px; font-weight: 500; }\n");
        html.append("    .suite-stat.passed { background: #d1fae5; color: #065f46; }\n");
        html.append("    .suite-stat.failed { background: #fee2e2; color: #991b1b; }\n");
        html.append("    .suite-stat.skipped { background: #e5e7eb; color: #374151; }\n");
        html.append("    .suite-body { display: none; padding: 0; }\n");
        html.append("    .suite-body.expanded { display: block; }\n");
        
        // 测试用例
        html.append("    .test-case-item { border-bottom: 1px solid #f3f4f6; transition: all 0.2s; }\n");
        html.append("    .test-case-item:hover { background: #fafafa; }\n");
        html.append("    .test-case-header { padding: 15px 40px; cursor: pointer; display: flex; justify-content: space-between; align-items: center; }\n");
        html.append("    .test-case-left { display: flex; align-items: center; gap: 15px; flex: 1; }\n");
        html.append("    .test-status-icon { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 16px; font-weight: bold; }\n");
        html.append("    .test-status-icon.passed { background: #d1fae5; color: #065f46; }\n");
        html.append("    .test-status-icon.failed { background: #fee2e2; color: #991b1b; }\n");
        html.append("    .test-status-icon.broken { background: #fef3c7; color: #92400e; }\n");
        html.append("    .test-status-icon.skipped { background: #e5e7eb; color: #6b7280; }\n");
        html.append("    .test-case-name { font-size: 15px; color: #333; font-weight: 500; }\n");
        html.append("    .test-case-right { display: flex; align-items: center; gap: 20px; font-size: 13px; color: #666; }\n");
        html.append("    .test-duration { display: flex; align-items: center; gap: 5px; }\n");
        html.append("    .test-case-body { display: none; padding: 0 40px 25px 87px; background: #fafafa; }\n");
        html.append("    .test-case-body.expanded { display: block; }\n");
        
        buildStylesPart4();
    }
    
    // 构建CSS样式 - 第四部分：测试详情和错误
    private void buildStylesPart4() {
        // 测试详情标签页
        html.append("    .test-details-tabs { border-bottom: 2px solid #e5e7eb; margin-bottom: 20px; }\n");
        html.append("    .test-details-tabs-list { display: flex; gap: 5px; list-style: none; }\n");
        html.append("    .test-details-tab { padding: 10px 20px; cursor: pointer; border: none; background: transparent; font-size: 14px; color: #666; border-bottom: 3px solid transparent; transition: all 0.3s; }\n");
        html.append("    .test-details-tab:hover { color: #667eea; }\n");
        html.append("    .test-details-tab.active { color: #667eea; border-bottom-color: #667eea; font-weight: 600; }\n");
        html.append("    .test-details-content { display: none; }\n");
        html.append("    .test-details-content.active { display: block; }\n");
        
        // 测试步骤
        html.append("    .test-steps { list-style: none; }\n");
        html.append("    .test-step { padding: 12px 20px; margin-bottom: 10px; border-radius: 8px; border-left: 4px solid #10b981; background: white; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); }\n");
        html.append("    .test-step.failed { border-left-color: #ef4444; background: #fef2f2; }\n");
        html.append("    .test-step-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }\n");
        html.append("    .test-step-title { font-size: 14px; font-weight: 500; color: #333; }\n");
        html.append("    .test-step-status { font-size: 12px; padding: 3px 10px; border-radius: 12px; }\n");
        html.append("    .test-step-status.passed { background: #d1fae5; color: #065f46; }\n");
        html.append("    .test-step-status.failed { background: #fee2e2; color: #991b1b; }\n");
        html.append("    .test-step-description { font-size: 13px; color: #666; margin-bottom: 8px; }\n");
        html.append("    .test-step-time { font-size: 12px; color: #999; }\n");
        
        // 参数和断言表格
        html.append("    .params-table, .assertions-table { width: 100%; border-collapse: collapse; margin-top: 15px; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); }\n");
        html.append("    .params-table th, .assertions-table th { background: #f9fafb; padding: 12px; text-align: left; font-size: 13px; font-weight: 600; color: #374151; border-bottom: 2px solid #e5e7eb; }\n");
        html.append("    .params-table td, .assertions-table td { padding: 10px 12px; font-size: 13px; border-bottom: 1px solid #f3f4f6; font-family: 'Consolas', 'Monaco', monospace; }\n");
        html.append("    .params-table tbody tr:hover, .assertions-table tbody tr:hover { background: #f9fafb; }\n");
        
        // 错误信息
        html.append("    .error-container { background: #fef2f2; border: 1px solid #fecaca; border-left: 4px solid #ef4444; border-radius: 8px; padding: 20px; margin-top: 15px; }\n");
        html.append("    .error-title { font-size: 16px; font-weight: 600; color: #991b1b; margin-bottom: 10px; display: flex; align-items: center; gap: 8px; }\n");
        html.append("    .error-message { background: white; padding: 15px; border-radius: 6px; font-family: 'Consolas', 'Monaco', monospace; font-size: 13px; color: #dc2626; margin-bottom: 15px; white-space: pre-wrap; word-break: break-word; }\n");
        html.append("    .error-stack { background: #1f2937; color: #f3f4f6; padding: 15px; border-radius: 6px; font-family: 'Consolas', 'Monaco', monospace; font-size: 12px; line-height: 1.8; max-height: 300px; overflow-y: auto; }\n");
        
        // HTTP请求/响应
        html.append("    .http-section { margin-top: 15px; }\n");
        html.append("    .http-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 15px; background: #f9fafb; border-radius: 8px 8px 0 0; border: 1px solid #e5e7eb; border-bottom: none; }\n");
        html.append("    .http-method { padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; color: white; }\n");
        html.append("    .http-method.GET { background: #10b981; }\n");
        html.append("    .http-method.POST { background: #3b82f6; }\n");
        html.append("    .http-method.PUT { background: #f59e0b; }\n");
        html.append("    .http-method.DELETE { background: #ef4444; }\n");
        html.append("    .http-url { font-family: 'Consolas', 'Monaco', monospace; font-size: 13px; color: #4b5563; flex: 1; margin: 0 15px; }\n");
        html.append("    .http-status { padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; }\n");
        html.append("    .http-status.success { background: #d1fae5; color: #065f46; }\n");
        html.append("    .http-status.error { background: #fee2e2; color: #991b1b; }\n");
        html.append("    .http-body { background: #1f2937; color: #f3f4f6; padding: 20px; border-radius: 0 0 8px 8px; border: 1px solid #e5e7eb; font-family: 'Consolas', 'Monaco', monospace; font-size: 13px; line-height: 1.8; max-height: 400px; overflow-y: auto; }\n");
        html.append("    .json-key { color: #93c5fd; }\n");
        html.append("    .json-string { color: #86efac; }\n");
        html.append("    .json-number { color: #fbbf24; }\n");
        
        // 滚动条和响应式
        html.append("    ::-webkit-scrollbar { width: 8px; height: 8px; }\n");
        html.append("    ::-webkit-scrollbar-track { background: #f1f1f1; }\n");
        html.append("    ::-webkit-scrollbar-thumb { background: #c1c1c1; border-radius: 4px; }\n");
        html.append("    ::-webkit-scrollbar-thumb:hover { background: #a8a8a8; }\n");
        html.append("    @media (max-width: 1024px) { .sidebar { width: 220px; } .content-area { padding: 20px; } }\n");
        html.append("    @media (max-width: 768px) { .sidebar { position: fixed; left: -260px; transition: left 0.3s; z-index: 999; } .content-area { padding: 15px; } }\n");
        
        html.append("  </style>\n");
    }
    
    // 辅助方法：HTML转义
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    // 辅助方法：格式化时间
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    // 辅助方法：格式化持续时间
    private String formatDuration(Long durationMs) {
        if (durationMs == null || durationMs == 0) return "0ms";
        if (durationMs < 1000) return durationMs + "ms";
        double seconds = durationMs / 1000.0;
        if (seconds < 60) return String.format("%.2fs", seconds);
        long minutes = (long)(seconds / 60);
        seconds = seconds % 60;
        return String.format("%d分%.0f秒", minutes, seconds);
    }
    
    // buildBody方法将在下一部分实现
    private void buildBody() {
        html.append("<body>\n");
        buildNavbar();
        html.append("  <div class=\"main-container\">\n");
        buildSidebar();
        html.append("    <main class=\"content-area\">\n");
        buildOverviewSection();
        buildSuitesSection();
        buildGraphsSection();
        html.append("    </main>\n");
        html.append("  </div>\n");
        buildJavaScript();
        html.append("</body>\n");
        html.append("</html>");
    }
    
    // 构建导航栏
    private void buildNavbar() {
        html.append("  <div class=\"top-navbar\">\n");
        html.append("    <div class=\"navbar-brand\">\n");
        html.append("      <div class=\"logo\">🧪</div>\n");
        html.append("      <span>Allure风格测试报告</span>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"navbar-info\">\n");
        html.append("      <div class=\"navbar-info-item\"><span>📅</span><span>").append(formatDateTime(reportData.getStartTime())).append("</span></div>\n");
        html.append("      <div class=\"navbar-info-item\"><span>⏱️</span><span>耗时: ").append(formatDuration(reportData.getTotalDuration())).append("</span></div>\n");
        html.append("      <div class=\"navbar-info-item\"><span>🎯</span><span>通过率: ").append(reportData.getSuccessRate()).append("%</span></div>\n");
        html.append("    </div>\n");
        html.append("  </div>\n\n");
    }
    
    // 构建侧边栏
    private void buildSidebar() {
        html.append("    <aside class=\"sidebar\">\n");
        html.append("      <ul class=\"sidebar-nav\">\n");
        html.append("        <li class=\"sidebar-nav-item active\" onclick=\"showSection('overview')\"><span class=\"sidebar-nav-icon\">📊</span><span>Overview</span></li>\n");
        html.append("        <li class=\"sidebar-nav-item\" onclick=\"showSection('suites')\"><span class=\"sidebar-nav-icon\">📦</span><span>Suites</span></li>\n");
        html.append("        <li class=\"sidebar-nav-item\" onclick=\"showSection('graphs')\"><span class=\"sidebar-nav-icon\">📈</span><span>Graphs</span></li>\n");
        html.append("      </ul>\n");
        html.append("    </aside>\n\n");
    }
    
    // 构建Overview部分
    private void buildOverviewSection() {
        html.append("      <section id=\"overview\" class=\"content-section active\">\n");
        html.append("        <div class=\"overview-header\">\n");
        html.append("          <h1 class=\"overview-title\">").append(escapeHtml(reportData.getReportTitle())).append("</h1>\n");
        html.append("          <p class=\"overview-subtitle\">生成时间: ").append(formatDateTime(reportData.getStartTime()));
        html.append(" | 执行ID: ").append(escapeHtml(reportData.getExecutionId())).append("</p>\n");
        html.append("        </div>\n\n");
        
        // 统计表格
        html.append("        <div class=\"charts-section\">\n");
        html.append("          <table class=\"stats-table\">\n");
        html.append("            <thead><tr><th style=\"text-align: left;\">测试名称</th><th>总用例数</th><th>已执行</th><th>通过</th><th>失败</th><th>异常</th><th>跳过</th><th>通过率</th></tr></thead>\n");
        html.append("            <tbody>\n");
        html.append("              <tr>\n");
        html.append("                <td>全部测试用例</td>\n");
        html.append("                <td>").append(reportData.getTotalCases()).append("</td>\n");
        html.append("                <td>").append(reportData.getExecutedCases()).append("</td>\n");
        html.append("                <td>").append(reportData.getPassedCases()).append("</td>\n");
        html.append("                <td>").append(reportData.getFailedCases()).append("</td>\n");
        html.append("                <td>").append(reportData.getBrokenCases()).append("</td>\n");
        html.append("                <td>").append(reportData.getSkippedCases()).append("</td>\n");
        html.append("                <td class=\"pass-rate\">").append(reportData.getSuccessRate()).append("%</td>\n");
        html.append("              </tr>\n");
        html.append("            </tbody>\n");
        html.append("          </table>\n");
        html.append("        </div>\n\n");
        
        // 图表区域
        html.append("        <div style=\"display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin-bottom: 30px;\">\n");
        html.append("          <div style=\"background: white; padding: 25px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);\">\n");
        html.append("            <div style=\"font-size: 18px; font-weight: 600; color: #333; margin-bottom: 20px; display: flex; align-items: center; gap: 10px;\"><span>🎯</span><span>用例状态分布</span></div>\n");
        html.append("            <div id=\"statusPieChart\" style=\"height: 400px;\"></div>\n");
        html.append("          </div>\n");
        html.append("          <div style=\"background: white; padding: 25px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);\">\n");
        html.append("            <div style=\"font-size: 18px; font-weight: 600; color: #333; margin-bottom: 20px; display: flex; align-items: center; gap: 10px;\"><span>🚦</span><span>缺陷优先级分布</span></div>\n");
        html.append("            <div id=\"severityChart\" style=\"height: 400px;\"></div>\n");
        html.append("          </div>\n");
        html.append("        </div>\n\n");
        
        // 测试结果统计柱状图
        html.append("        <div class=\"charts-section\">\n");
        html.append("          <h2 class=\"section-title\">📊 测试结果统计</h2>\n");
        html.append("          <div id=\"resultBarChart\" class=\"chart-container\"></div>\n");
        html.append("        </div>\n");
        html.append("      </section>\n\n");
    }
    
    // 构建Suites部分
    private void buildSuitesSection() {
        html.append("      <section id=\"suites\" class=\"content-section\">\n");
        html.append("        <div class=\"suites-container\">\n");
        
        if (reportData.getTestSuites() != null && !reportData.getTestSuites().isEmpty()) {
            for (AllureReportDTO.TestSuite suite : reportData.getTestSuites()) {
                buildTestSuite(suite);
            }
        } else {
            html.append("          <div style=\"padding: 40px; text-align: center; color: #999;\">暂无测试套件数据</div>\n");
        }
        
        html.append("        </div>\n");
        html.append("      </section>\n\n");
    }
    
    // 构建单个测试套件
    private void buildTestSuite(AllureReportDTO.TestSuite suite) {
        html.append("          <div class=\"suite-item\">\n");
        html.append("            <div class=\"suite-header\" onclick=\"toggleSuite(this)\">\n");
        html.append("              <div class=\"suite-info\">\n");
        html.append("                <span class=\"suite-icon\">").append(suite.getSuiteIcon() != null ? suite.getSuiteIcon() : "📦").append("</span>\n");
        html.append("                <div>\n");
        html.append("                  <div class=\"suite-name\">").append(escapeHtml(suite.getSuiteName())).append("</div>\n");
        if (suite.getSuiteDescription() != null) {
            html.append("                  <div style=\"font-size: 13px; color: #666; margin-top: 4px;\">").append(escapeHtml(suite.getSuiteDescription())).append("</div>\n");
        }
        html.append("                </div>\n");
        html.append("              </div>\n");
        html.append("              <div class=\"suite-stats\">\n");
        html.append("                <span class=\"suite-stat passed\">✓ ").append(suite.getPassedCases()).append(" Passed</span>\n");
        if (suite.getFailedCases() > 0) {
            html.append("                <span class=\"suite-stat failed\">✗ ").append(suite.getFailedCases()).append(" Failed</span>\n");
        }
        html.append("                <span class=\"suite-stat\">").append(suite.getTotalCases()).append(" Total</span>\n");
        html.append("              </div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"suite-body\">\n");
        
        // 构建测试用例列表
        if (suite.getTestCases() != null && !suite.getTestCases().isEmpty()) {
            for (AllureReportDTO.TestCase testCase : suite.getTestCases()) {
                buildTestCase(testCase);
            }
        }
        
        html.append("            </div>\n");
        html.append("          </div>\n\n");
    }
    
    // 构建单个测试用例
    private void buildTestCase(AllureReportDTO.TestCase testCase) {
        String statusClass = testCase.getStatus() != null ? testCase.getStatus().toLowerCase() : "skipped";
        String statusIcon = getStatusIcon(testCase.getStatus());
        
        html.append("              <div class=\"test-case-item\">\n");
        html.append("                <div class=\"test-case-header\" onclick=\"toggleTestCase(this)\">\n");
        html.append("                  <div class=\"test-case-left\">\n");
        html.append("                    <div class=\"test-status-icon ").append(statusClass).append("\">").append(statusIcon).append("</div>\n");
        html.append("                    <div class=\"test-case-name\">").append(escapeHtml(testCase.getCaseName())).append("</div>\n");
        html.append("                  </div>\n");
        html.append("                  <div class=\"test-case-right\">\n");
        html.append("                    <div class=\"test-duration\"><span>⏱️</span><span>").append(formatDuration(testCase.getDuration())).append("</span></div>\n");
        if (testCase.getTags() != null && !testCase.getTags().isEmpty()) {
            html.append("                    <span>🏷️ ").append(String.join(" ", testCase.getTags())).append("</span>\n");
        }
        html.append("                  </div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"test-case-body\">\n");
        
        // 构建测试详情标签页
        buildTestCaseDetails(testCase);
        
        html.append("                </div>\n");
        html.append("              </div>\n\n");
    }
    
    // 构建测试用例详情
    private void buildTestCaseDetails(AllureReportDTO.TestCase testCase) {
        String caseIdStr = String.valueOf(testCase.getCaseId());
        
        // 标签页导航
        html.append("                  <div class=\"test-details-tabs\">\n");
        html.append("                    <ul class=\"test-details-tabs-list\">\n");
        html.append("                      <li class=\"test-details-tab active\" onclick=\"switchTab(this, 'steps-").append(caseIdStr).append("')\">Steps</li>\n");
        html.append("                      <li class=\"test-details-tab\" onclick=\"switchTab(this, 'params-").append(caseIdStr).append("')\">Parameters</li>\n");
        html.append("                      <li class=\"test-details-tab\" onclick=\"switchTab(this, 'http-").append(caseIdStr).append("')\">HTTP</li>\n");
        if (testCase.getFailureInfo() != null) {
            html.append("                      <li class=\"test-details-tab\" onclick=\"switchTab(this, 'error-").append(caseIdStr).append("')\">Error</li>\n");
        }
        html.append("                    </ul>\n");
        html.append("                  </div>\n\n");
        
        // Steps内容
        buildStepsTab(testCase, caseIdStr);
        
        // Parameters内容
        buildParametersTab(testCase, caseIdStr);
        
        // HTTP内容
        buildHttpTab(testCase, caseIdStr);
        
        // Error内容（如果有）
        if (testCase.getFailureInfo() != null) {
            buildErrorTab(testCase, caseIdStr);
        }
    }
    
    // 构建Steps标签页
    private void buildStepsTab(AllureReportDTO.TestCase testCase, String caseIdStr) {
        html.append("                  <div id=\"steps-").append(caseIdStr).append("\" class=\"test-details-content active\">\n");
        html.append("                    <ol class=\"test-steps\">\n");
        
        if (testCase.getSteps() != null && !testCase.getSteps().isEmpty()) {
            for (AllureReportDTO.TestStep step : testCase.getSteps()) {
                String stepClass = "failed".equalsIgnoreCase(step.getStatus()) ? "failed" : "";
                String stepStatusClass = "failed".equalsIgnoreCase(step.getStatus()) ? "failed" : "passed";
                String stepStatusIcon = "failed".equalsIgnoreCase(step.getStatus()) ? "✗" : "✓";
                
                html.append("                      <li class=\"test-step ").append(stepClass).append("\">\n");
                html.append("                        <div class=\"test-step-header\">\n");
                html.append("                          <span class=\"test-step-title\">").append(step.getStepIndex()).append(". ").append(escapeHtml(step.getStepTitle())).append("</span>\n");
                html.append("                          <span class=\"test-step-status ").append(stepStatusClass).append("\">").append(stepStatusIcon).append(" ").append(step.getStatus()).append("</span>\n");
                html.append("                        </div>\n");
                if (step.getStepDescription() != null) {
                    html.append("                        <div class=\"test-step-description\">").append(escapeHtml(step.getStepDescription()).replace("\n", "<br>")).append("</div>\n");
                }
                if (step.getErrorMessage() != null) {
                    html.append("                        <div class=\"test-step-description\" style=\"color: #dc2626;\">❌ ").append(escapeHtml(step.getErrorMessage())).append("</div>\n");
                }
                html.append("                        <div class=\"test-step-time\">⏱️ ").append(formatDuration(step.getDuration())).append("</div>\n");
                html.append("                      </li>\n");
            }
        }
        
        html.append("                    </ol>\n");
        html.append("                  </div>\n\n");
    }
    
    // 构建Parameters标签页
    private void buildParametersTab(AllureReportDTO.TestCase testCase, String caseIdStr) {
        html.append("                  <div id=\"params-").append(caseIdStr).append("\" class=\"test-details-content\">\n");
        
        if (testCase.getParameters() != null && !testCase.getParameters().isEmpty()) {
            html.append("                    <table class=\"params-table\">\n");
            html.append("                      <thead><tr><th>Parameter</th><th>Value</th><th>Type</th></tr></thead>\n");
            html.append("                      <tbody>\n");
            for (AllureReportDTO.TestParameter param : testCase.getParameters()) {
                html.append("                        <tr>\n");
                html.append("                          <td>").append(escapeHtml(param.getParamName())).append("</td>\n");
                html.append("                          <td>").append(escapeHtml(param.getParamValue())).append("</td>\n");
                html.append("                          <td>").append(escapeHtml(param.getParamType())).append("</td>\n");
                html.append("                        </tr>\n");
            }
            html.append("                      </tbody>\n");
            html.append("                    </table>\n");
        }
        
        if (testCase.getAssertions() != null && !testCase.getAssertions().isEmpty()) {
            int passedAssertions = (int) testCase.getAssertions().stream().filter(a -> "passed".equalsIgnoreCase(a.getStatus())).count();
            html.append("                    <h3 style=\"margin-top: 25px; margin-bottom: 10px; font-size: 16px;\">Assertions (").append(passedAssertions).append(" passed)</h3>\n");
            html.append("                    <table class=\"assertions-table\">\n");
            html.append("                      <thead><tr><th>Assertion</th><th>Expected</th><th>Actual</th><th>Status</th></tr></thead>\n");
            html.append("                      <tbody>\n");
            for (AllureReportDTO.Assertion assertion : testCase.getAssertions()) {
                String assertStatusClass = "passed".equalsIgnoreCase(assertion.getStatus()) ? "passed" : "failed";
                String assertStatusIcon = "passed".equalsIgnoreCase(assertion.getStatus()) ? "✓" : "✗";
                html.append("                        <tr>\n");
                html.append("                          <td>").append(escapeHtml(assertion.getAssertionName())).append("</td>\n");
                html.append("                          <td>").append(escapeHtml(assertion.getExpected())).append("</td>\n");
                html.append("                          <td>").append(escapeHtml(assertion.getActual())).append("</td>\n");
                html.append("                          <td><span class=\"test-step-status ").append(assertStatusClass).append("\">").append(assertStatusIcon).append("</span></td>\n");
                html.append("                        </tr>\n");
            }
            html.append("                      </tbody>\n");
            html.append("                    </table>\n");
        }
        
        html.append("                  </div>\n\n");
    }
    
    // 构建HTTP标签页
    private void buildHttpTab(AllureReportDTO.TestCase testCase, String caseIdStr) {
        html.append("                  <div id=\"http-").append(caseIdStr).append("\" class=\"test-details-content\">\n");
        
        // 请求
        if (testCase.getHttpRequest() != null) {
            AllureReportDTO.HttpRequest request = testCase.getHttpRequest();
            html.append("                    <h3 style=\"margin-bottom: 15px; font-size: 16px;\">Request</h3>\n");
            html.append("                    <div class=\"http-section\">\n");
            html.append("                      <div class=\"http-header\">\n");
            html.append("                        <span class=\"http-method ").append(request.getMethod()).append("\">").append(request.getMethod()).append("</span>\n");
            html.append("                        <span class=\"http-url\">").append(escapeHtml(request.getUrl())).append("</span>\n");
            html.append("                      </div>\n");
            html.append("                      <div class=\"http-body\">").append(escapeHtml(request.getBody())).append("</div>\n");
            html.append("                    </div>\n");
        }
        
        // 响应
        if (testCase.getHttpResponse() != null) {
            AllureReportDTO.HttpResponse response = testCase.getHttpResponse();
            String statusClass = response.getStatusCode() >= 200 && response.getStatusCode() < 300 ? "success" : "error";
            html.append("                    <h3 style=\"margin: 25px 0 15px; font-size: 16px;\">Response</h3>\n");
            html.append("                    <div class=\"http-section\">\n");
            html.append("                      <div class=\"http-header\">\n");
            html.append("                        <span class=\"http-status ").append(statusClass).append("\">").append(response.getStatusCode()).append("</span>\n");
            html.append("                        <span style=\"font-size: 12px; color: #666;\">⏱️ ").append(formatDuration(response.getDuration())).append("</span>\n");
            html.append("                      </div>\n");
            html.append("                      <div class=\"http-body\">").append(escapeHtml(response.getBody())).append("</div>\n");
            html.append("                    </div>\n");
        }
        
        html.append("                  </div>\n\n");
    }
    
    // 构建Error标签页
    private void buildErrorTab(AllureReportDTO.TestCase testCase, String caseIdStr) {
        html.append("                  <div id=\"error-").append(caseIdStr).append("\" class=\"test-details-content\">\n");
        html.append("                    <div class=\"error-container\">\n");
        html.append("                      <div class=\"error-title\"><span>🚨</span><span>").append(escapeHtml(testCase.getFailureInfo().getErrorType())).append("</span></div>\n");
        if (testCase.getFailureInfo().getErrorMessage() != null) {
            html.append("                      <div class=\"error-message\">").append(escapeHtml(testCase.getFailureInfo().getErrorMessage())).append("</div>\n");
        }
        if (testCase.getFailureInfo().getStackTrace() != null) {
            html.append("                      <h4 style=\"margin: 15px 0 10px; font-size: 14px; font-weight: 600;\">Stack Trace:</h4>\n");
            html.append("                      <div class=\"error-stack\">").append(escapeHtml(testCase.getFailureInfo().getStackTrace())).append("</div>\n");
        }
        html.append("                    </div>\n");
        html.append("                  </div>\n\n");
    }
    
    // 构建Graphs部分
    private void buildGraphsSection() {
        html.append("      <section id=\"graphs\" class=\"content-section\">\n");
        html.append("        <div class=\"charts-section\">\n");
        html.append("          <h2 class=\"section-title\">📈 测试结果趋势分析</h2>\n");
        html.append("          <div id=\"historyChart\" class=\"chart-container\"></div>\n");
        html.append("        </div>\n");
        html.append("      </section>\n\n");
    }
    
    // 辅助方法：获取状态图标
    private String getStatusIcon(String status) {
        if (status == null) return "?";
        switch (status.toLowerCase()) {
            case "passed": return "✓";
            case "failed": return "✗";
            case "broken": return "⚠";
            case "skipped": return "⊘";
            default: return "?";
        }
    }
    
    // 构建JavaScript代码
    private void buildJavaScript() {
        html.append("  <script>\n");
        
        // 切换侧边栏导航
        html.append("    function showSection(sectionId) {\n");
        html.append("      document.querySelectorAll('.content-section').forEach(section => section.classList.remove('active'));\n");
        html.append("      document.getElementById(sectionId).classList.add('active');\n");
        html.append("      document.querySelectorAll('.sidebar-nav-item').forEach(item => item.classList.remove('active'));\n");
        html.append("      event.currentTarget.classList.add('active');\n");
        html.append("    }\n\n");
        
        // 切换测试套件
        html.append("    function toggleSuite(element) {\n");
        html.append("      const suiteBody = element.nextElementSibling;\n");
        html.append("      if (suiteBody.classList.contains('expanded')) {\n");
        html.append("        suiteBody.classList.remove('expanded');\n");
        html.append("        element.classList.remove('expanded');\n");
        html.append("      } else {\n");
        html.append("        suiteBody.classList.add('expanded');\n");
        html.append("        element.classList.add('expanded');\n");
        html.append("      }\n");
        html.append("    }\n\n");
        
        // 切换测试用例
        html.append("    function toggleTestCase(element) {\n");
        html.append("      const testBody = element.nextElementSibling;\n");
        html.append("      testBody.classList.toggle('expanded');\n");
        html.append("    }\n\n");
        
        // 切换标签页
        html.append("    function switchTab(tabElement, contentId) {\n");
        html.append("      tabElement.parentElement.querySelectorAll('.test-details-tab').forEach(tab => tab.classList.remove('active'));\n");
        html.append("      const container = tabElement.closest('.test-case-body');\n");
        html.append("      container.querySelectorAll('.test-details-content').forEach(content => content.classList.remove('active'));\n");
        html.append("      tabElement.classList.add('active');\n");
        html.append("      container.querySelector('#' + contentId).classList.add('active');\n");
        html.append("    }\n\n");
        
        // 初始化图表
        html.append("    window.addEventListener('load', function() {\n");
        
        // 图表1: 用例状态分布饼图
        buildStatusPieChart();
        
        // 图表2: 缺陷优先级分布
        buildSeverityChart();
        
        // 图表3: 测试结果统计柱状图
        buildResultBarChart();
        
        // 图表4: 历史趋势图
        buildHistoryChart();
        
        // 响应式调整
        html.append("      window.addEventListener('resize', function() {\n");
        html.append("        statusPieChart.resize();\n");
        html.append("        severityChart.resize();\n");
        html.append("        resultBarChart.resize();\n");
        html.append("        historyChart.resize();\n");
        html.append("      });\n");
        
        html.append("    });\n");
        html.append("  </script>\n");
    }
    
    // 构建状态饼图
    private void buildStatusPieChart() {
        html.append("      const statusPieChart = echarts.init(document.getElementById('statusPieChart'));\n");
        html.append("      statusPieChart.setOption({\n");
        html.append("        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },\n");
        html.append("        legend: { orient: 'vertical', right: 20, top: 'center', textStyle: { fontSize: 13, fontWeight: 500 } },\n");
        html.append("        series: [{\n");
        html.append("          name: '用例状态',\n");
        html.append("          type: 'pie',\n");
        html.append("          radius: ['50%', '75%'],\n");
        html.append("          center: ['40%', '50%'],\n");
        html.append("          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },\n");
        html.append("          label: { show: true, position: 'outside', fontSize: 13, fontWeight: 600 },\n");
        html.append("          data: [\n");
        html.append("            { value: ").append(reportData.getPassedCases()).append(", name: '通过', itemStyle: { color: '#5cb87a' } },\n");
        html.append("            { value: ").append(reportData.getFailedCases()).append(", name: '失败', itemStyle: { color: '#e05d5d' } },\n");
        html.append("            { value: ").append(reportData.getBrokenCases()).append(", name: '阻塞', itemStyle: { color: '#8b4513' } },\n");
        html.append("            { value: ").append(reportData.getSkippedCases()).append(", name: '跳过', itemStyle: { color: '#c0c0c0' } }\n");
        html.append("          ]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
    }
    
    // 构建严重程度图表
    private void buildSeverityChart() {
        html.append("      const severityChart = echarts.init(document.getElementById('severityChart'));\n");
        html.append("      severityChart.setOption({\n");
        html.append("        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },\n");
        html.append("        legend: { orient: 'vertical', right: 20, top: 'center', textStyle: { fontSize: 13, fontWeight: 500 } },\n");
        html.append("        series: [{\n");
        html.append("          name: '缺陷优先级',\n");
        html.append("          type: 'pie',\n");
        html.append("          radius: ['50%', '75%'],\n");
        html.append("          center: ['40%', '50%'],\n");
        html.append("          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },\n");
        html.append("          label: { show: true, position: 'outside', fontSize: 13, fontWeight: 600 },\n");
        html.append("          data: [\n");
        html.append("            { value: 0, name: 'P0 (阻塞)', itemStyle: { color: '#8b0000' } },\n");
        html.append("            { value: ").append(reportData.getFailedCases()).append(", name: 'P1 (严重)', itemStyle: { color: '#e85d5d' } },\n");
        html.append("            { value: 0, name: 'P2 (一般)', itemStyle: { color: '#f39c6b' } },\n");
        html.append("            { value: ").append(reportData.getPassedCases()).append(", name: 'P3 (轻微)', itemStyle: { color: '#5b9bd5' } }\n");
        html.append("          ]\n");
        html.append("        }]\n");
        html.append("      });\n\n");
    }
    
    // 构建结果柱状图
    private void buildResultBarChart() {
        html.append("      const resultBarChart = echarts.init(document.getElementById('resultBarChart'));\n");
        html.append("      resultBarChart.setOption({\n");
        html.append("        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },\n");
        html.append("        grid: { left: '3%', right: '5%', bottom: '8%', top: '10%', containLabel: true },\n");
        html.append("        xAxis: { type: 'category', data: ['总用例数', '已执行', '通过', '失败', '异常', '跳过'], axisLine: { lineStyle: { color: '#e5e7eb', width: 2 } }, axisLabel: { fontSize: 14, color: '#6b7280', margin: 15, fontWeight: 500 }, axisTick: { show: false } },\n");
        html.append("        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } }, axisLine: { show: false }, axisLabel: { fontSize: 13, color: '#6b7280' } },\n");
        html.append("        series: [{\n");
        html.append("          name: '数量',\n");
        html.append("          type: 'bar',\n");
        html.append("          data: [\n");
        html.append("            { value: ").append(reportData.getTotalCases()).append(", itemStyle: { color: '#60a5fa' } },\n");
        html.append("            { value: ").append(reportData.getExecutedCases()).append(", itemStyle: { color: '#60a5fa' } },\n");
        html.append("            { value: ").append(reportData.getPassedCases()).append(", itemStyle: { color: '#34d399' } },\n");
        html.append("            { value: ").append(reportData.getFailedCases()).append(", itemStyle: { color: '#f87171' } },\n");
        html.append("            { value: ").append(reportData.getBrokenCases()).append(", itemStyle: { color: '#fbbf24' } },\n");
        html.append("            { value: ").append(reportData.getSkippedCases()).append(", itemStyle: { color: '#d1d5db' } }\n");
        html.append("          ],\n");
        html.append("          barWidth: '45%',\n");
        html.append("          label: { show: true, position: 'top', fontSize: 14, fontWeight: 600, color: '#333' },\n");
        html.append("          itemStyle: { borderRadius: [8, 8, 0, 0] }\n");
        html.append("        }]\n");
        html.append("      });\n\n");
    }
    
    // 构建历史趋势图
    private void buildHistoryChart() {
        if (reportData.getHistoryTrends() == null || reportData.getHistoryTrends().isEmpty()) {
            html.append("      const historyChart = echarts.init(document.getElementById('historyChart'));\n");
            html.append("      historyChart.setOption({ title: { text: '暂无历史数据', left: 'center', top: 'center', textStyle: { color: '#999' } } });\n\n");
            return;
        }
        
        StringBuilder dates = new StringBuilder();
        StringBuilder passedData = new StringBuilder();
        StringBuilder failedData = new StringBuilder();
        StringBuilder skippedData = new StringBuilder();
        
        for (int i = 0; i < reportData.getHistoryTrends().size(); i++) {
            AllureReportDTO.HistoryTrend trend = reportData.getHistoryTrends().get(i);
            if (i > 0) {
                dates.append(", ");
                passedData.append(", ");
                failedData.append(", ");
                skippedData.append(", ");
            }
            dates.append("'").append(trend.getDate()).append("'");
            passedData.append(trend.getPassedCases());
            failedData.append(trend.getFailedCases());
            skippedData.append(trend.getSkippedCases());
        }
        
        html.append("      const historyChart = echarts.init(document.getElementById('historyChart'));\n");
        html.append("      historyChart.setOption({\n");
        html.append("        tooltip: { trigger: 'axis' },\n");
        html.append("        legend: { data: ['通过', '失败', '跳过'], top: 15, right: 30 },\n");
        html.append("        grid: { left: '3%', right: '5%', bottom: '5%', top: '15%', containLabel: true },\n");
        html.append("        xAxis: { type: 'category', data: [").append(dates).append("], axisLine: { lineStyle: { color: '#e5e7eb' } } },\n");
        html.append("        yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f3f4f6' } } },\n");
        html.append("        series: [\n");
        html.append("          { name: '通过', type: 'bar', stack: 'total', data: [").append(passedData).append("], itemStyle: { color: '#5cb87a' } },\n");
        html.append("          { name: '失败', type: 'bar', stack: 'total', data: [").append(failedData).append("], itemStyle: { color: '#e05d5d' } },\n");
        html.append("          { name: '跳过', type: 'bar', stack: 'total', data: [").append(skippedData).append("], itemStyle: { color: '#c0c0c0', borderRadius: [4, 4, 0, 0] } }\n");
        html.append("        ]\n");
        html.append("      });\n\n");
    }
}

