package com.victor.iatms.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static com.victor.iatms.entity.constants.Constants.*;
import static com.victor.iatms.entity.constants.Constants.CODE_EXPIRE_TIME;
import static com.victor.iatms.entity.constants.Constants.RESET_CODE_PREFIX;
import static com.victor.iatms.entity.constants.Constants.RESET_FREQUENCY_PREFIX;


@Component
public class RedisComponet {
    @Lazy
    @Autowired
    private RedisUtils<String> redisUtils;
    /**
     * 获取token信息
     *
     * @param token
     * @return
     */
//    public TokenUserInfoDto getTokenUserInfoDto(String token) {
//        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
//        return tokenUserInfoDto;
//    }

//    public TokenUserInfoDto getTokenUserInfoDtoByUserId(String userId) {
//        String token = (String) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN_USERID + userId);
//        return getTokenUserInfoDto(token);
//    }
    
//    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto) {
//        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken(), tokenUserInfoDto, Constants.REDIS_KEY_EXPIRES_DAY * 2);
//        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getUserId(), tokenUserInfoDto.getToken(), Constants.REDIS_KEY_EXPIRES_DAY * 2);
//    }

    /**
     * 清除token信息
     *
     * @param userId
     */
    public void cleanUserTokenByUserId(String userId) {
//        String token = (String) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN_USERID + userId);
//        if (!StringTools.isEmpty(token)) {
//            redisUtils.delete(Constants.REDIS_KEY_WS_TOKEN + token);
//        }
    }

    /**
     * 检查发送频率限制
     * @param account 账号（邮箱或手机号）
     * @return true表示可以发送，false表示频率限制
     */
    public boolean checkFrequencyLimit(String account) {
        String frequencyKey = RESET_FREQUENCY_PREFIX + account;
        return redisUtils.get(frequencyKey) == null;
    }

    /**
     * 设置发送频率限制
     * @param account 账号（邮箱或手机号）
     */
    public void setFrequencyLimit(String account) {
        String frequencyKey = RESET_FREQUENCY_PREFIX + account;
        redisUtils.setex(frequencyKey, "1", FREQUENCY_LIMIT_TIME);
    }

    /**
     * 存储验证码
     * @param resetTokenId 重置令牌ID
     * @param verificationCode 验证码
     */
    public void storeVerificationCode(String resetTokenId, String verificationCode) {
        String codeKey = RESET_CODE_PREFIX + resetTokenId;
        redisUtils.setex(codeKey, verificationCode, CODE_EXPIRE_TIME);
    }

    /**
     * 存储验证码（按账号）
     * @param account 账号
     * @param verificationCode 验证码
     */
    public void storeVerificationCodeByAccount(String account, String verificationCode) {
        String codeKey = RESET_CODE_PREFIX + account;
        redisUtils.setex(codeKey, verificationCode, CODE_EXPIRE_TIME);
    }

    /**
     * 获取验证码
     * @param resetTokenId 重置令牌ID
     * @return 验证码
     */
    public String getVerificationCode(String resetTokenId) {
        String codeKey = RESET_CODE_PREFIX + resetTokenId;
        Object code = redisUtils.get(codeKey);
        return code != null ? code.toString() : null;
    }

    /**
     * 获取验证码（按账号）
     * @param account 账号
     * @return 验证码
     */
    public String getVerificationCodeByAccount(String account) {
        String codeKey = RESET_CODE_PREFIX + account;
        Object code = redisUtils.get(codeKey);
        return code != null ? code.toString() : null;
    }

    /**
     * 删除验证码
     * @param resetTokenId 重置令牌ID
     */
    public void deleteVerificationCode(String resetTokenId) {
        String codeKey = RESET_CODE_PREFIX + resetTokenId;
        redisUtils.delete(codeKey);
    }

    /**
     * 删除验证码（按账号）
     * @param account 账号
     */
    public void deleteVerificationCodeByAccount(String account) {
        String codeKey = RESET_CODE_PREFIX + account;
        redisUtils.delete(codeKey);
    }

    /**
     * 删除频率限制
     * @param account 账号（邮箱或手机号）
     */
    public void deleteFrequencyLimit(String account) {
        String frequencyKey = RESET_FREQUENCY_PREFIX + account;
        redisUtils.delete(frequencyKey);
    }

    /**
     * 清理重置相关的所有Redis数据
     * @param resetTokenId 重置令牌ID
     * @param account 账号（邮箱或手机号）
     */
    public void cleanupResetData(String resetTokenId, String account) {
        deleteVerificationCode(resetTokenId);
        deleteFrequencyLimit(account);
    }
    
    /**
     * 设置字符串值
     * @param key 键
     * @param value 值
     * @param expireSeconds 过期时间（秒）
     */
    public void setString(String key, String value, int expireSeconds) {
        redisUtils.setex(key, value, expireSeconds);
    }
    
    /**
     * 获取字符串值
     * @param key 键
     * @return 值
     */
    public String getString(String key) {
        Object value = redisUtils.get(key);
        return value != null ? value.toString() : null;
    }

}
