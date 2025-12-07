package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.AddProjectMemberDTO;
import com.victor.iatms.entity.dto.ProjectMemberDTO;
import com.victor.iatms.entity.dto.UpdateProjectMemberDTO;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.po.ProjectMember;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.exception.BusinessException;
import com.victor.iatms.mappers.ProjectMemberMapper;
import com.victor.iatms.mappers.ProjectMapper;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.service.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 追加实现 3.9/3.10（与 ProjectServiceImpl 同包同名类分文件组织，便于审阅）
 */
public class ProjectServiceImpl_Additions {

}

