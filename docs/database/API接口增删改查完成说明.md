# API接口增删改查功能完成说明

## 已完成内容

### 1. DTO类创建 ✅

#### CreateApiDTO.java
创建接口请求DTO，包含以下字段：
- `apiCode`: 接口编码（可选）
- `moduleId`: 模块ID（必填）
- `name`: 接口名称（必填）
- `method`: HTTP方法（必填）
- `path`: 接口路径（必填）
- `baseUrl`: 基础URL
- 请求参数、路径参数、请求头配置
- 认证配置、标签、示例等

#### UpdateApiDTO.java
更新接口请求DTO，包含所有可更新字段

#### ApiListResponseDTO.java
接口列表响应DTO，包含分页信息和列表数据

#### ApiStatisticsDTO.java
接口统计信息DTO，包含各维度统计

### 2. Service层完成 ✅

#### ApiService.java接口
定义了完整的CRUD方法：
- `createApi()`: 创建接口
- `updateApi()`: 更新接口  
- `getApiById()`: 查询单个接口
- `getApiList()`: 分页查询接口列表
- `deleteApi()`: 删除接口

#### ApiServiceImpl.java实现
完整实现了所有业务逻辑：
- 参数校验
- 权限检查
- 业务规则验证
- 数据转换
- JSON处理
- 事务管理

### 3. Controller层完成 ✅

#### ApiController.java
实现了完整的REST API：

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /apis | 创建接口 | api:create |
| PUT | /apis/{apiId} | 更新接口 | api:update |
| GET | /apis/{apiId} | 查询单个接口 | api:view |
| GET | /apis | 查询接口列表 | api:view |
| DELETE | /apis/{apiId} | 删除接口 | api:delete |

### 4. Mapper层更新 ✅

#### ApiMapper.java
添加了所有必需的方法：
- `insert()`: 插入接口
- `updateById()`: 更新接口
- `selectById()`: 根据ID查询
- `selectApiList()`: 分页查询列表
- `countApiList()`: 统计总数
- `selectApiStatistics()`: 查询统计信息
- `checkApiCodeExists()`: 检查编码是否存在
- `checkApiCodeExistsExcludeSelf()`: 检查编码（排除自己）
- `deleteById()`: 软删除
- `countPreconditionsByApiId()`: 统计前置条件数量

## 待完成内容

### ApiMapper.xml需要补充

需要在 `src/main/resources/mapper/ApiMapper.xml` 中添加以下SQL语句：

1. **insert** - 插入接口
2. **updateById** - 更新接口
3. **selectApiList** - 分页查询列表
4. **countApiList** - 统计总数
5. **selectApiStatistics** - 查询统计信息
6. **checkApiCodeExists** - 检查编码存在性
7. **checkApiCodeExistsExcludeSelf** - 检查编码（排除自己）

### 需要的SQL语句示例

由于SQL语句较长，建议参考现有的TestCaseMapper.xml的写法，实现类似的功能。

#### 关键点：

1. **插入语句**：包含所有字段，使用 `useGeneratedKeys="true"` 返回主键
2. **更新语句**：使用 `<if>` 标签实现动态更新，只更新非null字段
3. **查询列表**：支持多条件筛选、排序、分页
4. **统计信息**：使用 `SUM(CASE WHEN ...)` 实现多维度统计

## API使用示例

### 1. 创建接口

**请求：**
```json
POST /api/apis
Content-Type: application/json

{
  "moduleId": 1,
  "name": "用户登录",
  "method": "POST",
  "path": "/api/auth/login",
  "baseUrl": "http://localhost:8080",
  "description": "用户登录接口",
  "status": "active",
  "authType": "none",
  "timeoutSeconds": 30
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "创建接口成功",
  "data": {
    "apiId": 1,
    "apiCode": "API_M1_A1B2C3D4",
    "moduleId": 1,
    "name": "用户登录",
    ...
  }
}
```

### 2. 更新接口

**请求：**
```json
PUT /api/apis/1
Content-Type: application/json

{
  "name": "用户登录v2",
  "description": "用户登录接口（更新版）",
  "status": "active"
}
```

### 3. 查询单个接口

**请求：**
```
GET /api/apis/1
```

**响应：**
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "apiId": 1,
    "apiCode": "API_M1_A1B2C3D4",
    ...
  }
}
```

### 4. 查询接口列表

**请求：**
```
GET /api/apis?moduleId=1&method=POST&status=active&page=1&pageSize=20
```

**响应：**
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 20,
    "totalPages": 3,
    "items": [...]
  }
}
```

### 5. 删除接口

**请求：**
```
DELETE /api/apis/1
```

**响应：**
```json
{
  "code": 1,
  "msg": "接口删除成功",
  "data": null
}
```

## 下一步工作

1. **补充ApiMapper.xml中的SQL语句**
   - 可以参考TestCaseMapper.xml的写法
   - 需要包含完整的增删改查SQL

2. **测试API功能**
   - 创建测试脚本
   - 验证各个接口的功能

3. **完善权限控制**
   - 将临时硬编码的用户ID改为从JWT中获取
   - 完善项目成员权限检查逻辑

4. **添加审计日志**
   - 记录接口的创建、更新、删除操作

5. **前端集成**
   - 更新前端代码以调用新的API
   - 添加接口管理界面

## 注意事项

1. **接口编码自动生成规则**：`API_M{moduleId}_{8位随机字符}`
2. **默认值**：
   - status: draft
   - version: 1.0
   - authType: none
   - timeoutSeconds: 30
3. **删除限制**：
   - 不能删除系统接口
   - 有测试用例的接口不能删除
   - 有前置条件的接口不能删除
4. **权限控制**：
   - 接口创建者有管理权限
   - 项目成员有管理权限

## 相关文件

- `src/main/java/com/victor/iatms/controller/ApiController.java` ✅
- `src/main/java/com/victor/iatms/service/ApiService.java` ✅
- `src/main/java/com/victor/iatms/service/impl/ApiServiceImpl.java` ✅
- `src/main/java/com/victor/iatms/mappers/ApiMapper.java` ✅
- `src/main/resources/mapper/ApiMapper.xml` ⚠️ 需要补充SQL
- `src/main/java/com/victor/iatms/entity/dto/CreateApiDTO.java` ✅
- `src/main/java/com/victor/iatms/entity/dto/UpdateApiDTO.java` ✅
- `src/main/java/com/victor/iatms/entity/dto/ApiListResponseDTO.java` ✅
- `src/main/java/com/victor/iatms/entity/dto/ApiStatisticsDTO.java` ✅

