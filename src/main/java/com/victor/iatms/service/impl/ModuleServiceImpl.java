package com.victor.iatms.service.impl;

import com.victor.iatms.entity.constants.Constants;
import com.victor.iatms.entity.dto.ApiListQueryDTO;
import com.victor.iatms.entity.dto.ApiListResponseDTO;
import com.victor.iatms.entity.dto.CreateModuleDTO;
import com.victor.iatms.entity.dto.CreateModuleResponseDTO;
import com.victor.iatms.entity.dto.UpdateModuleDTO;
import com.victor.iatms.entity.dto.UpdateModuleResponseDTO;
import com.victor.iatms.entity.po.Module;
import com.victor.iatms.entity.po.Project;
import com.victor.iatms.entity.po.User;
import com.victor.iatms.mappers.ModuleMapper;
import com.victor.iatms.mappers.ProjectMapper;
import com.victor.iatms.mappers.UserMapper;
import com.victor.iatms.service.ModuleService;
import com.victor.iatms.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 模块服务实现类
 */
@Service
public class ModuleServiceImpl implements ModuleService {
    
    @Autowired
    private ModuleMapper moduleMapper;
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    // 模块编码格式验证正则表达式：大写字母、数字、下划线
    private static final Pattern MODULE_CODE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");
    
    @Override
    public CreateModuleResponseDTO createModule(CreateModuleDTO createModuleDTO, Integer creatorId) {
        // 参数校验
        validateCreateModule(createModuleDTO, creatorId);
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(createModuleDTO.getProjectId());
        if (project == null) {
            throw new IllegalArgumentException("指定的项目不存在");
        }
        if (project.getIsDeleted()) {
            throw new IllegalArgumentException("指定的项目已被删除");
        }
        
        // 检查父模块是否存在（如果提供了父模块ID）
        if (createModuleDTO.getParentModuleId() != null) {
            Module parentModule = moduleMapper.selectById(createModuleDTO.getParentModuleId());
            if (parentModule == null) {
                throw new IllegalArgumentException("指定的父模块不存在");
            }
            if (parentModule.getIsDeleted()) {
                throw new IllegalArgumentException("指定的父模块已被删除");
            }
            if (!parentModule.getProjectId().equals(createModuleDTO.getProjectId())) {
                throw new IllegalArgumentException("父模块必须属于同一个项目");
            }
        }
        
        // 检查负责人是否存在（如果提供了负责人ID）
        if (createModuleDTO.getOwnerId() != null) {
            User owner = userMapper.findById(createModuleDTO.getOwnerId());
            if (owner == null) {
                throw new IllegalArgumentException("指定的负责人不存在");
            }
            if (owner.getIsDeleted()) {
                throw new IllegalArgumentException("指定的负责人已被删除");
            }
        }
        
        // 检查模块编码是否已存在
        if (moduleMapper.checkModuleCodeExists(createModuleDTO.getModuleCode(), createModuleDTO.getProjectId()) > 0) {
            throw new IllegalArgumentException("模块编码已存在");
        }
        
        // 创建模块实体
        Module module = new Module();
        module.setModuleCode(createModuleDTO.getModuleCode());
        module.setProjectId(createModuleDTO.getProjectId());
        module.setParentModuleId(createModuleDTO.getParentModuleId());
        module.setName(createModuleDTO.getName());
        module.setDescription(createModuleDTO.getDescription());
        module.setSortOrder(createModuleDTO.getSortOrder() != null ? createModuleDTO.getSortOrder() : 0);
        module.setStatus(createModuleDTO.getStatus() != null ? createModuleDTO.getStatus() : Constants.MODULE_STATUS_ACTIVE);
        module.setOwnerId(createModuleDTO.getOwnerId());
        module.setCreatedBy(creatorId);
        module.setUpdatedBy(creatorId);
        module.setCreatedAt(LocalDateTime.now());
        module.setUpdatedAt(LocalDateTime.now());
        module.setIsDeleted(false);
        
        // 处理标签信息
        if (createModuleDTO.getTags() != null && !createModuleDTO.getTags().isEmpty()) {
            // 将标签列表转换为JSON字符串存储
            module.setTags(String.join(",", createModuleDTO.getTags()));
        }
        
        // 插入模块
        int result = moduleMapper.insert(module);
        if (result <= 0) {
            throw new RuntimeException("创建模块失败");
        }
        
        // 查询创建的模块详情（包含关联信息）
        CreateModuleResponseDTO responseDTO = moduleMapper.selectModuleDetailById(module.getModuleId());
        if (responseDTO == null) {
            throw new RuntimeException("查询创建的模块信息失败");
        }
        
        return responseDTO;
    }
    
    @Override
    public void deleteModule(Integer moduleId, Integer deletedBy) {
        // 参数校验
        validateDeleteModule(moduleId, deletedBy);
        
        // 检查模块是否存在
        Module module = moduleMapper.selectById(moduleId);
        if (module == null) {
            throw new IllegalArgumentException("模块不存在");
        }
        
        // 检查模块是否已被删除
        if (module.getIsDeleted()) {
            throw new IllegalArgumentException("模块已被删除");
        }
        
        // 检查是否为系统模块
        if (isSystemModule(module)) {
            throw new IllegalArgumentException("不能删除系统模块");
        }
        
        // 检查权限（只能删除自己创建的模块，或者需要管理员权限）
        if (!hasDeletePermission(module, deletedBy)) {
            throw new IllegalArgumentException("权限不足，无法删除模块");
        }
        
        // 检查是否存在子模块
        if (moduleMapper.countChildModules(moduleId) > 0) {
            throw new IllegalArgumentException("模块存在子模块，无法删除");
        }
        
        // 检查是否存在接口数据
        if (moduleMapper.countModuleApis(moduleId) > 0) {
            throw new IllegalArgumentException("模块存在接口数据，无法删除");
        }
        
        // 检查模块是否正在被使用
        if (moduleMapper.isModuleInUse(moduleId)) {
            throw new IllegalArgumentException("模块正在被使用，无法删除");
        }
        
        // 执行软删除
        int result = moduleMapper.deleteById(moduleId, deletedBy);
        if (result <= 0) {
            throw new RuntimeException("删除模块失败");
        }
        
        // TODO: 记录审计日志
        // auditLogService.logModuleDelete(moduleId, deletedBy, module.getName(), module.getModuleCode());
    }
    
    @Override
    public UpdateModuleResponseDTO updateModule(Integer moduleId, UpdateModuleDTO updateModuleDTO, Integer updatedBy) {
        // 参数校验
        validateUpdateModule(moduleId, updateModuleDTO, updatedBy);
        
        // 检查模块是否存在
        Module module = moduleMapper.selectById(moduleId);
        if (module == null) {
            throw new IllegalArgumentException("模块不存在");
        }
        
        // 检查模块是否已被删除
        if (module.getIsDeleted()) {
            throw new IllegalArgumentException("模块已被删除，无法编辑");
        }
        
        // 检查是否为系统模块
        if (isSystemModule(module)) {
            throw new IllegalArgumentException("不能修改系统模块");
        }
        
        // 检查权限（只能修改自己创建的模块，或者需要管理员权限）
        if (!hasEditPermission(module, updatedBy)) {
            throw new IllegalArgumentException("权限不足，无法编辑模块信息");
        }
        
        // 验证模块编码唯一性（如果提供了新的模块编码）
        if (updateModuleDTO.getModuleCode() != null && 
            !updateModuleDTO.getModuleCode().equals(module.getModuleCode())) {
            if (moduleMapper.checkModuleCodeExistsExcludeSelf(updateModuleDTO.getModuleCode(), 
                module.getProjectId(), moduleId) > 0) {
                throw new IllegalArgumentException("模块编码已被其他模块使用");
            }
        }
        
        // 验证父模块（如果提供了父模块ID）
        if (updateModuleDTO.getParentModuleId() != null) {
            validateParentModule(updateModuleDTO.getParentModuleId(), moduleId, module.getProjectId());
        }
        
        // 验证负责人（如果提供了负责人ID）
        if (updateModuleDTO.getOwnerId() != null) {
            validateOwner(updateModuleDTO.getOwnerId());
        }
        
        // 验证状态（如果提供了状态）
        if (updateModuleDTO.getStatus() != null) {
            validateModuleStatus(updateModuleDTO.getStatus());
        }
        
        // 执行更新
        Module updateModule = new Module();
        updateModule.setModuleId(moduleId);
        updateModule.setModuleCode(updateModuleDTO.getModuleCode());
        updateModule.setName(updateModuleDTO.getName());
        updateModule.setDescription(updateModuleDTO.getDescription());
        updateModule.setParentModuleId(updateModuleDTO.getParentModuleId());
        updateModule.setSortOrder(updateModuleDTO.getSortOrder());
        updateModule.setStatus(updateModuleDTO.getStatus());
        updateModule.setOwnerId(updateModuleDTO.getOwnerId());
        updateModule.setUpdatedBy(updatedBy);
        updateModule.setUpdatedAt(LocalDateTime.now());
        
        // 处理标签
        if (updateModuleDTO.getTags() != null) {
            updateModule.setTags(JsonUtils.convertObj2Json(updateModuleDTO.getTags()));
        }
        
        int result = moduleMapper.updateById(updateModule);
        if (result <= 0) {
            throw new RuntimeException("更新模块信息失败");
        }
        
        // 查询更新后的模块信息
        UpdateModuleResponseDTO responseDTO = moduleMapper.selectUpdateModuleDetailById(moduleId);
        if (responseDTO == null) {
            throw new RuntimeException("查询更新后的模块信息失败");
        }
        
        // TODO: 记录审计日志
        // auditLogService.logModuleUpdate(moduleId, updatedBy, updateModuleDTO);
        
        return responseDTO;
    }
    
    @Override
    public ApiListResponseDTO getApiList(ApiListQueryDTO queryDTO) {
        // 参数校验
        validateApiListQuery(queryDTO);
        
        // 检查模块是否存在
        Module module = moduleMapper.selectById(queryDTO.getModuleId());
        if (module == null) {
            throw new IllegalArgumentException("模块不存在");
        }
        
        // 检查模块是否已被删除
        if (module.getIsDeleted()) {
            throw new IllegalArgumentException("模块已被删除");
        }
        
        // 检查权限（需要模块访问权限）
        if (!hasModuleAccessPermission(module, 1)) { // TODO: 从当前用户上下文获取用户ID
            throw new IllegalArgumentException("权限不足，无法查看接口列表");
        }
        
        // 设置默认值
        setApiListQueryDefaultValues(queryDTO);
        
        // 查询接口列表
        List<com.victor.iatms.entity.dto.ApiDTO> apiList = moduleMapper.selectApiList(queryDTO);
        
        // 查询总数
        Integer total = moduleMapper.countApiList(queryDTO);
        
        // 构建响应
        ApiListResponseDTO responseDTO = new ApiListResponseDTO();
        responseDTO.setTotal(Long.valueOf(total));
        responseDTO.setItems(apiList);
        responseDTO.setPage(queryDTO.getPage());
        responseDTO.setPageSize(queryDTO.getPageSize());
        
        // 如果需要统计信息
        if (queryDTO.getIncludeStatistics()) {
            com.victor.iatms.entity.dto.ApiSummaryDTO summary = moduleMapper.selectApiSummary(queryDTO.getModuleId());
            responseDTO.setSummary(summary);
        }
        
        return responseDTO;
    }
    
    /**
     * 接口列表查询参数校验
     */
    private void validateApiListQuery(ApiListQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        if (queryDTO.getModuleId() == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
    }
    
    /**
     * 设置接口列表查询默认值
     */
    private void setApiListQueryDefaultValues(ApiListQueryDTO queryDTO) {
        if (!StringUtils.hasText(queryDTO.getStatus())) {
            queryDTO.setStatus(Constants.DEFAULT_API_STATUS);
        }
        if (queryDTO.getIncludeDeleted() == null) {
            queryDTO.setIncludeDeleted(Constants.DEFAULT_INCLUDE_DELETED);
        }
        if (queryDTO.getIncludeStatistics() == null) {
            queryDTO.setIncludeStatistics(Constants.DEFAULT_INCLUDE_STATISTICS);
        }
        if (!StringUtils.hasText(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_API_SORT_BY);
        }
        if (!StringUtils.hasText(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(Constants.DEFAULT_PAGE);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (queryDTO.getPageSize() > Constants.MAX_PAGE_SIZE) {
            queryDTO.setPageSize(Constants.MAX_PAGE_SIZE);
        }
        
        // 计算分页偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        
        // 验证排序字段
        if (!isValidApiSortField(queryDTO.getSortBy())) {
            queryDTO.setSortBy(Constants.DEFAULT_API_SORT_BY);
        }
        
        // 验证排序顺序
        if (!com.victor.iatms.entity.enums.SortOrderEnum.isValidSortOrder(queryDTO.getSortOrder())) {
            queryDTO.setSortOrder(Constants.DEFAULT_SORT_ORDER);
        }
    }
    
    /**
     * 校验接口排序字段是否有效
     */
    private boolean isValidApiSortField(String sortBy) {
        for (com.victor.iatms.entity.enums.ApiSortFieldEnum sortField : com.victor.iatms.entity.enums.ApiSortFieldEnum.values()) {
            if (sortField.getField().equalsIgnoreCase(sortBy)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否有模块访问权限
     */
    private boolean hasModuleAccessPermission(Module module, Integer userId) {
        // 规则1：可以访问自己创建的模块
        if (module.getCreatedBy().equals(userId)) {
            return true;
        }
        
        // 规则2：项目成员可以访问模块
        // TODO: 这里应该检查用户的项目成员权限
        // 暂时返回true，实际应该查询项目成员权限
        return true;
    }
    
    /**
     * 修改模块参数校验
     */
    private void validateUpdateModule(Integer moduleId, UpdateModuleDTO updateModuleDTO, Integer updatedBy) {
        if (moduleId == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        if (updateModuleDTO == null) {
            throw new IllegalArgumentException("修改信息不能为空");
        }
        if (updatedBy == null) {
            throw new IllegalArgumentException("更新人ID不能为空");
        }
        
        // 验证模块名称
        if (updateModuleDTO.getName() != null && updateModuleDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("模块名称不能为空");
        }
        if (updateModuleDTO.getName() != null && 
            updateModuleDTO.getName().length() > Constants.MODULE_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("模块名称长度不能超过" + Constants.MODULE_NAME_MAX_LENGTH + "个字符");
        }
        
        // 验证模块编码
        if (updateModuleDTO.getModuleCode() != null) {
            if (updateModuleDTO.getModuleCode().trim().isEmpty()) {
                throw new IllegalArgumentException("模块编码不能为空");
            }
            if (updateModuleDTO.getModuleCode().length() > Constants.MODULE_CODE_MAX_LENGTH) {
                throw new IllegalArgumentException("模块编码长度不能超过" + Constants.MODULE_CODE_MAX_LENGTH + "个字符");
            }
            if (!Pattern.matches(Constants.MODULE_CODE_PATTERN, updateModuleDTO.getModuleCode())) {
                throw new IllegalArgumentException("模块编码只能包含大写字母、数字和下划线");
            }
        }
        
        // 验证描述
        if (updateModuleDTO.getDescription() != null && 
            updateModuleDTO.getDescription().length() > Constants.MODULE_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("模块描述长度不能超过" + Constants.MODULE_DESCRIPTION_MAX_LENGTH + "个字符");
        }
    }
    
    /**
     * 验证父模块
     */
    private void validateParentModule(Integer parentModuleId, Integer currentModuleId, Integer projectId) {
        if (parentModuleId.equals(currentModuleId)) {
            throw new IllegalArgumentException("不能将模块设置为自己的父模块");
        }
        
        Module parentModule = moduleMapper.selectById(parentModuleId);
        if (parentModule == null || parentModule.getIsDeleted()) {
            throw new IllegalArgumentException("指定的父模块不存在");
        }
        
        if (!parentModule.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("父模块必须属于同一个项目");
        }
        
        // 检查循环引用
        checkCircularReference(currentModuleId, parentModuleId);
    }
    
    /**
     * 检查循环引用
     */
    private void checkCircularReference(Integer moduleId, Integer parentModuleId) {
        Set<Integer> visited = new HashSet<>();
        visited.add(moduleId);
        
        Integer currentParentId = parentModuleId;
        while (currentParentId != null) {
            if (visited.contains(currentParentId)) {
                throw new IllegalArgumentException("检测到循环引用");
            }
            visited.add(currentParentId);
            
            Module parentModule = moduleMapper.selectById(currentParentId);
            if (parentModule == null) {
                break;
            }
            currentParentId = parentModule.getParentModuleId();
        }
    }
    
    /**
     * 验证负责人
     */
    private void validateOwner(Integer ownerId) {
        User owner = userMapper.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("指定的负责人不存在");
        }
        if (owner.getIsDeleted()) {
            throw new IllegalArgumentException("指定的负责人已被删除");
        }
        if (!"active".equals(owner.getStatus())) {
            throw new IllegalArgumentException("指定的负责人未激活");
        }
    }
    
    /**
     * 验证模块状态
     */
    private void validateModuleStatus(String status) {
        if (!isValidModuleStatus(status)) {
            throw new IllegalArgumentException("模块状态无效");
        }
    }
    
    /**
     * 校验模块状态是否有效
     */
    private boolean isValidModuleStatus(String status) {
        for (com.victor.iatms.entity.enums.ModuleStatusEnum statusEnum : com.victor.iatms.entity.enums.ModuleStatusEnum.values()) {
            if (statusEnum.getCode().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否有编辑权限
     */
    private boolean hasEditPermission(Module module, Integer updatedBy) {
        // 规则1：可以编辑自己创建的模块
        if (module.getCreatedBy().equals(updatedBy)) {
            return true;
        }
        
        // 规则2：管理员可以编辑任何模块
        // TODO: 这里应该检查用户的管理员权限
        // 暂时返回false，实际应该查询用户权限
        return false;
    }
    
    /**
     * 删除模块参数校验
     */
    private void validateDeleteModule(Integer moduleId, Integer deletedBy) {
        if (moduleId == null) {
            throw new IllegalArgumentException("模块ID不能为空");
        }
        if (deletedBy == null) {
            throw new IllegalArgumentException("删除人ID不能为空");
        }
    }
    
    /**
     * 检查是否为系统模块
     */
    private boolean isSystemModule(Module module) {
        // 系统模块的判断规则：模块编码以SYS_开头，或者模块名称包含"系统"
        return module.getModuleCode().startsWith(Constants.SYSTEM_MODULE_CODE_PREFIX) ||
               module.getName().contains(Constants.SYSTEM_MODULE_NAME_KEYWORD);
    }
    
    /**
     * 检查是否有删除权限
     */
    private boolean hasDeletePermission(Module module, Integer deletedBy) {
        // 规则1：可以删除自己创建的模块
        if (module.getCreatedBy().equals(deletedBy)) {
            return true;
        }
        
        // 规则2：管理员可以删除任何模块
        // TODO: 这里应该检查用户的管理员权限
        // 暂时返回false，实际应该查询用户权限
        return false;
    }
    
    /**
     * 创建模块参数校验
     */
    private void validateCreateModule(CreateModuleDTO createModuleDTO, Integer creatorId) {
        if (createModuleDTO == null) {
            throw new IllegalArgumentException("模块信息不能为空");
        }
        if (creatorId == null) {
            throw new IllegalArgumentException("创建人ID不能为空");
        }
        
        // 验证模块编码
        if (!StringUtils.hasText(createModuleDTO.getModuleCode())) {
            throw new IllegalArgumentException("模块编码不能为空");
        }
        if (createModuleDTO.getModuleCode().length() > Constants.MODULE_CODE_MAX_LENGTH) {
            throw new IllegalArgumentException("模块编码长度不能超过" + Constants.MODULE_CODE_MAX_LENGTH + "个字符");
        }
        if (!MODULE_CODE_PATTERN.matcher(createModuleDTO.getModuleCode()).matches()) {
            throw new IllegalArgumentException("模块编码只能包含大写字母、数字和下划线");
        }
        
        // 验证项目ID
        if (createModuleDTO.getProjectId() == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        
        // 验证模块名称
        if (!StringUtils.hasText(createModuleDTO.getName())) {
            throw new IllegalArgumentException("模块名称不能为空");
        }
        if (createModuleDTO.getName().length() > Constants.MODULE_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("模块名称长度不能超过" + Constants.MODULE_NAME_MAX_LENGTH + "个字符");
        }
        
        // 验证模块描述
        if (createModuleDTO.getDescription() != null && 
            createModuleDTO.getDescription().length() > Constants.MODULE_DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("模块描述长度不能超过" + Constants.MODULE_DESCRIPTION_MAX_LENGTH + "个字符");
        }
        
        // 验证排序顺序
        if (createModuleDTO.getSortOrder() != null && createModuleDTO.getSortOrder() < 0) {
            throw new IllegalArgumentException("排序顺序不能为负数");
        }
        
        // 验证模块状态
        if (createModuleDTO.getStatus() != null && 
            !Constants.MODULE_STATUS_ACTIVE.equals(createModuleDTO.getStatus()) &&
            !Constants.MODULE_STATUS_INACTIVE.equals(createModuleDTO.getStatus()) &&
            !Constants.MODULE_STATUS_ARCHIVED.equals(createModuleDTO.getStatus())) {
            throw new IllegalArgumentException("模块状态只能是active、inactive或archived");
        }
    }
}
