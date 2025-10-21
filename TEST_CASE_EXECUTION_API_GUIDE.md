# 测试用例执行API使用指南

## 1. 接口信息

- **接口路径**: `POST /test-cases/{case_id}/execute`
- **接口描述**: 执行指定的测试用例
- **认证要求**: 需要Bearer Token

## 2. 请求参数说明

### 2.1 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| case_id | Integer | 是 | 测试用例ID |

### 2.2 请求体参数

```json
{
  "environment": "test",  // 执行环境：dev, test, staging, prod
  "baseUrl": "http://8.138.127.144:8080",  // 可选：覆盖接口的baseUrl
  "timeout": 30,  // 可选：超时时间（秒）
  "async": false,  // 是否异步执行
  "variables": {  // 执行时的变量
    "test_username": "johndoe",
    "test_password": "Test@123456",
    "timestamp": "1234567890"
  },
  "authOverride": {  // 可选：认证覆盖
    "type": "bearer",
    "token": "custom_token_here"
  }
}
```

## 3. 数据库中的测试用例数据结构

### 3.1 关键字段说明

```sql
-- TestCases表
{
  "case_id": 10001,
  "case_code": "TC_AUTH001_001",
  "api_id": 1,
  "name": "正常登录测试",
  "description": "使用正确的用户名和密码登录",
  
  -- ✅ pre_conditions: 前提条件描述（仅文档用）
  "pre_conditions": {
    "description": "测试用户必须已注册且状态为激活",
    "data_requirements": {
      "username": "johndoe",
      "user_status": "active",
      "account_verified": true
    },
    "environment_requirements": {
      "auth_service": "必须可用",
      "database": "必须连接正常"
    }
  },
  
  -- ✅ request_override: 实际的请求参数
  "request_override": {
    "body": {
      "username": "johndoe",
      "password": "Test@123456"
    },
    "headers": {
      "X-Client-Type": "web"
    },
    "query": {
      "debug": "false"
    }
  },
  
  -- ✅ expected_http_status: 期待的HTTP状态码
  "expected_http_status": 200,
  
  -- ✅ expected_response_body: 期待的响应体
  "expected_response_body": {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "*",  // * 表示只验证存在，不验证具体值
      "userId": "*",
      "username": "johndoe"
    }
  },
  
  -- ✅ assertions: 断言规则
  "assertions": [
    {"type": "status_code", "expected": 200},
    {"type": "json_path", "path": "$.code", "expected": 200},
    {"type": "json_path", "path": "$.data.token", "operator": "exists"},
    {"type": "json_path", "path": "$.data.username", "expected": "johndoe"}
  ],
  
  -- ✅ extractors: 提取规则（供后续用例使用）
  "extractors": [
    {"name": "auth_token", "path": "$.data.token"},
    {"name": "user_id", "path": "$.data.userId"}
  ]
}
```

## 4. 执行逻辑说明

### 4.1 请求构建过程

```java
// 步骤1: 加载用例数据
TestCase testCase = loadFromDatabase(caseId);

// 步骤2: 构建基础请求
HttpRequest request = new HttpRequest();
request.setMethod(api.method);  // 来自Apis表
request.setUrl(api.baseUrl + api.path);  // 来自Apis表

// 步骤3: 设置请求头（接口默认 + 用例覆盖）
request.setHeaders(api.requestHeaders);  // 接口默认头
request.mergeHeaders(testCase.requestOverride.headers);  // 用例覆盖

// 步骤4: 设置请求体（优先使用request_override）
if (testCase.requestOverride.body != null) {
    request.setBody(testCase.requestOverride.body);  // ✅ 用例定义的请求体
} else {
    request.setBody(api.requestBody);  // 接口默认请求体
}

// 步骤5: 变量替换
request.replaceVariables(executionVariables);

// 步骤6: 执行请求
HttpResponse response = httpClient.execute(request);

// 步骤7: 验证响应
validateResponse(response, testCase.expectedResponseBody, testCase.assertions);
```

### 4.2 前置条件的使用

```java
// pre_conditions 仅用于文档/日志记录
log.info("用例前提条件: {}", testCase.preConditions);

// 实际的接口依赖通过 ApiPreconditions 表处理
List<ApiPrecondition> preconditions = loadApiPreconditions(api.apiId);
for (ApiPrecondition precond : preconditions) {
    // 执行前置接口（如登录）
    HttpResponse precondResponse = executePreconditionApi(precond);
    
    // 提取变量到上下文
    Map<String, Object> vars = extractVariables(precondResponse, precond.config.extract);
    executionContext.putAll(vars);
}
```

## 5. 前端调用示例

### 5.1 执行测试用例

```javascript
// POST /api/test-cases/10001/execute

const executeRequest = {
  environment: "test",
  variables: {
    base_url: "http://8.138.127.144:8080"
  },
  // ❌ 不要在这里传request_override
  // request_override已经在用例数据中定义好了
}

// 如果需要临时覆盖用例中的参数：
const executeRequestWithOverride = {
  environment: "test",
  variables: {
    base_url: "http://8.138.127.144:8080"
  },
  requestOverride: {  // 临时覆盖用例中定义的request_override
    body: {
      username: "another_user",  // 临时使用不同的用户名
      password: "AnotherPass123"
    }
  }
}
```

### 5.2 响应数据

```json
{
  "code": 1,
  "msg": "用例执行完成",
  "data": {
    "executionId": 1234567890,
    "caseId": 10001,
    "caseName": "正常登录测试",
    "status": "passed",  // passed, failed, broken, skipped
    "duration": 1250,  // 毫秒
    "startTime": "2024-10-20T10:30:00Z",
    "endTime": "2024-10-20T10:30:01Z",
    "responseStatus": 200,
    "failureMessage": null,
    "assertionsPassed": 4,
    "assertionsFailed": 0,
    "extractedValues": {
      "auth_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user_id": "12345"
    },
    "logsLink": "/api/test-results/1234567890/logs",
    "reportId": 5001
  }
}
```

## 6. 数据字段对照表

| 前端显示 | 数据库字段 | 用途 | 数据类型 |
|---------|-----------|------|---------|
| 前置条件 | pre_conditions | 描述用例的前提要求 | JSON（文档性质） |
| 请求参数 | request_override | 实际的HTTP请求参数 | JSON（可执行） |
| 期待响应 | expected_response_body | 预期的HTTP响应 | TEXT/JSON |
| 断言规则 | assertions | 验证规则列表 | JSON数组 |
| 提取规则 | extractors | 从响应中提取数据 | JSON数组 |
| 接口依赖 | ApiPreconditions表 | 需要先执行的接口 | 独立表 |

## 7. 最佳实践

### 7.1 ✅ 正确的用法

```json
// 用例定义
{
  "pre_conditions": {
    "description": "用户johndoe必须存在且已激活"
  },
  "request_override": {
    "body": {
      "username": "johndoe",
      "password": "Test@123456"
    }
  }
}
```

### 7.2 ❌ 错误的用法

```json
// 不要把请求参数放在pre_conditions中！
{
  "pre_conditions": {
    "username": "johndoe",
    "password": "Test@123456"
  }
}
```

## 8. 总结

### 关键原则

1. **pre_conditions** = 描述前提条件，不参与执行
2. **request_override** = 实际的请求参数，参与执行
3. **ApiPreconditions** = 接口依赖链，自动执行
4. **variables** = 动态变量，执行时传入

### 数据流

```
用例定义 (TestCases) 
  → request_override → 构建HTTP请求 → 执行 → 验证
  → extractors → 提取变量 → 供后续用例使用
  
前置接口 (ApiPreconditions)
  → 自动执行 → 提取变量 → 注入到当前请求
```

