package com.victor.iatms.service;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 检查用户是否有指定权限
     * @param userId 用户ID
     * @param permissions 权限列表
     * @return 是否有权限
     */
    boolean hasPermission(Integer userId, String[] permissions);

    /**
     * 检查用户是否有指定角色
     * @param userId 用户ID
     * @param roles 角色列表
     * @return 是否有角色
     */
    boolean hasRole(Integer userId, String[] roles);

    /**
     * 检查用户是否为管理员
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(Integer userId);

    /**
     * 检查用户是否有资源访问权限
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 是否有访问权限
     */
    boolean hasResourceAccess(Integer userId, String resourceType, Integer resourceId);

    /**
     * 获取用户的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Integer userId);

    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Integer userId);
}
