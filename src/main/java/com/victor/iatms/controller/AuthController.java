package com.victor.iatms.controller;

import com.victor.iatms.annotation.GlobalInterceptor;
import com.victor.iatms.entity.dto.LoginRequestDTO;
import com.victor.iatms.entity.dto.LoginResponseDTO;
import com.victor.iatms.entity.dto.PasswordResetDTO;
import com.victor.iatms.entity.dto.PasswordResetRequestDTO;
import com.victor.iatms.entity.dto.UserInfoDTO;
import com.victor.iatms.entity.vo.ResponseVO;
import com.victor.iatms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseVO<LoginResponseDTO> login(@Validated @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = userService.login(loginRequest);
            return ResponseVO.success("登录成功", response);
        } catch (RuntimeException e) {
            // 根据异常消息判断错误类型
            String errorMsg = e.getMessage();
            if ("邮箱或密码错误".equals(errorMsg)) {
                return ResponseVO.authError(errorMsg);
            } else if (errorMsg.contains("待审核") || errorMsg.contains("禁用")) {
                return ResponseVO.businessError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }
    
    /**
     * 密码重置请求
     * @param request 密码重置请求
     * @return 响应结果
     */
    @PostMapping("/password/reset-request")
    public ResponseVO<Map<String, String>> resetRequest(@Validated @RequestBody PasswordResetRequestDTO request) {
        try {
            String resetTokenId = userService.requestPasswordReset(request);
            
            Map<String, String> data = new HashMap<>();
            data.put("reset_token_id", resetTokenId);
            
            return ResponseVO.success("重置验证码已发送，请查收", data);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("该邮箱/手机号未注册".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("账户未激活，无法重置密码".equals(errorMsg) || 
                      "请求过于频繁，请稍后再试".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("不支持的验证码发送渠道".equals(errorMsg)) {
                return ResponseVO.paramError(errorMsg);
            } else if (errorMsg.contains("验证码发送失败")) {
                return ResponseVO.serverError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 执行密码重置
     * @param request 密码重置请求
     * @return 响应结果
     */
    @PostMapping("/password/reset")
    public ResponseVO<Void> executePasswordReset(@Validated @RequestBody PasswordResetDTO request) {
        try {
            boolean success = userService.executePasswordReset(request);
            if (success) {
                return ResponseVO.success("密码重置成功", null);
            } else {
                return ResponseVO.serverError("密码重置失败");
            }
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("该邮箱/手机号未注册".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("账户未激活，无法重置密码".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if ("验证码错误或已失效".equals(errorMsg)) {
                return ResponseVO.businessError(errorMsg);
            } else if (errorMsg.contains("密码必须包含") || errorMsg.contains("密码长度") || errorMsg.contains("密码不能为空")) {
                return ResponseVO.paramError(errorMsg);
            } else if ("密码更新失败".equals(errorMsg)) {
                return ResponseVO.serverError(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }

    /**
     * 获取当前用户信息
     * @param request HTTP请求对象
     * @return 用户信息
     */
    @GetMapping("/me")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<UserInfoDTO> getCurrentUserInfo(HttpServletRequest request) {
        try {
            // 从拦截器中获取用户ID
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseVO.authError("认证失败，请重新登录");
            }

            UserInfoDTO userInfo = userService.getCurrentUserInfo(userId);
            return ResponseVO.success("success", userInfo);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("用户不存在".equals(errorMsg)) {
                return ResponseVO.notFound(errorMsg);
            } else if ("用户状态异常，无法获取信息".equals(errorMsg)) {
                return ResponseVO.forbidden(errorMsg);
            } else {
                return ResponseVO.serverError("系统异常，请稍后重试");
            }
        } catch (Exception e) {
            return ResponseVO.serverError("系统异常，请稍后重试");
        }
    }
}
