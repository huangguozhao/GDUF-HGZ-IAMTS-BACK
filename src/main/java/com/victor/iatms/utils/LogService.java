package com.victor.iatms.utils;

import com.victor.iatms.entity.po.Log;
import com.victor.iatms.mappers.LogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 操作日志记录工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogService {

    private final LogMapper logMapper;

    // 操作类型常量
    public static final String OP_LOGIN = "login";
    public static final String OP_LOGOUT = "logout";
    public static final String OP_CREATE_PROJECT = "create_project";
    public static final String OP_UPDATE_PROJECT = "update_project";
    public static final String OP_DELETE_PROJECT = "delete_project";
    public static final String OP_EXECUTE_TEST = "execute_test";
    public static final String OP_CREATE_CASE = "create_case";
    public static final String OP_UPDATE_CASE = "update_case";
    public static final String OP_DELETE_CASE = "delete_case";
    public static final String OP_CREATE_TASK = "create_task";
    public static final String OP_UPDATE_TASK = "update_task";
    public static final String OP_GENERATE_REPORT = "generate_report";
    public static final String OP_SHARE_PROJECT = "share_project";
    
    // ==================== 测试执行相关操作类型 ====================
    public static final String OP_EXECUTE_TEST_CASE = "execute_test_case";      // 执行测试用例
    public static final String OP_EXECUTE_TEST_MODULE = "execute_test_module";  // 执行测试模块
    public static final String OP_EXECUTE_TEST_PROJECT = "execute_test_project";// 执行测试项目
    public static final String OP_EXECUTE_TEST_SUITE = "execute_test_suite";    // 执行测试套件
    public static final String OP_CREATE_SCHEDULED_TASK = "create_scheduled_task";     // 创建定时任务
    public static final String OP_UPDATE_SCHEDULED_TASK = "update_scheduled_task";     // 更新定时任务
    public static final String OP_DELETE_SCHEDULED_TASK = "delete_scheduled_task";     // 删除定时任务
    public static final String OP_EXECUTE_SCHEDULED_TASK = "execute_scheduled_task";   // 执行定时任务
    public static final String OP_AI_DIAGNOSIS = "ai_diagnosis";               // AI诊断分析
    public static final String OP_CREATE_API = "create_api";                   // 创建接口
    public static final String OP_UPDATE_API = "update_api";                   // 更新接口
    public static final String OP_DELETE_API = "delete_api";                   // 删除接口
    public static final String OP_CREATE_ENVIRONMENT = "create_environment";   // 创建环境配置
    public static final String OP_UPDATE_ENVIRONMENT = "update_environment";   // 更新环境配置
    public static final String OP_DELETE_ENVIRONMENT = "delete_environment";   // 删除环境配置
    public static final String OP_EXPORT_REPORT = "export_report";             // 导出报告

    // 目标类型常量
    public static final String TARGET_PROJECT = "project";
    public static final String TARGET_CASE = "case";
    public static final String TARGET_TASK = "task";
    public static final String TARGET_REPORT = "report";

    // 状态常量
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILED = "failed";

    /**
     * 记录操作日志
     */
    public void logOperation(Integer userId, String operationType, Integer targetId,
                            String targetName, String targetType, String description) {
        logOperation(userId, operationType, targetId, targetName, targetType, description,
                    STATUS_SUCCESS, null, null, null, null, null, null);
    }

    /**
     * 记录操作日志（完整参数）
     */
    public void logOperation(Integer userId, String operationType, Integer targetId,
                            String targetName, String targetType, String description,
                            String status, String requestMethod, String requestPath,
                            String ipAddress, String userAgent, Long executionTime,
                            String errorMessage) {
        try {
            Log log = new Log();
            log.setUserId(userId);
            log.setOperationType(operationType);
            log.setTargetId(targetId);
            log.setTargetName(targetName);
            log.setTargetType(targetType);
            log.setDescription(description);
            log.setStatus(status != null ? status : STATUS_SUCCESS);
            log.setRequestMethod(requestMethod);
            log.setRequestPath(requestPath);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setExecutionTime(executionTime);
            log.setErrorMessage(errorMessage);
            log.setTimestamp(LocalDateTime.now());
            log.setIsDeleted(false);
            log.setCreatedAt(LocalDateTime.now());

            logMapper.insert(log);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
    }

    /**
     * 记录成功的操作
     */
    public void logSuccess(Integer userId, String operationType, Integer targetId,
                          String targetName, String targetType, String description) {
        logOperation(userId, operationType, targetId, targetName, targetType, description,
                    STATUS_SUCCESS, null, null, null, null, null, null);
    }

    /**
     * 记录失败的操作
     */
    public void logFailed(Integer userId, String operationType, Integer targetId,
                         String targetName, String targetType, String description,
                         String errorMessage) {
        logOperation(userId, operationType, targetId, targetName, targetType, description,
                    STATUS_FAILED, null, null, null, null, null, errorMessage);
    }
}

