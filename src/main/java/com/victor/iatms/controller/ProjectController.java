package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.constants.Constants;
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
import com.victor.iatms.entity.dto.UpdateProjectDTO;
import com.victor.iatms.entity.dto.UpdateProjectResponseDTO;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目控制器
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    /**
     * 获取模块列表
     * 
     * @param projectId 项目ID
     * @param structure 返回结构（tree/flat）
     * @param status 模块状态过滤
     * @param includeDeleted 是否包含已删除的模块
     * @param includeStatistics 是否包含统计信息
     * @param searchKeyword 关键字搜索
     * @param sortBy 排序字段
     * @param sortOrder 排序顺序
     * @return 模块列表响应
     */
    @GetMapping("/{projectId}/modules")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ModuleListResponseDTO> getModuleList(
            @PathVariable("projectId") Integer projectId,
            @RequestParam(value = "structure", required = false) String structure,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "include_deleted", required = false) Boolean includeDeleted,
            @RequestParam(value = "include_statistics", required = false) Boolean includeStatistics,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder) {
        try {
            // 构建查询参数
            ModuleListQueryDTO queryDTO = new ModuleListQueryDTO();
            queryDTO.setProjectId(projectId);
            queryDTO.setStructure(structure);
            queryDTO.setStatus(status);
            queryDTO.setIncludeDeleted(includeDeleted);
            queryDTO.setIncludeStatistics(includeStatistics);
            queryDTO.setSearchKeyword(searchKeyword);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            
            ModuleListResponseDTO result = projectService.getModuleList(queryDTO);
            return ResponseVO.success("查询模块列表成功", result);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("项目不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询模块列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页获取项目成员列表
     * 
     * @param projectId 项目ID
     * @param status 成员状态过滤
     * @param permissionLevel 权限级别过滤
     * @param projectRole 项目角色过滤
     * @param searchKeyword 关键字搜索
     * @param sortBy 排序字段
     * @param sortOrder 排序顺序
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @GetMapping("/{projectId}/members")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ProjectMembersPageResultDTO> getProjectMembers(
            @PathVariable("projectId") Integer projectId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "permission_level", required = false) String permissionLevel,
            @RequestParam(value = "project_role", required = false) String projectRole,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "sort_order", required = false) String sortOrder,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize) {
        try {
            // 构建查询参数
            ProjectMembersQueryDTO queryDTO = new ProjectMembersQueryDTO();
            queryDTO.setProjectId(projectId);
            queryDTO.setStatus(status);
            queryDTO.setPermissionLevel(permissionLevel);
            queryDTO.setProjectRole(projectRole);
            queryDTO.setSearchKeyword(searchKeyword);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            
            ProjectMembersPageResultDTO result = projectService.findProjectMembers(queryDTO);
            return ResponseVO.success("查询项目成员成功", result);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("项目不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除")) {
                return ResponseVO.businessError(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("查询项目成员失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页获取项目列表
     * 
     * @param name 项目名称（模糊查询）
     * @param creatorId 创建人ID
     * @param includeDeleted 是否包含已删除的项目
     * @param sortBy 排序字段
     * @param sortOrder 排序顺序
     * @param page 页码
     * @param pageSize 每页条数
     * @return 分页项目列表
     */
    @GetMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ProjectPageResultDTO> getProjectList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "creator_id", required = false) Integer creatorId,
            @RequestParam(value = "include_deleted", required = false, defaultValue = "false") Boolean includeDeleted,
            @RequestParam(value = "sort_by", required = false, defaultValue = "created_at") String sortBy,
            @RequestParam(value = "sort_order", required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") Integer pageSize) {
        
        try {
            // 构建查询参数
            ProjectListQueryDTO queryDTO = new ProjectListQueryDTO();
            queryDTO.setName(name);
            queryDTO.setCreatorId(creatorId);
            queryDTO.setIncludeDeleted(includeDeleted);
            queryDTO.setSortBy(sortBy);
            queryDTO.setSortOrder(sortOrder);
            queryDTO.setPage(page);
            queryDTO.setPageSize(pageSize);
            
            // 参数校验
            validateQueryParams(queryDTO);
            
            // 查询项目列表
            ProjectPageResultDTO result = projectService.getProjectList(queryDTO);
            
            return ResponseVO.success("查询成功", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询项目列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取项目详情
     * 
     * @param projectId 项目ID
     * @return 项目详情
     */
    @GetMapping("/{projectId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Project> getProjectById(@PathVariable("projectId") Integer projectId) {
        try {
            Project project = projectService.getProjectById(projectId);
            if (project == null) {
                return ResponseVO.notFound("项目不存在");
            }
            return ResponseVO.success("查询成功", project);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询项目详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据项目编码获取项目详情
     * 
     * @param projectCode 项目编码
     * @return 项目详情
     */
    @GetMapping("/code/{projectCode}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Project> getProjectByCode(@PathVariable("projectCode") String projectCode) {
        try {
            Project project = projectService.getProjectByCode(projectCode);
            if (project == null) {
                return ResponseVO.notFound("项目不存在");
            }
            return ResponseVO.success("查询成功", project);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("查询项目详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加项目（简化版）
     * 
     * @param addProjectDTO 添加项目请求
     * @return 添加项目响应
     */
    @PostMapping
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<AddProjectResponseDTO> addProject(@RequestBody AddProjectDTO addProjectDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer creatorId = 1; // 临时硬编码，实际应该从认证上下文获取
            
            AddProjectResponseDTO result = projectService.addProject(addProjectDTO, creatorId);
            return ResponseVO.success("项目创建成功", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("创建项目失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建项目（完整版）
     * 
     * @param project 项目信息
     * @return 项目ID
     */
    @PostMapping("/create")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Integer> createProject(@RequestBody Project project) {
        try {
            Integer projectId = projectService.createProject(project);
            return ResponseVO.success("创建项目成功", projectId);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("创建项目失败：" + e.getMessage());
        }
    }
    
    /**
     * 编辑项目信息（简化版）
     * 
     * @param projectId 项目ID
     * @param updateProjectDTO 编辑项目请求
     * @return 编辑项目响应
     */
    @PutMapping("/{projectId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<UpdateProjectResponseDTO> editProject(@PathVariable("projectId") Integer projectId,
                                                           @RequestBody UpdateProjectDTO updateProjectDTO) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer updatedBy = 1; // 临时硬编码，实际应该从认证上下文获取
            
            UpdateProjectResponseDTO result = projectService.editProject(projectId, updateProjectDTO, updatedBy);
            return ResponseVO.success("项目信息更新成功", result);
            
        } catch (IllegalArgumentException e) {
            // 根据不同的错误类型返回不同的错误响应
            if (e.getMessage().contains("项目不存在")) {
                return ResponseVO.notFound(e.getMessage());
            } else if (e.getMessage().contains("已被删除") || 
                      e.getMessage().contains("已被其他项目使用")) {
                return ResponseVO.businessError(e.getMessage());
            } else if (e.getMessage().contains("不能编辑系统项目") || 
                      e.getMessage().contains("只能编辑自己创建的项目")) {
                return ResponseVO.forbidden(e.getMessage());
            } else {
                return ResponseVO.paramError(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseVO.serverError("编辑项目失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新项目（完整版）
     * 
     * @param projectId 项目ID
     * @param project 项目信息
     * @return 更新结果
     */
    @PutMapping("/{projectId}/full")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Boolean> updateProject(@PathVariable("projectId") Integer projectId, 
                                           @RequestBody Project project) {
        try {
            project.setProjectId(projectId);
            Boolean result = projectService.updateProject(project);
            if (result) {
                return ResponseVO.success("更新项目成功", true);
            } else {
                return ResponseVO.businessError("更新项目失败");
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("更新项目失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除项目（安全删除）
     * 
     * @param projectId 项目ID
     * @param forceDelete 是否强制删除（忽略关联数据检查）
     * @return 删除结果
     */
    @DeleteMapping("/{projectId}")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ProjectDeleteResultDTO> deleteProject(@PathVariable("projectId") Integer projectId,
                                                           @RequestParam(value = "force_delete", required = false, defaultValue = "false") Boolean forceDelete) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer deletedBy = 1; // 临时硬编码，实际应该从认证上下文获取
            
            ProjectDeleteResultDTO result = projectService.safeDeleteProject(projectId, deletedBy, forceDelete);
            
            if (result.getSuccess()) {
                return ResponseVO.success("项目删除成功", result);
            } else {
                // 根据错误代码返回不同的错误响应
                switch (result.getErrorCode()) {
                    case "PROJECT_NOT_FOUND":
                        return ResponseVO.notFound(result.getMessage());
                    case "PROJECT_ALREADY_DELETED":
                    case "CANNOT_DELETE_SYSTEM_PROJECT":
                    case "HAS_RELATED_DATA":
                        return ResponseVO.businessError(result.getMessage());
                    case "PERMISSION_DENIED":
                        return ResponseVO.forbidden(result.getMessage());
                    case "PARAM_ERROR":
                        return ResponseVO.paramError(result.getMessage());
                    default:
                        return ResponseVO.serverError(result.getMessage());
                }
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("删除项目失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查项目关联数据
     * 
     * @param projectId 项目ID
     * @return 关联数据检查结果
     */
    @GetMapping("/{projectId}/relations")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<ProjectRelationCheckDTO> checkProjectRelations(@PathVariable("projectId") Integer projectId) {
        try {
            ProjectRelationCheckDTO result = projectService.checkProjectRelations(projectId);
            return ResponseVO.success("检查完成", result);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("检查项目关联数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 简单删除项目（不检查关联数据）
     * 
     * @param projectId 项目ID
     * @return 删除结果
     */
    @DeleteMapping("/{projectId}/simple")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Boolean> simpleDeleteProject(@PathVariable("projectId") Integer projectId) {
        try {
            // TODO: 从当前用户上下文获取用户ID
            Integer deletedBy = 1; // 临时硬编码，实际应该从认证上下文获取
            
            Boolean result = projectService.deleteProject(projectId, deletedBy);
            if (result) {
                return ResponseVO.success("项目删除成功", true);
            } else {
                return ResponseVO.businessError("删除项目失败");
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("删除项目失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查项目编码是否存在
     * 
     * @param projectCode 项目编码
     * @param excludeId 排除的项目ID
     * @return 是否存在
     */
    @GetMapping("/check-code")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Boolean> checkProjectCodeExists(
            @RequestParam("project_code") String projectCode,
            @RequestParam(value = "exclude_id", required = false) Integer excludeId) {
        try {
            Boolean exists = projectService.checkProjectCodeExists(projectCode, excludeId);
            return ResponseVO.success("检查完成", exists);
            
        } catch (IllegalArgumentException e) {
            return ResponseVO.paramError(e.getMessage());
        } catch (Exception e) {
            return ResponseVO.serverError("检查项目编码失败：" + e.getMessage());
        }
    }
    
    /**
     * 参数校验
     */
    private void validateQueryParams(ProjectListQueryDTO queryDTO) {
        if (queryDTO.getPage() != null && queryDTO.getPage() < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("每页条数不能超过" + Constants.MAX_PAGE_SIZE);
        }
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() < 1) {
            throw new IllegalArgumentException("每页条数必须大于0");
        }
    }
}
