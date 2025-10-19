package com.victor.iatms.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 密码工具类
 */
@Component
public class PasswordUtils {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 密码强度正则表达式：至少包含大小写字母、数字和特殊字符，长度至少8位
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * 验证密码强度
     * @param password 密码
     * @return true表示符合要求，false表示不符合
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 获取密码强度要求描述
     * @return 密码强度要求描述
     */
    public String getPasswordRequirementDescription() {
        return "密码必须包含大小写字母、数字和特殊字符，且长度至少为8位";
    }

    /**
     * 验证密码并返回详细错误信息
     * @param password 密码
     * @return 验证结果描述，null表示验证通过
     */
    public String validatePasswordWithDetail(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }

        if (password.length() < 8) {
            return "密码长度至少为8位";
        }

        if (!password.matches(".*[a-z].*")) {
            return "密码必须包含小写字母";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "密码必须包含大写字母";
        }

        if (!password.matches(".*\\d.*")) {
            return "密码必须包含数字";
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            return "密码必须包含特殊字符(@$!%*?&)";
        }

        return null; // 验证通过
    }

}
