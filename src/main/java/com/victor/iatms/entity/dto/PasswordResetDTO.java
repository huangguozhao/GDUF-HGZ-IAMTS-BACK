package com.victor.iatms.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 执行密码重置请求DTO
 */
@Data
public class PasswordResetDTO {

    /**
     * 请求重置时使用的邮箱或手机号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 从邮箱或短信中收到的验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 8, message = "验证码长度必须在4-8位之间")
    private String verificationCode;

    /**
     * 新的密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 50, message = "密码长度必须在8-50位之间")
    private String newPassword;
}
