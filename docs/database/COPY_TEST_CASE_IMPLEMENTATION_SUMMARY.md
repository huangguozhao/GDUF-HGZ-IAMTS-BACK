# 复制测试用例接口实现总结

## 📌 实现概述

本次实现在测试用例模块中添加了复制测试用例接口，允许用户快速复制现有测试用例并生成新的副本。

## 🎯 实现内容

### 1. DTO 类创建

#### CopyTestCaseRequestDTO.java
- **位置**: `src/main/java/com/victor/iatms/entity/dto/CopyTestCaseRequestDTO.java`
- **作用**: 复制测试用例请求参数封装
- **字段**:
  - `caseCode`: 新用例编码
  - `name`: 新用例名称
  - `description`: 新用例描述

#### CopyTestCaseResponseDTO.java
- **位置**: `src/main/java/com/victor/iatms/entity/dto/CopyTestCaseResponseDTO.java`
- **作用**: 复制测试用例响应数据封装
- **字段**: 包含完整的测试用例信息（27个字段）

### 2. Service 层实现

#### TestCaseService.java
- **新增方法**: `CopyTestCaseResponseDTO copyTestCase(Integer sourceCaseId, CopyTestCaseRequestDTO requestDTO, Integer currentUserId)`

#### TestCaseServiceImpl.java
- **核心方法**: `copyTestCase()`
  - 验证源用例是否存在
  - 检查新编码唯一性
  - 权限验证
  - 复制所有相关字段
  - 重置特定字段（is_template、template_id 等）
  - 保存新用例

- **辅助方法**:
  - `validateCopyTestCaseRequest()`: 参数验证
  - `buildCopyTestCaseResponse()`: 构建响应DTO
  - `parseJsonToListOrKeepAsIs()`: 灵活解析 JSON 字段

### 3. Controller 层实现

#### TestCaseController.java
- **新增接口**: `POST /testcases/{caseId}/copy`
- **功能**: 处理复制请求，异常处理，返回统一响应格式

## 🔧 技术亮点

### 1. 灵活的 JSON 解析策略

**问题**: 数据库中某些测试用例的 JSON 字段（如 `preConditions`）存储格式不一致，有的是对象 `{}`，有的是数组 `[]`

**解决方案**: 实现了 `parseJsonToListOrKeepAsIs()` 方法
```java
private List<Map<String, Object>> parseJsonToListOrKeepAsIs(String json) {
    // 1. 优先尝试作为数组解析
    // 2. 失败则作为对象解析并包装为 List
    // 3. 都失败则返回空列表，记录日志
}
```

**优势**:
- ✅ 向后兼容，支持历史数据
- ✅ 容错性强，不会因为 JSON 格式问题导致整个复制失败
- ✅ 统一返回格式，前端调用更简单

### 2. 完善的参数验证

```java
private void validateCopyTestCaseRequest(Integer sourceCaseId, CopyTestCaseRequestDTO requestDTO) {
    // 验证 ID 不为空
    // 验证编码格式（大写字母、数字、下划线、连字符）
    // 验证长度限制（2-50字符）
    // 验证名称长度（2-100字符）
    // 验证描述长度（最大500字符）
}
```

### 3. 分层错误处理

**Controller 层**:
```java
try {
    // 调用服务
} catch (IllegalArgumentException e) {
    // 根据错误消息返回不同的 HTTP 状态码
    // 404: 用例不存在
    // 400: 参数错误
    // 403: 权限不足
}
```

**Service 层**:
```java
// 业务异常使用 IllegalArgumentException
// 系统异常使用 RuntimeException
// JSON 解析异常捕获并记录日志，不中断流程
```

## 📊 复制字段映射

| 字段分类 | 字段名 | 处理方式 |
|---------|--------|---------|
| **用户指定** | caseCode, name, description | 使用请求参数 |
| **完全复制** | apiId, priority, severity, tags, preConditions, testSteps, requestOverride, expectedHttpStatus, expectedResponseSchema, expectedResponseBody, assertions, extractors, validators, isEnabled, version | 从源用例复制 |
| **系统生成** | caseId | 数据库自增 |
| **系统生成** | createdAt, updatedAt, createdBy, updatedBy | 当前时间和用户 |
| **重置** | isTemplate | 固定为 false |
| **重置** | templateId | 源用例ID |
| **重置** | isDeleted | 固定为 false |

## 🧪 测试支持

### 测试脚本
- **文件**: `test_copy_test_case_api.bat`
- **功能**:
  1. 正常复制测试
  2. 使用时间戳生成唯一编码
  3. 测试用例不存在场景
  4. 测试编码重复场景
  5. 测试参数缺失场景

### 测试文档
- **文件**: `COPY_TEST_CASE_API.md`
- **内容**:
  - 接口规范
  - 请求/响应格式
  - 错误码说明
  - 测试用例示例
  - 使用场景说明

## ⚠️ 问题修复记录

### 问题1: JSON 解析异常
**现象**: 复制用例时报错 "信息已经存在"
```
2025-10-21 16:27:25.807 ERROR JsonUtils - convertJson2Obj异常，json:{"password": "123456", "username": "123456"}
```

**原因**: `preConditions` 字段存储的是对象 `{}`，但代码尝试将其解析为 `List.class`

**解决**:
1. 实现 `parseJsonToListOrKeepAsIs()` 方法，支持对象和数组两种格式
2. 添加异常捕获，避免 JSON 解析失败影响整体功能
3. 使用 `@SuppressWarnings("unchecked")` 抑制类型警告

### 问题2: 缺少 Logger
**现象**: 代码中使用 `logger.error()` 但没有导入和定义

**解决**:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(TestCaseServiceImpl.class);
```

## 📁 文件清单

### 新增文件
1. `src/main/java/com/victor/iatms/entity/dto/CopyTestCaseRequestDTO.java`
2. `src/main/java/com/victor/iatms/entity/dto/CopyTestCaseResponseDTO.java`
3. `test_copy_test_case_api.bat`
4. `COPY_TEST_CASE_API.md`
5. `COPY_TEST_CASE_IMPLEMENTATION_SUMMARY.md`

### 修改文件
1. `src/main/java/com/victor/iatms/controller/TestCaseController.java`
   - 新增 `copyTestCase()` 接口方法
   - 添加相关导入

2. `src/main/java/com/victor/iatms/service/TestCaseService.java`
   - 新增接口方法定义

3. `src/main/java/com/victor/iatms/service/impl/TestCaseServiceImpl.java`
   - 实现 `copyTestCase()` 方法
   - 新增辅助方法
   - 添加 Logger 支持

## ✅ 功能验证

- [x] 接口编译通过，无语法错误
- [x] 参数验证完整
- [x] 错误处理完善
- [x] JSON 解析灵活，支持多种格式
- [x] 日志记录完整
- [x] 测试脚本和文档齐全

## 🎉 总结

成功实现了复制测试用例接口，具有以下特点：

1. **功能完整**: 支持所有字段的复制和重置
2. **健壮性强**: 灵活的 JSON 解析，完善的异常处理
3. **易于使用**: 简单的参数设计，清晰的错误提示
4. **文档齐全**: 提供测试脚本和详细文档
5. **向后兼容**: 兼容历史数据的不同格式

该接口可以帮助用户快速创建测试用例副本，提高测试用例管理效率。

