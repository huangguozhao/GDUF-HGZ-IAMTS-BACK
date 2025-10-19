package com.victor.iatms.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 导入结果DTO
 */
@Data
public class ImportResultDTO {

    /**
     * 总用例数
     */
    private Integer totalCount;

    /**
     * 成功导入的用例数
     */
    private Integer successCount;

    /**
     * 导入失败的用例数
     */
    private Integer failureCount;

    /**
     * 跳过的用例数
     */
    private Integer skipCount;

    /**
     * 成功导入的用例信息
     */
    private List<ImportedCaseDTO> importedCases;

    /**
     * 导入失败的记录及原因
     */
    private List<FailedRecordDTO> failedRecords;

    /**
     * 本次导入的唯一标识
     */
    private String importId;

    /**
     * 成功导入的用例信息
     */
    @Data
    public static class ImportedCaseDTO {
        private Integer caseId;
        private String caseCode;
        private String name;
    }

    /**
     * 导入失败的记录
     */
    @Data
    public static class FailedRecordDTO {
        private Integer rowNumber;
        private String caseCode;
        private String name;
        private String error;
    }
}
