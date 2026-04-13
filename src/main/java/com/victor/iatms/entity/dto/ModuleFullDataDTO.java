package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 模块完整数据DTO（包含接口和用例）
 */
@Data
public class ModuleFullDataDTO {
    
    /**
     * 模块ID
     */
    private Integer moduleId;
    
    /**
     * 模块名称
     */
    private String moduleName;
    
    /**
     * 接口列表
     */
    private List<ApiWithTestCasesDTO> apis;
    
    /**
     * 接口及其关联的测试用例
     */
    @Data
    public static class ApiWithTestCasesDTO {
        /**
         * 接口ID
         */
        private Integer apiId;
        
        /**
         * 接口编码
         */
        private String apiCode;
        
        /**
         * 接口名称
         */
        private String name;
        
        /**
         * 请求方法
         */
        private String method;
        
        /**
         * 接口路径
         */
        private String path;
        
        /**
         * 接口状态
         */
        private String status;
        
        /**
         * 测试用例列表
         */
        private List<TestCaseSimpleDTO> testCases;
    }
    
    /**
     * 测试用例简单信息DTO
     */
    @Data
    public static class TestCaseSimpleDTO {
        /**
         * 用例ID
         */
        private Integer caseId;

        /**
         * 用例编码
         */
        private String caseCode;

        /**
         * 用例名称
         */
        private String name;

        /**
         * 是否启用
         */
        private Boolean isEnabled;

        /**
         * 优先级
         */
        private String priority;
    }
}
