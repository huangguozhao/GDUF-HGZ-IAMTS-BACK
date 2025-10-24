package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.AddProjectDTO;
import com.victor.iatms.entity.dto.AddProjectResponseDTO;
import com.victor.iatms.entity.dto.ModuleDTO;
import com.victor.iatms.entity.dto.ModuleListQueryDTO;
import com.victor.iatms.entity.dto.ModuleListResponseDTO;
import com.victor.iatms.entity.dto.ProjectDeleteResultDTO;
import com.victor.iatms.entity.dto.ProjectListQueryDTO;
import com.victor.iatms.entity.dto.ProjectListResponseDTO;
import com.victor.iatms.entity.dto.ProjectMemberDTO;
import com.victor.iatms.entity.dto.ProjectMembersPageResultDTO;
import com.victor.iatms.entity.dto.ProjectMembersQueryDTO;
import com.victor.iatms.entity.dto.ProjectMembersSummaryDTO;
import com.victor.iatms.entity.dto.ProjectPageResultDTO;
import com.victor.iatms.entity.dto.ProjectRelationCheckDTO;
import com.victor.iatms.entity.dto.RecentProjectItemDTO;
import com.victor.iatms.entity.dto.RecentProjectsQueryDTO;
import com.victor.iatms.entity.dto.RecentProjectsResponseDTO;
import com.victor.iatms.entity.dto.TimeRangeDTO;
import com.victor.iatms.entity.dto.UpdateProjectDTO;
import com.victor.iatms.entity.dto.UpdateProjectResponseDTO;
import com.victor.iatms.entity.enums.ModuleStructureEnum;
import com.victor.iatms.entity.enums.ModuleSortFieldEnum;
import com.victor.iatms.entity.enums.ProjectSortFieldEnum;
import com.victor.iatms.entity.enums.SortOrderEnum;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.mappers.ProjectMapper;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目服务实现类
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private TestExecutionMapper testExecutionMapper;
    
    @Override
    public ModuleListResponseDTO getModuleList(ModuleListQueryDTO queryDTO) {
        // 参数校验
        validateModuleListQuery(queryDTO);
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(queryDTO.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 检查项目是否已被删除
        if (project.getIsDeleted()) {
            throw new IllegalArgumentException("项目已被删除");
        }
        
        // 设置默认值
        setModuleQueryDefaultValues(queryDTO);
        
        // 查询模块列表
        List<ModuleDTO> modules;
        if (ModuleStructureEnum.TREE.getCode().equals(queryDTO.getStructure())) {
            modules = projectMapper.selectModuleListTree(queryDTO);
            // 构建树形结构
            modules = buildModuleTree(modules);
        } else {
            modules = projectMapper.selectModuleListFlat(queryDTO);
            // 为平铺结构添加层级和路径信息
            addLevelAndPathInfo(modules);
        }
        
        // 统计总数
        Integer totalModules = projectMapper.countModules(queryDTO);
        
        // 构建响应
        ModuleListResponseDTO response = new ModuleListResponseDTO();
        response.setProjectId(queryDTO.getProjectId());
        response.setProjectName(project.getName());
        response.setTotalModules(totalModules);
        response.setModules(modules);
        
        return response;
    }
    
    @Override
    public ProjectMembersPageResultDTO findProjectMembers(ProjectMembersQueryDTO queryDTO) {
        // 参数校验
        validateProjectMembersQuery(queryDTO);
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(queryDTO.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 检查项目是否已被删除
        if (project.getIsDeleted()) {
            throw new IllegalArgumentException("项目已被删除");
        }
        
        // 设置默认值
        setDefaultValues(queryDTO);
        
        // 计算分页偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        
        // 查询成员列表
        List<ProjectMemberDTO> members = projectMapper.selectProjectMembers(queryDTO);
        
        // 查询总数
        Long total = projectMapper.countProjectMembers(queryDTO);
        
        // 查询统计摘要
        ProjectMembersSummaryDTO summary = projectMapper.selectProjectMembersSummary(queryDTO.getProjectId());
        
        // 构建结果
        ProjectMembersPageResultDTO result = new ProjectMembersPageResultDTO();
        result.setTotal(total);
        result.setItems(members);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());
        result.setSummary(summary);
        
        return result;
    }
    
    @Override
    public ProjectPageResultDTO getProjectList(ProjectListQueryDTO queryDTO) {
        // 参数校验和默认值设置
        validateAndSetDefaults(queryDTO);
        
        // 计算分页偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        
        // 查询项目列表
        List<ProjectListResponseDTO> items = projectMapper.selectProjectList(queryDTO);
        
        // 查询总数
        Long total = projectMapper.countProjects(queryDTO);
        
        // 构建分页结果
        ProjectPageResultDTO result = new ProjectPageResultDTO();
        result.setTotal(total);
        result.setItems(items);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());
        
        return result;
    }
    
    @Override
    public Project getProjectById(Integer projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        return projectMapper.selectById(projectId);
    }
    
    @Override
    public Project getProjectByCode(String projectCode) {
        // 由于数据库表中没有project_code字段，此方法暂时不支持
        throw new UnsupportedOperationException("项目编码查询功能暂不支持");
    }
    
    @Override
    public Integer createProject(Project project) {
        // 参数校验
        validateProject(project);
        
        // 设置默认值
        setProjectDefaults(project);
        
        // 插入项目
        int result = projectMapper.insert(project);
        if (result > 0) {
            return project.getProjectId();
        }
        throw new RuntimeException("创建项目失败");
    }
    
    @Override
    public UpdateProjectResponseDTO editProject(Integer projectId, UpdateProjectDTO updateProjectDTO, Integer updatedBy) {
        // 参数校验
        validateEditProject(projectId, updateProjectDTO, updatedBy);
        
        // 检查项目是否存在
        Project existingProject = projectMapper.selectById(projectId);
        if (existingProject == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 检查项目是否已被删除
        if (existingProject.getIsDeleted()) {
            throw new IllegalArgumentException("项目已被删除，无法编辑");
        }
        
        // 检查是否为系统项目
        if (isSystemProject(existingProject)) {
            throw new IllegalArgumentException("不能编辑系统项目");
        }
        
        // 检查权限（只能编辑自己创建的项目）
        if (!existingProject.getCreatorId().equals(updatedBy)) {
            throw new IllegalArgumentException("只能编辑自己创建的项目");
        }
        
        // 检查项目名称是否已存在（如果更新了名称）
        if (StringUtils.hasText(updateProjectDTO.getName()) && 
            !updateProjectDTO.getName().equals(existingProject.getName())) {
            if (checkProjectNameExists(updateProjectDTO.getName(), projectId)) {
                throw new IllegalArgumentException("项目名称已被其他项目使用");
            }
        }
        
        // 更新项目信息
        Project projectToUpdate = new Project();
        projectToUpdate.setProjectId(projectId);
        
        // 只更新提供的字段
        if (StringUtils.hasText(updateProjectDTO.getName())) {
            projectToUpdate.setName(updateProjectDTO.getName());
        }
        if (updateProjectDTO.getDescription() != null) {
            projectToUpdate.setDescription(updateProjectDTO.getDescription());
        }
        
        // 执行更新
        int result = projectMapper.updateById(projectToUpdate);
        if (result <= 0) {
            throw new RuntimeException("更新项目失败");
        }
        
        // 查询更新后的项目详情
        UpdateProjectResponseDTO responseDTO = projectMapper.selectProjectForUpdate(projectId);
        if (responseDTO == null) {
            throw new RuntimeException("获取项目详情失败");
        }
        
        return responseDTO;
    }
    
    @Override
    public Boolean updateProject(Project project) {
        if (project.getProjectId() == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        
        // 检查项目是否存在
        Project existingProject = projectMapper.selectById(project.getProjectId());
        if (existingProject == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 由于数据库表中没有project_code字段，跳过项目编码检查
        
        // 更新项目
        int result = projectMapper.updateById(project);
        return result > 0;
    }
    
    @Override
    public Boolean deleteProject(Integer projectId, Integer deletedBy) {
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        
        // 检查项目是否存在
        Project existingProject = projectMapper.selectById(projectId);
        if (existingProject == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 逻辑删除项目
        int result = projectMapper.deleteById(projectId, deletedBy);
        return result > 0;
    }
    
    @Override
    public ProjectDeleteResultDTO safeDeleteProject(Integer projectId, Integer deletedBy, Boolean forceDelete) {
        ProjectDeleteResultDTO result = new ProjectDeleteResultDTO();
        
        try {
            // 参数校验
            if (projectId == null) {
                throw new IllegalArgumentException("项目ID不能为空");
            }
            if (deletedBy == null) {
                throw new IllegalArgumentException("删除人ID不能为空");
            }
            
            // 检查项目是否存在
            Project existingProject = projectMapper.selectById(projectId);
            if (existingProject == null) {
                result.setSuccess(false);
                result.setMessage("项目不存在");
                result.setErrorCode("PROJECT_NOT_FOUND");
                return result;
            }
            
            // 检查项目是否已被删除
            if (existingProject.getIsDeleted()) {
                result.setSuccess(false);
                result.setMessage("项目已被删除");
                result.setErrorCode("PROJECT_ALREADY_DELETED");
                return result;
            }
            
            // 检查是否为系统项目
            if (isSystemProject(existingProject)) {
                result.setSuccess(false);
                result.setMessage("不能删除系统项目");
                result.setErrorCode("CANNOT_DELETE_SYSTEM_PROJECT");
                return result;
            }
            
            // 检查权限（只能删除自己创建的项目）
            if (!existingProject.getCreatorId().equals(deletedBy)) {
                result.setSuccess(false);
                result.setMessage("只能删除自己创建的项目");
                result.setErrorCode("PERMISSION_DENIED");
                return result;
            }
            
            // 检查关联数据
            ProjectRelationCheckDTO relationCheck = checkProjectRelations(projectId);
            if (!forceDelete && !relationCheck.getCanDelete()) {
                result.setSuccess(false);
                result.setMessage("项目存在关联数据，无法删除");
                result.setErrorCode("HAS_RELATED_DATA");
                return result;
            }
            
            // 执行级联删除
            int deletedModulesCount = 0;
            int deletedApisCount = 0;
            int deletedTestCasesCount = 0;
            
            if (relationCheck.getHasModules()) {
                deletedModulesCount = projectMapper.cascadeDeleteModules(projectId, deletedBy);
            }
            if (relationCheck.getHasApis()) {
                deletedApisCount = projectMapper.cascadeDeleteApis(projectId, deletedBy);
            }
            if (relationCheck.getHasTestCases()) {
                deletedTestCasesCount = projectMapper.cascadeDeleteTestCases(projectId, deletedBy);
            }
            
            // 删除项目本身
            int projectDeleteResult = projectMapper.deleteById(projectId, deletedBy);
            if (projectDeleteResult <= 0) {
                result.setSuccess(false);
                result.setMessage("删除项目失败");
                result.setErrorCode("DELETE_FAILED");
                return result;
            }
            
            // 设置成功结果
            result.setSuccess(true);
            result.setMessage("项目删除成功");
            result.setDeletedModulesCount(deletedModulesCount);
            result.setDeletedApisCount(deletedApisCount);
            result.setDeletedTestCasesCount(deletedTestCasesCount);
            
        } catch (IllegalArgumentException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setErrorCode("PARAM_ERROR");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("删除项目失败：" + e.getMessage());
            result.setErrorCode("SYSTEM_ERROR");
        }
        
        return result;
    }
    
    @Override
    public ProjectRelationCheckDTO checkProjectRelations(Integer projectId) {
        ProjectRelationCheckDTO result = new ProjectRelationCheckDTO();
        
        try {
            // 检查关联模块
            int modulesCount = projectMapper.checkProjectHasModules(projectId);
            result.setHasModules(modulesCount > 0);
            result.setModulesCount(modulesCount);
            
            // 检查关联接口
            int apisCount = projectMapper.checkProjectHasApis(projectId);
            result.setHasApis(apisCount > 0);
            result.setApisCount(apisCount);
            
            // 检查关联用例
            int testCasesCount = projectMapper.checkProjectHasTestCases(projectId);
            result.setHasTestCases(testCasesCount > 0);
            result.setTestCasesCount(testCasesCount);
            
            // 检查关联测试报告
            int testReportsCount = projectMapper.checkProjectHasTestReports(projectId);
            result.setHasTestReports(testReportsCount > 0);
            result.setTestReportsCount(testReportsCount);
            
            // 判断是否可以删除
            boolean canDelete = modulesCount == 0 && apisCount == 0 && testCasesCount == 0 && testReportsCount == 0;
            result.setCanDelete(canDelete);
            
            // 设置需要级联删除的数据类型
            List<String> cascadeTypes = new ArrayList<>();
            if (modulesCount > 0) cascadeTypes.add("modules");
            if (apisCount > 0) cascadeTypes.add("apis");
            if (testCasesCount > 0) cascadeTypes.add("testCases");
            if (testReportsCount > 0) cascadeTypes.add("testReports");
            result.setCascadeDeleteTypes(cascadeTypes.toArray(new String[0]));
            
        } catch (Exception e) {
            result.setCanDelete(false);
        }
        
        return result;
    }
    
    /**
     * 检查是否为系统项目
     */
    private boolean isSystemProject(Project project) {
        // 可以根据项目名称或其他标识来判断是否为系统项目
        // 这里简单示例：项目名称包含"系统"
        return project.getName().contains("系统");
    }
    
    @Override
    public AddProjectResponseDTO addProject(AddProjectDTO addProjectDTO, Integer creatorId) {
        // 参数校验
        validateAddProject(addProjectDTO, creatorId);
        
        // 检查项目名称是否已存在
        if (checkProjectNameExists(addProjectDTO.getName(), null)) {
            throw new IllegalArgumentException("项目名称已存在");
        }
        
        // 创建项目实体
        Project project = new Project();
        project.setName(addProjectDTO.getName());
        project.setDescription(addProjectDTO.getDescription());
        project.setCreatorId(creatorId);
        
        // 设置默认值
        setProjectDefaults(project);
        
        // 插入项目
        int result = projectMapper.insert(project);
        if (result <= 0) {
            throw new RuntimeException("创建项目失败");
        }
        
        // 查询创建的项目详情
        AddProjectResponseDTO responseDTO = projectMapper.selectProjectDetailById(project.getProjectId());
        if (responseDTO == null) {
            throw new RuntimeException("获取项目详情失败");
        }
        
        return responseDTO;
    }
    
    @Override
    public Boolean checkProjectCodeExists(String projectCode, Integer excludeId) {
        // 由于数据库表中没有project_code字段，此方法暂时不支持
        throw new UnsupportedOperationException("项目编码检查功能暂不支持");
    }
    
    /**
     * 检查项目名称是否存在
     */
    public Boolean checkProjectNameExists(String projectName, Integer excludeId) {
        if (!StringUtils.hasText(projectName)) {
            return false;
        }
        int count = projectMapper.checkProjectNameExists(projectName, excludeId);
        return count > 0;
    }
    
    /**
     * 模块列表查询参数校验
     */
    private void validateModuleListQuery(ModuleListQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        if (queryDTO.getProjectId() == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
    }
    
    /**
     * 设置模块查询默认值
     */
    private void setModuleQueryDefaultValues(ModuleListQueryDTO queryDTO) {
        if (!StringUtils.hasText(queryDTO.getStructure())) {
            queryDTO.setStructure(Constants.DEFAULT_MODULE_STRUCTURE);
        }
        if (!StringUtils.hasText(queryDTO.getStatus())) {
            queryDTO.setStatus(Constants.DEFAULT_MODULE_STATUS);
        }
        if (queryDTO.getIncludeDeleted() == null) {
            queryDTO.setIncludeDeleted(Constants.DEFAULT_INCLUDE_DELETED);
        }
        if (queryDTO.getIncludeStatistics() == null) {
            queryDTO.setIncludeStatistics(Constants.DEFAULT_INCLUDE_STATISTICS);
        }
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_MODULE_SORT_BY);
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
        
        // 验证排序字段
        if (!ModuleSortFieldEnum.isValidSortField(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_MODULE_SORT_BY);
        }
        
        // 验证排序顺序
        if (!SortOrderEnum.isValidSortOrder(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
    }
    
    /**
     * 构建模块树形结构
     */
    private List<ModuleDTO> buildModuleTree(List<ModuleDTO> modules) {
        if (modules == null || modules.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 创建模块ID到模块的映射
        java.util.Map<Integer, ModuleDTO> moduleMap = new java.util.HashMap<>();
        for (ModuleDTO module : modules) {
            moduleMap.put(module.getModuleId(), module);
            module.setChildren(new ArrayList<>());
        }
        
        // 构建树形结构
        List<ModuleDTO> rootModules = new ArrayList<>();
        for (ModuleDTO module : modules) {
            if (module.getParentModuleId() == null) {
                // 根模块
                rootModules.add(module);
            } else {
                // 子模块
                ModuleDTO parent = moduleMap.get(module.getParentModuleId());
                if (parent != null) {
                    parent.getChildren().add(module);
                }
            }
        }
        
        return rootModules;
    }
    
    /**
     * 为平铺结构添加层级和路径信息
     */
    private void addLevelAndPathInfo(List<ModuleDTO> modules) {
        if (modules == null || modules.isEmpty()) {
            return;
        }
        
        // 创建模块ID到模块的映射
        java.util.Map<Integer, ModuleDTO> moduleMap = new java.util.HashMap<>();
        for (ModuleDTO module : modules) {
            moduleMap.put(module.getModuleId(), module);
        }
        
        // 为每个模块计算层级和路径
        for (ModuleDTO module : modules) {
            calculateLevelAndPath(module, moduleMap);
        }
    }
    
    /**
     * 计算模块的层级和路径
     */
    private void calculateLevelAndPath(ModuleDTO module, java.util.Map<Integer, ModuleDTO> moduleMap) {
        if (module.getParentModuleId() == null) {
            // 根模块
            module.setLevel(1);
            module.setPath(module.getName());
        } else {
            // 子模块
            ModuleDTO parent = moduleMap.get(module.getParentModuleId());
            if (parent != null) {
                // 递归计算父模块的层级和路径
                if (parent.getLevel() == null) {
                    calculateLevelAndPath(parent, moduleMap);
                }
                module.setLevel(parent.getLevel() + 1);
                module.setPath(parent.getPath() + "/" + module.getName());
            } else {
                // 父模块不存在，设为根模块
                module.setLevel(1);
                module.setPath(module.getName());
            }
        }
    }
    
    /**
     * 项目成员查询参数校验
     */
    private void validateProjectMembersQuery(ProjectMembersQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        if (queryDTO.getProjectId() == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        if (queryDTO.getPage() != null && queryDTO.getPage() < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (queryDTO.getPageSize() != null && (queryDTO.getPageSize() < 1 || queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE)) {
            throw new IllegalArgumentException("每页条数必须在1-" + Constants.MAX_PAGE_SIZE + "之间");
        }
    }
    
    /**
     * 设置项目成员查询默认值
     */
    private void setDefaultValues(ProjectMembersQueryDTO queryDTO) {
        if (queryDTO.getPage() == null) {
            queryDTO.setPage(Constants.DEFAULT_PAGE);
        }
        if (queryDTO.getPageSize() == null) {
            queryDTO.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (!StringUtils.hasText(queryDTO.getStatus())) {
            queryDTO.setStatus(Constants.DEFAULT_MEMBER_STATUS);
        }
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_MEMBER_SORT_BY);
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
    }
    
    /**
     * 编辑项目参数校验
     */
    private void validateEditProject(Integer projectId, UpdateProjectDTO updateProjectDTO, Integer updatedBy) {
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        if (updateProjectDTO == null) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        if (updatedBy == null) {
            throw new IllegalArgumentException("更新人ID不能为空");
        }
        
        // 检查是否至少有一个字段需要更新
        if (!StringUtils.hasText(updateProjectDTO.getName()) && updateProjectDTO.getDescription() == null) {
            throw new IllegalArgumentException("至少需要提供一个字段进行更新");
        }
        
        // 如果提供了项目名称，检查长度
        if (StringUtils.hasText(updateProjectDTO.getName())) {
            if (updateProjectDTO.getName().length() > Constants.PROJECT_NAME_MAX_LENGTH) {
                throw new IllegalArgumentException("项目名称长度不能超过" + Constants.PROJECT_NAME_MAX_LENGTH + "个字符");
            }
        }
        
        // 如果提供了项目描述，检查长度
        if (updateProjectDTO.getDescription() != null && 
            updateProjectDTO.getDescription().length() > Constants.PROJECT_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("项目描述长度不能超过" + Constants.PROJECT_DESCRIPTION_MAX_LENGTH + "个字符");
        }
    }
    
    /**
     * 添加项目参数校验
     */
    private void validateAddProject(AddProjectDTO addProjectDTO, Integer creatorId) {
        if (addProjectDTO == null) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        if (!StringUtils.hasText(addProjectDTO.getName())) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (addProjectDTO.getName().length() > Constants.PROJECT_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("项目名称长度不能超过" + Constants.PROJECT_NAME_MAX_LENGTH + "个字符");
        }
        if (addProjectDTO.getDescription() != null && 
            addProjectDTO.getDescription().length() > Constants.PROJECT_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("项目描述长度不能超过" + Constants.PROJECT_DESCRIPTION_MAX_LENGTH + "个字符");
        }
        if (creatorId == null) {
            throw new IllegalArgumentException("创建人ID不能为空");
        }
    }
    
    /**
     * 参数校验和默认值设置
     */
    private void validateAndSetDefaults(ProjectListQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 设置默认分页参数
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(Constants.DEFAULT_PAGE);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE) {
            queryDTO.setPageSize(Constants.MAX_PAGE_SIZE);
        }
        
        // 设置默认排序参数
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy("created_at");
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder("desc");
        }
        
        // 验证排序字段
        if (!ProjectSortFieldEnum.isValidSortField(queryDTO.getSortBy())) {
            queryDTO.setSortBy("created_at");
        }
        
        // 验证排序顺序
        if (!SortOrderEnum.isValidSortOrder(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder("desc");
        }
        
        // 设置默认的包含删除状态
        if (queryDTO.getIncludeDeleted() == null) {
            queryDTO.setIncludeDeleted(false);
        }
    }
    
    /**
     * 项目参数校验
     */
    private void validateProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("项目信息不能为空");
        }
        if (!StringUtils.hasText(project.getName())) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (project.getCreatorId() == null) {
            throw new IllegalArgumentException("创建人ID不能为空");
        }
    }
    
    /**
     * 设置项目默认值
     */
    private void setProjectDefaults(Project project) {
        if (project.getIsDeleted() == null) {
            project.setIsDeleted(false);
        }
        if (project.getCreatedAt() == null) {
            project.setCreatedAt(LocalDateTime.now());
        }
        if (project.getUpdatedAt() == null) {
            project.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    @Override
    public RecentProjectsResponseDTO getRecentProjects(RecentProjectsQueryDTO queryDTO, Integer currentUserId) {
        // 参数校验
        validateRecentProjectsQuery(queryDTO);

        // 设置默认值
        setRecentProjectsDefaultValues(queryDTO);

        // 权限检查
        if (!hasRecentProjectsPermission(currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法查看最近编辑项目列表");
        }

        // 计算时间范围
        TimeRangeDTO timeRange = calculateTimeRange(queryDTO.getTimeRange());

        // 查询最近编辑的项目列表
        List<RecentProjectItemDTO> items = projectMapper.selectRecentProjects(queryDTO, currentUserId, timeRange);
        
        // 查询总数
        Long total = projectMapper.countRecentProjects(queryDTO, currentUserId, timeRange);

        // 构建响应DTO
        RecentProjectsResponseDTO responseDTO = new RecentProjectsResponseDTO();
        responseDTO.setTotal(total);
        responseDTO.setItems(items);
        responseDTO.setPage(queryDTO.getPage());
        responseDTO.setPageSize(queryDTO.getPageSize());
        responseDTO.setTimeRange(timeRange);

        return responseDTO;
    }
    
    /**
     * 验证分页获取最近编辑的项目查询参数
     */
    private void validateRecentProjectsQuery(RecentProjectsQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 验证时间范围
        if (StringUtils.hasText(queryDTO.getTimeRange())) {
            if (!isValidTimeRange(queryDTO.getTimeRange())) {
                throw new IllegalArgumentException("时间范围参数错误");
            }
        }
        
        // 验证分页大小
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() > Constants.MAX_RECENT_PROJECTS_PAGE_SIZE) {
            throw new IllegalArgumentException("分页大小不能超过" + Constants.MAX_RECENT_PROJECTS_PAGE_SIZE);
        }
        
        // 验证排序字段
        if (StringUtils.hasText(queryDTO.getSortBy())) {
            if (!isValidRecentProjectsSortField(queryDTO.getSortBy())) {
                throw new IllegalArgumentException("排序字段无效");
            }
        }
        
        // 验证排序顺序
        if (StringUtils.hasText(queryDTO.getSortOrder())) {
            if (!"asc".equalsIgnoreCase(queryDTO.getSortOrder()) && 
                !"desc".equalsIgnoreCase(queryDTO.getSortOrder())) {
                throw new IllegalArgumentException("排序顺序无效");
            }
        }
    }
    
    /**
     * 设置分页获取最近编辑的项目默认值
     */
    private void setRecentProjectsDefaultValues(RecentProjectsQueryDTO queryDTO) {
        if (!StringUtils.hasText(queryDTO.getTimeRange())) {
            queryDTO.setTimeRange(Constants.DEFAULT_RECENT_PROJECTS_TIME_RANGE);
        }
        if (queryDTO.getIncludeStats() == null) {
            queryDTO.setIncludeStats(false);
        }
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_RECENT_PROJECTS_SORT_BY);
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(Constants.DEFAULT_PAGE);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(Constants.DEFAULT_RECENT_PROJECTS_PAGE_SIZE);
        }
    }
    
    /**
     * 检查是否有分页获取最近编辑的项目权限
     */
    private boolean hasRecentProjectsPermission(Integer userId) {
        // TODO: 这里应该检查用户的项目成员权限
        // 暂时返回true，实际应该查询项目成员权限
        return true;
    }
    
    /**
     * 验证时间范围是否有效
     */
    private boolean isValidTimeRange(String timeRange) {
        return "1d".equals(timeRange) || "7d".equals(timeRange) || "30d".equals(timeRange);
    }
    
    /**
     * 验证排序字段是否有效
     */
    private boolean isValidRecentProjectsSortField(String sortField) {
        return "last_accessed".equalsIgnoreCase(sortField) ||
               "updated_at".equalsIgnoreCase(sortField) ||
               "created_at".equalsIgnoreCase(sortField);
    }
    
    /**
     * 计算时间范围
     */
    private TimeRangeDTO calculateTimeRange(String timeRange) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime;
        int days;
        
        switch (timeRange) {
            case "1d":
                startTime = endTime.minusDays(1);
                days = 1;
                break;
            case "7d":
                startTime = endTime.minusDays(7);
                days = 7;
                break;
            case "30d":
                startTime = endTime.minusDays(30);
                days = 30;
                break;
            default:
                startTime = endTime.minusDays(7);
                days = 7;
                break;
        }
        
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);
        timeRangeDTO.setDays(days);
        
        return timeRangeDTO;
    }
    
    @Override
    public com.victor.iatms.entity.dto.ProjectStatisticsDTO getProjectStatistics(Integer projectId) {
        // 参数校验
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        if (project.getIsDeleted()) {
            throw new IllegalArgumentException("项目已被删除");
        }
        
        // 构建统计数据
        com.victor.iatms.entity.dto.ProjectStatisticsDTO statistics = new com.victor.iatms.entity.dto.ProjectStatisticsDTO();
        
        // 基本信息
        statistics.setProjectId(project.getProjectId());
        statistics.setProjectName(project.getName());
        statistics.setProjectCode(null); // Project 实体暂无 projectCode 字段
        
        // 模块统计
        ModuleListQueryDTO moduleQuery = new ModuleListQueryDTO();
        moduleQuery.setProjectId(projectId);
        moduleQuery.setIncludeDeleted(false);
        Integer moduleCount = projectMapper.countModules(moduleQuery);
        statistics.setModuleCount(moduleCount != null ? moduleCount : 0);
        
        // 接口统计
        Integer apiCount = projectMapper.checkProjectHasApis(projectId);
        statistics.setApiCount(apiCount != null ? apiCount : 0);
        
        // 测试用例统计
        Integer testCaseCount = testExecutionMapper.countTestCasesByProjectId(projectId, null, null, null, true);
        statistics.setTestCaseCount(testCaseCount != null ? testCaseCount : 0);
        
        // 通过/失败统计（基于最近的测试结果）
        Integer passedCount = testExecutionMapper.countPassedTestCasesByProjectId(projectId);
        Integer failedCount = testExecutionMapper.countFailedTestCasesByProjectId(projectId);
        statistics.setPassedCount(passedCount != null ? passedCount : 0);
        statistics.setFailedCount(failedCount != null ? failedCount : 0);
        
        // 未执行数 = 总用例数 - 通过数 - 失败数
        int executedCount = statistics.getPassedCount() + statistics.getFailedCount();
        int notExecutedCount = statistics.getTestCaseCount() - executedCount;
        statistics.setNotExecutedCount(notExecutedCount > 0 ? notExecutedCount : 0);
        
        // 计算通过率
        if (executedCount > 0) {
            double passRate = (statistics.getPassedCount() * 100.0) / executedCount;
            statistics.setPassRate(Math.round(passRate * 100.0) / 100.0); // 保留2位小数
        } else {
            statistics.setPassRate(0.0);
        }
        
        // 测试执行记录统计
        Integer executionRecordCount = testExecutionMapper.countExecutionRecordsByProjectId(projectId);
        statistics.setExecutionRecordCount(executionRecordCount != null ? executionRecordCount : 0);
        
        // 测试报告统计
        Integer testReportCount = projectMapper.checkProjectHasTestReports(projectId);
        statistics.setTestReportCount(testReportCount != null ? testReportCount : 0);
        
        // 项目成员统计
        ProjectMembersQueryDTO memberQuery = new ProjectMembersQueryDTO();
        memberQuery.setProjectId(projectId);
        Long memberCount = projectMapper.countProjectMembers(memberQuery);
        statistics.setMemberCount(memberCount != null ? memberCount.intValue() : 0);
        
        // 最近执行时间
        String lastExecutionTime = testExecutionMapper.getLatestExecutionTimeByProjectId(projectId);
        statistics.setLastExecutionTime(lastExecutionTime);
        
        // 项目时间信息
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (project.getCreatedAt() != null) {
            statistics.setCreatedAt(project.getCreatedAt().format(formatter));
        }
        if (project.getUpdatedAt() != null) {
            statistics.setUpdatedAt(project.getUpdatedAt().format(formatter));
        }
        
        return statistics;
    }
}
