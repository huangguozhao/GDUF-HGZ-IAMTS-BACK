package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.ReportListQueryDTO;
import com.victor.iatms.entity.dto.ReportPageResultDTO;
import com.victor.iatms.entity.dto.ReportSummaryDTO;
import com.victor.iatms.entity.enums.ReportSortFieldEnum;
import com.victor.iatms.entity.enums.SortOrderEnum;
import com.victor.iatms.entity.po.TestReportSummary;
import com.victor.iatms.mappers.ReportMapper;
import com.victor.iatms.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报告管理服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    private ReportMapper reportMapper;
    
    @Override
    public ReportPageResultDTO getReportList(ReportListQueryDTO queryDTO) {
        // 参数校验
        validateReportListQuery(queryDTO);
        
        // 设置默认值
        setDefaultValues(queryDTO);
        
        // 计算分页偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        
        // 查询报告列表
        List<com.victor.iatms.entity.dto.ReportListResponseDTO> items = reportMapper.selectReportList(queryDTO);
        
        // 查询总数
        Long total = reportMapper.countReports(queryDTO);
        
        // 查询统计摘要
        ReportSummaryDTO summary = reportMapper.selectReportSummary(queryDTO);
        
        // 构建结果
        ReportPageResultDTO result = new ReportPageResultDTO();
        result.setTotal(total);
        result.setItems(items);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());
        result.setSummary(summary);
        
        return result;
    }
    
    @Override
    public TestReportSummary getReportById(Long reportId) {
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("报告ID不能为空或小于等于0");
        }
        
        TestReportSummary report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        
        return report;
    }
    
    @Override
    public List<TestReportSummary> getReportsByProjectId(Integer projectId) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("项目ID不能为空或小于等于0");
        }
        
        return reportMapper.selectByProjectId(projectId);
    }
    
    @Override
    public TestReportSummary getReportByExecutionId(Long executionId) {
        if (executionId == null || executionId <= 0) {
            throw new IllegalArgumentException("执行ID不能为空或小于等于0");
        }
        
        return reportMapper.selectByExecutionId(executionId);
    }
    
    @Override
    public TestReportSummary createReport(TestReportSummary report) {
        // 参数校验
        validateReport(report);
        
        // 设置默认值
        setReportDefaultValues(report);
        
        // 插入报告
        int result = reportMapper.insert(report);
        if (result <= 0) {
            throw new RuntimeException("创建报告失败");
        }
        
        return report;
    }
    
    @Override
    public TestReportSummary updateReport(TestReportSummary report) {
        if (report == null || report.getReportId() == null) {
            throw new IllegalArgumentException("报告信息或报告ID不能为空");
        }
        
        // 检查报告是否存在
        TestReportSummary existingReport = reportMapper.selectById(report.getReportId());
        if (existingReport == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        
        if (existingReport.getIsDeleted()) {
            throw new IllegalArgumentException("报告已被删除");
        }
        
        // 设置更新时间
        report.setUpdatedAt(LocalDateTime.now());
        
        // 更新报告
        int result = reportMapper.update(report);
        if (result <= 0) {
            throw new RuntimeException("更新报告失败");
        }
        
        return report;
    }
    
    @Override
    public boolean deleteReport(Long reportId, Integer deletedBy) {
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("报告ID不能为空或小于等于0");
        }
        
        if (deletedBy == null || deletedBy <= 0) {
            throw new IllegalArgumentException("删除人ID不能为空或小于等于0");
        }
        
        // 检查报告是否存在
        TestReportSummary report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        
        if (report.getIsDeleted()) {
            throw new IllegalArgumentException("报告已被删除");
        }
        
        // 逻辑删除报告
        int result = reportMapper.deleteById(reportId, deletedBy);
        return result > 0;
    }
    
    @Override
    public int batchDeleteReports(List<Long> reportIds, Integer deletedBy) {
        if (CollectionUtils.isEmpty(reportIds)) {
            throw new IllegalArgumentException("报告ID列表不能为空");
        }
        
        if (deletedBy == null || deletedBy <= 0) {
            throw new IllegalArgumentException("删除人ID不能为空或小于等于0");
        }
        
        // 批量逻辑删除报告
        return reportMapper.batchDeleteByIds(reportIds, deletedBy);
    }
    
    @Override
    public int deleteReportsByProjectId(Integer projectId, Integer deletedBy) {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("项目ID不能为空或小于等于0");
        }
        
        if (deletedBy == null || deletedBy <= 0) {
            throw new IllegalArgumentException("删除人ID不能为空或小于等于0");
        }
        
        // 根据项目ID删除报告
        return reportMapper.deleteByProjectId(projectId, deletedBy);
    }
    
    @Override
    public boolean updateReportStatus(Long reportId, String reportStatus) {
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("报告ID不能为空或小于等于0");
        }
        
        if (!StringUtils.hasText(reportStatus)) {
            throw new IllegalArgumentException("报告状态不能为空");
        }
        
        // 检查报告是否存在
        TestReportSummary report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        
        if (report.getIsDeleted()) {
            throw new IllegalArgumentException("报告已被删除");
        }
        
        // 更新报告状态
        int result = reportMapper.updateReportStatus(reportId, reportStatus);
        return result > 0;
    }
    
    @Override
    public boolean updateReportFileInfo(Long reportId, String filePath, Long fileSize, String downloadUrl) {
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("报告ID不能为空或小于等于0");
        }
        
        // 检查报告是否存在
        TestReportSummary report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("报告不存在");
        }
        
        if (report.getIsDeleted()) {
            throw new IllegalArgumentException("报告已被删除");
        }
        
        // 更新报告文件信息
        int result = reportMapper.updateReportFileInfo(reportId, filePath, fileSize, downloadUrl);
        return result > 0;
    }
    
    /**
     * 校验报告列表查询参数
     */
    private void validateReportListQuery(ReportListQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 校验分页参数
        if (queryDTO.getPage() != null && queryDTO.getPage() < 1) {
            throw new IllegalArgumentException("页码必须大于等于1");
        }
        
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() < 1) {
            throw new IllegalArgumentException("每页条数必须大于等于1");
        }
        
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() > Constants.MAX_REPORT_PAGE_SIZE) {
            throw new IllegalArgumentException("每页条数不能超过" + Constants.MAX_REPORT_PAGE_SIZE);
        }
        
        // 校验排序字段
        if (StringUtils.hasText(queryDTO.getSortBy()) && !ReportSortFieldEnum.isValid(queryDTO.getSortBy())) {
            throw new IllegalArgumentException("无效的排序字段");
        }
        
        // 校验排序顺序
        if (StringUtils.hasText(queryDTO.getSortOrder()) && 
            !SortOrderEnum.ASC.getCode().equals(queryDTO.getSortOrder()) && 
            !SortOrderEnum.DESC.getCode().equals(queryDTO.getSortOrder())) {
            throw new IllegalArgumentException("无效的排序顺序");
        }
        
        // 校验时间范围
        if (queryDTO.getStartTimeBegin() != null && queryDTO.getStartTimeEnd() != null) {
            if (queryDTO.getStartTimeBegin().isAfter(queryDTO.getStartTimeEnd())) {
                throw new IllegalArgumentException("开始时间不能晚于结束时间");
            }
        }
        
        // 校验成功率范围
        if (queryDTO.getSuccessRateMin() != null && queryDTO.getSuccessRateMax() != null) {
            if (queryDTO.getSuccessRateMin().compareTo(queryDTO.getSuccessRateMax()) > 0) {
                throw new IllegalArgumentException("最小成功率不能大于最大成功率");
            }
        }
    }
    
    /**
     * 设置默认值
     */
    private void setDefaultValues(ReportListQueryDTO queryDTO) {
        if (queryDTO.getPage() == null) {
            queryDTO.setPage(Constants.DEFAULT_PAGE);
        }
        
        if (queryDTO.getPageSize() == null) {
            queryDTO.setPageSize(Constants.DEFAULT_REPORT_PAGE_SIZE);
        }
        
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_REPORT_SORT_BY);
        }
        
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_REPORT_SORT_ORDER);
        }
        
        if (queryDTO.getIncludeDeleted() == null) {
            queryDTO.setIncludeDeleted(Constants.DEFAULT_REPORT_INCLUDE_DELETED);
        }
    }
    
    /**
     * 校验报告信息
     */
    private void validateReport(TestReportSummary report) {
        if (report == null) {
            throw new IllegalArgumentException("报告信息不能为空");
        }
        
        if (!StringUtils.hasText(report.getReportName())) {
            throw new IllegalArgumentException("报告名称不能为空");
        }
        
        if (report.getReportName().length() > Constants.REPORT_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("报告名称长度不能超过" + Constants.REPORT_NAME_MAX_LENGTH + "个字符");
        }
        
        if (report.getProjectId() == null || report.getProjectId() <= 0) {
            throw new IllegalArgumentException("项目ID不能为空或小于等于0");
        }
        
        if (!StringUtils.hasText(report.getEnvironment())) {
            throw new IllegalArgumentException("测试环境不能为空");
        }
        
        if (report.getGeneratedBy() == null || report.getGeneratedBy() <= 0) {
            throw new IllegalArgumentException("生成人员ID不能为空或小于等于0");
        }
    }
    
    /**
     * 设置报告默认值
     */
    private void setReportDefaultValues(TestReportSummary report) {
        LocalDateTime now = LocalDateTime.now();
        
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(now);
        }
        
        if (report.getUpdatedAt() == null) {
            report.setUpdatedAt(now);
        }
        
        if (report.getIsDeleted() == null) {
            report.setIsDeleted(false);
        }
        
        if (!StringUtils.hasText(report.getReportStatus())) {
            report.setReportStatus(Constants.REPORT_STATUS_GENERATING);
        }
        
        if (!StringUtils.hasText(report.getFileFormat())) {
            report.setFileFormat(Constants.DEFAULT_REPORT_FORMAT);
        }
        
        if (report.getTotalCases() == null) {
            report.setTotalCases(0);
        }
        
        if (report.getExecutedCases() == null) {
            report.setExecutedCases(0);
        }
        
        if (report.getPassedCases() == null) {
            report.setPassedCases(0);
        }
        
        if (report.getFailedCases() == null) {
            report.setFailedCases(0);
        }
        
        if (report.getBrokenCases() == null) {
            report.setBrokenCases(0);
        }
        
        if (report.getSkippedCases() == null) {
            report.setSkippedCases(0);
        }
        
        if (report.getFileSize() == null) {
            report.setFileSize(0L);
        }
    }
}
