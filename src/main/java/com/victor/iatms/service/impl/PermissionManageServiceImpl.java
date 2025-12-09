package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.PermissionListItemDTO;
import com.victor.iatms.entity.dto.PermissionListQueryDTO;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.mappers.PermissionMapper;
import com.victor.iatms.service.PermissionManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionManageServiceImpl implements PermissionManageService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public PaginationResultVO<PermissionListItemDTO> listPermissions(PermissionListQueryDTO queryDTO) {
        Long total = permissionMapper.countPermissions(queryDTO);
        List<PermissionListItemDTO> items = permissionMapper.selectPermissionList(queryDTO);
        return new PaginationResultVO<>(total, items, queryDTO.getPage(), queryDTO.getPageSize());
    }
}




