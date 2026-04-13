# PreConditions字段修复总结

## 修复日期
2025-10-20

## 问题描述

测试用例中的 `pre_conditions` 字段被错误地用于存储请求参数（如用户名和密码），这与数据库设计的原本用途不符。

### 错误用法示例
```json
{
  "pre_conditions": {
    "username": "johndoe",
    "password": "Test@123456"
  }
}
```

## 正确的设计

### 1. pre_conditions（前置条件）
- **用途**：描述用例执行的前提条件（文档性质）
- **不参与执行**：仅用于说明和验证
```json
{
  "pre_conditions": {
    "description": "用户johndoe必须已注册且状态为激活",
    "data_requirements": {
      "user_exists": true,
      "user_status": "active"
    }
  }
}
```

### 2. request_override（请求参数覆盖）
- **用途**：实际的HTTP请求参数
- **参与执行**：用于构建HTTP请求
```json
{
  "request_override": {
    "body": {
      "username": "johndoe",
      "password": "Test@123456"
    },
    "headers": {
      "X-Client-Type": "web"
    }
  }
}
```

### 3. ApiPreconditions（接口前置条件表）
- **用途**：定义接口依赖链
- **自动执行**：执行前自动调用依赖接口
```sql
-- 示例：订单接口依赖登录接口
INSERT INTO ApiPreconditions (api_id, precondition_api_id, name, type, config)
VALUES (
    2,  -- 订单接口
    1,  -- 依赖登录接口
    '获取用户认证Token',
    'auth',
    '{
        "execute": {
            "method": "POST",
            "path": "/api/auth/login"
        },
        "extract": {
            "auth_token": "$.data.token"
        }
    }'
);
```

## 已完成的修复

### 1. ✅ 修改 TestCaseExecutor.java

**修改内容**：
- 重写了 `buildHttpRequest` 方法
- 正确处理 `request_override` 字段：
  - 优先使用 `request_override.body` 作为请求体
  - 支持 `request_override.headers` 覆盖请求头
  - 支持 `request_override.query` 添加查询参数
- 添加了详细的日志输出，便于调试

**关键代码**：
```java
// 优先使用 request_override 中的 body
if (executionDTO.getRequestOverride() != null) {
    JsonNode overrideNode = objectMapper.readTree(executionDTO.getRequestOverride());
    if (overrideNode.has("body")) {
        body = objectMapper.writeValueAsString(overrideNode.get("body"));
        log.info("使用request_override中的请求体");
    }
}

// 如果没有request_override，使用接口默认请求体
if (body == null && apiInfo.getRequestBody() != null) {
    body = apiInfo.getRequestBody();
    log.info("使用接口默认请求体");
}
```

### 2. ✅ 创建设计文档

| 文档 | 说明 |
|------|------|
| `TEST_EXECUTION_DATA_FLOW_DESIGN.md` | 数据流转设计文档 |
| `TEST_CASE_EXECUTION_API_GUIDE.md` | API使用指南 |
| `fix_testcase_data_structure.sql` | 数据修复SQL脚本 |

### 3. ✅ 字段用途明确化

| 字段 | 存储位置 | 用途 | 参与执行 |
|------|---------|------|---------|
| `pre_conditions` | TestCases表 | 描述前提条件 | ❌ 否 |
| `request_override` | TestCases表 | HTTP请求参数 | ✅ 是 |
| `ApiPreconditions` | 独立表 | 接口依赖链 | ✅ 是 |
| `variables` | 执行时传入 | 动态变量 | ✅ 是 |

## 使用建议

### 前端开发者

执行测试用例时，请求体示例：
```json
POST /api/test-cases/10001/execute

{
  "environment": "test",
  "variables": {
    "base_url": "http://8.138.127.144:8080"
  }
  // 不需要传request_override，它已经在用例数据中定义好了
}
```

### 测试用例设计者

创建测试用例时的数据结构：
```json
{
  "pre_conditions": {
    "description": "用户必须已注册",
    "data_requirements": {"user_status": "active"}
  },
  "request_override": {
    "body": {
      "username": "johndoe",
      "password": "Test@123456"
    }
  },
  "expected_response_body": {
    "code": 200,
    "data": {"token": "*", "userId": "*"}
  },
  "assertions": [
    {"type": "status_code", "expected": 200},
    {"type": "json_path", "path": "$.data.token", "operator": "exists"}
  ]
}
```

### 后端开发者

执行流程：
```
1. 加载用例数据（包含request_override）
   ↓
2. 执行ApiPreconditions（如有依赖接口）
   ↓
3. 合并参数：接口默认 + request_override + variables
   ↓
4. 执行HTTP请求
   ↓
5. 验证响应（assertions）
   ↓
6. 提取变量（extractors）
```

## 迁移步骤

### 步骤1：更新现有测试用例数据
```sql
-- 执行 fix_testcase_data_structure.sql
source fix_testcase_data_structure.sql;
```

### 步骤2：验证修复结果
```sql
SELECT 
    case_code,
    JSON_PRETTY(pre_conditions) AS pre_conditions,
    JSON_PRETTY(request_override) AS request_override
FROM TestCases
LIMIT 5;
```

### 步骤3：测试执行功能
```bash
# 测试登录用例
curl -X POST "http://localhost:8080/api/test-cases/10001/execute" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "variables": {
      "base_url": "http://8.138.127.144:8080"
    }
  }'
```

## 关键改进

1. ✅ **数据分离**：请求参数不再混在前置条件中
2. ✅ **语义清晰**：每个字段各司其职
3. ✅ **易于维护**：前端/测试人员更容易理解
4. ✅ **符合规范**：遵循测试自动化最佳实践

## 注意事项

⚠️ **兼容性**：如果前端代码还在使用旧的数据结构，需要同步更新前端代码

⚠️ **数据迁移**：现有测试用例数据需要批量更新，请在测试环境验证后再在生产环境执行

⚠️ **文档更新**：相关API文档和用户手册需要同步更新

## 验证清单

- [x] TestCaseExecutor代码已更新
- [x] 请求构建逻辑已修复
- [x] 设计文档已创建
- [x] 数据修复SQL已准备
- [x] API使用指南已编写
- [ ] 前端代码需要同步更新
- [ ] 现有测试用例数据需要迁移
- [ ] 用户文档需要更新

