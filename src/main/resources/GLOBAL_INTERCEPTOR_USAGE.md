# GlobalInterceptor注解使用指南

## 概述

`@GlobalInterceptor`注解是一个基于AOP的权限校验注解，可以用于方法级别的权限控制。它支持多种权限校验方式，包括登录校验、管理员校验、权限校验、角色校验和资源访问权限校验。

## 注解参数说明

| 参数名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `checkLogin` | boolean | true | 是否校验登录状态 |
| `checkAdmin` | boolean | false | 是否校验管理员权限 |
| `checkPermission` | String[] | {} | 需要校验的权限列表 |
| `checkRole` | String[] | {} | 需要校验的角色列表 |
| `checkResourceAccess` | boolean | false | 是否校验资源访问权限 |
| `resourceType` | String | "" | 资源类型（如api、module等） |
| `resourceIdParam` | String | "" | 资源ID参数名（用于从请求参数中获取资源ID） |

## 使用示例

### 1. 基础登录校验

```java
@GetMapping("/profile")
@GlobalInterceptor(checkLogin = true)
public ResponseVO<UserInfoDTO> getUserProfile() {
    // 只需要登录即可访问
    return ResponseVO.success("success", userInfo);
}
```

### 2. 管理员权限校验

```java
@PostMapping("/admin/users")
@GlobalInterceptor(checkLogin = true, checkAdmin = true)
public ResponseVO<Void> createUser(@RequestBody CreateUserDTO request) {
    // 需要登录且为管理员才能访问
    return ResponseVO.success("用户创建成功", null);
}
```

### 3. 权限校验

```java
@GetMapping("/test-cases")
@GlobalInterceptor(
    checkLogin = true,
    checkPermission = {"testcase:view"}
)
public ResponseVO<List<TestCaseDTO>> getTestCases() {
    // 需要登录且拥有testcase:view权限
    return ResponseVO.success("success", testCases);
}
```

### 4. 角色校验

```java
@PostMapping("/test-cases")
@GlobalInterceptor(
    checkLogin = true,
    checkRole = {"test_manager", "admin"}
)
public ResponseVO<Void> createTestCase(@RequestBody CreateTestCaseDTO request) {
    // 需要登录且为test_manager或admin角色
    return ResponseVO.success("测试用例创建成功", null);
}
```

### 5. 资源访问权限校验

```java
@GetMapping("/apis/{apiId}/test-cases")
@GlobalInterceptor(
    checkLogin = true,
    checkPermission = {"testcase:view"},
    checkResourceAccess = true,
    resourceType = "api",
    resourceIdParam = "apiId"
)
public ResponseVO<List<TestCaseDTO>> getApiTestCases(@PathVariable Integer apiId) {
    // 需要登录、拥有testcase:view权限，且有apiId对应API的访问权限
    return ResponseVO.success("success", testCases);
}
```

### 6. 组合权限校验

```java
@DeleteMapping("/test-cases/{caseId}")
@GlobalInterceptor(
    checkLogin = true,
    checkPermission = {"testcase:delete"},
    checkRole = {"test_manager", "admin"},
    checkResourceAccess = true,
    resourceType = "testcase",
    resourceIdParam = "caseId"
)
public ResponseVO<Void> deleteTestCase(@PathVariable Integer caseId) {
    // 需要登录、拥有testcase:delete权限、为test_manager或admin角色，
    // 且有caseId对应测试用例的访问权限
    return ResponseVO.success("测试用例删除成功", null);
}
```

### 7. 无需权限校验（公开接口）

```java
@PostMapping("/auth/login")
@GlobalInterceptor(checkLogin = false)
public ResponseVO<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
    // 登录接口不需要权限校验
    return ResponseVO.success("登录成功", loginResponse);
}
```

## 权限枚举说明

### PermissionEnum（权限枚举）

```java
public enum PermissionEnum {
    // 用户管理权限
    USER_VIEW("user:view", "查看用户"),
    USER_CREATE("user:create", "创建用户"),
    USER_UPDATE("user:update", "更新用户"),
    USER_DELETE("user:delete", "删除用户"),
    
    // 接口管理权限
    API_VIEW("api:view", "查看接口"),
    API_CREATE("api:create", "创建接口"),
    API_UPDATE("api:update", "更新接口"),
    API_DELETE("api:delete", "删除接口"),
    
    // 测试用例管理权限
    TEST_CASE_VIEW("testcase:view", "查看测试用例"),
    TEST_CASE_CREATE("testcase:create", "创建测试用例"),
    TEST_CASE_UPDATE("testcase:update", "更新测试用例"),
    TEST_CASE_DELETE("testcase:delete", "删除测试用例"),
    TEST_CASE_EXECUTE("testcase:execute", "执行测试用例"),
    
    // 模块管理权限
    MODULE_VIEW("module:view", "查看模块"),
    MODULE_CREATE("module:create", "创建模块"),
    MODULE_UPDATE("module:update", "更新模块"),
    MODULE_DELETE("module:delete", "删除模块"),
    
    // 系统管理权限
    SYSTEM_CONFIG("system:config", "系统配置"),
    SYSTEM_LOG("system:log", "系统日志"),
    SYSTEM_MONITOR("system:monitor", "系统监控");
}
```

### RoleEnum（角色枚举）

```java
public enum RoleEnum {
    SUPER_ADMIN("super_admin", "超级管理员"),
    ADMIN("admin", "管理员"),
    TEST_MANAGER("test_manager", "测试经理"),
    TEST_ENGINEER("test_engineer", "测试工程师"),
    DEVELOPER("developer", "开发人员"),
    VIEWER("viewer", "查看者");
}
```

## 角色权限映射

| 角色 | 权限 |
|------|------|
| SUPER_ADMIN | 所有权限 |
| ADMIN | 用户管理、接口管理、测试用例管理、模块管理 |
| TEST_MANAGER | 接口查看、测试用例管理、模块查看 |
| TEST_ENGINEER | 接口查看、测试用例管理 |
| DEVELOPER | 接口管理、测试用例查看和执行 |
| VIEWER | 接口查看、测试用例查看、模块查看 |

## 错误码说明

| 错误码 | 含义 | 处理方式 |
|--------|------|----------|
| 901 | 认证失败，请重新登录 | 清除本地Token，跳转至登录页 |
| 404 | 权限不足 | 提示用户"权限不足" |

## 实现原理

1. **AOP切面**: 使用`@Aspect`和`@Before`注解在方法执行前进行权限校验
2. **JWT认证**: 从请求头中获取Bearer Token，使用JwtUtils验证Token有效性
3. **权限服务**: 通过PermissionService检查用户权限、角色和资源访问权限
4. **异常处理**: 权限校验失败时抛出BusinessException，统一错误处理

## 注意事项

1. **参数名匹配**: `resourceIdParam`必须与方法参数名完全匹配
2. **权限组合**: 多个权限校验条件为AND关系，必须全部满足
3. **角色校验**: 角色校验为OR关系，拥有任一角色即可
4. **资源访问**: 资源访问权限需要根据具体业务逻辑实现
5. **性能考虑**: 权限校验会增加方法执行时间，建议合理使用

## 扩展说明

### 自定义权限校验

如果需要自定义权限校验逻辑，可以：

1. 扩展`PermissionService`接口
2. 在`PermissionServiceImpl`中实现自定义逻辑
3. 在`GlobalOperationAspect`中调用自定义方法

### 缓存优化

为了提高性能，可以：

1. 在Redis中缓存用户权限信息
2. 设置合理的缓存过期时间
3. 在权限变更时清除相关缓存

### 动态权限

支持动态权限配置：

1. 从数据库读取权限配置
2. 支持运行时权限变更
3. 提供权限管理界面

## 测试建议

1. **单元测试**: 测试各种权限组合
2. **集成测试**: 测试完整的权限校验流程
3. **性能测试**: 测试权限校验对性能的影响
4. **安全测试**: 测试权限绕过漏洞

## 相关文件

- `GlobalInterceptor.java` - 注解定义
- `GlobalOperationAspect.java` - AOP切面实现
- `PermissionService.java` - 权限服务接口
- `PermissionServiceImpl.java` - 权限服务实现
- `PermissionEnum.java` - 权限枚举
- `RoleEnum.java` - 角色枚举
