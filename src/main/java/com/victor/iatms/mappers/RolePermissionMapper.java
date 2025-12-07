package com.victor.iatms.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper {

    int exists(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);

    int insert(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);

    int batchInsert(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);
}

