package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.AddProjectDTO;
import com.victor.iatms.entity.dto.AddProjectMemberDTO;
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
import com.victor.iatms.entity.dto.UpdateProjectMemberDTO;
import com.victor.iatms.entity.dto.UpdateProjectResponseDTO;
import com.victor.iatms.entity.dto.UserInfoDTO;
import com.victor.iatms.entity.enums.ModuleSortFieldEnum;
import com.victor.iatms.entity.enums.ModuleStructureEnum;
import com.victor.iatms.entity.enums.ProjectSortFieldEnum;
import com.victor.iatms.entity.enums.SortOrderEnum;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.po.ProjectMember;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.mappers.ProjectMapper;
import com.victor.iatms.mappers.ProjectMemberMapper;
import com.victor.iatms.mappers.TestExecutionMapper;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    
    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private UserMapper userMapper;

    // ================= 模块列表 =================
    @Override
    public ModuleListResponseDTO getModuleList(ModuleListQueryDTO queryDTO) {
        validateModuleListQuery(queryDTO);
        Project project = projectMapper.selectById(queryDTO.getProjectId());
        if (project == null) { throw new IllegalArgumentException("项目不存在"); }
        if (project.getIsDeleted()) { throw new IllegalArgumentException("项目已被删除"); }
        setModuleQueryDefaultValues(queryDTO);
        
        List<ModuleDTO> modules;
        if (ModuleStructureEnum.TREE.getCode().equals(queryDTO.getStructure())) {
            modules = projectMapper.selectModuleListTree(queryDTO);
            modules = buildModuleTree(modules);
        } else {
            modules = projectMapper.selectModuleListFlat(queryDTO);
            addLevelAndPathInfo(modules);
        }
        Integer totalModules = projectMapper.countModules(queryDTO);
        
        ModuleListResponseDTO response = new ModuleListResponseDTO();
        response.setProjectId(queryDTO.getProjectId());
        response.setProjectName(project.getName());
        response.setTotalModules(totalModules);
        response.setModules(modules);
        return response;
    }
    
    // ================= 项目成员列表 =================
    @Override
    public ProjectMembersPageResultDTO findProjectMembers(ProjectMembersQueryDTO queryDTO) {
        validateProjectMembersQuery(queryDTO);
        Project project = projectMapper.selectById(queryDTO.getProjectId());
        if (project == null) { throw new IllegalArgumentException("项目不存在"); }
        if (project.getIsDeleted()) { throw new IllegalArgumentException("项目已被删除"); }
        setDefaultValues(queryDTO);
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        List<ProjectMemberDTO> members = projectMapper.selectProjectMembers(queryDTO);
        Long total = projectMapper.countProjectMembers(queryDTO);
        ProjectMembersSummaryDTO summary = projectMapper.selectProjectMembersSummary(queryDTO.getProjectId());
        
        ProjectMembersPageResultDTO result = new ProjectMembersPageResultDTO();
        result.setTotal(total);
        result.setItems(members);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());
        result.setSummary(summary);
        return result;
    }
    
    @Override
    public Boolean checkProjectCodeExists(String projectCode, Integer excludeId) {
        return null;
    }

    // ================= 项目分页 =================
    @Override
    public ProjectPageResultDTO getProjectList(ProjectListQueryDTO queryDTO) {
        validateAndSetDefaults(queryDTO);
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        List<ProjectListResponseDTO> items = projectMapper.selectProjectList(queryDTO);
        Long total = projectMapper.countProjects(queryDTO);
        ProjectPageResultDTO result = new ProjectPageResultDTO();
        result.setTotal(total);
        result.setItems(items);
        result.setPage(queryDTO.getPage());
        result.setPageSize(queryDTO.getPageSize());
        return result;
    }
    
    // ================= 项目 CRUD =================
    @Override
    public Project getProjectById(Integer projectId) {
        if (projectId == null) { throw new IllegalArgumentException("项目ID不能为空"); }
        return projectMapper.selectById(projectId);
    }
    
    @Override
    public Project getProjectByCode(String projectCode) {
        throw new UnsupportedOperationException("项目编码查询功能暂不支持");
    }
    
    @Override
    public Integer createProject(Project project) {
        validateProject(project);
        setProjectDefaults(project);
        int result = projectMapper.insert(project);
        if (result > 0) { return project.getProjectId(); }
        throw new RuntimeException("创建项目失败");
    }
    
    @Override
    public AddProjectResponseDTO addProject(AddProjectDTO addProjectDTO, Integer creatorId) {
        return null;
    }

    @Override
    public UpdateProjectResponseDTO editProject(Integer projectId, UpdateProjectDTO updateProjectDTO, Integer updatedBy) {
        validateEditProject(projectId, updateProjectDTO, updatedBy);
        Project existingProject = projectMapper.selectById(projectId);
        if (existingProject == null) { throw new IllegalArgumentException("项目不存在"); }
        if (existingProject.getIsDeleted()) { throw new IllegalArgumentException("项目已被删除，无法编辑"); }
        if (isSystemProject(existingProject)) { throw new IllegalArgumentException("不能编辑系统项目"); }
        if (!existingProject.getCreatorId().equals(updatedBy)) { throw new IllegalArgumentException("只能编辑自己创建的项目"); }
        if (StringUtils.hasText(updateProjectDTO.getName()) && !updateProjectDTO.getName().equals(existingProject.getName())) {
            if (checkProjectNameExists(updateProjectDTO.getName(), projectId)) {
                throw new IllegalArgumentException("项目名称已被其他项目使用");
            }
        }
        Project projectToUpdate = new Project();
        projectToUpdate.setProjectId(projectId);
        if (StringUtils.hasText(updateProjectDTO.getName())) { projectToUpdate.setName(updateProjectDTO.getName()); }
        if (updateProjectDTO.getDescription() != null) { projectToUpdate.setDescription(updateProjectDTO.getDescription()); }
        int result = projectMapper.updateById(projectToUpdate);
        if (result <= 0) { throw new RuntimeException("更新项目失败"); }
        UpdateProjectResponseDTO responseDTO = projectMapper.selectProjectForUpdate(projectId);
        if (responseDTO == null) { throw new RuntimeException("获取项目详情失败"); }
        return responseDTO;
    }
    
    @Override
    public Boolean updateProject(Project project) {
        if (project.getProjectId() == null) { throw new IllegalArgumentException("项目ID不能为空"); }
        Project existingProject = projectMapper.selectById(project.getProjectId());
        if (existingProject == null) { throw new IllegalArgumentException("项目不存在"); }
        int result = projectMapper.updateById(project);
        return result > 0;
    }
    
    @Override
    public Boolean deleteProject(Integer projectId, Integer deletedBy) {
        if (projectId == null) { throw new IllegalArgumentException("项目ID不能为空"); }
        Project existingProject = projectMapper.selectById(projectId);
        if (existingProject == null) { throw new IllegalArgumentException("项目不存在"); }
        int result = projectMapper.deleteById(projectId, deletedBy);
        return result > 0;
    }
    
    @Override
    public ProjectDeleteResultDTO safeDeleteProject(Integer projectId, Integer deletedBy, Boolean forceDelete) {
        ProjectDeleteResultDTO result = new ProjectDeleteResultDTO();
        try {
            if (projectId == null) { throw new IllegalArgumentException("项目ID不能为空"); }
            if (deletedBy == null) { throw new IllegalArgumentException("删除人ID不能为空"); }
            Project existingProject = projectMapper.selectById(projectId);
            if (existingProject == null) {
                result.setSuccess(false); result.setMessage("项目不存在"); result.setErrorCode("PROJECT_NOT_FOUND"); return result;
            }
            if (existingProject.getIsDeleted()) {
                result.setSuccess(false); result.setMessage("项目已被删除"); result.setErrorCode("PROJECT_ALREADY_DELETED"); return result;
            }
            if (isSystemProject(existingProject)) {
                result.setSuccess(false); result.setMessage("不能删除系统项目"); result.setErrorCode("CANNOT_DELETE_SYSTEM_PROJECT"); return result;
            }
            if (!existingProject.getCreatorId().equals(deletedBy)) {
                result.setSuccess(false); result.setMessage("只能删除自己创建的项目"); result.setErrorCode("PERMISSION_DENIED"); return result;
            }
            ProjectRelationCheckDTO relationCheck = checkProjectRelations(projectId);
            if (!forceDelete && !relationCheck.getCanDelete()) {
                result.setSuccess(false); result.setMessage("项目存在关联数据，无法删除"); result.setErrorCode("HAS_RELATED_DATA"); return result;
            }
            int deletedModulesCount = 0, deletedApisCount = 0, deletedTestCasesCount = 0;
            if (relationCheck.getHasModules()) deletedModulesCount = projectMapper.cascadeDeleteModules(projectId, deletedBy);
            if (relationCheck.getHasApis()) deletedApisCount = projectMapper.cascadeDeleteApis(projectId, deletedBy);
            if (relationCheck.getHasTestCases()) deletedTestCasesCount = projectMapper.cascadeDeleteTestCases(projectId, deletedBy);
            int projectDeleteResult = projectMapper.deleteById(projectId, deletedBy);
            if (projectDeleteResult <= 0) { result.setSuccess(false); result.setMessage("删除项目失败"); result.setErrorCode("DELETE_FAILED"); return result; }
            result.setSuccess(true); result.setMessage("项目删除成功");
            result.setDeletedModulesCount(deletedModulesCount);
            result.setDeletedApisCount(deletedApisCount);
            result.setDeletedTestCasesCount(deletedTestCasesCount);
        } catch (IllegalArgumentException e) {
            result.setSuccess(false); result.setMessage(e.getMessage()); result.setErrorCode("PARAM_ERROR");
        } catch (Exception e) {
            result.setSuccess(false); result.setMessage("删除项目失败：" + e.getMessage()); result.setErrorCode("SYSTEM_ERROR");
        }
        return result;
    }
    
    @Override
    public ProjectRelationCheckDTO checkProjectRelations(Integer projectId) {
        ProjectRelationCheckDTO result = new ProjectRelationCheckDTO();
        try {
            int modulesCount = projectMapper.checkProjectHasModules(projectId);
            result.setHasModules(modulesCount > 0); result.setModulesCount(modulesCount);
            int apisCount = projectMapper.checkProjectHasApis(projectId);
            result.setHasApis(apisCount > 0); result.setApisCount(apisCount);
            int testCasesCount = projectMapper.checkProjectHasTestCases(projectId);
            result.setHasTestCases(testCasesCount > 0); result.setTestCasesCount(testCasesCount);
            int testReportsCount = projectMapper.checkProjectHasTestReports(projectId);
            result.setHasTestReports(testReportsCount > 0); result.setTestReportsCount(testReportsCount);
            boolean canDelete = modulesCount == 0 && apisCount == 0 && testCasesCount == 0 && testReportsCount == 0;
            result.setCanDelete(canDelete);
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
    
    // ================= 最近编辑项目 =================
    @Override
    public RecentProjectsResponseDTO getRecentProjects(RecentProjectsQueryDTO queryDTO, Integer currentUserId) {
        validateRecentProjectsQuery(queryDTO);
        setRecentProjectsDefaultValues(queryDTO);
        if (!hasRecentProjectsPermission(currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法查看最近编辑项目列表");
        }
        TimeRangeDTO timeRange = calculateTimeRange(queryDTO.getTimeRange());
        List<RecentProjectItemDTO> items = projectMapper.selectRecentProjects(queryDTO, currentUserId, timeRange);
        Long total = projectMapper.countRecentProjects(queryDTO, currentUserId, timeRange);
        RecentProjectsResponseDTO responseDTO = new RecentProjectsResponseDTO();
        responseDTO.setTotal(total);
        responseDTO.setItems(items);
        responseDTO.setPage(queryDTO.getPage());
        responseDTO.setPageSize(queryDTO.getPageSize());
        responseDTO.setTimeRange(timeRange);
        return responseDTO;
    }

    // ================= 统计 =================
    @Override
    public com.victor.iatms.entity.dto.ProjectStatisticsDTO getProjectStatistics(Integer projectId) {
        if (projectId == null) { throw new IllegalArgumentException("项目ID不能为空"); }
        Project project = projectMapper.selectById(projectId);
        if (project == null) { throw new IllegalArgumentException("项目不存在"); }
        if (project.getIsDeleted()) { throw new IllegalArgumentException("项目已被删除"); }
        com.victor.iatms.entity.dto.ProjectStatisticsDTO statistics = new com.victor.iatms.entity.dto.ProjectStatisticsDTO();
        statistics.setProjectId(project.getProjectId());
        statistics.setProjectName(project.getName());
        statistics.setProjectCode(null);
        ModuleListQueryDTO moduleQuery = new ModuleListQueryDTO();
        moduleQuery.setProjectId(projectId); moduleQuery.setIncludeDeleted(false);
        Integer moduleCount = projectMapper.countModules(moduleQuery);
        statistics.setModuleCount(moduleCount != null ? moduleCount : 0);
        Integer apiCount = projectMapper.checkProjectHasApis(projectId);
        statistics.setApiCount(apiCount);
        Integer testCaseCount = testExecutionMapper.countTestCasesByProjectId(projectId, null, null, null, true);
        statistics.setTestCaseCount(testCaseCount != null ? testCaseCount : 0);
        Integer passedCount = testExecutionMapper.countPassedTestCasesByProjectId(projectId);
        Integer failedCount = testExecutionMapper.countFailedTestCasesByProjectId(projectId);
        statistics.setPassedCount(passedCount != null ? passedCount : 0);
        statistics.setFailedCount(failedCount != null ? failedCount : 0);
        int executedCount = statistics.getPassedCount() + statistics.getFailedCount();
        int notExecutedCount = statistics.getTestCaseCount() - executedCount;
        statistics.setNotExecutedCount(Math.max(notExecutedCount, 0));
        if (executedCount > 0) {
            double passRate = (statistics.getPassedCount() * 100.0) / executedCount;
            statistics.setPassRate(Math.round(passRate * 100.0) / 100.0);
        } else { statistics.setPassRate(0.0); }
        Integer executionRecordCount = testExecutionMapper.countExecutionRecordsByProjectId(projectId);
        statistics.setExecutionRecordCount(executionRecordCount != null ? executionRecordCount : 0);
        Integer testReportCount = projectMapper.checkProjectHasTestReports(projectId);
        statistics.setTestReportCount(testReportCount);
        ProjectMembersQueryDTO memberQuery = new ProjectMembersQueryDTO();
        memberQuery.setProjectId(projectId);
        Long memberCount = projectMapper.countProjectMembers(memberQuery);
        statistics.setMemberCount(memberCount != null ? memberCount.intValue() : 0);
        String lastExecutionTime = testExecutionMapper.getLatestExecutionTimeByProjectId(projectId);
        statistics.setLastExecutionTime(lastExecutionTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (project.getCreatedAt() != null) { statistics.setCreatedAt(project.getCreatedAt().format(formatter)); }
        if (project.getUpdatedAt() != null) { statistics.setUpdatedAt(project.getUpdatedAt().format(formatter)); }
        return statistics;
    }

    // ================= 3.9/3.10 成员维护 =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectMemberDTO addProjectMember(Integer projectId, AddProjectMemberDTO dto, Integer operatorId) {
        if (projectId == null) throw new IllegalArgumentException("项目ID不能为空");
        if (dto == null || dto.getUserId() == null) throw new IllegalArgumentException("用户ID不能为空");
        Project project = projectMapper.selectById(projectId);
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new IllegalArgumentException("项目不存在");
        }
        User user = userMapper.selectUserById(dto.getUserId());
        if (user == null) throw new IllegalArgumentException("用户不存在");
        if (!"active".equals(user.getStatus())) throw new IllegalArgumentException("用户账户未激活，无法添加到项目");
        String role = dto.getProjectRole() == null ? "viewer" : dto.getProjectRole();
        String perm = dto.getPermissionLevel() == null ? "read" : dto.getPermissionLevel();
        if (!java.util.Set.of("owner","manager","developer","tester","viewer").contains(role)) {
            throw new IllegalArgumentException("项目角色无效，可选值: owner, manager, developer, tester, viewer");
        }
        if (!java.util.Set.of("read","write","admin").contains(perm)) {
            throw new IllegalArgumentException("权限级别无效，可选值: read, write, admin");
        }
        ProjectMember existing = projectMemberMapper.findByProjectAndUser(projectId, dto.getUserId());
        if (existing != null && (existing.getStatus() == null || !"removed".equalsIgnoreCase(existing.getStatus()))) {
            throw new IllegalArgumentException("用户已存在于该项目中");
        }
        if (existing == null) {
            ProjectMember member = new ProjectMember();
            member.setProjectId(projectId);
            member.setUserId(dto.getUserId());
            member.setPermissionLevel(perm);
            member.setProjectRole(role);
            member.setStatus("active");
            member.setAssignedTasks(0);
            member.setCompletedTasks(0);
            member.setAdditionalRoles(dto.getAdditionalRoles());
            member.setCustomPermissions(dto.getCustomPermissions());
            member.setNotes(dto.getNotes());
            member.setCreatedBy(operatorId);
            member.setUpdatedBy(operatorId);
            member.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));
            member.setUpdatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));
            projectMemberMapper.insert(member);
            existing = member;
        } else {
            existing.setStatus("active");
            existing.setLeaveTime(null);
            existing.setJoinTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));
            existing.setPermissionLevel(perm);
            existing.setProjectRole(role);
            existing.setUpdatedBy(operatorId);
            projectMemberMapper.updateMember(existing);
        }
        ProjectMemberDTO dtoRes = new ProjectMemberDTO();
        dtoRes.setMemberId(existing.getMemberId());
        dtoRes.setUserId(existing.getUserId());
        dtoRes.setPermissionLevel(existing.getPermissionLevel());
        dtoRes.setProjectRole(existing.getProjectRole());
        dtoRes.setStatus(existing.getStatus());
        if (existing.getJoinTime() != null) {
            dtoRes.setJoinTime(existing.getJoinTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        UserInfoDTO ui = new UserInfoDTO();
        ui.setName(user.getName()); ui.setAvatarUrl(user.getAvatarUrl()); ui.setPosition(user.getPosition());
        dtoRes.setUserInfo(ui);
        return dtoRes;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectMemberDTO updateProjectMember(Integer projectId, Integer userId, UpdateProjectMemberDTO dto, Integer operatorId) {
        if (projectId == null) throw new IllegalArgumentException("项目ID不能为空");
        if (userId == null) throw new IllegalArgumentException("用户ID不能为空");
        if (dto == null) throw new IllegalArgumentException("修改信息不能为空");
        Project project = projectMapper.selectById(projectId);
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new IllegalArgumentException("项目不存在");
    }
        User user = userMapper.selectUserById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        ProjectMember relation = projectMemberMapper.findByProjectAndUser(projectId, userId);
        if (relation == null || "removed".equalsIgnoreCase(relation.getStatus())) {
            throw new IllegalArgumentException("该用户不是项目成员");
        }
        ProjectMember update = new ProjectMember();
        update.setProjectId(projectId);
        update.setUserId(userId);
        if (dto.getProjectRole() != null) {
            if (!java.util.Set.of("owner","manager","developer","tester","viewer").contains(dto.getProjectRole())) {
                throw new IllegalArgumentException("项目角色无效，可选值: owner, manager, developer, tester, viewer");
            }
            update.setProjectRole(dto.getProjectRole());
        }
        if (dto.getPermissionLevel() != null) {
            if (!java.util.Set.of("read","write","admin").contains(dto.getPermissionLevel())) {
                throw new IllegalArgumentException("权限级别无效，可选值: read, write, admin");
            }
            update.setPermissionLevel(dto.getPermissionLevel());
        }
        if (dto.getStatus() != null) {
            if (!java.util.Set.of("active","inactive","removed").contains(dto.getStatus())) {
                throw new IllegalArgumentException("状态值无效，可选值: active, inactive, removed");
            }
            update.setStatus(dto.getStatus());
            if ("removed".equalsIgnoreCase(dto.getStatus())) {
                update.setLeaveTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        }
        }
        update.setAdditionalRoles(dto.getAdditionalRoles());
        update.setCustomPermissions(dto.getCustomPermissions());
        update.setNotes(dto.getNotes());
        update.setUpdatedBy(operatorId);
        int affected = projectMemberMapper.updateMember(update);
        if (affected <= 0) throw new RuntimeException("更新项目成员失败");
        ProjectMemberDTO res = new ProjectMemberDTO();
        res.setMemberId(relation.getMemberId());
        res.setUserId(userId);
        res.setProjectRole(update.getProjectRole() != null ? update.getProjectRole() : relation.getProjectRole());
        res.setPermissionLevel(update.getPermissionLevel() != null ? update.getPermissionLevel() : relation.getPermissionLevel());
        res.setStatus(update.getStatus() != null ? update.getStatus() : relation.getStatus());
        if (relation.getJoinTime() != null) {
            res.setJoinTime(relation.getJoinTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        UserInfoDTO ui = new UserInfoDTO();
        ui.setName(user.getName()); ui.setAvatarUrl(user.getAvatarUrl()); ui.setPosition(user.getPosition());
        res.setUserInfo(ui);
        return res;
    }
    
    // ================= 私有方法 =================
    private void validateModuleListQuery(ModuleListQueryDTO queryDTO) {
        if (queryDTO == null) { throw new IllegalArgumentException("查询参数不能为空"); }
        if (queryDTO.getProjectId() == null) { throw new IllegalArgumentException("项目ID不能为空"); }
    }
    
    private void setModuleQueryDefaultValues(ModuleListQueryDTO queryDTO) {
        if (!StringUtils.hasText(queryDTO.getStructure())) { queryDTO.setStructure(Constants.DEFAULT_MODULE_STRUCTURE); }
        if (!StringUtils.hasText(queryDTO.getStatus())) { queryDTO.setStatus(Constants.DEFAULT_MODULE_STATUS); }
        if (queryDTO.getIncludeDeleted() == null) { queryDTO.setIncludeDeleted(Constants.DEFAULT_INCLUDE_DELETED); }
        if (queryDTO.getIncludeStatistics() == null) { queryDTO.setIncludeStatistics(Constants.DEFAULT_INCLUDE_STATISTICS); }
        if (!StringUtils.hasText(queryDTO.getSortBy())) { queryDTO.setSortBy(Constants.DEFAULT_MODULE_SORT_BY); }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) { queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER); }
        if (!ModuleSortFieldEnum.isValidSortField(queryDTO.getSortBy())) { queryDTO.setSortBy(Constants.DEFAULT_MODULE_SORT_BY); }
        if (!SortOrderEnum.isValidSortOrder(queryDTO.getSortOrder())) { queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER); }
    }
    
    private List<ModuleDTO> buildModuleTree(List<ModuleDTO> modules) {
        if (modules == null || modules.isEmpty()) { return new ArrayList<>(); }
        java.util.Map<Integer, ModuleDTO> moduleMap = new java.util.HashMap<>();
        for (ModuleDTO module : modules) { moduleMap.put(module.getModuleId(), module); module.setChildren(new ArrayList<>()); }
        List<ModuleDTO> rootModules = new ArrayList<>();
        for (ModuleDTO module : modules) {
            if (module.getParentModuleId() == null) {
                rootModules.add(module);
            } else {
                ModuleDTO parent = moduleMap.get(module.getParentModuleId());
                if (parent != null) { parent.getChildren().add(module); }
            }
        }
        return rootModules;
    }
    
    private void addLevelAndPathInfo(List<ModuleDTO> modules) {
        if (modules == null || modules.isEmpty()) { return; }
        java.util.Map<Integer, ModuleDTO> moduleMap = new java.util.HashMap<>();
        for (ModuleDTO module : modules) { moduleMap.put(module.getModuleId(), module); }
        for (ModuleDTO module : modules) { calculateLevelAndPath(module, moduleMap); }
    }
    
    private void calculateLevelAndPath(ModuleDTO module, java.util.Map<Integer, ModuleDTO> moduleMap) {
        if (module.getParentModuleId() == null) {
            module.setLevel(1); module.setPath(module.getName());
        } else {
            ModuleDTO parent = moduleMap.get(module.getParentModuleId());
            if (parent != null) {
                if (parent.getLevel() == null) { calculateLevelAndPath(parent, moduleMap); }
                module.setLevel(parent.getLevel() + 1);
                module.setPath(parent.getPath() + "/" + module.getName());
            } else {
                module.setLevel(1); module.setPath(module.getName());
            }
        }
    }
    
    private void validateProjectMembersQuery(ProjectMembersQueryDTO queryDTO) {
        if (queryDTO == null) { throw new IllegalArgumentException("查询参数不能为空"); }
        if (queryDTO.getProjectId() == null) { throw new IllegalArgumentException("项目ID不能为空"); }
        if (queryDTO.getPage() != null && queryDTO.getPage() < 1) { throw new IllegalArgumentException("页码必须大于0"); }
        if (queryDTO.getPageSize() != null && (queryDTO.getPageSize() < 1 || queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE)) {
            throw new IllegalArgumentException("每页条数必须在1-" + Constants.MAX_PAGE_SIZE + "之间");
        }
    }
    
    private void setDefaultValues(ProjectMembersQueryDTO queryDTO) {
        if (queryDTO.getPage() == null) { queryDTO.setPage(Constants.DEFAULT_PAGE); }
        if (queryDTO.getPageSize() == null) { queryDTO.setPageSize(Constants.DEFAULT_PAGE_SIZE); }
        if (!StringUtils.hasText(queryDTO.getStatus())) { queryDTO.setStatus(Constants.DEFAULT_MEMBER_STATUS); }
        if (!StringUtils.hasText(queryDTO.getSortBy())) { queryDTO.setSortBy(Constants.DEFAULT_MEMBER_SORT_BY); }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) { queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER); }
    }
    
    private void validateEditProject(Integer projectId, UpdateProjectDTO updateProjectDTO, Integer updatedBy) {
        if (projectId == null) { throw new IllegalArgumentException("项目ID不能为空"); }
        if (updateProjectDTO == null) { throw new IllegalArgumentException("项目信息不能为空"); }
        if (updatedBy == null) { throw new IllegalArgumentException("更新人ID不能为空"); }
        if (!StringUtils.hasText(updateProjectDTO.getName()) && updateProjectDTO.getDescription() == null) {
            throw new IllegalArgumentException("至少需要提供一个字段进行更新");
        }
        if (StringUtils.hasText(updateProjectDTO.getName())) {
            if (updateProjectDTO.getName().length() > Constants.PROJECT_NAME_MAX_LENGTH) {
                throw new IllegalArgumentException("项目名称长度不能超过" + Constants.PROJECT_NAME_MAX_LENGTH + "个字符");
            }
        }
        if (updateProjectDTO.getDescription() != null && updateProjectDTO.getDescription().length() > Constants.PROJECT_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("项目描述长度不能超过" + Constants.PROJECT_DESCRIPTION_MAX_LENGTH + "个字符");
        }
    }
    
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
    
    private void validateAddProject(AddProjectDTO addProjectDTO, Integer creatorId) {
        if (addProjectDTO == null) { throw new IllegalArgumentException("项目信息不能为空"); }
        if (!StringUtils.hasText(addProjectDTO.getName())) { throw new IllegalArgumentException("项目名称不能为空"); }
        if (addProjectDTO.getName().length() > Constants.PROJECT_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("项目名称长度不能超过" + Constants.PROJECT_NAME_MAX_LENGTH + "个字符");
        }
        if (addProjectDTO.getDescription() != null && addProjectDTO.getDescription().length() > Constants.PROJECT_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("项目描述长度不能超过" + Constants.PROJECT_DESCRIPTION_MAX_LENGTH + "个字符");
        }
        if (creatorId == null) { throw new IllegalArgumentException("创建人ID不能为空"); }
    }

    private void validateAndSetDefaults(ProjectListQueryDTO queryDTO) {
        if (queryDTO == null) { throw new IllegalArgumentException("查询参数不能为空"); }
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) { queryDTO.setPage(Constants.DEFAULT_PAGE); }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) { queryDTO.setPageSize(Constants.DEFAULT_PAGE_SIZE); }
        if (queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE) { queryDTO.setPageSize(Constants.MAX_PAGE_SIZE); }
        if (!StringUtils.hasText(queryDTO.getSortBy())) { queryDTO.setSortBy("created_at"); }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) { queryDTO.setSortOrder("desc"); }
        if (!ProjectSortFieldEnum.isValidSortField(queryDTO.getSortBy())) { queryDTO.setSortBy("created_at"); }
        if (!SortOrderEnum.isValidSortOrder(queryDTO.getSortOrder())) { queryDTO.setSortOrder("desc"); }
        if (queryDTO.getIncludeDeleted() == null) { queryDTO.setIncludeDeleted(false); }
    }

    private void validateRecentProjectsQuery(RecentProjectsQueryDTO queryDTO) {
        if (queryDTO == null) { throw new IllegalArgumentException("查询参数不能为空"); }
        if (StringUtils.hasText(queryDTO.getTimeRange())) {
            if (!isValidTimeRange(queryDTO.getTimeRange())) { throw new IllegalArgumentException("时间范围参数错误"); }
        }
        if (queryDTO.getPageSize() != null && queryDTO.getPageSize() > Constants.MAX_RECENT_PROJECTS_PAGE_SIZE) {
            throw new IllegalArgumentException("分页大小不能超过" + Constants.MAX_RECENT_PROJECTS_PAGE_SIZE);
        }
        if (StringUtils.hasText(queryDTO.getSortBy())) {
            if (!isValidRecentProjectsSortField(queryDTO.getSortBy())) { throw new IllegalArgumentException("排序字段无效"); }
        }
        if (StringUtils.hasText(queryDTO.getSortOrder())) {
            if (!"asc".equalsIgnoreCase(queryDTO.getSortOrder()) && !"desc".equalsIgnoreCase(queryDTO.getSortOrder())) {
                throw new IllegalArgumentException("排序顺序无效");
            }
        }
    }
    
    private void setRecentProjectsDefaultValues(RecentProjectsQueryDTO queryDTO) {
        if (!StringUtils.hasText(queryDTO.getTimeRange())) { queryDTO.setTimeRange(Constants.DEFAULT_RECENT_PROJECTS_TIME_RANGE); }
        if (queryDTO.getIncludeStats() == null) { queryDTO.setIncludeStats(false); }
        if (!StringUtils.hasText(queryDTO.getSortBy())) { queryDTO.setSortBy(Constants.DEFAULT_RECENT_PROJECTS_SORT_BY); }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) { queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER); }
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) { queryDTO.setPage(Constants.DEFAULT_PAGE); }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) { queryDTO.setPageSize(Constants.DEFAULT_RECENT_PROJECTS_PAGE_SIZE); }
    }
    
    private boolean hasRecentProjectsPermission(Integer userId) { return true; }
    
    private boolean isValidTimeRange(String timeRange) {
        return "1d".equals(timeRange) || "7d".equals(timeRange) || "30d".equals(timeRange);
    }
    
    private boolean isValidRecentProjectsSortField(String sortField) {
        return "last_accessed".equalsIgnoreCase(sortField) || "updated_at".equalsIgnoreCase(sortField) || "created_at".equalsIgnoreCase(sortField);
    }
    
    private TimeRangeDTO calculateTimeRange(String timeRange) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = switch (timeRange) {
            case "1d" -> endTime.minusDays(1);
            case "30d" -> endTime.minusDays(30);
            default -> endTime.minusDays(7);
        };
        TimeRangeDTO timeRangeDTO = new TimeRangeDTO();
        timeRangeDTO.setStartTime(startTime);
        timeRangeDTO.setEndTime(endTime);
        timeRangeDTO.setDays((int) java.time.Duration.between(startTime, endTime).toDays());
        return timeRangeDTO;
    }
    
    private boolean isSystemProject(Project project) { return project.getName().contains("系统"); }

    public Boolean checkProjectNameExists(String projectName, Integer excludeId) {
        if (!StringUtils.hasText(projectName)) { return false; }
        int count = projectMapper.checkProjectNameExists(projectName, excludeId);
        return count > 0;
    }
}
