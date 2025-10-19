package com.victor.iatms.service;

import com.victor.iatms.entity.dto.CreateTestCaseDTO;
import com.victor.iatms.entity.dto.CreateTestCaseResponseDTO;

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
}