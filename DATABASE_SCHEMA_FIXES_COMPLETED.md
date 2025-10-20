# 数据库架构修复总结

## 修复日期
2025-10-20

## 问题描述
代码中的实体类、Mapper和SQL查询与实际数据库表结构不匹配，导致多个 `Unknown column` 错误。

## 已完成的修复

### 1. Projects 表修复

**问题字段：**
- ❌ `project_code` - 数据库中不存在
- ❌ `status` - 数据库中不存在  
- ❌ `version` - 数据库中不存在
- ❌ `updated_by` - 数据库中不存在
- ❌ `createdBy` (Java) - 应该是 `creatorId`

**实际数据库字段：**
```sql
CREATE TABLE Projects (
    project_id INT,
    name VARCHAR(255),
    description TEXT,
    creator_id INT,          -- ✓ 使用 creator_id，不是 created_by
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN,
    deleted_at TIMESTAMP,
    deleted_by INT
);
```

**修复内容：**
- ✅ 从 `Project.java` 实体类中移除 `projectCode`、`status`、`version`、`updatedBy` 字段
- ✅ 将 `createdBy` 重命名为 `creatorId`
- ✅ 更新 `ProjectMapper.xml` 中的所有SQL查询
- ✅ 更新 `ProjectServiceImpl.java` 中的所有方法调用
- ✅ 删除 `ProjectMapper.java` 接口中的 `selectByCode` 和 `checkProjectCodeExists` 方法

### 2. Modules 表修复

**问题字段：**
- ❌ `m.creator_id` - 应该是 `m.created_by`

**实际数据库字段：**
```sql
CREATE TABLE Modules (
    module_id INT,
    module_code VARCHAR(50),
    project_id INT,
    parent_module_id INT,
    name VARCHAR(255),
    description TEXT,
    sort_order INT,
    status ENUM('active', 'inactive', 'archived'),
    owner_id INT,
    tags JSON,
    created_by INT,          -- ✓ 使用 created_by，不是 creator_id
    updated_by INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN,
    deleted_at TIMESTAMP,
    deleted_by INT
);
```

**修复内容：**
- ✅ 将 `ProjectMapper.xml` 中所有的 `m.creator_id` 替换为 `m.created_by`
- ✅ 更新所有涉及模块查询的 JOIN 语句

### 3. ProjectMembers 表修复

**问题字段：**
- ❌ `pm.creator_id` - 应该是 `pm.created_by`

**修复内容：**
- ✅ 将 `ProjectMapper.xml` 中所有的 `pm.creator_id` 替换为 `pm.created_by`
- ✅ 更新所有涉及项目成员查询的 JOIN 语句

## 数据库字段命名规范总结

根据实际数据库表结构，各表使用的创建人字段名称：

| 表名 | 创建人字段名 | 更新人字段名 |
|------|------------|------------|
| **Projects** | `creator_id` | ❌ 不存在 |
| **Modules** | `created_by` | `updated_by` |
| **Apis** | `created_by` | `updated_by` |
| **ApiPreconditions** | `created_by` | `updated_by` |
| **Users** | `creator_id` | ❌ 不存在 |
| **ProjectMembers** | `created_by` (推测) | `updated_by` (推测) |

## 修复的文件清单

### Java文件
1. `src/main/java/com/victor/iatms/entity/po/Project.java`
   - 移除 `projectCode`、`status`、`version`、`updatedBy` 字段
   - 将 `createdBy` 重命名为 `creatorId`

2. `src/main/java/com/victor/iatms/service/impl/ProjectServiceImpl.java`
   - 更新所有 `getCreatedBy()` 为 `getCreatorId()`
   - 更新所有 `setCreatedBy()` 为 `setCreatorId()`
   - 移除所有对 `updatedBy`、`status`、`version` 的设置
   - 移除 `checkProjectCodeExists` 方法实现
   - 修改 `getProjectByCode` 方法抛出 `UnsupportedOperationException`

3. `src/main/java/com/victor/iatms/mappers/ProjectMapper.java`
   - 删除 `selectByCode` 方法
   - 删除 `checkProjectCodeExists` 方法

### XML文件
1. `src/main/resources/mapper/ProjectMapper.xml`
   - 更新 `ProjectMap` resultMap，移除不存在的字段映射
   - 更新 `selectById` 查询，移除不存在的字段
   - 更新 `insert` 语句，移除不存在的字段
   - 更新 `updateById` 语句，移除不存在的字段
   - 删除 `selectByCode` 查询
   - 删除 `checkProjectCodeExists` 查询
   - 将所有 `m.creator_id` 替换为 `m.created_by`
   - 将所有 `pm.creator_id` 替换为 `pm.created_by`
   - 更新所有相关的 JOIN 语句

## 注意事项

### ⚠️ Java版本问题
项目当前使用 Java 8，但 Spring Boot 3.5.5 需要 Java 17。
需要升级 Java 版本或降级 Spring Boot 版本。

**错误信息：**
```
类文件具有错误的版本 61.0, 应为 52.0
```

**版本对应：**
- Java 8 = 52.0
- Java 11 = 55.0
- Java 17 = 61.0

### ✅ 编译状态
- 代码修复已完成
- Maven编译因Java版本问题失败
- 一旦解决Java版本问题，代码应该能够正常编译

### 4. SQL语法错误修复

**问题：** MySQL的 `OFFSET` 子句不支持表达式计算

**错误SQL：**
```sql
LIMIT #{pageSize} OFFSET #{pageSize} * (#{page} - 1)
```

**修复为：**
```sql
LIMIT #{pageSize} OFFSET #{offset}
```

**修复的文件：**
- ✅ `ModuleMapper.xml` - `selectApiList` 查询
- ✅ `ProjectMapper.xml` - `selectRecentProjects` 查询  
- ✅ `TestCaseMapper.xml` - 测试用例列表查询

**说明：** offset值应该在Service层计算好（`offset = (page - 1) * pageSize`），然后传入SQL查询。

## 建议

1. **立即行动：** 升级到 Java 17 或降级 Spring Boot 到 2.x 版本
2. **长期规划：** 统一所有表的字段命名规范，建议使用 `created_by` 和 `updated_by`
3. **代码审查：** 检查其他模块是否也存在类似的字段不匹配问题
4. **文档更新：** 维护一份完整的数据库表结构文档
5. **分页查询：** 确保所有Service层在调用Mapper前都正确计算offset值

## 测试建议

修复完成后，建议测试以下功能：
- ✅ 项目创建
- ✅ 项目查询（按ID）
- ✅ 项目更新
- ✅ 项目删除
- ✅ 模块列表查询
- ✅ 项目成员查询
- ✅ 最近编辑的项目查询

