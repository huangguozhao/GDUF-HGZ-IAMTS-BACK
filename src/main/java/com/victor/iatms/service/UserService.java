package com.victor.iatms.service;

import com.victor.iatms.entity.dto.LoginRequestDTO;
import com.victor.iatms.entity.dto.LoginResponseDTO;
import com.victor.iatms.entity.dto.PasswordResetDTO;
import com.victor.iatms.entity.dto.PasswordResetRequestDTO;
import com.victor.iatms.entity.dto.UserInfoDTO;

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
}
