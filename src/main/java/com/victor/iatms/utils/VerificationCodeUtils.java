package com.victor.iatms.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

/**
 * 验证码工具类
 */
@Component
public class VerificationCodeUtils {
    
    private static final String EMAIL_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SMS_CODE_CHARS = "0123456789";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * 生成邮箱验证码（6位字母数字组合）
     * @return 验证码
     */
    public String generateEmailCode() {
        return generateCode(EMAIL_CODE_CHARS, 6);
    }
    
    /**
     * 生成短信验证码（6位数字）
     * @return 验证码
     */
    public String generateSmsCode() {
        return generateCode(SMS_CODE_CHARS, 6);
    }
    
    /**
     * 生成验证码
     * @param chars 字符集
     * @param length 长度
     * @return 验证码
     */
    private String generateCode(String chars, int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    /**
     * 生成重置令牌ID
     * @return 令牌ID
     */
    public String generateResetTokenId() {
        return "req_" + System.currentTimeMillis() + "_" + generateCode(EMAIL_CODE_CHARS, 8);
    }
}
