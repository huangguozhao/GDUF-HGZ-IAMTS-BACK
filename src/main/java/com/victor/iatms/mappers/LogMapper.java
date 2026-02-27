package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志Mapper
 */
@Mapper
public interface LogMapper {

    /**
     * 插入日志
     * @param log 日志对象
     * @return 影响行数
     */
    int insert(Log log);

    /**
     * 获取用户最近活动
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 日志列表
     */
    List<Log> getUserRecentActivity(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * 获取所有用户的最近活动（用于管理员）
     * @param limit 限制数量
     * @return 日志列表
     */
    List<Log> getRecentActivity(@Param("limit") Integer limit);
}

