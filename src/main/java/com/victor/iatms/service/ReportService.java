package com.victor.iatms.service;

import com.victor.iatms.entity.dto.ReportListQueryDTO;
import com.victor.iatms.entity.dto.ReportPageResultDTO;
import com.victor.iatms.entity.dto.DeleteReportResponseDTO;
import com.victor.iatms.entity.dto.ReportDependencyCheckDTO;
import com.victor.iatms.entity.po.TestReportSummary;

import java.util.List;

/**
 * 报告管理服务接口
 */
public interface ReportService {
    
    /**
     * 分页查询报告列表
     * 
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    ReportPageResultDTO getReportList(ReportListQueryDTO queryDTO);
    
    /**
     * 根据ID查询报告详情
     * 
     * @param reportId 报告ID
     * @return 报告详情
     */
    TestReportSummary getReportById(Long reportId);
    
    /**
     * 根据项目ID查询报告列表
     * 
     * @param projectId 项目ID
     * @return 报告列表
     */
    List<TestReportSummary> getReportsByProjectId(Integer projectId);
    
    /**
     * 根据执行ID查询报告
     * 
     * @param executionId 执行ID
     * @return 报告
     */
    TestReportSummary getReportByExecutionId(Long executionId);
    
    /**
     * 创建报告
     * 
     * @param report 报告信息
     * @return 创建的报告
     */
    TestReportSummary createReport(TestReportSummary report);
    
    /**
     * 更新报告
     * 
     * @param report 报告信息
     * @return 更新后的报告
     */
    TestReportSummary updateReport(TestReportSummary report);
    
    /**
     * 删除报告
     * 
     * @param reportId 报告ID
     * @param deletedBy 删除人ID
     * @return 是否删除成功
     */
    boolean deleteReport(Long reportId, Integer deletedBy);
    
    /**
     * 批量删除报告
     * 
     * @param reportIds 报告ID列表
     * @param deletedBy 删除人ID
     * @return 删除成功的数量
     */
    int batchDeleteReports(List<Long> reportIds, Integer deletedBy);
    
    /**
     * 根据项目ID删除报告
     * 
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 删除成功的数量
     */
    int deleteReportsByProjectId(Integer projectId, Integer deletedBy);
    
    /**
     * 更新报告状态
     * 
     * @param reportId 报告ID
     * @param reportStatus 报告状态
     * @return 是否更新成功
     */
    boolean updateReportStatus(Long reportId, String reportStatus);
    
    /**
     * 更新报告文件信息
     * 
     * @param reportId 报告ID
     * @param filePath 文件路径
     * @param fileSize 文件大小
     * @param downloadUrl 下载地址
     * @return 是否更新成功
     */
    boolean updateReportFileInfo(Long reportId, String filePath, Long fileSize, String downloadUrl);
    
    // ==================== 报告删除相关方法 ====================
    
    /**
     * 删除测试报告（支持软删除和硬删除）
     * 
     * @param reportId 报告ID
     * @param force 是否强制删除（物理删除）
     * @param currentUserId 当前用户ID
     * @return 删除结果
     */
    DeleteReportResponseDTO deleteTestReport(Long reportId, Boolean force, Integer currentUserId);
    
    /**
     * 检查报告是否存在且未被删除
     * 
     * @param reportId 报告ID
     * @return 报告信息
     */
    TestReportSummary checkReportExists(Long reportId);
    
    /**
     * 检查报告依赖关系
     * 
     * @param reportId 报告ID
     * @return 依赖检查结果
     */
    ReportDependencyCheckDTO checkReportDependencies(Long reportId);
    
    /**
     * 软删除报告
     * 
     * @param reportId 报告ID
     * @param deletedBy 删除人ID
     * @return 删除结果
     */
    DeleteReportResponseDTO softDeleteReport(Long reportId, Integer deletedBy);
    
    /**
     * 硬删除报告
     * 
     * @param reportId 报告ID
     * @return 删除结果
     */
    DeleteReportResponseDTO hardDeleteReport(Long reportId);
}
