package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.AddProjectResponseDTO;
import com.victor.iatms.entity.dto.ModuleDTO;
import com.victor.iatms.entity.dto.ModuleListQueryDTO;
import com.victor.iatms.entity.dto.ProjectListQueryDTO;
import com.victor.iatms.entity.dto.ProjectListResponseDTO;
import com.victor.iatms.entity.dto.ProjectMemberDTO;
import com.victor.iatms.entity.dto.ProjectMembersQueryDTO;
import com.victor.iatms.entity.dto.ProjectMembersSummaryDTO;
import com.victor.iatms.entity.dto.UpdateProjectResponseDTO;
import com.victor.iatms.entity.po.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目Mapper接口
 */
@Mapper
public interface ProjectMapper {
    
    /**
     * 分页查询项目列表
     * @param queryDTO 查询条件
     * @return 项目列表
     */
    List<ProjectListResponseDTO> selectProjectList(@Param("query") ProjectListQueryDTO queryDTO);
    
    /**
     * 统计项目总数
     * @param queryDTO 查询条件
     * @return 总数
     */
    Long countProjects(@Param("query") ProjectListQueryDTO queryDTO);
    
    /**
     * 根据ID查询项目
     * @param projectId 项目ID
     * @return 项目信息
     */
    Project selectById(@Param("projectId") Integer projectId);
    
    /**
     * 根据项目编码查询项目
     * @param projectCode 项目编码
     * @return 项目信息
     */
    Project selectByCode(@Param("projectCode") String projectCode);
    
    /**
     * 插入项目
     * @param project 项目信息
     * @return 影响行数
     */
    int insert(Project project);
    
    /**
     * 更新项目
     * @param project 项目信息
     * @return 影响行数
     */
    int updateById(Project project);
    
    /**
     * 逻辑删除项目
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteById(@Param("projectId") Integer projectId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 检查项目编码是否存在
     * @param projectCode 项目编码
     * @param excludeId 排除的项目ID
     * @return 存在数量
     */
    int checkProjectCodeExists(@Param("projectCode") String projectCode, @Param("excludeId") Integer excludeId);
    
    /**
     * 检查项目名称是否存在
     * @param projectName 项目名称
     * @param excludeId 排除的项目ID
     * @return 存在数量
     */
    int checkProjectNameExists(@Param("projectName") String projectName, @Param("excludeId") Integer excludeId);
    
    /**
     * 根据项目ID获取项目详情（包含创建人信息）
     * @param projectId 项目ID
     * @return 项目详情
     */
    AddProjectResponseDTO selectProjectDetailById(@Param("projectId") Integer projectId);
    
    /**
     * 根据项目ID获取项目详情（用于编辑项目响应）
     * @param projectId 项目ID
     * @return 项目详情
     */
    UpdateProjectResponseDTO selectProjectForUpdate(@Param("projectId") Integer projectId);
    
    /**
     * 查询模块列表（平铺结构）
     * @param queryDTO 查询参数
     * @return 模块列表
     */
    List<ModuleDTO> selectModuleListFlat(ModuleListQueryDTO queryDTO);
    
    /**
     * 查询模块列表（树形结构）
     * @param queryDTO 查询参数
     * @return 模块列表
     */
    List<ModuleDTO> selectModuleListTree(ModuleListQueryDTO queryDTO);
    
    /**
     * 统计模块总数
     * @param queryDTO 查询参数
     * @return 总数
     */
    Integer countModules(ModuleListQueryDTO queryDTO);
    
    /**
     * 分页查询项目成员列表
     * @param queryDTO 查询参数
     * @return 成员列表
     */
    List<ProjectMemberDTO> selectProjectMembers(ProjectMembersQueryDTO queryDTO);
    
    /**
     * 统计项目成员总数
     * @param queryDTO 查询参数
     * @return 总数
     */
    Long countProjectMembers(ProjectMembersQueryDTO queryDTO);
    
    /**
     * 获取项目成员统计摘要
     * @param projectId 项目ID
     * @return 统计摘要
     */
    ProjectMembersSummaryDTO selectProjectMembersSummary(@Param("projectId") Integer projectId);
    
    /**
     * 检查项目是否存在关联的模块
     * @param projectId 项目ID
     * @return 关联模块数量
     */
    int checkProjectHasModules(@Param("projectId") Integer projectId);
    
    /**
     * 检查项目是否存在关联的接口
     * @param projectId 项目ID
     * @return 关联接口数量
     */
    int checkProjectHasApis(@Param("projectId") Integer projectId);
    
    /**
     * 检查项目是否存在关联的用例
     * @param projectId 项目ID
     * @return 关联用例数量
     */
    int checkProjectHasTestCases(@Param("projectId") Integer projectId);
    
    /**
     * 检查项目是否存在关联的测试报告
     * @param projectId 项目ID
     * @return 关联测试报告数量
     */
    int checkProjectHasTestReports(@Param("projectId") Integer projectId);
    
    /**
     * 级联软删除项目下的所有模块
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int cascadeDeleteModules(@Param("projectId") Integer projectId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 级联软删除项目下的所有接口
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int cascadeDeleteApis(@Param("projectId") Integer projectId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 级联软删除项目下的所有用例
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int cascadeDeleteTestCases(@Param("projectId") Integer projectId, @Param("deletedBy") Integer deletedBy);
}
