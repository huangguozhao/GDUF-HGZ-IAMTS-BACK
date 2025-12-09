package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.po.Role;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.exception.BusinessException;
import com.victor.iatms.mappers.PermissionMapper;
import com.victor.iatms.mappers.RoleMapper;
import com.victor.iatms.mappers.RolePermissionMapper;
import com.victor.iatms.mappers.UserRoleMapper;
import com.victor.iatms.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public PaginationResultVO<RoleListItemDTO> listRoles(RoleListQueryDTO queryDTO) {
        Long total = roleMapper.countRoles(queryDTO);
        List<RoleListItemDTO> items = roleMapper.selectRoleList(queryDTO);
        return new PaginationResultVO<>(total, items, queryDTO.getPage(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleListItemDTO createRole(CreateRoleDTO dto, Integer operatorId) {
        if (roleMapper.existsByName(dto.getRoleName(), null) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        if (Boolean.TRUE.equals(dto.getIsSuperAdmin())) {
            // TODO: Add check if operator has permission to create super admin roles
        }

        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        roleMapper.insert(role);

        RoleListItemDTO result = new RoleListItemDTO();
        BeanUtils.copyProperties(role, result);
        result.setUserCount(0);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleListItemDTO updateRole(Integer roleId, UpdateRoleDTO dto, Integer operatorId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null || Boolean.TRUE.equals(role.getIsDeleted())) {
            throw new BusinessException("角色不存在");
        }
        if (dto.getRoleName() != null && !dto.getRoleName().equals(role.getRoleName())) {
            if (roleMapper.existsByName(dto.getRoleName(), roleId) > 0) {
                throw new BusinessException("角色名称已被其他角色使用");
            }
        }

        Role roleToUpdate = new Role();
        BeanUtils.copyProperties(dto, roleToUpdate);
        roleToUpdate.setRoleId(roleId);
        roleMapper.updateById(roleToUpdate);

        Role updatedRole = roleMapper.selectById(roleId);
        RoleListItemDTO result = new RoleListItemDTO();
        BeanUtils.copyProperties(updatedRole, result);
        result.setUserCount(userRoleMapper.countByRoleId(roleId));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Integer roleId, Integer operatorId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (Boolean.TRUE.equals(role.getIsDeleted())) {
            throw new BusinessException("角色已被删除");
        }
        if (Boolean.TRUE.equals(role.getIsSuperAdmin())) {
            throw new BusinessException("不能删除超级管理员角色");
        }
        if (userRoleMapper.countByRoleId(roleId) > 0) {
            throw new BusinessException("角色正在被用户使用，无法删除");
        }
        roleMapper.softDelete(roleId, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Integer roleId, AssignRolePermissionsDTO dto, Integer operatorId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null || Boolean.TRUE.equals(role.getIsDeleted())) {
            throw new BusinessException("角色不存在");
        }
        if (dto.getPermissionIds() == null || dto.getPermissionIds().isEmpty()) {
            throw new BusinessException("权限ID列表不能为空");
        }
        int existingPermissions = permissionMapper.existsByIds(dto.getPermissionIds());
        if (existingPermissions != dto.getPermissionIds().size()) {
            throw new BusinessException("包含不存在的权限ID");
        }

        // This is an append-only operation as per the spec
        for (Integer permId : dto.getPermissionIds()) {
            if (rolePermissionMapper.exists(roleId, permId) == 0) {
                rolePermissionMapper.insert(roleId, permId);
            }
        }
    }
}




