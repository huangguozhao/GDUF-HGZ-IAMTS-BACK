package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.PendingTaskDTO;
import com.victor.iatms.entity.po.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务Mapper接口
 */
@Mapper
public interface TaskMapper {

    /**
     * 插入任务
     * @param task 任务信息
     * @return 影响行数
     */
    int insert(Task task);

    /**
     * 更新任务
     * @param task 任务信息
     * @return 影响行数
     */
    int update(Task task);

    /**
     * 根据ID查询任务
     * @param taskId 任务ID
     * @return 任务信息
     */
    Task selectById(@Param("taskId") Long taskId);

    /**
     * 根据ID删除任务（软删除）
     * @param taskId 任务ID
     * @return 影响行数
     */
    int deleteById(@Param("taskId") Long taskId);

    /**
     * 获取用户的待处理任务
     * @param userId 用户ID
     * @return 待处理任务列表
     */
    List<PendingTaskDTO> getUserPendingTasks(@Param("userId") Integer userId);

    /**
     * 获取所有待处理任务（管理员用）
     * @return 待处理任务列表
     */
    List<PendingTaskDTO> getAllPendingTasks();

    /**
     * 统计用户的待处理任务数量
     * @param userId 用户ID
     * @return 待处理任务数量
     */
    int countUserPendingTasks(@Param("userId") Integer userId);

    /**
     * 根据项目ID查询任务列表
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<Task> selectByProjectId(@Param("projectId") Integer projectId);

    /**
     * 根据执行记录ID查询任务
     * @param executionId 执行记录ID
     * @return 任务信息
     */
    Task selectByExecutionId(@Param("executionId") Long executionId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("taskId") Long taskId, @Param("status") String status);

    /**
     * 更新任务进度
     * @param taskId 任务ID
     * @param progress 进度
     * @return 影响行数
     */
    int updateProgress(@Param("taskId") Long taskId, @Param("progress") Integer progress);
}

