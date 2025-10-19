package com.victor.iatms.service.impl;

import com.victor.iatms.entity.dto.LoginRequestDTO;
import com.victor.iatms.entity.dto.LoginResponseDTO;
import com.victor.iatms.entity.dto.PasswordResetDTO;
import com.victor.iatms.entity.dto.PasswordResetRequestDTO;
import com.victor.iatms.entity.dto.UserInfoDTO;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.redis.RedisComponet;
import com.victor.iatms.service.UserService;
import com.victor.iatms.utils.EmailUtils;
import com.victor.iatms.utils.JwtUtils;
import com.victor.iatms.utils.PasswordUtils;
import com.victor.iatms.utils.SmsUtils;
import com.victor.iatms.utils.VerificationCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
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
        // 根据邮箱查询用户
        User user = userMapper.findByEmail(loginRequest.getEmail());
        
        // 用户不存在
        if (user == null) {
            throw new RuntimeException("邮箱或密码错误");
        }
        
        // 验证密码
        if (!passwordUtils.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }
        
        // 检查用户状态
        if (!"active".equals(user.getStatus())) {
            if ("pending".equals(user.getStatus())) {
                throw new RuntimeException("账户待审核，请联系管理员");
            } else if ("inactive".equals(user.getStatus())) {
                throw new RuntimeException("账户已被禁用，请联系管理员");
            }
        }
        
        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getUserId(), LocalDateTime.now());
        
        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUserId(), user.getEmail());
        
        // 构建响应数据
        LoginResponseDTO response = new LoginResponseDTO();
        LoginResponseDTO.UserInfo userInfo = new LoginResponseDTO.UserInfo();
        
        userInfo.setUserId(user.getUserId());
        userInfo.setName(user.getName());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setPhone(user.getPhone());
        userInfo.setDepartmentId(user.getDepartmentId());
        userInfo.setEmployeeId(user.getEmployeeId());
        userInfo.setPosition(user.getPosition());
        userInfo.setDescription(user.getDescription());
        userInfo.setStatus(user.getStatus());
        userInfo.setLastLoginTime(LocalDateTime.now());
        
        response.setUser(userInfo);
        response.setToken(token);
        
        return response;
    }
    
    @Override
    public String requestPasswordReset(PasswordResetRequestDTO request) {
        // 1. 查询用户是否存在
        User user = userMapper.findByEmailOrPhone(request.getAccount());
        if (user == null) {
            throw new RuntimeException("该邮箱/手机号未注册");
        }
        
        // 2. 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账户未激活，无法重置密码");
        }
        
        // 3. 检查发送频率限制
        if (!redisComponet.checkFrequencyLimit(request.getAccount())) {
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }
        
        // 4. 生成验证码和重置令牌
        String verificationCode;
        String resetTokenId = verificationCodeUtils.generateResetTokenId();
        
        if ("email".equals(request.getChannel())) {
            verificationCode = verificationCodeUtils.generateEmailCode();
        } else if ("sms".equals(request.getChannel())) {
            verificationCode = verificationCodeUtils.generateSmsCode();
        } else {
            throw new RuntimeException("不支持的验证码发送渠道");
        }
        
        // 5. 存储验证码到Redis（按账号存储）
        redisComponet.storeVerificationCodeByAccount(request.getAccount(), verificationCode);
        
        // 6. 设置发送频率限制
        redisComponet.setFrequencyLimit(request.getAccount());
        
        // 7. 发送验证码
        boolean sendSuccess = false;
        if ("email".equals(request.getChannel())) {
            sendSuccess = emailUtils.sendPasswordResetCode(user.getEmail(), verificationCode, user.getName());
        } else if ("sms".equals(request.getChannel())) {
            sendSuccess = smsUtils.sendPasswordResetCode(user.getPhone(), verificationCode);
        }
        
        if (!sendSuccess) {
            // 发送失败，清理Redis中的数据
            redisComponet.deleteVerificationCodeByAccount(request.getAccount());
            redisComponet.deleteFrequencyLimit(request.getAccount());
            throw new RuntimeException("验证码发送失败，请稍后重试");
        }
        
        return resetTokenId;
    }

    @Override
    public boolean executePasswordReset(PasswordResetDTO request) {
        // 1. 查询用户是否存在
        User user = userMapper.findByEmailOrPhone(request.getAccount());
        if (user == null) {
            throw new RuntimeException("该邮箱/手机号未注册");
        }

        // 2. 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账户未激活，无法重置密码");
        }

        // 3. 验证新密码强度
        String passwordValidationError = passwordUtils.validatePasswordWithDetail(request.getNewPassword());
        if (passwordValidationError != null) {
            throw new RuntimeException(passwordValidationError);
        }

        // 4. 验证验证码
        String storedCode = redisComponet.getVerificationCodeByAccount(request.getAccount());
        
        if (storedCode == null || !storedCode.equals(request.getVerificationCode())) {
            throw new RuntimeException("验证码错误或已失效");
        }

        // 5. 加密新密码
        String encryptedPassword = passwordUtils.encodePassword(request.getNewPassword());

        // 6. 更新用户密码
        int updateResult = userMapper.updatePassword(user.getUserId(), encryptedPassword);
        if (updateResult <= 0) {
            throw new RuntimeException("密码更新失败");
        }

        // 7. 使验证码失效（删除Redis中的验证码）
        redisComponet.deleteVerificationCodeByAccount(request.getAccount());

        return true;
    }

    @Override
    public UserInfoDTO getCurrentUserInfo(Integer userId) {
        // 1. 根据用户ID查询用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查用户状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("用户状态异常，无法获取信息");
        }

        // 3. 构建用户信息DTO
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setName(user.getName());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setPosition(user.getPosition());

        return userInfo;
    }

}
