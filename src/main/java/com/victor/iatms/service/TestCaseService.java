package com.victor.iatms.service;

import com.victor.iatms.entity.dto.AddTestCaseDTO;
import com.victor.iatms.entity.dto.AddTestCaseResponseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;
import com.victor.iatms.entity.dto.TestCaseListQueryDTO;
import com.victor.iatms.entity.dto.TestCaseListResponseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseDTO;
import com.victor.iatms.entity.dto.UpdateTestCaseResponseDTO;

/**
 * 测试用例服务接口
 */
public interface TestCaseService {

    /**
     * 创建测试用例
     * @param createTestCaseDTO 创建测试用例请求DTO
     * @param currentUserId 当前操作用户ID
     * @return 创建的测试用例信息
     */
    CreateTestCaseResponseDTO createTestCase(CreateTestCaseDTO createTestCaseDTO, Integer currentUserId);
    
    /**
     * 更新测试用例
     * @param caseId 测试用例ID
     * @param updateTestCaseDTO 更新测试用例请求DTO
     * @param currentUserId 当前操作用户ID
     * @return 更新后的测试用例信息
     */
    UpdateTestCaseResponseDTO updateTestCase(Integer caseId, UpdateTestCaseDTO updateTestCaseDTO, Integer currentUserId);
    
    /**
     * 添加测试用例
     * @param addTestCaseDTO 添加测试用例请求DTO
     * @param currentUserId 当前操作用户ID
     * @return 创建的测试用例信息
     */
    AddTestCaseResponseDTO addTestCase(AddTestCaseDTO addTestCaseDTO, Integer currentUserId);
    
    /**
     * 删除测试用例
     * @param caseId 测试用例ID
     * @param currentUserId 当前操作用户ID
     */
    void deleteTestCase(Integer caseId, Integer currentUserId);
    
    /**
     * 分页获取测试用例列表
     * @param queryDTO 查询参数
     * @param currentUserId 当前操作用户ID
     * @return 分页的测试用例列表
     */
    TestCaseListResponseDTO getTestCaseList(TestCaseListQueryDTO queryDTO, Integer currentUserId);
}