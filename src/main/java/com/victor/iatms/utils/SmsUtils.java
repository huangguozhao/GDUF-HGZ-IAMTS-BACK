package com.victor.iatms.utils;

import org.springframework.stereotype.Component;

/**
 * 短信工具类（模拟实现）
 */
@Component
public class SmsUtils {
    
    /**
     * 发送密码重置验证码短信
     * @param phone 手机号
     * @param verificationCode 验证码
     * @return 是否发送成功
     */
    public boolean sendPasswordResetCode(String phone, String verificationCode) {
        try {
            // 模拟短信发送过程
            System.out.println("模拟发送短信到: " + phone + ", 验证码: " + verificationCode);
            
            // 在实际项目中，这里应该调用真实的短信服务商API
            // 例如：阿里云短信、腾讯云短信等
            
            // 模拟发送延迟
            Thread.sleep(100);
            
            return true;
        } catch (Exception e) {
            System.err.println("发送短信失败: " + e.getMessage());
            return false;
        }
    }
}
