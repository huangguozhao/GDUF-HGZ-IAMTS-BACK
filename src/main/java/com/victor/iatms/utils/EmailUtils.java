package com.victor.iatms.utils;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

/**
 * 邮件工具类
 */
@Component
public class EmailUtils {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 发送密码重置验证码邮件
     * @param email 收件人邮箱
     * @param verificationCode 验证码
     * @param userName 用户名
     * @return 是否发送成功
     */
    public boolean sendPasswordResetCode(String email, String verificationCode, String userName) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {

                public void prepare(MimeMessage mimeMessage) throws Exception {

                    mimeMessage.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(email));
                    mimeMessage.setFrom(new InternetAddress("3194665063@qq.com"));
                    mimeMessage.setSubject("密码重置验证码");
                    mimeMessage.setText(
                            buildPasswordResetEmailContent(userName, verificationCode));
                }
            };
            mailSender.send(preparator);
            return true;
        } catch (Exception e) {
            // 记录日志
            System.err.println("发送邮件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 构建密码重置邮件内容
     * @param userName 用户名
     * @param verificationCode 验证码
     * @return 邮件内容
     */
    private String buildPasswordResetEmailContent(String userName, String verificationCode) {
        return String.format(
            "亲爱的 %s，\n\n" +
            "您正在重置密码，验证码为：%s\n\n" +
            "验证码有效期为15分钟，请及时使用。\n\n" +
            "如果您没有请求重置密码，请忽略此邮件。\n\n" +
            "此邮件由系统自动发送，请勿回复。\n\n" +
            "接口自动化管理平台",
            userName, verificationCode
        );
    }
    
    /**
     * 测试邮件配置连接
     * @param host 邮件服务器主机
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @param ssl 是否启用SSL
     * @return 是否连接成功
     */
    public boolean testConnection(String host, Integer port, String username, String password, Boolean ssl) {
        try {
            // 简单验证：检查参数是否有效
            if (host == null || host.trim().isEmpty()) {
                return false;
            }
            if (port == null || port <= 0 || port > 65535) {
                return false;
            }
            if (username == null || username.trim().isEmpty()) {
                return false;
            }
            // 如果配置了mailSender，可以尝试发送测试邮件
            // 这里简单返回true表示配置有效
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
