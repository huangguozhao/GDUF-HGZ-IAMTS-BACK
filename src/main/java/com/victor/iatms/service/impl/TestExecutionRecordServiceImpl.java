package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.TestExecutionRecordDetailDTO;
import com.victor.iatms.entity.dto.TestExecutionRecordPageResultDTO;
import com.victor.iatms.entity.dto.TestExecutionRecordStatisticsDTO;
import com.victor.iatms.entity.dto.UpdateTestExecutionRecordDTO;
import com.victor.iatms.entity.po.TestExecutionRecord;
import com.victor.iatms.entity.query.TestExecutionRecordQuery;
import com.victor.iatms.mappers.TestExecutionRecordMapper;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.service.TestExecutionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试执行记录服务实现类
 */
@Slf4j
@Service
public class TestExecutionRecordServiceImpl implements TestExecutionRecordService {
    
    @Autowired
    private TestExecutionRecordMapper testExecutionRecordMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public TestExecutionRecordPageResultDTO findExecutionRecords(TestExecutionRecordQuery query) {
        try {
            // 参数校验和默认值设置
            validateAndSetQueryDefaults(query);
            
            // 查询总数
            Long total = testExecutionRecordMapper.countExecutionRecords(
                query.getExecutionScope(),
                query.getRefId(),
                query.getExecutedBy(),
                query.getStatus(),
                query.getEnvironment(),
                query.getStartTimeBegin(),
                query.getStartTimeEnd()
            );
            
            // 查询列表
            Integer offset = (query.getPage() - 1) * query.getPageSize();
            List<TestExecutionRecord> records = testExecutionRecordMapper.findExecutionRecordsWithPagination(
                query.getExecutionScope(),
                query.getRefId(),
                query.getExecutedBy(),
                query.getStatus(),
                query.getEnvironment(),
                query.getStartTimeBegin(),
                query.getStartTimeEnd(),
                offset,
                query.getPageSize()
            );
            
            // 转换为DTO并填充执行人姓名
            List<TestExecutionRecordDetailDTO> items = records.stream()
                .map(record -> {
                    TestExecutionRecordDetailDTO dto = TestExecutionRecordDetailDTO.fromEntity(record);
                    // 填充执行人姓名
                    if (record.getExecutedBy() != null) {
                        String executorName = userMapper.findNameById(record.getExecutedBy());
                        dto.setExecutorName(executorName);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
            
            // 构建分页结果
            TestExecutionRecordPageResultDTO result = new TestExecutionRecordPageResultDTO();
            result.setTotal(total);
            result.setItems(items);
            result.setPage(query.getPage());
            result.setPageSize(query.getPageSize());
            
            return result;
            
        } catch (Exception e) {
            log.error("查询测试执行记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询测试执行记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public TestExecutionRecordDetailDTO findExecutionRecordById(Long recordId) {
        try {
            if (recordId == null) {
                throw new IllegalArgumentException("记录ID不能为空");
            }
            
            TestExecutionRecord record = testExecutionRecordMapper.findExecutionRecordById(recordId);
            if (record == null) {
                throw new IllegalArgumentException("执行记录不存在");
            }
            
            TestExecutionRecordDetailDTO dto = TestExecutionRecordDetailDTO.fromEntity(record);
            
            // 填充执行人姓名
            if (record.getExecutedBy() != null) {
                String executorName = userMapper.findNameById(record.getExecutedBy());
                dto.setExecutorName(executorName);
            }
            
            return dto;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询执行记录详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询执行记录详情失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<TestExecutionRecordDetailDTO> findRecentExecutionRecordsByScope(
            String executionScope, Integer refId, Integer limit) {
        try {
            if (executionScope == null || executionScope.isEmpty()) {
                throw new IllegalArgumentException("执行范围类型不能为空");
            }
            if (refId == null) {
                throw new IllegalArgumentException("关联ID不能为空");
            }
            
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            if (limit > 100) {
                limit = 100;
            }
            
            List<TestExecutionRecord> records = testExecutionRecordMapper.findExecutionRecordsByScope(
                executionScope, refId, limit);
            
            return records.stream()
                .map(record -> {
                    TestExecutionRecordDetailDTO dto = TestExecutionRecordDetailDTO.fromEntity(record);
                    if (record.getExecutedBy() != null) {
                        String executorName = userMapper.findNameById(record.getExecutedBy());
                        dto.setExecutorName(executorName);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
                
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询最近执行记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询最近执行记录失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestExecutionRecordDetailDTO updateExecutionRecord(Long recordId, UpdateTestExecutionRecordDTO updateDTO) {
        try {
            if (recordId == null) {
                throw new IllegalArgumentException("记录ID不能为空");
            }
            if (updateDTO == null) {
                throw new IllegalArgumentException("更新数据不能为空");
            }
            
            // 查询记录是否存在
            TestExecutionRecord existingRecord = testExecutionRecordMapper.findExecutionRecordById(recordId);
            if (existingRecord == null) {
                throw new IllegalArgumentException("执行记录不存在");
            }
            
            // 构建更新对象
            TestExecutionRecord updateRecord = new TestExecutionRecord();
            updateRecord.setRecordId(recordId);
            
            if (updateDTO.getStatus() != null) {
                updateRecord.setStatus(updateDTO.getStatus());
            }
            if (updateDTO.getEndTime() != null) {
                updateRecord.setEndTime(updateDTO.getEndTime());
            }
            if (updateDTO.getDurationSeconds() != null) {
                updateRecord.setDurationSeconds(updateDTO.getDurationSeconds());
            }
            if (updateDTO.getTotalCases() != null) {
                updateRecord.setTotalCases(updateDTO.getTotalCases());
            }
            if (updateDTO.getExecutedCases() != null) {
                updateRecord.setExecutedCases(updateDTO.getExecutedCases());
            }
            if (updateDTO.getPassedCases() != null) {
                updateRecord.setPassedCases(updateDTO.getPassedCases());
            }
            if (updateDTO.getFailedCases() != null) {
                updateRecord.setFailedCases(updateDTO.getFailedCases());
            }
            if (updateDTO.getSkippedCases() != null) {
                updateRecord.setSkippedCases(updateDTO.getSkippedCases());
            }
            if (updateDTO.getSuccessRate() != null) {
                updateRecord.setSuccessRate(updateDTO.getSuccessRate());
            }
            if (updateDTO.getBrowser() != null) {
                updateRecord.setBrowser(updateDTO.getBrowser());
            }
            if (updateDTO.getAppVersion() != null) {
                updateRecord.setAppVersion(updateDTO.getAppVersion());
            }
            if (updateDTO.getReportUrl() != null) {
                updateRecord.setReportUrl(updateDTO.getReportUrl());
            }
            if (updateDTO.getLogFilePath() != null) {
                updateRecord.setLogFilePath(updateDTO.getLogFilePath());
            }
            if (updateDTO.getErrorMessage() != null) {
                updateRecord.setErrorMessage(updateDTO.getErrorMessage());
            }
            
            // 执行更新
            int affected = testExecutionRecordMapper.updateExecutionRecord(updateRecord);
            if (affected == 0) {
                throw new RuntimeException("更新执行记录失败");
            }
            
            // 返回更新后的记录
            return findExecutionRecordById(recordId);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新执行记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新执行记录失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExecutionRecord(Long recordId, Integer deletedBy) {
        try {
            if (recordId == null) {
                throw new IllegalArgumentException("记录ID不能为空");
            }
            if (deletedBy == null) {
                throw new IllegalArgumentException("删除人ID不能为空");
            }
            
            // 查询记录是否存在
            TestExecutionRecord record = testExecutionRecordMapper.findExecutionRecordById(recordId);
            if (record == null) {
                throw new IllegalArgumentException("执行记录不存在");
            }
            
            int affected = testExecutionRecordMapper.softDeleteExecutionRecord(recordId, deletedBy);
            return affected > 0;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除执行记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除执行记录失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteExecutionRecords(List<Long> recordIds, Integer deletedBy) {
        try {
            if (recordIds == null || recordIds.isEmpty()) {
                throw new IllegalArgumentException("记录ID列表不能为空");
            }
            if (deletedBy == null) {
                throw new IllegalArgumentException("删除人ID不能为空");
            }
            
            return testExecutionRecordMapper.batchSoftDeleteExecutionRecords(recordIds, deletedBy);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除执行记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量删除执行记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public TestExecutionRecordStatisticsDTO getExecutionStatistics(TestExecutionRecordQuery query) {
        try {
            validateAndSetQueryDefaults(query);
            
            TestExecutionRecordStatisticsDTO statistics = testExecutionRecordMapper.getExecutionStatistics(
                query.getExecutionScope(),
                query.getRefId(),
                query.getExecutedBy(),
                query.getStatus(),
                query.getEnvironment(),
                query.getStartTimeBegin(),
                query.getStartTimeEnd()
            );
            
            return statistics != null ? statistics : new TestExecutionRecordStatisticsDTO();
            
        } catch (Exception e) {
            log.error("获取执行统计信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取执行统计信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<TestExecutionRecordDetailDTO> findExecutionRecordsByExecutor(Integer executedBy, Integer limit) {
        try {
            if (executedBy == null) {
                throw new IllegalArgumentException("执行人ID不能为空");
            }
            
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            if (limit > 100) {
                limit = 100;
            }
            
            List<TestExecutionRecord> records = testExecutionRecordMapper.findExecutionRecordsByExecutor(
                executedBy, null, null, limit);
            
            return records.stream()
                .map(record -> {
                    TestExecutionRecordDetailDTO dto = TestExecutionRecordDetailDTO.fromEntity(record);
                    String executorName = userMapper.findNameById(executedBy);
                    dto.setExecutorName(executorName);
                    return dto;
                })
                .collect(Collectors.toList());
                
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询执行人的执行记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询执行人的执行记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证并设置查询参数默认值
     */
    private void validateAndSetQueryDefaults(TestExecutionRecordQuery query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getPageSize() > 100) {
            query.setPageSize(100);
        }
        if (query.getIncludeDeleted() == null) {
            query.setIncludeDeleted(false);
        }
        if (query.getSortOrder() == null || query.getSortOrder().isEmpty()) {
            query.setSortOrder("desc");
        }
        if (query.getSortBy() == null || query.getSortBy().isEmpty()) {
            query.setSortBy("start_time");
        }
    }
}

