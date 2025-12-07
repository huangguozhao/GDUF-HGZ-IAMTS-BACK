package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.ProjectMember;
import com.victor.iatms.entity.dto.UserProjectsQueryDTO;
import com.victor.iatms.entity.dto.UserProjectItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMemberMapper {


    /**
     * 
     * @param projectId
     * @param userId
     * @return
     */
    ProjectMember findByProjectAndUser(@Param("projectId") Integer projectId,
                                       @Param("userId") Integer userId);

    int insert(ProjectMember member);

    int softRemove(@Param("projectId") Integer projectId,
                   @Param("userId") Integer userId,
                   @Param("updatedBy") Integer updatedBy);

    Long countUserProjects(@Param("userId") Integer userId,
                           @Param("status") String status,
                           @Param("projectRole") String projectRole);

    List<UserProjectItemDTO> selectUserProjects(@Param("userId") Integer userId,
                                                @Param("status") String status,
                                                @Param("projectRole") String projectRole,
                                                @Param("offset") Integer offset,
                                                @Param("limit") Integer limit);

    int updateMember(com.victor.iatms.entity.po.ProjectMember member);
}
