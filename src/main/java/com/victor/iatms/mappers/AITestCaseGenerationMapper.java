package com.victor.iatms.mappers;

import com.victor.iatms.entity.po.AITestCaseGeneration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AITestCaseGenerationMapper {

    void insert(AITestCaseGeneration generation);

    AITestCaseGeneration findById(@Param("generationId") Long generationId);

    void updateStatus(@Param("generationId") Long generationId, 
                      @Param("status") String status,
                      @Param("generatedCases") String generatedCases,
                      @Param("caseCount") Integer caseCount,
                      @Param("completedAt") java.time.LocalDateTime completedAt,
                      @Param("errorMessage") String errorMessage);

    void updateTokenUsage(@Param("generationId") Long generationId,
                          @Param("promptTokens") Integer promptTokens,
                          @Param("completionTokens") Integer completionTokens);
}
