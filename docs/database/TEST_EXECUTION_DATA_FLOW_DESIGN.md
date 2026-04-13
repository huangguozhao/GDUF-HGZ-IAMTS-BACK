# 测试用例执行数据流转设计

## 1. 核心概念澄清

### 1.1 前置条件的两个层面

#### ApiPreconditions（接口前置条件表）
- **作用域**：接口级别
- **用途**：定义执行接口之前必须先执行的其他接口（接口依赖链）
- **示例**：
  ```json
  {
    "precondition_api_id": 1001,  // 需要先调用登录接口
    "name": "获取认证Token",
    "type": "auth",
    "config": {
      "method": "POST",
      "path": "/api/auth/login",
      "body": {
        "username": "${env.TEST_USERNAME}",
        "password": "${env.TEST_PASSWORD}"
      },
      "extract": {
        "token": "$.data.token"  // 提取token供后续使用
      }
    }
  }
  ```

#### TestCases.pre_conditions（用例前置条件字段）
- **作用域**：用例级别  
- **用途**：描述用例执行的前提状态/环境要求
- **示例**：
  ```json
  {
    "data_requirements": {
      "user_must_exist": true,
      "user_status": "active",
      "account_balance_min": 100.0
    },
    "environment_state": {
      "cache_cleared": true,
      "session_reset": false
    },
    "dependencies": [
      "订单系统已启动",
      "支付网关可用"
    ]
  }
  ```

### 1.2 请求参数的正确位置

#### request_override（请求参数覆盖）
- **用途**：覆盖接口默认的请求参数
- **示例**：
  ```json
  {
    "body": {
      "username": "testuser@example.com",
      "password": "Test@123456"
    },
    "headers": {
      "X-Test-Mode": "true",
      "X-Request-ID": "test-${timestamp}"
    },
    "query": {
      "debug": "true"
    }
  }
  ```

## 2. 数据流转设计

### 2.1 测试用例执行流程

```
1. 加载测试用例数据
   ↓
2. 检查并执行ApiPreconditions（接口前置条件）
   - 按依赖顺序执行前置接口
   - 提取变量到执行上下文
   ↓
3. 合并请求参数
   - 接口默认参数
   - request_override覆盖参数  
   - variables变量替换
   ↓
4. 执行HTTP请求
   ↓
5. 验证响应
   - HTTP状态码验证
   - 响应体Schema验证
   - 断言规则验证
   ↓
6. 提取响应数据（extractors）
   ↓
7. 运行验证器（validators）
   ↓
8. 记录执行结果
```

### 2.2 数据字段用途明确

| 字段 | 存储位置 | 用途 | 数据示例 |
|------|---------|------|---------|
| `pre_conditions` | TestCases表 | 描述用例的前提条件（文档性质） | `{"user_must_exist": true, "user_status": "active"}` |
| `ApiPreconditions` | 独立表 | 定义需要执行的前置接口 | 先调用登录接口获取token |
| `request_override` | TestCases表 | 覆盖/补充请求参数 | `{"body": {"username": "...", "password": "..."}}` |
| `variables` | 执行时传入 | 环境变量和动态值 | `{"base_url": "http://...", "timestamp": "..."}` |

## 3. 推荐的数据结构

### 3.1 TestCases 表数据示例

```json
{
  "case_id": 10001,
  "case_code": "TC_AUTH001_001",
  "api_id": 1,
  "name": "正常登录测试",
  
  // ✅ pre_conditions - 前提条件描述
  "pre_conditions": {
    "description": "用户已注册且状态为激活",
    "data_requirements": {
      "username": "测试用户必须存在于数据库",
      "user_status": "active",
      "account_verified": true
    },
    "environment_requirements": {
      "auth_service_available": true,
      "database_accessible": true
    }
  },
  
  // ✅ request_override - 实际的请求参数
  "request_override": {
    "body": {
      "username": "johndoe",
      "password": "Test@123456"
    },
    "headers": {
      "X-Test-Mode": "true",
      "X-Client-Type": "web"
    }
  },
  
  // ✅ expected_response_body - 期待的响应
  "expected_response_body": {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "*",  // * 表示任意值，只验证存在
      "userId": "*",
      "username": "johndoe"
    }
  },
  
  // ✅ assertions - 断言规则
  "assertions": [
    {
      "type": "status_code",
      "expected": 200
    },
    {
      "type": "json_path",
      "path": "$.code",
      "expected": 200
    },
    {
      "type": "json_path",
      "path": "$.data.token",
      "operator": "exists"
    },
    {
      "type": "json_path",
      "path": "$.data.username",
      "expected": "johndoe"
    }
  ],
  
  // ✅ extractors - 提取规则（用于后续用例）
  "extractors": [
    {
      "name": "auth_token",
      "type": "json_path",
      "expression": "$.data.token"
    },
    {
      "name": "user_id",
      "type": "json_path",
      "expression": "$.data.userId"
    }
  ]
}
```

### 3.2 ApiPreconditions 表数据示例

```json
{
  "precondition_id": 1001,
  "api_id": 2,  // 订单创建接口
  "precondition_api_id": 1,  // 依赖登录接口
  "name": "获取用户认证Token",
  "type": "auth",
  "config": {
    "execute": {
      "method": "POST",
      "path": "/api/auth/login",
      "body": {
        "username": "${env.TEST_USERNAME}",
        "password": "${env.TEST_PASSWORD}"
      }
    },
    "extract": {
      "auth_token": "$.data.token",
      "user_id": "$.data.userId"
    },
    "validate": {
      "status_code": 200,
      "required_fields": ["data.token", "data.userId"]
    }
  },
  "is_required": true
}
```

## 4. 执行时的数据合并逻辑

### 4.1 请求构建优先级

```
最终请求参数 = 接口默认参数 
              + request_override覆盖 
              + variables变量替换
              + ApiPreconditions提取的变量
```

### 4.2 代码示例

```java
// 1. 加载接口默认参数
Map<String, Object> requestParams = loadApiDefaults(apiInfo);

// 2. 执行ApiPreconditions，获取前置变量
Map<String, Object> preconditionVars = executeApiPreconditions(apiId);

// 3. 合并variables
requestParams.putAll(preconditionVars);
requestParams.putAll(executionVariables);

// 4. 应用request_override
if (requestOverride != null) {
    applyOverride(requestParams, requestOverride);
}

// 5. 变量替换
requestParams = replaceVariables(requestParams, allVariables);

// 6. 构建最终HTTP请求
HttpRequest request = buildHttpRequest(apiInfo, requestParams);
```

## 5. 修改建议

### 5.1 修改 TestCaseExecutionDTO

```java
/**
 * 前置条件 - 仅用于文档/描述
 */
private Object preConditions;  // 改为Object类型，支持复杂结构

/**
 * 请求参数覆盖 - 实际使用的请求数据
 */
private Object requestOverride;  // 改为Object类型，支持嵌套结构
```

### 5.2 执行逻辑调整

```java
public TestCaseExecutionResult executeTestCase(TestCaseExecutionDTO dto) {
    // 1. 验证pre_conditions（可选，仅用于检查环境状态）
    validatePreConditions(dto.getPreConditions());
    
    // 2. 执行ApiPreconditions（接口依赖链）
    Map<String, Object> contextVars = new HashMap<>();
    List<ApiPrecondition> preconditions = loadApiPreconditions(dto.getApiInfo().getApiId());
    for (ApiPrecondition precond : preconditions) {
        ExecutionResult precondResult = executePreconditionApi(precond);
        contextVars.putAll(extractVariables(precondResult, precond.getConfig()));
    }
    
    // 3. 构建最终请求参数
    HttpRequest request = buildFinalRequest(
        dto.getApiInfo(),
        dto.getRequestOverride(),  // 使用request_override作为主要请求数据
        contextVars,
        dto.getVariables()
    );
    
    // 4. 执行请求
    HttpResponse response = executeHttpRequest(request);
    
    // 5. 验证响应
    validateResponse(response, dto);
    
    // 6. 提取变量
    Map<String, Object> extracted = extractVariables(response, dto.getExtractors());
    
    // 7. 返回结果
    return buildResult(response, extracted);
}
```

## 6. 前后端交互示例

### 6.1 前端发送测试用例执行请求

```javascript
POST /api/test-cases/1/execute

{
  "environment": "test",
  "variables": {
    "base_url": "http://8.138.127.144:8080",
    "test_username": "johndoe",
    "test_password": "Test@123456"
  },
  "request_override": {
    // 这里可以临时覆盖用例中定义的请求参数
    "body": {
      "username": "johndoe",  // 覆盖用例中的用户名
      "password": "Test@123456"  // 覆盖用例中的密码
    }
  }
}
```

### 6.2 后端处理逻辑

```java
// 从数据库加载用例
TestCase testCase = loadTestCase(caseId);

// testCase.preConditions = 前提条件描述（不参与执行）
// testCase.requestOverride = 用例定义的请求参数
// executeDTO.requestOverride = 执行时临时覆盖的参数

// 最终参数 = testCase.requestOverride + executeDTO.requestOverride
Map<String, Object> finalParams = merge(
    testCase.getRequestOverride(),
    executeDTO.getRequestOverride()
);
```

## 7. 总结

### 关键原则

1. **pre_conditions** = 文档/描述性质，说明用例的前提条件
2. **ApiPreconditions** = 执行时的接口依赖链
3. **request_override** = 实际的请求参数
4. **variables** = 动态变量和环境配置

### 数据不要混淆

❌ **错误**：把请求参数放在 `pre_conditions` 中
```json
"pre_conditions": {
  "username": "johndoe",
  "password": "Test@123456"
}
```

✅ **正确**：把请求参数放在 `request_override` 中
```json
"request_override": {
  "body": {
    "username": "johndoe",
    "password": "Test@123456"
  }
}
```

✅ **正确**：`pre_conditions` 只描述前提
```json
"pre_conditions": {
  "description": "用户johndoe必须已注册且状态为激活",
  "data_requirements": {
    "user_exists": true,
    "user_status": "active"
  }
}
```

