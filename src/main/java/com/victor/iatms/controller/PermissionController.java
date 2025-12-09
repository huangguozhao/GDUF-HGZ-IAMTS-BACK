package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.PermissionListItemDTO;
import com.victor.iatms.entity.dto.PermissionListQueryDTO;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.PermissionManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@GlobalInterceptor(checkLogin = true, checkAdmin = true)
public class PermissionController {

    @Autowired
    private PermissionManageService permissionManageService;

    @GetMapping
    public ResponseVO<PaginationResultVO<PermissionListItemDTO>> listPermissions(PermissionListQueryDTO queryDTO) {
        return ResponseVO.success(permissionManageService.listPermissions(queryDTO));
    }
}




