package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.po.Api;
import com.victor.iatms.mappers.ApiMapper;
import com.victor.iatms.mappers.TestCaseMapper;
import com.victor.iatms.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 接口服务实现类
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public void deleteApi(Integer apiId, Integer currentUserId) {
        // 参数校验
        validateDeleteApi(apiId);

        // 检查接口是否存在
        Api api = apiMapper.selectById(apiId);
        if (api == null) {
            throw new IllegalArgumentException("接口不存在");
        }

        // 检查接口是否已被删除
        if (api.getIsDeleted()) {
            throw new IllegalArgumentException("接口已被删除");
        }

        // 检查权限（需要接口管理权限）
        if (!hasApiManagePermission(api, currentUserId)) {
            throw new IllegalArgumentException("权限不足，无法删除接口");
        }

        // 检查是否为系统接口
        if (isSystemApi(api)) {
            throw new IllegalArgumentException("不能删除系统接口");
        }

        // 检查接口是否存在测试用例
        if (hasTestCases(apiId)) {
            throw new IllegalArgumentException("接口存在测试用例，无法删除");
        }

        // 检查接口是否存在前置条件
        if (hasPreconditions(apiId)) {
            throw new IllegalArgumentException("接口存在前置条件配置，无法删除");
        }

        // 检查接口是否正在被使用
        if (isApiInUse(apiId)) {
            throw new IllegalArgumentException("接口正在被测试计划使用，无法删除");
        }

        // 执行软删除
        int result = apiMapper.deleteById(apiId, currentUserId);
        if (result <= 0) {
            throw new RuntimeException("删除接口失败");
        }

        // TODO: 记录审计日志
        // auditLogService.logApiDelete(apiId, currentUserId, api.getName(), api.getModuleId());
    }

    /**
     * 删除接口参数校验
     */
    private void validateDeleteApi(Integer apiId) {
        if (apiId == null) {
            throw new IllegalArgumentException("接口ID不能为空");
        }
    }

    /**
     * 检查是否有接口管理权限
     */
    private boolean hasApiManagePermission(Api api, Integer userId) {
        // 规则1：可以管理自己创建的接口
        if (api.getCreatedBy().equals(userId)) {
            return true;
        }

        // 规则2：项目成员可以管理接口
        // TODO: 这里应该检查用户的项目成员权限
        // 暂时返回true，实际应该查询项目成员权限
        return true;
    }

    /**
     * 检查是否为系统接口
     */
    private boolean isSystemApi(Api api) {
        // 规则1：系统接口通常有特定的编码前缀
        if (StringUtils.hasText(api.getApiCode()) && 
            api.getApiCode().startsWith(Constants.SYSTEM_API_CODE_PREFIX)) {
            return true;
        }

        // 规则2：系统接口通常有特定的名称关键字
        if (StringUtils.hasText(api.getName()) && 
            api.getName().contains(Constants.SYSTEM_API_NAME_KEYWORD)) {
            return true;
        }

        return false;
    }

    /**
     * 检查接口是否存在测试用例
     */
    private boolean hasTestCases(Integer apiId) {
        return testCaseMapper.countByApiId(apiId) > 0;
    }

    /**
     * 检查接口是否存在前置条件
     */
    private boolean hasPreconditions(Integer apiId) {
        return apiMapper.countPreconditionsByApiId(apiId) > 0;
    }

    /**
     * 检查接口是否正在被使用
     */
    private boolean isApiInUse(Integer apiId) {
        // TODO: 这里应该检查接口是否正在被测试计划、测试套件等使用
        // 暂时返回false，实际应该查询相关表
        return false;
    }
}
