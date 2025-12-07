package com.victor.iatms.service;

import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.vo.PaginationResultVO;

public interface RoleService {
    PaginationResultVO<RoleListItemDTO> listRoles(RoleListQueryDTO queryDTO);
    RoleListItemDTO createRole(CreateRoleDTO dto, Integer operatorId);
    RoleListItemDTO updateRole(Integer roleId, UpdateRoleDTO dto, Integer operatorId);
    void deleteRole(Integer roleId, Integer operatorId);
    void assignPermissions(Integer roleId, AssignRolePermissionsDTO dto, Integer operatorId);
}

