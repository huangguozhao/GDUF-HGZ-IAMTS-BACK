package com.victor.iatms.entity.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 密码重置请求DTO
 */
@Data
public class PasswordResetRequestDTO {
    
    /**
     * 用户注册的邮箱或手机号
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 发送验证码的渠道，可选值: email, sms
     */
    @NotBlank(message = "发送渠道不能为空")
    @Pattern(regexp = "^(email|sms)$", message = "发送渠道只能是email或sms")
    private String channel;
}
