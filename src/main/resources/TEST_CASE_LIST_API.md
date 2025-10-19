# 分页获取接口相关用例列表接口文档

## 接口概述

**接口名称**: 分页获取接口相关用例列表  
**接口路径**: `/apis/{api_id}/test-cases`  
**请求方式**: `GET`  
**接口描述**: 分页查询指定接口相关的测试用例列表。此接口需要认证。

## 请求参数

### 请求头
```
Authorization: Bearer {token}
Content-Type: application/json
```

### 路径参数
| 参数名 | 类型 | 是否必须 | 说明 |
|--------|------|----------|------|
| api_id | number | 必须 | 接口ID |

### 查询参数
| 参数名 | 类型 | 是否必须 | 说明 | 示例值 |
|--------|------|----------|------|--------|
| name | string | 否 | 用例名称模糊查询 | "登录" |
| priority | string | 否 | 优先级过滤 | "P0", "P1", "P2", "P3" |
| severity | string | 否 | 严重程度过滤 | "critical", "high", "medium", "low" |
| is_enabled | boolean | 否 | 是否启用过滤 | true, false |
| is_template | boolean | 否 | 是否模板用例过滤 | true, false |
| tags | string[] | 否 | 标签过滤（支持多个） | ["冒烟测试", "登录功能"] |
| created_by | number | 否 | 创建人ID过滤 | 123 |
| page | number | 否 | 页码，默认为1 | 1 |
| page_size | number | 否 | 每页条数，默认为10，最大100 | 10 |

## 响应数据

### 成功响应

**HTTP状态码**: `200`

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 8,
    "items": [
      {
        "case_id": 1001,
        "case_code": "TC-API-101-001",
        "api_id": 101,
        "name": "用户登录-成功场景",
        "description": "测试用户登录成功的情况",
        "priority": "P0",
        "severity": "high",
        "tags": ["冒烟测试", "登录功能"],
        "is_enabled": true,
        "is_template": false,
        "version": "1.0",
        "created_by": 123,
        "creator_name": "张三",
        "created_at": "2024-06-15T10:30:00.000Z",
        "updated_at": "2024-08-20T14:25:00.000Z"
      }
    ],
    "page": 1,
    "page_size": 10
  }
}
```

### 失败响应

#### 接口不存在
```json
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}
```

#### 认证失败
```json
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

## 响应字段说明

### 分页数据对象 (data)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | number | 符合条件的数据总条数 |
| items | object[] | 当前页的用例数据列表 |
| page | number | 当前页码 |
| page_size | number | 当前每页条数 |

### 用例对象 (items[])
| 字段名 | 类型 | 说明 |
|--------|------|------|
| case_id | number | 用例ID |
| case_code | string | 用例编码 |
| api_id | number | 接口ID |
| name | string | 用例名称 |
| description | string | 用例描述 |
| priority | string | 优先级: `P0`, `P1`, `P2`, `P3` |
| severity | string | 严重程度: `critical`, `high`, `medium`, `low` |
| tags | string[] | 标签数组 |
| is_enabled | boolean | 是否启用 |
| is_template | boolean | 是否为模板用例 |
| version | string | 版本号 |
| created_by | number | 创建人ID |
| creator_name | string | 创建人姓名 |
| created_at | string | 创建时间 (ISO 8601) |
| updated_at | string | 更新时间 (ISO 8601) |

## 业务逻辑

1. **认证与授权**: 通过JWT拦截器验证Token和用户权限
2. **验证接口**: 根据api_id检查接口是否存在且状态为active
3. **构建查询**: 
   - 查询TestCases表，过滤api_id
   - 根据查询参数进行过滤（名称模糊查询、优先级、严重程度、启用状态等）
   - 关联Users表获取创建人姓名
4. **标签过滤处理**: 如果提供了tags，解析标签参数，构建JSON条件查询
5. **分页处理**: 根据page和page_size计算分页偏移量
6. **返回结果**: 返回分页的用例列表

## 查询条件说明

### 优先级 (priority)
- `P0`: 最高优先级
- `P1`: 高优先级  
- `P2`: 中优先级
- `P3`: 低优先级

### 严重程度 (severity)
- `critical`: 严重
- `high`: 高
- `medium`: 中
- `low`: 低

### 标签查询 (tags)
- 支持多个标签的OR查询
- 使用JSON_CONTAINS函数进行JSON数组查询
- 示例: `?tags=冒烟测试&tags=登录功能`

## 错误码说明

| 错误码 | 含义 | HTTP状态码 | 处理建议 |
|--------|------|------------|----------|
| 1 | 成功 | 200 | - |
| -1 | 认证失败 | 401 | 清除本地Token，跳转至登录页 |
| -2 | 权限不足 | 403 | 提示用户"权限不足" |
| -4 | 资源不存在 | 404 | 提示用户"请求的资源不存在" |
| -5 | 服务器内部异常 | 500 | 提示用户"系统繁忙，请稍后再试" |

## 分页规范

### 请求参数
- `page`: 页码，默认为1
- `page_size`: 每页条数，默认为10，最大100

### 响应结构
- `total`: 符合条件的数据总条数
- `items`: 当前页的数据列表
- `page`: 当前页码
- `page_size`: 当前每页条数

## 技术实现

### 分页处理
使用PageHelper进行分页处理：
```java
PageHelper.startPage(query.getPage(), query.getPageSize());
List<TestCaseDTO> testCaseList = testCaseMapper.findTestCaseList(query);
PageInfo<TestCaseDTO> pageInfo = new PageInfo<>(testCaseList);
```

### 标签查询
使用MySQL的JSON_CONTAINS函数：
```sql
JSON_CONTAINS(tc.tags, JSON_QUOTE(#{tag}))
```

### 关联查询
关联Users表获取创建人姓名：
```sql
LEFT JOIN Users u ON tc.created_by = u.user_id
```

## 注意事项

1. **分页限制**: page_size最大值为100
2. **接口状态**: 只有状态为"active"的接口才能查询用例
3. **标签查询**: 支持多个标签的OR查询
4. **时间格式**: 所有时间字段使用ISO 8601格式
5. **权限验证**: 需要有效的JWT Token
6. **返回信息**: 返回的用例信息为基本信息，不包含详细的测试步骤、断言规则等敏感或大数据量信息

## 相关接口

- [用户登录接口](./README_AUTH.md)
- [获取当前用户信息接口](./CURRENT_USER_INFO_API.md)
