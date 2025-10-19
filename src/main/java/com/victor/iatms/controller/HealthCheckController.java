package com.victor.iatms.controller;

import com.victor.iatms.entity.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于快速诊断系统问题
 */
@RestController
@RequestMapping("")
public class HealthCheckController {

    @Autowired(required = false)
    private DataSource dataSource;

    /**
     * 最简单的健康检查
     */
    @GetMapping("/health")
    public ResponseVO<String> health() {
        return ResponseVO.success("OK", "System is running");
    }

    /**
     * 详细的健康检查
     */
    @GetMapping("/health/detail")
    public ResponseVO<Map<String, Object>> healthDetail() {
        Map<String, Object> health = new HashMap<>();
        
        // 检查应用状态
        health.put("application", "running");
        health.put("timestamp", System.currentTimeMillis());
        
        // 检查数据库连接
        if (dataSource != null) {
            try (Connection conn = dataSource.getConnection()) {
                health.put("database", "connected");
                health.put("databaseUrl", conn.getMetaData().getURL());
            } catch (Exception e) {
                health.put("database", "error: " + e.getMessage());
            }
        } else {
            health.put("database", "datasource not configured");
        }
        
        return ResponseVO.success("Health check completed", health);
    }

    /**
     * 测试简单的JSON响应
     */
    @GetMapping("/test/json")
    public ResponseVO<Map<String, String>> testJson() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "JSON serialization works");
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseVO.success("success", data);
    }
}

