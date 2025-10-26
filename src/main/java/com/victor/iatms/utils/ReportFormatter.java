package com.victor.iatms.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 报告格式化工具类
 * 提供各种数据格式化方法，用于报告生成
 * 
 * @author Victor
 * @since 2024-10-26
 */
public class ReportFormatter {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 格式化报告类型
     * 
     * @param type 报告类型
     * @return 中文显示
     */
    public static String formatReportType(String type) {
        if (type == null) return "未知";
        
        switch (type.toLowerCase()) {
            case "api":
                return "接口测试";
            case "performance":
                return "性能测试";
            case "automation":
                return "自动化测试";
            case "manual":
                return "手工测试";
            case "execution":
                return "执行报告";
            case "trend":
                return "趋势分析";
            default:
                return type;
        }
    }
    
    /**
     * 格式化环境
     * 
     * @param env 环境
     * @return 中文显示
     */
    public static String formatEnvironment(String env) {
        if (env == null) return "未知";
        
        switch (env.toLowerCase()) {
            case "dev":
            case "development":
                return "开发环境";
            case "test":
            case "testing":
                return "测试环境";
            case "staging":
            case "pre":
                return "预发布环境";
            case "prod":
            case "production":
                return "生产环境";
            default:
                return env;
        }
    }
    
    /**
     * 格式化状态
     * 
     * @param status 状态
     * @return 中文显示
     */
    public static String formatStatus(String status) {
        if (status == null) return "未知";
        
        switch (status.toLowerCase()) {
            case "generating":
                return "生成中";
            case "completed":
                return "已完成";
            case "failed":
                return "失败";
            case "passed":
                return "通过";
            case "broken":
                return "异常";
            case "skipped":
                return "跳过";
            default:
                return status;
        }
    }
    
    /**
     * 格式化日期时间
     * 
     * @param dateTime 日期时间
     * @return 格式化后的字符串 "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * 格式化持续时间（毫秒转为可读格式）
     * 
     * @param durationMs 持续时间（毫秒）
     * @return 格式化后的字符串，如 "1小时30分25秒" 或 "5分30秒" 或 "45秒"
     */
    public static String formatDuration(Long durationMs) {
        if (durationMs == null || durationMs < 0) {
            return "-";
        }
        
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        
        StringBuilder sb = new StringBuilder();
        
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append("秒");
        }
        
        return sb.toString();
    }
    
    /**
     * 格式化文件大小
     * 
     * @param sizeBytes 文件大小（字节）
     * @return 格式化后的字符串，如 "1.5 MB" 或 "256 KB"
     */
    public static String formatFileSize(Long sizeBytes) {
        if (sizeBytes == null || sizeBytes < 0) {
            return "-";
        }
        
        if (sizeBytes < 1024) {
            return sizeBytes + " B";
        } else if (sizeBytes < 1024 * 1024) {
            return String.format("%.2f KB", sizeBytes / 1024.0);
        } else if (sizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", sizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * 格式化百分比
     * 
     * @param value 数值（0-100）
     * @return 格式化后的字符串，如 "95.5%"
     */
    public static String formatPercentage(Number value) {
        if (value == null) {
            return "0%";
        }
        return String.format("%.1f%%", value.doubleValue());
    }
    
    /**
     * 计算百分比
     * 
     * @param part 部分值
     * @param total 总值
     * @return 百分比（0-100）
     */
    public static double calculatePercentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (part * 100.0) / total;
    }
    
    /**
     * HTML转义，防止XSS攻击
     * 
     * @param text 原始文本
     * @return 转义后的文本
     */
    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * 根据成功率获取颜色
     * 
     * @param successRate 成功率（0-100）
     * @return 颜色代码
     */
    public static String getSuccessRateColor(double successRate) {
        if (successRate >= 80) {
            return "#67c23a"; // 绿色
        } else if (successRate >= 60) {
            return "#e6a23c"; // 橙色
        } else {
            return "#f56c6c"; // 红色
        }
    }
}

