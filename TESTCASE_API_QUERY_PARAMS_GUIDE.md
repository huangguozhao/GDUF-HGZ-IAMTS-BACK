# 测试用例查询API参数说明

## 接口信息

- **接口路径**: `GET /api/testcases`
- **接口描述**: 分页查询测试用例列表
- **认证要求**: 需要Bearer Token

## ⚠️ 重要提示

**参数名称使用驼峰命名（camelCase），不是下划线命名！**

## 正确的查询参数

| 参数名（驼峰） | 类型 | 说明 | 示例 |
|---------------|------|------|------|
| `apiId` | Integer | ✅ 接口ID过滤 | `?apiId=1` |
| `moduleId` | Integer | ✅ 模块ID过滤 | `?moduleId=2` |
| `projectId` | Integer | ✅ 项目ID过滤 | `?projectId=1` |
| `name` | String | 用例名称模糊查询 | `?name=登录` |
| `caseCode` | String | 用例编码精确查询 | `?caseCode=TC_AUTH001_001` |
| `priority` | String | 优先级过滤 | `?priority=P0` |
| `severity` | String | 严重程度过滤 | `?severity=critical` |
| `status` | String | 状态过滤 | `?status=active` |
| `isTemplate` | Boolean | 是否模板用例 | `?isTemplate=false` |
| `tags` | Array | 标签过滤 | `?tags=登录,认证` |
| `createdBy` | Integer | 创建人ID | `?createdBy=1` |
| `includeDeleted` | Boolean | 是否包含已删除 | `?includeDeleted=false` |
| `searchKeyword` | String | 关键字搜索 | `?searchKeyword=登录` |
| `sortBy` | String | 排序字段 | `?sortBy=created_at` |
| `sortOrder` | String | 排序顺序 | `?sortOrder=desc` |
| `page` | Integer | 页码 | `?page=1` |
| `pageSize` | Integer | 每页条数 | `?pageSize=20` |

## API调用示例

### 1. 查询某个接口下的所有测试用例

```bash
# ✅ 正确
GET /api/testcases?apiId=1

# ❌ 错误（使用了下划线）
GET /api/testcases?api_id=1
```

### 2. 查询某个模块下的测试用例

```bash
# ✅ 正确
GET /api/testcases?moduleId=2&page=1&pageSize=10

# ❌ 错误
GET /api/testcases?module_id=2&page=1&page_size=10
```

### 3. 组合查询

```bash
# 查询接口1下，优先级为P0的测试用例
GET /api/testcases?apiId=1&priority=P0&sortBy=created_at&sortOrder=desc
```

### 4. 前端调用示例（JavaScript）

```javascript
// ✅ 正确的调用方式
const queryParams = {
  apiId: 1,           // 驼峰命名
  priority: 'P0',
  page: 1,
  pageSize: 20
};

// 构建查询字符串
const queryString = new URLSearchParams(queryParams).toString();

// 发送请求
fetch(`/api/testcases?${queryString}`, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

// 或者使用axios
axios.get('/api/testcases', {
  params: {
    apiId: 1,         // 驼峰命名
    priority: 'P0',
    page: 1,
    pageSize: 20
  },
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

## 参数绑定说明

Spring Boot会自动将查询参数绑定到DTO对象的字段：

| URL参数 | DTO字段 | 绑定结果 |
|---------|---------|---------|
| `?apiId=1` | `private Integer apiId` | ✅ 绑定成功 |
| `?api_id=1` | `private Integer apiId` | ❌ 绑定失败（名称不匹配） |

## 常见问题

### Q1: 为什么传了api_id但是没有筛选？

**A**: 参数名称不匹配。应该使用 `apiId`（驼峰命名）而不是 `api_id`（下划线命名）。

### Q2: 如何同时按多个条件筛选？

**A**: 直接在URL中添加多个参数即可：
```
/api/testcases?apiId=1&priority=P0&severity=critical
```

### Q3: 如何查询某个接口下的所有测试用例？

**A**: 使用 `apiId` 参数：
```
/api/testcases?apiId=1
```

## 调试技巧

### 1. 检查参数是否正确传递

查看后端日志，确认参数值：
```
Method Arguments:
  apiId : 1          ← 应该能看到这个
  priority : P0
```

### 2. 检查SQL查询条件

查看MyBatis生成的SQL：
```sql
WHERE tc.is_deleted = FALSE 
AND tc.api_id = ?    ← 应该有这个条件
```

### 3. 前端Network面板

检查实际发送的URL：
```
Request URL: http://localhost:8080/api/testcases?apiId=1
                                                  ^^^^^^
                                            确认是驼峰命名
```

## 总结

✅ **正确**：使用驼峰命名（camelCase）
- `apiId`, `moduleId`, `projectId`, `pageSize`, `sortBy`, `sortOrder`

❌ **错误**：使用下划线命名（snake_case）
- `api_id`, `module_id`, `project_id`, `page_size`, `sort_by`, `sort_order`

**记住**：Java后端使用驼峰命名规范！

