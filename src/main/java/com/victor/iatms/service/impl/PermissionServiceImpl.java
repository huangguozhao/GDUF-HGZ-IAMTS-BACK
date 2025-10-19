package com.victor.iatms.service.impl;

import com.victor.iatms.entity.enums.RoleEnum;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.entity.po.UserRole;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.mappers.UserRoleMapper;
import com.victor.iatms.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

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

        // 超级管理员拥有所有权限
        if (isSuperAdmin(userId)) {
            return true;
        }

        // 获取用户权限列表
        List<String> userPermissions = getUserPermissions(userId);
        
        // 检查是否拥有所有需要的权限
        for (String permission : permissions) {
            if (!userPermissions.contains(permission)) {
                return false;
            }
        }
        
        return true;
    }

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

        // 获取用户角色列表
        List<String> userRoles = getUserRoles(userId);
        
        // 检查是否拥有任一需要的角色
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        
        return false;
    }

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

        // 检查是否为超级管理员或管理员
        return isSuperAdmin(userId) || hasRole(userId, new String[]{RoleEnum.ADMIN.getCode()});
    }

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

        // 超级管理员可以访问所有资源
        if (isSuperAdmin(userId)) {
            return true;
        }

        // 根据资源类型进行不同的权限检查
        switch (resourceType.toLowerCase()) {
            case "api":
                return hasApiAccess(userId, resourceId);
            case "module":
                return hasModuleAccess(userId, resourceId);
            case "testcase":
                return hasTestCaseAccess(userId, resourceId);
            default:
                return false;
        }
    }

    @Override
    public List<String> getUserPermissions(Integer userId) {
        List<String> permissions = new ArrayList<>();
        
        if (userId == null) {
            return permissions;
        }

        // 获取用户角色
        List<String> userRoles = getUserRoles(userId);
        
        // 根据角色分配权限
        for (String roleCode : userRoles) {
            RoleEnum role = RoleEnum.fromCode(roleCode);
            if (role != null) {
                permissions.addAll(getRolePermissions(role));
            }
        }
        
        return permissions;
    }

    @Override
    public List<String> getUserRoles(Integer userId) {
        List<String> roles = new ArrayList<>();
        
        if (userId == null) {
            return roles;
        }

        // 从数据库查询用户角色
        List<UserRole> userRoles = userRoleMapper.findByUserId(userId);
        for (UserRole userRole : userRoles) {
            // 这里需要根据roleId查询角色信息，简化处理
            // 临时返回test_engineer角色，实际应该根据roleId查询角色表获取角色代码
            // 可以根据userRole.getRoleId()查询角色表获取具体的角色代码
            roles.add("test_engineer");
        }
        
        return roles;
    }

    /**
     * 检查是否为超级管理员
     */
    private boolean isSuperAdmin(Integer userId) {
        // 这里可以根据实际业务逻辑判断
        // 比如检查用户是否有超级管理员角色，或者用户ID是否为1等
        return userId != null && userId == 1;
    }

    /**
     * 根据角色获取权限
     */
    private List<String> getRolePermissions(RoleEnum role) {
        List<String> permissions = new ArrayList<>();
        
        switch (role) {
            case SUPER_ADMIN:
                // 超级管理员拥有所有权限
                permissions.addAll(Arrays.asList(
                    "user:view", "user:create", "user:update", "user:delete",
                    "api:view", "api:create", "api:update", "api:delete",
                    "testcase:view", "testcase:create", "testcase:update", "testcase:delete", "testcase:execute",
                    "module:view", "module:create", "module:update", "module:delete",
                    "system:config", "system:log", "system:monitor"
                ));
                break;
            case ADMIN:
                permissions.addAll(Arrays.asList(
                    "user:view", "user:create", "user:update",
                    "api:view", "api:create", "api:update",
                    "testcase:view", "testcase:create", "testcase:update", "testcase:execute",
                    "module:view", "module:create", "module:update"
                ));
                break;
            case TEST_MANAGER:
                permissions.addAll(Arrays.asList(
                    "api:view",
                    "testcase:view", "testcase:create", "testcase:update", "testcase:execute",
                    "module:view"
                ));
                break;
            case TEST_ENGINEER:
                permissions.addAll(Arrays.asList(
                    "api:view",
                    "testcase:view", "testcase:create", "testcase:update", "testcase:execute"
                ));
                break;
            case DEVELOPER:
                permissions.addAll(Arrays.asList(
                    "api:view", "api:create", "api:update",
                    "testcase:view", "testcase:execute"
                ));
                break;
            case VIEWER:
                permissions.addAll(Arrays.asList(
                    "api:view",
                    "testcase:view",
                    "module:view"
                ));
                break;
        }
        
        return permissions;
    }

    /**
     * 检查API访问权限
     */
    private boolean hasApiAccess(Integer userId, Integer apiId) {
        // 这里可以根据实际业务逻辑实现
        // 比如检查用户是否有该API的访问权限，或者检查用户所属部门等
        return true; // 临时返回true，实际应该根据业务逻辑判断
    }

    /**
     * 检查模块访问权限
     */
    private boolean hasModuleAccess(Integer userId, Integer moduleId) {
        // 这里可以根据实际业务逻辑实现
        return true; // 临时返回true，实际应该根据业务逻辑判断
    }

    /**
     * 检查测试用例访问权限
     */
    private boolean hasTestCaseAccess(Integer userId, Integer testCaseId) {
        // 这里可以根据实际业务逻辑实现
        return true; // 临时返回true，实际应该根据业务逻辑判断
    }
}
