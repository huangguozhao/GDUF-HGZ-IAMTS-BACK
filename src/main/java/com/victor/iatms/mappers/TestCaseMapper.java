package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
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
     * 根据测试用例ID获取创建后的测试用例详情
     * @param caseId 测试用例ID
     * @return 测试用例详情
     */
    CreateTestCaseResponseDTO selectCreateTestCaseDetailById(@Param("caseId") Integer caseId);
}