# SQL分页查询语法修复说明

## 问题描述

MySQL的 `OFFSET` 子句**不支持表达式计算**，只能接受预先计算好的数值。

## 错误示例

❌ **错误的SQL：**
```sql
LIMIT #{pageSize} OFFSET #{pageSize} * (#{page} - 1)
```

**错误信息：**
```
You have an error in your SQL syntax; check the manual that corresponds 
to your MySQL server version for the right syntax to use near '* (1 - 1)' at line 40
```

## 正确做法

✅ **修复后的SQL：**
```sql
LIMIT #{pageSize} OFFSET #{offset}
```

**在Service层计算offset：**
```java
// 计算分页偏移量
int offset = (page - 1) * pageSize;
queryDTO.setOffset(offset);
```

## 已修复的文件

| 文件 | 查询方法 | 行号 |
|------|---------|------|
| `ModuleMapper.xml` | `selectApiList` | 378 |
| `ProjectMapper.xml` | `selectRecentProjects` | 731 |
| `TestCaseMapper.xml` | 测试用例列表查询 | 365 |

## 完整示例

### DTO类添加offset字段

```java
public class ApiListQueryDTO {
    private Integer page;
    private Integer pageSize;
    private Integer offset;  // ← 添加这个字段
    
    // getters and setters...
}
```

### Service层计算offset

```java
public ApiListResponseDTO getApiList(ApiListQueryDTO queryDTO) {
    // 参数校验和默认值设置
    if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
        queryDTO.setPage(1);
    }
    if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
        queryDTO.setPageSize(10);
    }
    
    // ✓ 计算offset
    int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
    queryDTO.setOffset(offset);
    
    // 调用Mapper查询
    List<ApiDTO> items = moduleMapper.selectApiList(queryDTO);
    Long total = moduleMapper.countApiList(queryDTO);
    
    // 构建响应...
}
```

### Mapper XML

```xml
<select id="selectApiList" resultMap="ApiMap">
    SELECT 
        a.api_id,
        a.name,
        a.method,
        a.path
        -- ...其他字段
    FROM Apis a
    WHERE a.module_id = #{moduleId}
    ORDER BY a.created_at DESC
    <!-- ✓ 使用计算好的offset值 -->
    <if test="pageSize != null and offset != null">
        LIMIT #{pageSize} OFFSET #{offset}
    </if>
</select>
```

## 为什么不能在SQL中计算？

MySQL的 `LIMIT` 和 `OFFSET` 子句期望的是**常量值**或**变量**，不支持**表达式**。

### 允许的写法：
```sql
LIMIT 10 OFFSET 20              -- ✓ 常量
LIMIT #{pageSize} OFFSET #{offset}  -- ✓ 变量
LIMIT 10 OFFSET (SELECT COUNT(*) / 2 FROM table)  -- ✓ 子查询（返回单个值）
```

### 不允许的写法：
```sql
LIMIT #{pageSize} OFFSET #{pageSize} * (#{page} - 1)  -- ✗ 表达式
LIMIT #{pageSize} OFFSET #{offset} + 10  -- ✗ 运算
```

## 验证修复

修复后，分页查询应该正常工作：

```bash
# 测试API列表查询
curl "http://localhost:8080/api/modules/1/apis?page=2&pageSize=10"

# 应该返回第2页的10条记录（offset=10）
```

## 相关文档

- [MySQL LIMIT语法](https://dev.mysql.com/doc/refman/8.0/en/select.html)
- [MyBatis动态SQL](https://mybatis.org/mybatis-3/dynamic-sql.html)

