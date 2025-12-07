package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.RoleListItemDTO;
import com.victor.iatms.entity.dto.RoleListQueryDTO;
import com.victor.iatms.entity.po.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<RoleListItemDTO> selectRoleList(RoleListQueryDTO queryDTO);

    Long countRoles(RoleListQueryDTO queryDTO);

    Role selectById(@Param("roleId") Integer roleId);

    int insert(Role role);

    int updateById(Role role);

    int softDelete(@Param("roleId") Integer roleId, @Param("deletedBy") Integer deletedBy);

    int existsByName(@Param("roleName") String roleName, @Param("excludeId") Integer excludeId);
}

