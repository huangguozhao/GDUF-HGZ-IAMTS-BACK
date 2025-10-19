package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据邮箱查询用户信息
     * @param email 用户邮箱
     * @return 用户信息
     */
    User findByEmail(@Param("email") String email);
    
    /**
     * 根据邮箱或手机号查询用户信息
     * @param account 邮箱或手机号
     * @return 用户信息
     */
    User findByEmailOrPhone(@Param("account") String account);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("userId") Integer userId, @Param("lastLoginTime") java.time.LocalDateTime lastLoginTime);

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码（已加密）
     * @return 影响行数
     */
    int updatePassword(@Param("userId") Integer userId, @Param("newPassword") String newPassword);

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User findById(@Param("userId") Integer userId);
}
