package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
import com.victor.iatms.entity.dto.TestCaseItemDTO;
import com.victor.iatms.entity.dto.TestCaseListQueryDTO;
import com.victor.iatms.entity.dto.TestCaseSummaryDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;
import com.victor.iatms.entity.po.TestCase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 测试用例Mapper接口
 */
@Mapper
public interface TestCaseMapper {

    /**
     * 插入测试用例
     * @param testCase 测试用例信息
     * @return 影响行数
     */
    int insert(TestCase testCase);

    /**
     * 根据ID查询测试用例
     * @param caseId 测试用例ID
     * @return 测试用例信息
     */
    TestCase selectById(@Param("caseId") Integer caseId);

    /**
     * 检查用例编码在指定接口下是否存在
     * @param caseCode 用例编码
     * @param apiId 接口ID
     * @return 存在数量
     */
    int checkCaseCodeExists(@Param("caseCode") String caseCode, @Param("apiId") Integer apiId);

    /**
     * 统计指定接口下的测试用例数量
     * @param apiId 接口ID
     * @return 测试用例数量
     */
    int countByApiId(@Param("apiId") Integer apiId);
    
    /**
     * 统计指定接口下的测试用例数量（未删除）
     * @param apiId 接口ID
     * @return 测试用例数量
     */
    Integer countTestCasesByApiId(@Param("apiId") Integer apiId);

    /**
     * 根据测试用例ID获取创建后的测试用例详情
     * @param caseId 测试用例ID
     * @return 测试用例详情
     */
    CreateTestCaseResponseDTO selectCreateTestCaseDetailById(@Param("caseId") Integer caseId);
    
    /**
     * 更新测试用例信息
     * @param testCase 测试用例信息
     * @return 影响行数
     */
    int updateById(TestCase testCase);
    
    /**
     * 检查用例编码在指定接口下是否存在（排除指定用例）
     * @param caseCode 用例编码
     * @param apiId 接口ID
     * @param excludeCaseId 排除的用例ID
     * @return 存在数量
     */
    int checkCaseCodeExistsExcludeSelf(@Param("caseCode") String caseCode, 
                                      @Param("apiId") Integer apiId, 
                                      @Param("excludeCaseId") Integer excludeCaseId);
    
    /**
     * 根据测试用例ID获取更新后的测试用例详情
     * @param caseId 测试用例ID
     * @return 测试用例详情
     */
    UpdateTestCaseResponseDTO selectUpdateTestCaseDetailById(@Param("caseId") Integer caseId);
    
    /**
     * 软删除测试用例
     * @param caseId 测试用例ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteById(@Param("caseId") Integer caseId, @Param("deletedBy") Integer deletedBy);
    
    /**
     * 分页查询测试用例列表
     * @param queryDTO 查询参数
     * @param currentUserId 当前用户ID
     * @return 测试用例列表
     */
    java.util.List<TestCaseItemDTO> selectTestCaseList(@Param("queryDTO") TestCaseListQueryDTO queryDTO, 
                                                      @Param("currentUserId") Integer currentUserId);
    
    /**
     * 统计测试用例总数
     * @param queryDTO 查询参数
     * @param currentUserId 当前用户ID
     * @return 总数
     */
    Long countTestCaseList(@Param("queryDTO") TestCaseListQueryDTO queryDTO, 
                           @Param("currentUserId") Integer currentUserId);
    
    /**
     * 查询测试用例统计摘要
     * @param queryDTO 查询参数
     * @param currentUserId 当前用户ID
     * @return 统计摘要
     */
    TestCaseSummaryDTO selectTestCaseSummary(@Param("queryDTO") TestCaseListQueryDTO queryDTO, 
                                            @Param("currentUserId") Integer currentUserId);
}