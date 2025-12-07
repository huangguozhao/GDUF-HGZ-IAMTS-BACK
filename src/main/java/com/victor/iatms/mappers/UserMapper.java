package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.User;
import com.victor.iatms.entity.dto.UserQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    List<User> selectUsers(UserQueryDTO queryDTO);
    
    /**
     * 查询用户总数
     *
     * @param queryDTO 查询条件
     * @return 用户总数
     */
    long countUsers(UserQueryDTO queryDTO);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户
     */
    User selectUserByEmail(@Param("email") String email);

    /**
     * 插入用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int insertUser(User user);
    
    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    User selectUserById(@Param("userId") Integer userId);
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int updateUser(User user);
    
    /**
     * 软删除用户
     *
     * @param userId    用户ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int softDeleteUser(@Param("userId") Integer userId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("userId") Integer userId, @Param("lastLoginTime") java.time.LocalDateTime lastLoginTime);

    /**
     * 根据邮箱或手机号查询用户
     *
     * @param account 邮箱或手机号
     * @return 用户
     */
    User findByEmailOrPhone(@Param("account") String account);

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param password 新密码
     * @return 影响行数
     */
    int updatePassword(@Param("userId") Integer userId, @Param("password") String password);

    /**
     * 根据ID查询用户（别名方法，兼容旧代码）
     */
    User findById(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID查询执行人信息（供执行记录模块使用）
     */
    com.victor.iatms.entity.dto.ExecutorInfoDTO findExecutorInfoById(@Param("userId") Integer userId);
}
