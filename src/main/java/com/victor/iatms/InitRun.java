package com.victor.iatms;

import com.victor.iatms.utils.PasswordUtils;

/**
 * 初始化运行类
 */
public class InitRun {
    public static void main(String[] args) {
        PasswordUtils passwordUtils = new PasswordUtils();
        String s = passwordUtils.encodePassword("123456");
        System.out.println(s);
    }
}
