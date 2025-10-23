# 接口信息查询接口增强说明

## 问题描述
当前的接口信息查询接口返回的数据不完整，缺少数据库表中的重要字段，无法满足前端展示和测试执行的需求。

## 解决方案

### 1. 扩展 ApiDTO 字段
**文件**: `src/main/java/com/victor/iatms/entity/dto/ApiDTO.java`

**新增字段**:
- `baseUrl` - 基础URL
- `requestParameters` - 查询参数（JSON格式）
- `pathParameters` - 路径参数（JSON格式）
- `requestHeaders` - 请求头信息（JSON格式）
- `requestBody` - 请求体内容
- `authConfig` - 认证配置（JSON格式）
- `examples` - 请求示例（JSON格式）

### 2. 更新 SQL 查询
**文件**: `src/main/resources/mapper/ModuleMapper.xml`

**修改内容**:
- 在 `selectApiList` 查询中添加了所有缺失的字段
- 更新了 `ApiMap` 结果映射，正确映射新字段
- 为JSON字段配置了 `JsonTypeHandler` 类型处理器

**新增查询字段**:
```sql
a.base_url,
a.request_parameters,
a.path_parameters,
a.request_headers,
a.request_body,
a.auth_config,
a.examples,
```

### 3. 结果映射增强
**文件**: `src/main/resources/mapper/ModuleMapper.xml`

**新增映射**:
```xml
<result column="base_url" property="baseUrl"/>
<result column="request_parameters" property="requestParameters" typeHandler="com.victor.iatms.utils.JsonTypeHandler"/>
<result column="path_parameters" property="pathParameters" typeHandler="com.victor.iatms.utils.JsonTypeHandler"/>
<result column="request_headers" property="requestHeaders" typeHandler="com.victor.iatms.utils.JsonTypeHandler"/>
<result column="request_body" property="requestBody"/>
<result column="auth_config" property="authConfig" typeHandler="com.victor.iatms.utils.JsonTypeHandler"/>
<result column="examples" property="examples" typeHandler="com.victor.iatms.utils.JsonTypeHandler"/>
```

## 数据库字段对应关系

| 数据库字段 | Java 属性 | 类型 | 说明 |
|-----------|-----------|------|------|
| base_url | baseUrl | String | 基础URL |
| request_parameters | requestParameters | Object | 查询参数，JSON格式 |
| path_parameters | pathParameters | Object | 路径参数，JSON格式 |
| request_headers | requestHeaders | Object | 请求头信息，JSON格式 |
| request_body | requestBody | String | 请求体内容 |
| auth_config | authConfig | Object | 认证配置，JSON格式 |
| examples | examples | Object | 请求示例，JSON格式 |

## 增强后的接口响应示例

```json
{
  "code": 1,
  "msg": "查询接口列表成功",
  "data": {
    "total": 10,
    "items": [
      {
        "apiId": 1,
        "apiCode": "USER_LOGIN",
        "moduleId": 1,
        "name": "用户登录接口",
        "method": "POST",
        "path": "/api/user/login",
        "baseUrl": "https://api.example.com",
        "fullUrl": "https://api.example.com/api/user/login",
        "requestParameters": {
          "page": "integer",
          "size": "integer"
        },
        "pathParameters": {
          "userId": "integer"
        },
        "requestHeaders": {
          "Content-Type": "application/json",
          "Authorization": "Bearer {token}"
        },
        "requestBody": "{\"username\":\"string\",\"password\":\"string\"}",
        "requestBodyType": "json",
        "responseBodyType": "json",
        "description": "用户登录接口，支持用户名密码登录",
        "status": "active",
        "version": "1.0",
        "timeoutSeconds": 30,
        "authType": "bearer",
        "authConfig": {
          "tokenType": "Bearer",
          "headerName": "Authorization"
        },
        "tags": ["用户管理", "认证"],
        "examples": [
          {
            "name": "正常登录",
            "request": {
              "username": "admin",
              "password": "123456"
            },
            "response": {
              "code": 200,
              "data": {
                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
              }
            }
          }
        ],
        "preconditionCount": 2,
        "testCaseCount": 5,
        "creatorInfo": {
          "userId": 1,
          "name": "系统管理员",
          "avatarUrl": "https://avatar.example.com/admin.jpg"
        },
        "createdAt": "2025-10-23T10:00:00",
        "updatedAt": "2025-10-23T10:00:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "summary": {
      "totalApis": 10,
      "activeApis": 8,
      "deprecatedApis": 2,
      "totalTestCases": 50,
      "totalPreconditions": 20
    }
  }
}
```

## 改进效果

### 1. 数据完整性
- ✅ 返回所有数据库表中的重要字段
- ✅ 支持完整的接口信息展示
- ✅ 提供测试执行所需的全部参数

### 2. 功能增强
- ✅ 支持请求参数和路径参数的展示
- ✅ 支持请求头信息的配置
- ✅ 支持认证配置的详细信息
- ✅ 支持请求示例的展示

### 3. 开发体验
- ✅ 前端可以获取完整的接口信息
- ✅ 支持接口测试的完整参数配置
- ✅ 提供丰富的接口文档信息

## 注意事项

1. **JSON字段处理**: 所有JSON字段都使用 `JsonTypeHandler` 进行类型转换
2. **性能考虑**: 新增字段可能增加查询时间，建议根据实际需要选择性查询
3. **向后兼容**: 新增字段不会影响现有功能，保持向后兼容
4. **数据验证**: 前端需要处理JSON字段的解析和展示

---

**修改时间**: 2025-10-23
**状态**: ✅ 已完成
**影响范围**: 接口信息查询模块
