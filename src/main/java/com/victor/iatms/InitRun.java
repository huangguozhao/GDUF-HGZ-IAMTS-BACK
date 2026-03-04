package com.victor.iatms;

import com.victor.iatms.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化运行类
 */
@Slf4j
public class InitRun {
    public static void main(String[] args) {
        PasswordUtils passwordUtils = new PasswordUtils();
        String s = passwordUtils.encodePassword("123456");
        log.info("生成的密码哈希: {}", s);
    }
}
