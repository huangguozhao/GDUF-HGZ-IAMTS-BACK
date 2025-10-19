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
}
