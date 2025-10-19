package com.victor.iatms.service;

import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.ExportResultDTO;
import com.victor.iatms.entity.dto.ImportResultDTO;
import com.victor.iatms.entity.dto.PageResultDTO;
import com.victor.iatms.entity.dto.TestCaseDTO;
import com.victor.iatms.entity.dto.TestCaseResponseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;
import com.victor.iatms.entity.query.TestCaseQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 测试用例服务接口
 */
public interface TestCaseService {

    /**
     * 分页查询测试用例列表
     * @param query 查询参数
     * @return 分页结果
     */
    PageResultDTO<TestCaseDTO> getTestCaseList(TestCaseQuery query);

    /**
     * 创建测试用例
     * @param apiId 接口ID
     * @param createRequest 创建请求
     * @param userId 当前用户ID
     * @return 创建的测试用例信息
     */
    TestCaseResponseDTO createTestCase(Integer apiId, CreateTestCaseDTO createRequest, Integer userId);

    /**
     * 更新测试用例
     * @param apiId 接口ID
     * @param caseId 用例ID
     * @param updateRequest 更新请求
     * @param userId 当前用户ID
     * @return 更新后的测试用例信息
     */
    UpdateTestCaseResponseDTO updateTestCase(Integer apiId, Integer caseId, UpdateTestCaseDTO updateRequest, Integer userId);

    /**
     * 删除测试用例
     * @param apiId 接口ID
     * @param caseId 用例ID
     * @param userId 当前用户ID
     * @return 删除结果
     */
    boolean deleteTestCase(Integer apiId, Integer caseId, Integer userId);

    /**
     * 导入测试用例
     * @param apiId 接口ID
     * @param file 上传的文件
     * @param importMode 导入模式
     * @param conflictStrategy 冲突处理策略
     * @param templateType 模板类型
     * @param userId 当前用户ID
     * @return 导入结果
     */
    ImportResultDTO importTestCases(Integer apiId, MultipartFile file, String importMode, 
                                   String conflictStrategy, String templateType, Integer userId);

    /**
     * 导出测试用例
     * @param apiId 接口ID
     * @param format 导出格式
     * @param includeDisabled 是否包含已禁用的用例
     * @param includeTemplates 是否包含模板用例
     * @param fields 指定导出的字段
     * @param filename 导出文件的名称
     * @param userId 当前用户ID
     * @return 文件字节数组和MIME类型
     */
    ExportResultDTO exportTestCases(Integer apiId, String format, Boolean includeDisabled, 
                                   Boolean includeTemplates, List<String> fields, String filename, Integer userId);
}
