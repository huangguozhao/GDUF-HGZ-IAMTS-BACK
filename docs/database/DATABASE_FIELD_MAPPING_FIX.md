# 数据库字段映射修复说明

## 问题描述
在执行测试记录查询时出现 SQL 错误：
```
Unknown column 'd.name' in 'field list'
```

## 问题原因
数据库中的 `Departments` 表的字段名是 `department_name`，但我们的 SQL 查询中使用的是 `d.name`，导致字段名不匹配。

## 修复内容

### 1. 修复 UserMapper.xml 中的 SQL 查询
**文件**: `src/main/resources/mapper/UserMapper.xml`

**修改前**:
```xml
d.name as departmentName,
```

**修改后**:
```xml
d.department_name as departmentName,
```

### 2. 完善 Department 实体类
**文件**: `src/main/java/com/victor/iatms/entity/po/Department.java`

根据数据库表结构，添加了完整的字段映射：
- `departmentName` - 部门名称
- `parentId` - 父部门ID
- `level` - 部门层级
- `path` - 部门路径
- `isLeaf` - 是否为叶子部门
- `establishedDate` - 成立日期

## 数据库表结构对应关系

### Departments 表字段映射
| 数据库字段 | Java 属性 | 说明 |
|-----------|-----------|------|
| department_id | departmentId | 部门ID |
| department_name | departmentName | 部门名称 |
| parent_id | parentId | 父部门ID |
| manager_id | managerId | 部门负责人ID |
| description | description | 部门描述 |
| sort_order | sortOrder | 排序顺序 |
| level | level | 部门层级 |
| path | path | 部门路径 |
| is_leaf | isLeaf | 是否为叶子部门 |
| status | status | 部门状态 |
| established_date | establishedDate | 成立日期 |
| created_by | createdBy | 创建人ID |
| updated_by | updatedBy | 更新人ID |
| created_at | createdAt | 创建时间 |
| updated_at | updatedAt | 更新时间 |

## 修复后的 SQL 查询
```xml
<select id="findExecutorInfoById" parameterType="int" resultType="com.victor.iatms.entity.dto.ExecutorInfoDTO">
    SELECT 
        u.user_id as userId,
        u.name,
        u.email,
        u.avatar_url as avatarUrl,
        u.phone,
        u.department_id as departmentId,
        d.department_name as departmentName,
        u.employee_id as employeeId,
        u.position,
        u.status
    FROM Users u
    LEFT JOIN Departments d ON u.department_id = d.department_id
    WHERE u.user_id = #{userId}
    AND u.is_deleted = FALSE
</select>
```

## 验证
修复后，执行记录查询应该能够正常返回包含完整用户信息的响应：

```json
{
  "executorInfo": {
    "userId": 1,
    "name": "张三",
    "email": "zhangsan@example.com",
    "avatarUrl": "https://example.com/avatar/1.jpg",
    "phone": "13800138000",
    "departmentId": 10,
    "departmentName": "测试部",
    "employeeId": "EMP001",
    "position": "高级测试工程师",
    "status": "active"
  }
}
```

## 注意事项
1. 确保数据库中的 `Departments` 表已创建
2. 确保 `Users` 表的 `department_id` 字段正确关联到 `Departments` 表
3. 如果用户没有关联部门，`departmentName` 字段将为 `null`

---

**修复时间**: 2025-10-22
**状态**: ✅ 已修复

