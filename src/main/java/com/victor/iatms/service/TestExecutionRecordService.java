package com.victor.iatms.service;

import com.victor.iatms.entity.dto.TestExecutionRecordDetailDTO;
import com.victor.iatms.entity.dto.TestExecutionRecordPageResultDTO;
import com.victor.iatms.entity.dto.TestExecutionRecordStatisticsDTO;
import com.victor.iatms.entity.dto.UpdateTestExecutionRecordDTO;
import com.victor.iatms.entity.query.TestExecutionRecordQuery;

import java.util.List;

/**
 * 测试执行记录服务接口
 */
public interface TestExecutionRecordService {
    
    /**
     * 分页查询测试执行记录
     * @param query 查询参数
     * @return 分页结果
     */
    TestExecutionRecordPageResultDTO findExecutionRecords(TestExecutionRecordQuery query);
    
    /**
     * 根据ID查询执行记录详情
     * @param recordId 记录ID
     * @return 执行记录详情
     */
    TestExecutionRecordDetailDTO findExecutionRecordById(Long recordId);
    
    /**
     * 根据执行范围查询最近的执行记录
     * @param executionScope 执行范围类型
     * @param refId 关联ID
     * @param limit 限制数量
     * @return 执行记录列表
     */
    List<TestExecutionRecordDetailDTO> findRecentExecutionRecordsByScope(
        String executionScope, Integer refId, Integer limit);
    
    /**
     * 更新执行记录
     * @param recordId 记录ID
     * @param updateDTO 更新数据
     * @return 更新后的记录
     */
    TestExecutionRecordDetailDTO updateExecutionRecord(Long recordId, UpdateTestExecutionRecordDTO updateDTO);
    
    /**
     * 删除执行记录（软删除）
     * @param recordId 记录ID
     * @param deletedBy 删除人ID
     * @return 是否删除成功
     */
    boolean deleteExecutionRecord(Long recordId, Integer deletedBy);
    
    /**
     * 批量删除执行记录
     * @param recordIds 记录ID列表
     * @param deletedBy 删除人ID
     * @return 删除数量
     */
    int batchDeleteExecutionRecords(List<Long> recordIds, Integer deletedBy);
    
    /**
     * 获取执行记录统计信息
     * @param query 查询参数（用于筛选统计范围）
     * @return 统计信息
     */
    TestExecutionRecordStatisticsDTO getExecutionStatistics(TestExecutionRecordQuery query);
    
    /**
     * 根据执行人查询执行记录
     * @param executedBy 执行人ID
     * @param limit 限制数量
     * @return 执行记录列表
     */
    List<TestExecutionRecordDetailDTO> findExecutionRecordsByExecutor(Integer executedBy, Integer limit);
}

