package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.ReportListQueryDTO;
import com.victor.iatms.entity.dto.ReportListResponseDTO;
import com.victor.iatms.entity.dto.ReportSummaryDTO;
import com.victor.iatms.entity.dto.ReportExportResponseDTO;
import com.victor.iatms.entity.dto.DeleteReportResponseDTO;
import com.victor.iatms.entity.dto.ReportDependencyCheckDTO;
import com.victor.iatms.entity.po.TestReportSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 报告管理Mapper接口
 */
@Mapper
public interface ReportMapper {
    
    /**
     * 分页查询报告列表
     * 
     * @param queryDTO 查询参数
     * @return 报告列表
     */
    List<ReportListResponseDTO> selectReportList(ReportListQueryDTO queryDTO);
    
    /**
     * 统计报告总数
     * 
     * @param queryDTO 查询参数
     * @return 总数
     */
    Long countReports(ReportListQueryDTO queryDTO);
    
    /**
     * 查询报告统计摘要
     * 
     * @param queryDTO 查询参数
     * @return 统计摘要
     */
    ReportSummaryDTO selectReportSummary(ReportListQueryDTO queryDTO);
    
    /**
     * 按报告类型统计数量
     * 
     * @param queryDTO 查询参数
     * @return Map<reportType, count>
     */
    Map<String, Long> countReportsByType(ReportListQueryDTO queryDTO);
    
    /**
     * 按报告状态统计数量
     * 
     * @param queryDTO 查询参数
     * @return Map<reportStatus, count>
     */
    Map<String, Long> countReportsByStatus(ReportListQueryDTO queryDTO);
    
    /**
     * 按环境统计数量
     * 
     * @param queryDTO 查询参数
     * @return Map<environment, count>
     */
    Map<String, Long> countReportsByEnvironment(ReportListQueryDTO queryDTO);
    
    /**
     * 根据ID查询报告详情
     * 
     * @param reportId 报告ID
     * @return 报告详情
     */
    TestReportSummary selectById(@Param("reportId") Long reportId);
    
    /**
     * 根据项目ID查询报告列表
     * 
     * @param projectId 项目ID
     * @return 报告列表
     */
    List<TestReportSummary> selectByProjectId(@Param("projectId") Integer projectId);
    
    /**
     * 根据执行ID查询报告
     * 
     * @param executionId 执行ID
     * @return 报告
     */
    TestReportSummary selectByExecutionId(@Param("executionId") Long executionId);
    
    /**
     * 插入报告
     * 
     * @param report 报告信息
     * @return 影响行数
     */
    int insert(TestReportSummary report);
    
    /**
     * 更新报告
     * 
     * @param report 报告信息
     * @return 影响行数
     */
    int update(TestReportSummary report);
    
    /**
     * 逻辑删除报告
     * 
     * @param reportId 报告ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteById(@Param("reportId") Long reportId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 物理删除报告
     * 
     * @param reportId 报告ID
     * @return 影响行数
     */
    int physicalDeleteById(@Param("reportId") Long reportId);
    
    /**
     * 批量逻辑删除报告
     * 
     * @param reportIds 报告ID列表
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("reportIds") List<Long> reportIds, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 根据项目ID删除报告
     * 
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteByProjectId(@Param("projectId") Integer projectId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 更新报告状态
     * 
     * @param reportId 报告ID
     * @param reportStatus 报告状态
     * @return 影响行数
     */
    int updateReportStatus(@Param("reportId") Long reportId, @Param("reportStatus") String reportStatus);
    
    /**
     * 更新报告文件信息
     * 
     * @param reportId 报告ID
     * @param filePath 文件路径
     * @param fileSize 文件大小
     * @param downloadUrl 下载地址
     * @return 影响行数
     */
    int updateReportFileInfo(@Param("reportId") Long reportId, 
                             @Param("filePath") String filePath, 
                             @Param("fileSize") Long fileSize, 
                             @Param("downloadUrl") String downloadUrl);
    
    // ==================== 报告导出相关方法 ====================
    
    /**
     * 查询报告导出数据（包含报告摘要和统计信息）
     * 
     * @param reportId 报告ID
     * @return 报告导出响应数据
     */
    ReportExportResponseDTO.ReportSummaryInfoDTO selectReportExportData(@Param("reportId") Long reportId);
    
    /**
     * 查询报告测试结果详情（用于导出）
     * 
     * @param reportId 报告ID
     * @param includeDetails 是否包含详细信息
     * @param includeAttachments 是否包含附件信息
     * @param includeFailureDetails 是否包含失败详情
     * @return 测试结果列表
     */
    List<ReportExportResponseDTO.TestCaseResultDTO> selectReportTestResults(@Param("reportId") Long reportId,
                                                                           @Param("includeDetails") Boolean includeDetails,
                                                                           @Param("includeAttachments") Boolean includeAttachments,
                                                                           @Param("includeFailureDetails") Boolean includeFailureDetails);
    
    /**
     * 查询报告统计信息（按状态、优先级、严重程度）
     * 
     * @param reportId 报告ID
     * @return 统计信息
     */
    ReportExportResponseDTO.ReportStatisticsDTO selectReportStatistics(@Param("reportId") Long reportId);
    
    // ==================== 报告删除相关方法 ====================
    
    /**
     * 检查报告是否存在且未被删除
     * 
     * @param reportId 报告ID
     * @return 报告信息
     */
    TestReportSummary selectByIdForDelete(@Param("reportId") Long reportId);
    
    /**
     * 检查报告依赖关系
     * 
     * @param reportId 报告ID
     * @return 依赖检查结果
     */
    ReportDependencyCheckDTO checkReportDependencies(@Param("reportId") Long reportId);
    
    /**
     * 软删除报告（更新is_deleted字段）
     * 
     * @param reportId 报告ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int softDeleteReport(@Param("reportId") Long reportId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 硬删除报告（物理删除）
     * 
     * @param reportId 报告ID
     * @return 影响行数
     */
    int hardDeleteReport(@Param("reportId") Long reportId);
    
    /**
     * 删除报告相关的测试结果
     * 
     * @param reportId 报告ID
     * @return 影响行数
     */
    int deleteReportTestResults(@Param("reportId") Long reportId);
    
    /**
     * 获取删除报告的基本信息
     * 
     * @param reportId 报告ID
     * @return 删除响应信息
     */
    DeleteReportResponseDTO getDeleteReportInfo(@Param("reportId") Long reportId);
}
