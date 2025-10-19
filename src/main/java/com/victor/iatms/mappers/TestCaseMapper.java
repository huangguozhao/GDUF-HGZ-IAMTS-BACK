package com.victor.iatms.mappers;

import com.victor.iatms.entity.dto.ExportQueryDTO;
import com.victor.iatms.entity.dto.ExportTestCaseDTO;
import com.victor.iatms.entity.dto.TestCaseDTO;
import com.victor.iatms.entity.po.TestCase;
import com.victor.iatms.entity.query.TestCaseQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 测试用例数据访问层
 */
@Mapper
public interface TestCaseMapper {

    /**
     * 分页查询测试用例列表
     * @param query 查询参数
     * @return 测试用例列表
     */
    List<TestCaseDTO> findTestCaseList(@Param("query") TestCaseQuery query);

    /**
     * 统计测试用例总数
     * @param query 查询参数
     * @return 总数
     */
    Long countTestCases(@Param("query") TestCaseQuery query);

    /**
     * 插入测试用例
     * @param testCase 测试用例实体
     * @return 影响行数
     */
    int insertTestCase(TestCase testCase);

    /**
     * 根据接口ID统计用例数量
     * @param apiId 接口ID
     * @return 用例数量
     */
    Long countByApiId(@Param("apiId") Integer apiId);

    /**
     * 根据接口ID和用例编码统计数量
     * @param apiId 接口ID
     * @param caseCode 用例编码
     * @return 数量
     */
    Long countByApiIdAndCaseCode(@Param("apiId") Integer apiId, @Param("caseCode") String caseCode);

    /**
     * 根据用例ID查询测试用例
     * @param caseId 用例ID
     * @return 测试用例
     */
    TestCase findById(@Param("caseId") Integer caseId);

    /**
     * 根据用例ID和接口ID查询测试用例
     * @param caseId 用例ID
     * @param apiId 接口ID
     * @return 测试用例
     */
    TestCase findByIdAndApiId(@Param("caseId") Integer caseId, @Param("apiId") Integer apiId);

    /**
     * 更新测试用例
     * @param testCase 测试用例实体
     * @return 影响行数
     */
    int updateTestCase(TestCase testCase);

    /**
     * 根据接口ID和用例编码统计数量（排除指定用例ID）
     * @param apiId 接口ID
     * @param caseCode 用例编码
     * @param excludeCaseId 排除的用例ID
     * @return 数量
     */
    Long countByApiIdAndCaseCodeExcludeId(@Param("apiId") Integer apiId, @Param("caseCode") String caseCode, @Param("excludeCaseId") Integer excludeCaseId);

    /**
     * 软删除测试用例
     * @param caseId 用例ID
     * @param deletedBy 删除人ID
     * @return 影响行数
     */
    int deleteTestCase(@Param("caseId") Integer caseId, @Param("deletedBy") Integer deletedBy);

    /**
     * 检查用例是否已被删除
     * @param caseId 用例ID
     * @return 是否已被删除
     */
    boolean isTestCaseDeleted(@Param("caseId") Integer caseId);

    /**
     * 批量插入测试用例
     * @param testCases 测试用例列表
     * @return 影响行数
     */
    int batchInsertTestCases(@Param("testCases") List<TestCase> testCases);

    /**
     * 根据接口ID和用例编码列表查询已存在的用例
     * @param apiId 接口ID
     * @param caseCodes 用例编码列表
     * @return 已存在的用例编码列表
     */
    List<String> findExistingCaseCodes(@Param("apiId") Integer apiId, @Param("caseCodes") List<String> caseCodes);

    /**
     * 查询用于导出的测试用例列表
     * @param query 导出查询参数
     * @return 测试用例列表
     */
    List<ExportTestCaseDTO> findTestCasesForExport(ExportQueryDTO query);
}
