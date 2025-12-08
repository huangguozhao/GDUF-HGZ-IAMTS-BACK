package com.victor.iatms.service;

import com.victor.iatms.entity.dto.PermissionListItemDTO;
import com.victor.iatms.entity.dto.PermissionListQueryDTO;
import com.victor.iatms.entity.vo.PaginationResultVO;

public interface PermissionManageService {
    PaginationResultVO<PermissionListItemDTO> listPermissions(PermissionListQueryDTO queryDTO);
}

