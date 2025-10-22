package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.TestExecutionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试执行记录数据访问层
 */
@Mapper
public interface TestExecutionRecordMapper {

    /**
     * 插入测试执行记录
     * @param record 执行记录
     * @return 影响行数
     */
    int insertExecutionRecord(TestExecutionRecord record);

    /**
     * 更新测试执行记录
     * @param record 执行记录
     * @return 影响行数
     */
    int updateExecutionRecord(TestExecutionRecord record);

    /**
     * 根据记录ID查询执行记录
     * @param recordId 记录ID
     * @return 执行记录
     */
    TestExecutionRecord findExecutionRecordById(@Param("recordId") Long recordId);

    /**
     * 根据执行范围和引用ID查询最近的执行记录
     * @param executionScope 执行范围
     * @param refId 引用ID
     * @param limit 限制数量
     * @return 执行记录列表
     */
    List<TestExecutionRecord> findExecutionRecordsByScope(
        @Param("executionScope") String executionScope,
        @Param("refId") Integer refId,
        @Param("limit") Integer limit);

    /**
     * 根据执行人查询执行记录
     * @param executedBy 执行人ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量
     * @return 执行记录列表
     */
    List<TestExecutionRecord> findExecutionRecordsByExecutor(
        @Param("executedBy") Integer executedBy,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("limit") Integer limit);

    /**
     * 统计执行记录数量
     * @param executionScope 执行范围（可选）
     * @param refId 引用ID（可选）
     * @param executedBy 执行人ID（可选）
     * @param status 状态（可选）
     * @param environment 环境（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 记录数量
     */
    Long countExecutionRecords(
        @Param("executionScope") String executionScope,
        @Param("refId") Integer refId,
        @Param("executedBy") Integer executedBy,
        @Param("status") String status,
        @Param("environment") String environment,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询执行记录
     * @param executionScope 执行范围（可选）
     * @param refId 引用ID（可选）
     * @param executedBy 执行人ID（可选）
     * @param status 状态（可选）
     * @param environment 环境（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 执行记录列表
     */
    List<TestExecutionRecord> findExecutionRecordsWithPagination(
        @Param("executionScope") String executionScope,
        @Param("refId") Integer refId,
        @Param("executedBy") Integer executedBy,
        @Param("status") String status,
        @Param("environment") String environment,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("offset") Integer offset,
        @Param("limit") Integer limit);

    /**
     * 软删除执行记录
     * @param recordId 记录ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int softDeleteExecutionRecord(
        @Param("recordId") Long recordId,
        @Param("deletedBy") Integer deletedBy);

    /**
     * 批量软删除执行记录
     * @param recordIds 记录ID列表
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int batchSoftDeleteExecutionRecords(
        @Param("recordIds") List<Long> recordIds,
        @Param("deletedBy") Integer deletedBy);
    
    /**
     * 获取执行记录统计信息
     * @param executionScope 执行范围（可选）
     * @param refId 引用ID（可选）
     * @param executedBy 执行人ID（可选）
     * @param status 状态（可选）
     * @param environment 环境（可选）
     * @param startTimeBegin 开始时间-起始（可选）
     * @param startTimeEnd 开始时间-结束（可选）
     * @return 统计信息
     */
    com.victor.iatms.entity.dto.TestExecutionRecordStatisticsDTO getExecutionStatistics(
        @Param("executionScope") String executionScope,
        @Param("refId") Integer refId,
        @Param("executedBy") Integer executedBy,
        @Param("status") String status,
        @Param("environment") String environment,
        @Param("startTimeBegin") LocalDateTime startTimeBegin,
        @Param("startTimeEnd") LocalDateTime startTimeEnd);
}

