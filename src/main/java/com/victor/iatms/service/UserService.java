package com.victor.iatms.service;

import com.victor.iatms.entity.dto.LoginRequestDTO;
import com.victor.iatms.entity.dto.LoginResponseDTO;
import com.victor.iatms.entity.dto.PasswordResetDTO;
import com.victor.iatms.entity.dto.PasswordResetRequestDTO;
import com.victor.iatms.entity.dto.UserInfoDTO;
import com.victor.iatms.entity.dto.CreateUserDTO;
import com.victor.iatms.entity.dto.UpdateUserDTO;
import com.victor.iatms.entity.dto.UpdateUserStatusDTO;
import com.victor.iatms.entity.dto.UserQueryDTO;
import com.victor.iatms.entity.vo.PaginationResultVO;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.entity.dto.AssignUserProjectDTO;
import com.victor.iatms.entity.dto.UserProjectsQueryDTO;
import com.victor.iatms.entity.dto.UserProjectItemDTO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    /**
     * 请求密码重置
     * @param request 密码重置请求
     * @return 重置令牌ID
     */
    String requestPasswordReset(PasswordResetRequestDTO request);

    /**
     * 执行密码重置
     * @param request 密码重置请求
     * @return 是否重置成功
     */
    boolean executePasswordReset(PasswordResetDTO request);

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoDTO getCurrentUserInfo(Integer userId);

    /**
     * 分页查询用户列表
     *
     * @param userQueryDTO 查询条件
     * @return 分页结果
     */
    PaginationResultVO<User> findUserListByPage(UserQueryDTO userQueryDTO);

    /**
     * 添加用户
     *
     * @param createUserDTO 用户信息
     * @param creatorId 创建人ID
     * @return 新用户的ID
     */
    Integer addUser(CreateUserDTO createUserDTO, Integer creatorId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param updateUserDTO 用户信息
     */
    void updateUser(Integer userId, UpdateUserDTO updateUserDTO);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param updateUserStatusDTO 状态信息
     */
    void updateUserStatus(Integer userId, UpdateUserStatusDTO updateUserStatusDTO);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @param currentUserId 当前用户ID
     */
    void deleteUser(Integer userId, Integer currentUserId);

    // 3.6 为用户分配项目
    Integer assignUserToProject(Integer userId, AssignUserProjectDTO dto, Integer operatorId);

    // 3.7 移除用户项目分配
    void removeUserFromProject(Integer userId, Integer projectId, Integer operatorId);

    // 3.8 分页获取用户项目列表
    PaginationResultVO<UserProjectItemDTO> findUserProjects(UserProjectsQueryDTO queryDTO);
}
