package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.PermissionListItemDTO;
import com.victor.iatms.entity.dto.PermissionListQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    List<PermissionListItemDTO> selectPermissionList(@Param("q") PermissionListQueryDTO queryDTO);

    Long countPermissions(@Param("q") PermissionListQueryDTO queryDTO);

    int existsByIds(@Param("ids") List<Integer> ids);
}
