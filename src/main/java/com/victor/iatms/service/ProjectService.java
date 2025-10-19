package com.victor.iatms.service;

import com.victor.iatms.entity.dto.AddProjectDTO;
import com.victor.iatms.entity.dto.AddProjectResponseDTO;
import com.victor.iatms.entity.dto.ModuleListQueryDTO;
import com.victor.iatms.entity.dto.ModuleListResponseDTO;
import com.victor.iatms.entity.dto.ProjectDeleteResultDTO;
import com.victor.iatms.entity.dto.ProjectListQueryDTO;
import com.victor.iatms.entity.dto.ProjectMembersPageResultDTO;
import com.victor.iatms.entity.dto.ProjectMembersQueryDTO;
import com.victor.iatms.entity.dto.ProjectPageResultDTO;
import com.victor.iatms.entity.dto.ProjectRelationCheckDTO;
import com.victor.iatms.entity.dto.RecentProjectsQueryDTO;
import com.victor.iatms.entity.dto.RecentProjectsResponseDTO;
import com.victor.iatms.entity.dto.UpdateProjectDTO;
import com.victor.iatms.entity.dto.UpdateProjectResponseDTO;
import com.victor.iatms.entity.po.Project;

/**
 * 项目服务接口
 */
public interface ProjectService {
    
    /**
     * 分页查询项目列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    ProjectPageResultDTO getProjectList(ProjectListQueryDTO queryDTO);
    
    /**
     * 根据ID获取项目详情
     * @param projectId 项目ID
     * @return 项目信息
     */
    Project getProjectById(Integer projectId);
    
    /**
     * 根据项目编码获取项目详情
     * @param projectCode 项目编码
     * @return 项目信息
     */
    Project getProjectByCode(String projectCode);
    
    /**
     * 创建项目
     * @param project 项目信息
     * @return 项目ID
     */
    Integer createProject(Project project);
    
    /**
     * 添加项目（简化版）
     * @param addProjectDTO 添加项目请求
     * @param creatorId 创建人ID
     * @return 添加项目响应
     */
    AddProjectResponseDTO addProject(AddProjectDTO addProjectDTO, Integer creatorId);
    
    /**
     * 更新项目
     * @param project 项目信息
     * @return 是否成功
     */
    Boolean updateProject(Project project);
    
    /**
     * 编辑项目信息（简化版）
     * @param projectId 项目ID
     * @param updateProjectDTO 编辑项目请求
     * @param updatedBy 更新人ID
     * @return 编辑项目响应
     */
    UpdateProjectResponseDTO editProject(Integer projectId, UpdateProjectDTO updateProjectDTO, Integer updatedBy);
    
    /**
     * 删除项目
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @return 是否成功
     */
    Boolean deleteProject(Integer projectId, Integer deletedBy);
    
    /**
     * 安全删除项目（带业务逻辑检查）
     * @param projectId 项目ID
     * @param deletedBy 删除人ID
     * @param forceDelete 是否强制删除（忽略关联数据检查）
     * @return 删除结果信息
     */
    ProjectDeleteResultDTO safeDeleteProject(Integer projectId, Integer deletedBy, Boolean forceDelete);
    
    /**
     * 检查项目是否存在关联数据
     * @param projectId 项目ID
     * @return 关联数据检查结果
     */
    ProjectRelationCheckDTO checkProjectRelations(Integer projectId);
    
    /**
     * 获取模块列表
     * @param queryDTO 查询参数
     * @return 模块列表响应
     */
    ModuleListResponseDTO getModuleList(ModuleListQueryDTO queryDTO);
    
    /**
     * 分页查询项目成员列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    ProjectMembersPageResultDTO findProjectMembers(ProjectMembersQueryDTO queryDTO);
    
    /**
     * 检查项目编码是否存在
     * @param projectCode 项目编码
     * @param excludeId 排除的项目ID
     * @return 是否存在
     */
    Boolean checkProjectCodeExists(String projectCode, Integer excludeId);
    
    /**
     * 分页获取最近编辑的项目
     * @param queryDTO 查询参数
     * @param currentUserId 当前用户ID
     * @return 分页的最近编辑项目列表
     */
    RecentProjectsResponseDTO getRecentProjects(RecentProjectsQueryDTO queryDTO, Integer currentUserId);
}
