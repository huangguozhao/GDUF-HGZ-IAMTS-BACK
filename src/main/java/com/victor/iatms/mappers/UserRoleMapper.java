package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色数据访问层
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 根据用户ID查询用户角色列表
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<UserRole> findByUserId(@Param("userId") Integer userId);

    /**
     * 根据角色ID查询用户角色列表
     * @param roleId 角色ID
     * @return 用户角色列表
     */
    List<UserRole> findByRoleId(@Param("roleId") Integer roleId);

    /** 统计某角色被使用的用户数量 */
    int countByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRolesByUserId(@Param("userId") Integer userId);

    /**
     * 批量插入用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int batchInsertUserRoles(@Param("userId") Integer userId, @Param("roleIds") List<Integer> roleIds);
}
