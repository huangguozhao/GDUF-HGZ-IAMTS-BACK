# 分页获取测试用例列表API文档

## 接口概述

分页获取测试用例列表API提供了分页查询测试用例列表的功能，支持多种过滤和排序条件，包含完整的统计摘要信息。

## 接口详情

### 分页获取测试用例列表

**请求路径**: `/testcases`  
**请求方式**: `GET`  
**接口描述**: 分页查询测试用例列表，支持多种过滤和排序条件

#### 请求参数

**请求头**:
- `Authorization` (string, 必须): 认证令牌，格式: `Bearer {token}`

**查询参数**:
- `api_id` (number, 可选): 按接口ID过滤
- `module_id` (number, 可选): 按模块ID过滤
- `project_id` (number, 可选): 按项目ID过滤
- `name` (string, 可选): 用例名称模糊查询
- `case_code` (string, 可选): 用例编码精确查询
- `priority` (string, 可选): 优先级过滤。可选: `P0`, `P1`, `P2`, `P3`
- `severity` (string, 可选): 严重程度过滤。可选: `critical`, `high`, `medium`, `low`
- `status` (string, 可选): 状态过滤。可选: `active`, `inactive`
- `is_template` (boolean, 可选): 是否模板用例过滤
- `tags` (string[], 可选): 标签过滤（支持多个标签）
- `created_by` (number, 可选): 创建人ID过滤
- `include_deleted` (boolean, 可选): 是否包含已删除的用例，默认: `false`
- `search_keyword` (string, 可选): 关键字搜索（用例名称、描述）
- `sort_by` (string, 可选): 排序字段。可选: `name`, `case_code`, `priority`, `severity`, `created_at`, `updated_at`，默认: `created_at`
- `sort_order` (string, 可选): 排序顺序。可选: `asc`, `desc`，默认: `desc`
- `page` (number, 可选): 分页查询的页码，默认为 `1`
- `page_size` (number, 可选): 分页查询的每页记录数，默认为 `20`，最大 `100`

#### 请求示例

```bash
# 基本分页查询
curl -X GET "http://localhost:8080/testcases?page=1&page_size=10" \
  -H "Authorization: Bearer your_token_here"

# 按接口ID过滤
curl -X GET "http://localhost:8080/testcases?api_id=101&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按模块ID过滤
curl -X GET "http://localhost:8080/testcases?module_id=5&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按项目ID过滤
curl -X GET "http://localhost:8080/testcases?project_id=1&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按用例名称模糊查询
curl -X GET "http://localhost:8080/testcases?name=登录&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按用例编码精确查询
curl -X GET "http://localhost:8080/testcases?case_code=TC-API-101-001&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按优先级过滤
curl -X GET "http://localhost:8080/testcases?priority=P0&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按严重程度过滤
curl -X GET "http://localhost:8080/testcases?severity=high&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按状态过滤
curl -X GET "http://localhost:8080/testcases?status=active&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按模板用例过滤
curl -X GET "http://localhost:8080/testcases?is_template=false&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按创建人过滤
curl -X GET "http://localhost:8080/testcases?created_by=123&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 关键字搜索
curl -X GET "http://localhost:8080/testcases?search_keyword=用户&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 包含已删除用例
curl -X GET "http://localhost:8080/testcases?include_deleted=true&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按名称排序
curl -X GET "http://localhost:8080/testcases?sort_by=name&sort_order=asc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按优先级排序
curl -X GET "http://localhost:8080/testcases?sort_by=priority&sort_order=desc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按创建时间排序
curl -X GET "http://localhost:8080/testcases?sort_by=created_at&sort_order=desc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 按更新时间排序
curl -X GET "http://localhost:8080/testcases?sort_by=updated_at&sort_order=desc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 多条件组合查询
curl -X GET "http://localhost:8080/testcases?project_id=1&module_id=5&priority=P0&severity=high&status=active&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 大分页
curl -X GET "http://localhost:8080/testcases?page=1&page_size=100" \
  -H "Authorization: Bearer your_token_here"

# 超大分页（应该失败）
curl -X GET "http://localhost:8080/testcases?page=1&page_size=101" \
  -H "Authorization: Bearer your_token_here"

# 无效页码
curl -X GET "http://localhost:8080/testcases?page=0&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 无效分页大小
curl -X GET "http://localhost:8080/testcases?page=1&page_size=0" \
  -H "Authorization: Bearer your_token_here"

# 无效排序字段
curl -X GET "http://localhost:8080/testcases?sort_by=invalid_field&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 无效排序顺序
curl -X GET "http://localhost:8080/testcases?sort_order=invalid_order&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 无效优先级
curl -X GET "http://localhost:8080/testcases?priority=P5&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 无效严重程度
curl -X GET "http://localhost:8080/testcases?severity=invalid&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 无效状态
curl -X GET "http://localhost:8080/testcases?status=invalid&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 无认证令牌
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20"

# 过期令牌
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20" \
  -H "Authorization: Bearer expired_token_here"

# 无效令牌
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20" \
  -H "Authorization: Bearer invalid_token_here"

# 错误的请求方法
curl -X POST "http://localhost:8080/testcases?page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 错误的请求路径
curl -X GET "http://localhost:8080/testcase?page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 空查询参数
curl -X GET "http://localhost:8080/testcases" \
  -H "Authorization: Bearer your_token_here"

# 特殊字符查询
curl -X GET "http://localhost:8080/testcases?name=测试%20用例&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 长查询参数
curl -X GET "http://localhost:8080/testcases?name=这是一个非常长的测试用例名称用于测试系统对长名称的处理能力&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 边界值查询
curl -X GET "http://localhost:8080/testcases?page=1&page_size=1" \
  -H "Authorization: Bearer your_token_here"

# 最大页码
curl -X GET "http://localhost:8080/testcases?page=999999&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 负数页码
curl -X GET "http://localhost:8080/testcases?page=-1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 负数分页大小
curl -X GET "http://localhost:8080/testcases?page=1&page_size=-1" \
  -H "Authorization: Bearer your_token_here"

# 并发查询
curl -X GET "http://localhost:8080/testcases?page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here" &
curl -X GET "http://localhost:8080/testcases?page=2&page_size=20" \
  -H "Authorization: Bearer your_token_here" &
wait

# 压力查询
for i in {1..10}; do
  curl -X GET "http://localhost:8080/testcases?page=$i&page_size=10" \
    -H "Authorization: Bearer your_token_here"
done

# 不同排序组合
curl -X GET "http://localhost:8080/testcases?sort_by=name&sort_order=asc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?sort_by=name&sort_order=desc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?sort_by=case_code&sort_order=asc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?sort_by=case_code&sort_order=desc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 不同优先级组合
curl -X GET "http://localhost:8080/testcases?priority=P0&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?priority=P1&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?priority=P2&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?priority=P3&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 不同严重程度组合
curl -X GET "http://localhost:8080/testcases?severity=critical&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?severity=high&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?severity=medium&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?severity=low&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 不同状态组合
curl -X GET "http://localhost:8080/testcases?status=active&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?status=inactive&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 不同模板状态组合
curl -X GET "http://localhost:8080/testcases?is_template=true&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?is_template=false&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 不同删除状态组合
curl -X GET "http://localhost:8080/testcases?include_deleted=true&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?include_deleted=false&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 复杂组合查询
curl -X GET "http://localhost:8080/testcases?project_id=1&module_id=5&api_id=101&priority=P0&severity=high&status=active&is_template=false&created_by=123&search_keyword=登录&sort_by=created_at&sort_order=desc&page=1&page_size=20" \
  -H "Authorization: Bearer your_token_here"

# 统计摘要功能
curl -X GET "http://localhost:8080/testcases?page=1&page_size=5" \
  -H "Authorization: Bearer your_token_here"

# 分页边界
curl -X GET "http://localhost:8080/testcases?page=1&page_size=1" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?page=2&page_size=1" \
  -H "Authorization: Bearer your_token_here"
curl -X GET "http://localhost:8080/testcases?page=3&page_size=1" \
  -H "Authorization: Bearer your_token_here"
```

#### 响应数据

**成功响应** (HTTP 200):

```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "total": 125,
    "items": [
      {
        "case_id": 1001,
        "case_code": "TC-API-101-001",
        "api_id": 101,
        "api_name": "用户登录接口",
        "api_method": "POST",
        "api_path": "/api/v1/auth/login",
        "module_id": 5,
        "module_name": "用户管理模块",
        "project_id": 1,
        "project_name": "电商平台项目",
        "name": "用户登录-成功场景",
        "description": "测试用户使用正确凭据登录的成功情况",
        "priority": "P0",
        "severity": "high",
        "tags": ["冒烟测试", "登录功能"],
        "is_enabled": true,
        "is_template": false,
        "version": "1.0",
        "creator_info": {
          "user_id": 123,
          "name": "张三",
          "avatar_url": "/avatars/zhangsan.jpg"
        },
        "created_at": "2024-01-15T10:30:00.000Z",
        "updated_at": "2024-09-10T16:45:00.000Z",
        "is_deleted": false
      },
      {
        "case_id": 1002,
        "case_code": "TC-API-101-002",
        "api_id": 101,
        "api_name": "用户登录接口",
        "api_method": "POST",
        "api_path": "/api/v1/auth/login",
        "module_id": 5,
        "module_name": "用户管理模块",
        "project_id": 1,
        "project_name": "电商平台项目",
        "name": "用户登录-密码错误",
        "description": "测试密码错误时的登录情况",
        "priority": "P1",
        "severity": "medium",
        "tags": ["登录功能", "异常测试"],
        "is_enabled": true,
        "is_template": false,
        "version": "1.0",
        "creator_info": {
          "user_id": 123,
          "name": "张三",
          "avatar_url": "/avatars/zhangsan.jpg"
        },
        "created_at": "2024-01-16T14:20:00.000Z",
        "updated_at": "2024-08-20T11:15:00.000Z",
        "is_deleted": false
      },
      {
        "case_id": 1003,
        "case_code": "TC-API-102-001",
        "api_id": 102,
        "api_name": "用户注册接口",
        "api_method": "POST",
        "api_path": "/api/v1/auth/register",
        "module_id": 5,
        "module_name": "用户管理模块",
        "project_id": 1,
        "project_name": "电商平台项目",
        "name": "用户注册-成功场景",
        "description": "测试用户注册成功情况",
        "priority": "P0",
        "severity": "high",
        "tags": ["冒烟测试", "注册功能"],
        "is_enabled": true,
        "is_template": false,
        "version": "1.0",
        "creator_info": {
          "user_id": 456,
          "name": "李四",
          "avatar_url": "/avatars/lisi.jpg"
        },
        "created_at": "2024-01-17T09:15:00.000Z",
        "updated_at": "2024-07-25T13:30:00.000Z",
        "is_deleted": false
      }
    ],
    "page": 1,
    "page_size": 20,
    "summary": {
      "total_cases": 125,
      "by_priority": {
        "P0": 35,
        "P1": 50,
        "P2": 30,
        "P3": 10
      },
      "by_severity": {
        "critical": 20,
        "high": 45,
        "medium": 40,
        "low": 20
      },
      "by_status": {
        "active": 110,
        "inactive": 15
      }
    }
  }
}
```

**失败响应**:

```json
// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看测试用例列表",
  "data": null
}

// 参数错误 (HTTP 400)
{
  "code": -3,
  "msg": "分页大小不能超过100",
  "data": null
}

// 排序字段无效 (HTTP 400)
{
  "code": -3,
  "msg": "排序字段无效",
  "data": null
}

// 排序顺序无效 (HTTP 400)
{
  "code": -3,
  "msg": "排序顺序无效",
  "data": null
}
```

## 业务逻辑说明

### 查询流程
1. **认证与授权**: 验证 Token 和用户权限
2. **参数校验**: 验证查询参数的有效性
3. **设置默认值**: 设置分页和排序的默认值
4. **构建查询**: 多表关联查询测试用例信息
5. **过滤处理**: 根据查询参数进行多条件过滤
6. **排序处理**: 根据排序参数进行排序
7. **分页处理**: 根据分页参数计算分页偏移量
8. **统计摘要**: 计算用例统计信息
9. **返回结果**: 返回分页的用例列表和统计摘要

### 多表关联查询
- 查询 `TestCases` 表
- 关联 `Apis` 表获取接口信息
- 关联 `Modules` 表获取模块信息  
- 关联 `Projects` 表获取项目信息
- 关联 `Users` 表获取创建人信息
- 默认过滤已删除的用例

### 过滤处理
- 根据查询参数进行多条件过滤
- 处理标签过滤逻辑
- 处理关键字搜索
- 处理状态过滤（通过is_enabled转换）

### 排序处理
- 根据 `sort_by` 和 `sort_order` 参数进行排序
- 支持的排序字段：`name`, `case_code`, `priority`, `severity`, `created_at`, `updated_at`
- 支持的排序顺序：`asc`, `desc`

### 分页处理
- 根据 `page` 和 `page_size` 计算分页偏移量
- 默认页码：1
- 默认每页条数：20
- 最大每页条数：100

### 统计摘要
- 计算用例统计信息
- 按优先级统计
- 按严重程度统计
- 按状态统计

## 安全特性

### 1. 认证要求
- 使用`@GlobalInterceptor(checkLogin = true)`进行认证
- 必须提供有效的认证令牌

### 2. 权限控制
- 验证用户是否有测试用例列表查看权限
- 根据用户权限过滤可访问的用例数据

### 3. 参数验证
- 分页大小限制（最大100）
- 排序字段有效性验证
- 排序顺序有效性验证

### 4. 数据安全
- 默认过滤已删除的用例
- 支持包含已删除用例的查询
- 多表关联查询确保数据完整性

## 错误处理

### 错误码说明
| 错误码 | 含义 | HTTP状态码 |
|--------|------|------------|
| 1 | 成功 | 200 |
| 0 | 业务逻辑失败 | 200 |
| -1 | 认证失败 | 401 |
| -2 | 权限不足 | 200 |
| -3 | 参数校验失败 | 200 |
| -4 | 资源不存在 | 200 |
| -5 | 服务器内部异常 | 500 |

### 常见错误场景
1. **认证失败**: 未提供Token或Token无效/过期
2. **权限不足**: 用户没有测试用例列表查看权限
3. **参数错误**: 分页大小超限、排序字段无效等
4. **查询失败**: 数据库查询异常

## 性能优化

### 1. 索引优化
- 为常用查询字段建立索引（如 `api_id`, `priority`, `is_enabled` 等）
- 为关联表建立合适的索引
- 为排序字段建立索引

### 2. 查询优化
- 使用分页查询避免一次性返回大量数据
- 限制关键字搜索的字段范围，避免全表扫描
- 使用合适的JOIN策略

### 3. 缓存策略
- 对于统计信息，可以使用缓存或物化视图
- 对于频繁查询的数据，可以使用Redis缓存

### 4. 分页优化
- 使用LIMIT和OFFSET进行分页
- 对于大数据量，考虑使用游标分页

## 测试建议

1. **功能测试**：
   - 测试基本分页查询
   - 测试各种过滤条件
   - 测试排序功能
   - 测试统计摘要

2. **参数测试**：
   - 测试分页参数
   - 测试排序参数
   - 测试过滤参数

3. **边界测试**：
   - 测试分页边界值
   - 测试参数边界值
   - 测试大数据量查询

4. **性能测试**：
   - 测试查询性能
   - 测试并发查询
   - 测试压力查询

5. **权限测试**：
   - 测试认证失败
   - 测试权限不足
   - 测试不同权限级别

## 注意事项

1. **认证要求**: 此接口需要认证，请求头必须包含有效的 `Authorization` 字段
2. **权限检查**: 用户需要有测试用例列表查看权限
3. **分页限制**: 每页最大条数为100
4. **排序字段**: 只支持指定的排序字段
5. **统计摘要**: 提供完整的统计信息
6. **多表关联**: 涉及多表关联查询，需要合适的索引优化

## 后续扩展建议

1. **缓存优化**: 实现查询结果缓存
2. **搜索优化**: 实现全文搜索功能
3. **导出功能**: 支持数据导出
4. **批量操作**: 支持批量操作
5. **高级过滤**: 支持更复杂的过滤条件
6. **自定义排序**: 支持自定义排序规则
7. **数据统计**: 提供更详细的数据统计
8. **性能监控**: 提供查询性能监控
9. **查询历史**: 记录查询历史
10. **查询模板**: 支持查询模板保存
11. **数据可视化**: 提供数据可视化功能
12. **实时更新**: 支持实时数据更新
13. **数据同步**: 支持数据同步功能
14. **数据备份**: 支持数据备份功能
15. **数据恢复**: 支持数据恢复功能
16. **数据迁移**: 支持数据迁移功能
17. **数据清理**: 支持数据清理功能
18. **数据归档**: 支持数据归档功能
19. **数据审计**: 支持数据审计功能
20. **数据安全**: 增强数据安全功能
