package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.*;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.po.ProjectMember;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.exception.BusinessException;
import com.victor.iatms.mappers.ProjectMapper;
import com.victor.iatms.mappers.ProjectMemberMapper;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.mappers.UserRoleMapper;
import com.victor.iatms.redis.RedisComponet;
import com.victor.iatms.service.UserService;
import com.victor.iatms.utils.EmailUtils;
import com.victor.iatms.utils.JwtUtils;
import com.victor.iatms.utils.PasswordUtils;
import com.victor.iatms.utils.SmsUtils;
import com.victor.iatms.utils.VerificationCodeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;
    
    @Autowired
    private PasswordUtils passwordUtils;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private EmailUtils emailUtils;
    
    @Autowired
    private SmsUtils smsUtils;
    
    @Autowired
    private VerificationCodeUtils verificationCodeUtils;
    
    @Autowired
    private RedisComponet redisComponet;
    
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userMapper.selectUserByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new BusinessException("邮箱或密码错误");
        }
        if (!passwordUtils.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("邮箱或密码错误");
        }
        if (!"active".equals(user.getStatus())) {
            throw new BusinessException("账户已被禁用");
            }
        userMapper.updateLastLoginTime(user.getUserId(), LocalDateTime.now());
        String token = jwtUtils.generateToken(user.getUserId(), user.getEmail());
        
        LoginResponseDTO response = new LoginResponseDTO();
        LoginResponseDTO.UserInfo userInfo = new LoginResponseDTO.UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setLastLoginTime(LocalDateTime.now());
        
        response.setUser(userInfo);
        response.setToken(token);
        return response;
    }
    
    @Override
    public String requestPasswordReset(PasswordResetRequestDTO request) {
        User user = userMapper.findByEmailOrPhone(request.getAccount());
        if (user == null) {
            throw new BusinessException("该邮箱/手机号未注册");
        }
        if (!"active".equals(user.getStatus())) {
            throw new BusinessException("账户未激活，无法重置密码");
        }
        // 略：发送验证码等逻辑，可后续补齐
        throw new UnsupportedOperationException("Not fully implemented");
    }

    @Override
    public boolean executePasswordReset(PasswordResetDTO request) {
        User user = userMapper.findByEmailOrPhone(request.getAccount());
        if (user == null) {
            throw new BusinessException("该邮箱/手机号未注册");
        }
        // 略：验证码与密码更新完整逻辑
        throw new UnsupportedOperationException("Not fully implemented");
    }

    @Override
    public UserInfoDTO getCurrentUserInfo(Integer userId) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserInfoDTO userInfo = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfo);
        return userInfo;
    }

    @Override
    public PaginationResultVO<User> findUserListByPage(UserQueryDTO userQueryDTO) {
        int page = userQueryDTO.getPage() == null || userQueryDTO.getPage() < 1 ? 1 : userQueryDTO.getPage();
        int pageSize = userQueryDTO.getPageSize() == null || userQueryDTO.getPageSize() < 1 ? 10 : userQueryDTO.getPageSize();
        int offset = (page - 1) * pageSize;
        userQueryDTO.setOffset(offset);

        long count = userMapper.countUsers(userQueryDTO);
        List<User> userList = userMapper.selectUsers(userQueryDTO);
        return new PaginationResultVO<>(count, userList, page, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addUser(CreateUserDTO createUserDTO, Integer creatorId) {
        User existingUser = userMapper.selectUserByEmail(createUserDTO.getEmail());
        if (existingUser != null) {
            throw new BusinessException("邮箱已被注册");
        }
        User newUser = new User();
        BeanUtils.copyProperties(createUserDTO, newUser);
        newUser.setPassword(passwordUtils.encodePassword(createUserDTO.getPassword()));
        newUser.setCreatorId(creatorId);
        if (newUser.getStatus() == null) {
            newUser.setStatus("pending");
        }
        userMapper.insertUser(newUser);
        return newUser.getUserId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Integer userId, UpdateUserDTO updateUserDTO) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(user.getEmail())) {
            User existingUser = userMapper.selectUserByEmail(updateUserDTO.getEmail());
            if (existingUser != null) {
                throw new BusinessException("邮箱已被其他用户注册");
        }
        }
        User userToUpdate = new User();
        BeanUtils.copyProperties(updateUserDTO, userToUpdate);
        userToUpdate.setUserId(userId);
        userMapper.updateUser(userToUpdate);

        if (!CollectionUtils.isEmpty(updateUserDTO.getRoleIds())) {
            userRoleMapper.deleteUserRolesByUserId(userId);
            userRoleMapper.batchInsertUserRoles(userId, updateUserDTO.getRoleIds());
        }
    }

    @Override
    public void updateUserStatus(Integer userId, UpdateUserStatusDTO updateUserStatusDTO) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        userToUpdate.setStatus(updateUserStatusDTO.getStatus());
        userMapper.updateUser(userToUpdate);
    }

    @Override
    public void deleteUser(Integer userId, Integer currentUserId) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (userId.equals(currentUserId)) {
            throw new BusinessException("不能删除当前登录的账户");
        }
        userMapper.softDeleteUser(userId, currentUserId);
        }

    // ================= 3.6/3.7/3.8 =================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer assignUserToProject(Integer userId, AssignUserProjectDTO dto, Integer operatorId) {
        // 用户校验
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!"active".equals(user.getStatus())) {
            throw new BusinessException("用户账户未激活，无法添加到项目");
        }
        // 项目校验
        Project project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new BusinessException("项目已被删除");
        }
        // 关系检查
        ProjectMember exists = projectMemberMapper.findByProjectAndUser(dto.getProjectId(), userId);
        if (exists != null && (exists.getStatus() == null || !"removed".equalsIgnoreCase(exists.getStatus()))) {
            throw new BusinessException("用户已存在于该项目中");
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(dto.getProjectId());
        member.setUserId(userId);
        member.setPermissionLevel(dto.getPermissionLevel() == null ? "read" : dto.getPermissionLevel());
        member.setProjectRole(dto.getProjectRole() == null ? "viewer" : dto.getProjectRole());
        member.setStatus("active");
        member.setAssignedTasks(0);
        member.setCompletedTasks(0);
        member.setAdditionalRoles(dto.getAdditionalRoles());
        member.setCustomPermissions(dto.getCustomPermissions());
        member.setNotes(dto.getNotes());
        member.setCreatedBy(operatorId);
        member.setUpdatedBy(operatorId);
        member.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        member.setUpdatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        projectMemberMapper.insert(member);
        return member.getMemberId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserFromProject(Integer userId, Integer projectId, Integer operatorId) {
        // 校验用户/项目是否存在
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Project project = projectMapper.selectById(projectId);
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new BusinessException("项目不存在");
        }
        ProjectMember relation = projectMemberMapper.findByProjectAndUser(projectId, userId);
        if (relation == null) {
            throw new BusinessException("用户不在该项目中");
        }
        if ("removed".equalsIgnoreCase(relation.getStatus())) {
            throw new BusinessException("用户已被从项目中移除");
        }
        // 软删除
        projectMemberMapper.softRemove(projectId, userId, operatorId);
    }

    @Override
    public PaginationResultVO<UserProjectItemDTO> findUserProjects(UserProjectsQueryDTO queryDTO) {
        // 用户存在性
        User user = userMapper.selectUserById(queryDTO.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
    }
        int page = queryDTO.getPage() == null || queryDTO.getPage() < 1 ? 1 : queryDTO.getPage();
        int pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : Math.min(queryDTO.getPageSize(), 100);
        int offset = (page - 1) * pageSize;
        Long total = projectMemberMapper.countUserProjects(queryDTO.getUserId(), queryDTO.getStatus(), queryDTO.getProjectRole());
        List<UserProjectItemDTO> items = projectMemberMapper.selectUserProjects(queryDTO.getUserId(), queryDTO.getStatus(), queryDTO.getProjectRole(), offset, pageSize);
        return new PaginationResultVO<>(total == null ? 0 : total, items, page, pageSize);
    }
}
