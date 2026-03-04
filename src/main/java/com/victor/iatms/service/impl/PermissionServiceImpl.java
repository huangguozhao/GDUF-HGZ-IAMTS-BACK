package com.victor.iatms.service.impl;

import com.victor.iatms.entity.po.*;
import com.victor.iatms.mappers.*;
import com.victor.iatms.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限服务实现类 - 极简版：只区分管理员和普通用户
 * 管理员拥有所有权限，普通用户需要检查项目成员身份
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 检查用户是否拥有指定权限
     * 逻辑：admin 拥有所有权限，普通人需要检查具体权限
     */
    @Override
    public boolean hasPermission(Integer userId, String[] permissions) {
        if (userId == null || permissions == null || permissions.length == 0) {
            return false;
        }

        // 检查用户是否存在且状态为active
        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return false;
        }

        // 管理员拥有所有权限
        if (isAdmin(userId)) {
            return true;
        }

        // TODO: 如果需要更细粒度的普通用户权限控制，在这里添加
        // 当前简化版本：普通用户没有权限（如果需要可在此扩展）
        return false;
    }

    /**
     * 检查用户是否拥有指定角色
     */
    @Override
    public boolean hasRole(Integer userId, String[] roles) {
        if (userId == null || roles == null || roles.length == 0) {
            return false;
        }

        // 检查用户是否存在且状态为active
        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return false;
        }

        // 管理员拥有所有角色
        if (isAdmin(userId)) {
            return true;
        }

        // 检查用户的角色是否匹配
        String userRole = user.getRole();
        if (userRole != null) {
            for (String role : roles) {
                if (role.equals(userRole)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断用户是否是管理员
     */
    @Override
    public boolean isAdmin(Integer userId) {
        if (userId == null) {
            return false;
        }

        // 检查用户是否存在且状态为active
        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return false;
        }

        // 根据用户的 role 字段判断是否是管理员
        return "admin".equals(user.getRole());
    }

    /**
     * 检查用户是否有资源访问权限
     * 管理员可以访问所有资源，普通用户需要是项目成员
     */
    @Override
    public boolean hasResourceAccess(Integer userId, String resourceType, Integer resourceId) {
        if (userId == null || resourceType == null || resourceId == null) {
            return false;
        }

        // 检查用户是否存在且状态为active
        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return false;
        }

        // 管理员可以访问所有资源
        if (isAdmin(userId)) {
            return true;
        }

        // 根据资源类型进行不同的权限检查（普通用户需要是项目成员）
        switch (resourceType.toLowerCase()) {
            case "api":
                return hasApiAccess(userId, resourceId);
            case "module":
                return hasModuleAccess(userId, resourceId);
            case "testcase":
                return hasTestCaseAccess(userId, resourceId);
            case "project":
                return isProjectMember(userId, resourceId);
            default:
                return false;
        }
    }

    /**
     * 获取用户权限列表（简化版）
     */
    @Override
    public List<String> getUserPermissions(Integer userId) {
        List<String> permissions = new ArrayList<>();
        
        if (userId == null) {
            return permissions;
        }

        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return permissions;
        }

        // 管理员拥有所有权限
        if (isAdmin(userId)) {
            permissions.add("*"); // 表示所有权限
        }
        // 普通用户暂无细粒度权限（可扩展）
        
        return permissions;
    }

    /**
     * 获取用户角色列表
     */
    @Override
    public List<String> getUserRoles(Integer userId) {
        List<String> roles = new ArrayList<>();

        if (userId == null) {
            return roles;
        }

        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return roles;
        }

        // 从用户的 role 字段读取角色
        if (user.getRole() != null) {
            roles.add(user.getRole());
        } else {
            roles.add("user");
        }

        return roles;
    }

    /**
     * 检查API访问权限：用户是否是项目成员
     */
    private boolean hasApiAccess(Integer userId, Integer apiId) {
        if (userId == null || apiId == null) {
            return false;
        }

        Api api = apiMapper.selectById(apiId);
        if (api == null) {
            return false;
        }

        com.victor.iatms.entity.po.Module module = moduleMapper.selectById(api.getModuleId());
        if (module == null) {
            return false;
        }

        return isProjectMember(userId, module.getProjectId());
    }

    /**
     * 检查模块访问权限：用户是否是项目成员
     */
    private boolean hasModuleAccess(Integer userId, Integer moduleId) {
        if (userId == null || moduleId == null) {
            return false;
        }

        com.victor.iatms.entity.po.Module module = moduleMapper.selectById(moduleId);
        if (module == null) {
            return false;
        }

        return isProjectMember(userId, module.getProjectId());
    }

    /**
     * 检查测试用例访问权限：用户是否是项目成员
     */
    private boolean hasTestCaseAccess(Integer userId, Integer testCaseId) {
        if (userId == null || testCaseId == null) {
            return false;
        }

        TestCase testCase = testCaseMapper.selectById(testCaseId);
        if (testCase == null) {
            return false;
        }

        Api api = apiMapper.selectById(testCase.getApiId());
        if (api == null) {
            return false;
        }

        com.victor.iatms.entity.po.Module module = moduleMapper.selectById(api.getModuleId());
        if (module == null) {
            return false;
        }

        return isProjectMember(userId, module.getProjectId());
    }

    /**
     * 检查用户是否是项目成员
     */
    private boolean isProjectMember(Integer userId, Integer projectId) {
        if (userId == null || projectId == null) {
            return false;
        }

        ProjectMember member = projectMemberMapper.findByProjectAndUser(projectId, userId);
        return member != null;
    }

    /**
     * 检查用户是否有项目资源操作权限
     * 逻辑：
     * 1. admin 拥有所有项目权限
     * 2. 普通用户需要是项目成员，且根据 project_role 判断权限
     *
     * 权限矩阵：
     * | 项目角色   | project:view | project:edit | project:delete | project:manage_members |
     * |-----------|-------------|---------------|----------------|----------------------|
     * | owner     | ✅          | ✅            | ✅             | ✅                    |
     * | manager   | ✅          | ✅            | ❌             | ✅                    |
     * | developer | ✅          | ❌            | ❌             | ❌                    |
     * | tester    | ✅          | ❌            | ❌             | ❌                    |
     * | viewer    | ✅          | ❌            | ❌             | ❌                    |
     *
     * | 项目角色   | module:view | module:create | module:edit | module:delete |
     * |-----------|-----------|---------------|-------------|---------------|
     * | owner     | ✅        | ✅             | ✅          | ✅             |
     * | manager   | ✅        | ✅             | ✅          | ✅             |
     * | developer | ✅        | ✅             | ✅          | ✅             |
     * | tester    | ✅        | ✅             | ✅          | ❌             |
     * | viewer    | ✅        | ❌             | ❌          | ❌             |
     *
     * | 项目角色   | api:view | api:create | api:edit | api:delete |
     * |-----------|---------|------------|----------|------------|
     * | owner     | ✅      | ✅         | ✅       | ✅         |
     * | manager   | ✅      | ✅         | ✅       | ✅         |
     * | developer | ✅      | ✅         | ✅       | ✅         |
     * | tester    | ✅      | ✅         | ✅       | ❌         |
     * | viewer    | ✅      | ❌         | ❌       | ❌         |
     *
     * | 项目角色   | testcase:view | testcase:create | testcase:edit | testcase:delete | testcase:execute |
     * |-----------|--------------|------------------|---------------|-----------------|------------------|
     * | owner     | ✅           | ✅                | ✅            | ✅              | ✅               |
     * | manager   | ✅           | ✅                | ✅            | ✅              | ✅               |
     * | developer | ✅           | ✅                | ✅            | ✅              | ✅               |
     * | tester    | ✅           | ✅                | ✅            | ❌              | ✅               |
     * | viewer    | ✅           | ❌                | ❌            | ❌              | ❌               |
     *
     * | 项目角色   | task:view | task:create | task:edit | task:delete |
     * |-----------|----------|-------------|-----------|-------------|
     * | owner     | ✅       | ✅           | ✅        | ✅          |
     * | manager   | ✅       | ✅           | ✅        | ✅          |
     * | developer | ✅       | ✅           | ✅        | ✅          |
     * | tester    | ✅       | ✅           | ✅        | ❌          |
     * | viewer    | ✅       | ❌           | ❌        | ❌          |
     */
    @Override
    public boolean hasProjectPermission(Integer userId, Integer projectId, String permission) {
        if (userId == null || projectId == null || permission == null) {
            return false;
        }

        // 检查用户是否存在且状态为active
        User user = userMapper.findById(userId);
        if (user == null || !"active".equals(user.getStatus())) {
            return false;
        }

        // 管理员拥有所有项目权限
        if (isAdmin(userId)) {
            return true;
        }

        // 检查用户是否是项目成员
        ProjectMember member = projectMemberMapper.findByProjectAndUser(projectId, userId);
        if (member == null || !"active".equals(member.getStatus())) {
            return false;
        }

        // 根据 project_role 判断权限
        String projectRole = member.getProjectRole();
        if (projectRole == null) {
            projectRole = "viewer"; // 默认为查看者
        }

        return checkProjectRolePermission(projectRole, permission);
    }

    /**
     * 根据项目角色检查权限
     */
    private boolean checkProjectRolePermission(String projectRole, String permission) {
        // 所有角色都可以查看
        if (permission.endsWith(":view")) {
            return true;
        }

        switch (projectRole) {
            case "owner":
                // owner 拥有所有权限
                return true;
            case "manager":
                // manager 拥有除删除项目外的所有权限
                if ("project:delete".equals(permission)) {
                    return false;
                }
                return true;
            case "developer":
                // developer 不能管理项目和成员
                if (permission.startsWith("project:") && !"project:view".equals(permission)) {
                    return false;
                }
                if ("project:manage_members".equals(permission)) {
                    return false;
                }
                // developer 不能删除模块
                if ("module:delete".equals(permission)) {
                    return false;
                }
                return true;
            case "tester":
                // tester 不能删除和编辑项目
                if (permission.startsWith("project:") && !"project:view".equals(permission)) {
                    return false;
                }
                if ("project:manage_members".equals(permission)) {
                    return false;
                }
                // tester 不能删除模块、接口、用例
                if ("module:delete".equals(permission) ||
                    "api:delete".equals(permission) ||
                    "testcase:delete".equals(permission) ||
                    "task:delete".equals(permission)) {
                    return false;
                }
                return true;
            case "viewer":
                // viewer 只能查看
                return false;
            default:
                return false;
        }
    }

    /**
     * 根据资源类型和资源ID获取项目ID
     */
    @Override
    public Integer getProjectIdByResource(String resourceType, Integer resourceId) {
        if (resourceType == null || resourceId == null) {
            return null;
        }

        switch (resourceType.toLowerCase()) {
            case "api":
                Api api = apiMapper.selectById(resourceId);
                if (api != null) {
                    com.victor.iatms.entity.po.Module module = moduleMapper.selectById(api.getModuleId());
                    if (module != null) {
                        return module.getProjectId();
                    }
                }
                return null;
            case "module":
                com.victor.iatms.entity.po.Module module = moduleMapper.selectById(resourceId);
                if (module != null) {
                    return module.getProjectId();
                }
                return null;
            case "testcase":
                TestCase testCase = testCaseMapper.selectById(resourceId);
                if (testCase != null) {
                    Api testCaseApi = apiMapper.selectById(testCase.getApiId());
                    if (testCaseApi != null) {
                        com.victor.iatms.entity.po.Module m = moduleMapper.selectById(testCaseApi.getModuleId());
                        if (m != null) {
                            return m.getProjectId();
                        }
                    }
                }
                return null;
            case "task":
                Task task = taskMapper.selectById(resourceId.longValue());
                if (task != null) {
                    return task.getProjectId();
                }
                return null;
            default:
                return null;
        }
    }
}
