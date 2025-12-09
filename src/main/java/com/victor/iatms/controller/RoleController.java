package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@GlobalInterceptor(checkLogin = true, checkAdmin = true)
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseVO<PaginationResultVO<RoleListItemDTO>> listRoles(RoleListQueryDTO queryDTO) {
        return ResponseVO.success(roleService.listRoles(queryDTO));
    }

    @PostMapping
    public ResponseVO<RoleListItemDTO> createRole(@RequestBody CreateRoleDTO dto, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        return ResponseVO.success("角色创建成功", roleService.createRole(dto, operatorId));
    }

    @PutMapping("/{roleId}")
    public ResponseVO<RoleListItemDTO> updateRole(@PathVariable("roleId") Integer roleId, @RequestBody UpdateRoleDTO dto, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        return ResponseVO.success("角色信息更新成功", roleService.updateRole(roleId, dto, operatorId));
    }

    @DeleteMapping("/{roleId}")
    public ResponseVO<Object> deleteRole(@PathVariable("roleId") Integer roleId, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        roleService.deleteRole(roleId, operatorId);
        return ResponseVO.success("角色删除成功", null);
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseVO<Object> assignPermissions(@PathVariable("roleId") Integer roleId, @RequestBody AssignRolePermissionsDTO dto, HttpServletRequest request) {
        Integer operatorId = (Integer) request.getAttribute("userId");
        roleService.assignPermissions(roleId, dto, operatorId);
        return ResponseVO.success("权限分配成功", null);
    }
}




