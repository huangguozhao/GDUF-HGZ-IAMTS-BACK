package com.victor.iatms.utils;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.mappers.TestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 测试用例编码生成工具类
 */
@Component
public class TestCaseCodeGenerator {

    @Autowired
    private TestCaseMapper testCaseMapper;

    /**
     * 生成测试用例编码
     * @param apiId 接口ID
     * @return 生成的用例编码
     */
    public String generateTestCaseCode(Integer apiId) {
        // 查询该接口下已存在的用例数量
        Long count = testCaseMapper.countByApiId(apiId);
        
        // 生成序列号（从1开始）
        int sequenceNumber = (count != null ? count.intValue() : 0) + 1;
        
        // 生成编码：TC-API-{apiId}-{序列号}
        return Constants.TEST_CASE_CODE_PREFIX + apiId + Constants.TEST_CASE_CODE_SEPARATOR + String.format("%03d", sequenceNumber);
    }

    /**
     * 验证用例编码是否在指定接口下唯一
     * @param apiId 接口ID
     * @param caseCode 用例编码
     * @return 是否唯一
     */
    public boolean isTestCaseCodeUnique(Integer apiId, String caseCode) {
        return testCaseMapper.countByApiIdAndCaseCode(apiId, caseCode) == 0;
    }
}
