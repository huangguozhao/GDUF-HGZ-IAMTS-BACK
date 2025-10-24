# 测试用例 test_type 字段添加总结

## 修改内容

为 `TestCases` 表添加了 `test_type` 字段，用于标识测试用例的类型。

### 数据库修改

```sql
-- 添加测试类型字段
ALTER TABLE TestCases 
ADD COLUMN test_type ENUM('functional', 'performance', 'security', 'compatibility', 'smoke', 'regression') 
DEFAULT 'functional' 
COMMENT '测试类型：functional-功能, performance-性能, security-安全, compatibility-兼容性, smoke-冒烟, regression-回归' 
AFTER description;

-- 为测试类型字段添加索引
ALTER TABLE TestCases 
ADD INDEX idx_test_type (test_type);
```

### 字段说明

- **字段名**: `test_type`
- **类型**: ENUM
- **可选值**:
  - `functional` - 功能测试（默认值）
  - `performance` - 性能测试
  - `security` - 安全测试
  - `compatibility` - 兼容性测试
  - `smoke` - 冒烟测试
  - `regression` - 回归测试
- **位置**: 在 `description` 字段之后
- **索引**: 已添加 `idx_test_type` 索引

## 代码修改清单

### 1. 实体类 (Entity)

#### ✅ TestCase.java
**文件路径**: `src/main/java/com/victor/iatms/entity/po/TestCase.java`

**修改内容**:
```java
/**
 * 测试类型：functional-功能, performance-性能, security-安全, 
 * compatibility-兼容性, smoke-冒烟, regression-回归
 */
private String testType;
```

### 2. DTO类

#### ✅ TestCaseDTO.java
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/TestCaseDTO.java`

**修改内容**:
```java
/**
 * 测试类型：functional-功能, performance-性能, security-安全, 
 * compatibility-兼容性, smoke-冒烟, regression-回归
 */
private String testType;
```

#### ✅ TestCaseItemDTO.java
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/TestCaseItemDTO.java`

**修改内容**:
```java
/**
 * 测试类型：functional-功能, performance-性能, security-安全, 
 * compatibility-兼容性, smoke-冒烟, regression-回归
 */
private String testType;
```

#### ✅ CreateTestCaseDTO.java
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/CreateTestCaseDTO.java`

**修改内容**:
```java
/**
 * 测试类型：functional-功能, performance-性能, security-安全, 
 * compatibility-兼容性, smoke-冒烟, regression-回归
 */
private String testType;
```

#### ✅ UpdateTestCaseDTO.java
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/UpdateTestCaseDTO.java`

**修改内容**:
```java
/**
 * 测试类型：functional-功能, performance-性能, security-安全, 
 * compatibility-兼容性, smoke-冒烟, regression-回归
 */
private String testType;
```

### 3. 查询类 (Query)

#### ✅ TestCaseQuery.java
**文件路径**: `src/main/java/com/victor/iatms/entity/query/TestCaseQuery.java`

**修改内容**:
```java
/**
 * 测试类型过滤：functional, performance, security, compatibility, smoke, regression
 */
private String testType;
```

### 4. MyBatis映射文件

#### ✅ TestCaseMapper.xml
**文件路径**: `src/main/resources/mapper/TestCaseMapper.xml`

**修改内容**:

##### 4.1 ResultMap映射
```xml
<!-- TestCaseMap -->
<result column="test_type" property="testType"/>

<!-- TestCaseItemMap -->
<result column="test_type" property="testType"/>
```

##### 4.2 INSERT语句
```xml
<insert id="insert">
    INSERT INTO TestCases (
        ...
        description,
        test_type,  <!-- 新增 -->
        priority,
        ...
    ) VALUES (
        ...
        #{description},
        #{testType},  <!-- 新增 -->
        #{priority},
        ...
    )
</insert>
```

##### 4.3 SELECT语句
```xml
<!-- selectById -->
SELECT 
    ...
    description,
    test_type,  <!-- 新增 -->
    priority,
    ...
FROM TestCases

<!-- selectTestCaseList -->
SELECT 
    ...
    tc.description,
    tc.test_type,  <!-- 新增 -->
    tc.priority,
    ...
FROM TestCases tc
```

##### 4.4 UPDATE语句
```xml
<update id="updateById">
    UPDATE TestCases
    <set>
        ...
        <if test="description != null">description = #{description},</if>
        <if test="testType != null">test_type = #{testType},</if>  <!-- 新增 -->
        <if test="priority != null">priority = #{priority},</if>
        ...
    </set>
</update>
```

##### 4.5 WHERE条件过滤（3处）
```xml
<!-- selectTestCaseList -->
<if test="queryDTO.testType != null and queryDTO.testType != ''">
    AND tc.test_type = #{queryDTO.testType}
</if>

<!-- countTestCaseList -->
<if test="queryDTO.testType != null and queryDTO.testType != ''">
    AND tc.test_type = #{queryDTO.testType}
</if>

<!-- selectTestCaseSummary -->
<if test="queryDTO.testType != null and queryDTO.testType != ''">
    AND tc.test_type = #{queryDTO.testType}
</if>
```

## 使用示例

### 1. 创建测试用例时指定类型

```json
{
  "apiId": 1,
  "name": "登录功能测试",
  "description": "测试用户登录功能",
  "testType": "functional",
  "priority": "P0",
  "severity": "critical"
}
```

### 2. 更新测试用例类型

```json
{
  "testType": "smoke"
}
```

### 3. 按测试类型查询

**请求示例**:
```
GET /api/test-cases?testType=functional&page=1&pageSize=10
```

**查询参数**:
- `testType`: 测试类型（可选值：functional, performance, security, compatibility, smoke, regression）

### 4. 组合查询

```
GET /api/test-cases?apiId=1&testType=smoke&priority=P0&page=1&pageSize=20
```

## API接口影响

### 受影响的接口

所有测试用例相关的接口都已支持 `testType` 字段：

1. **创建测试用例** - `POST /api/test-cases`
   - 请求体可包含 `testType` 字段
   - 如不提供，默认为 `functional`

2. **更新测试用例** - `PUT /api/test-cases/{caseId}`
   - 请求体可包含 `testType` 字段
   - 可单独更新测试类型

3. **查询测试用例** - `GET /api/test-cases/{caseId}`
   - 响应中包含 `testType` 字段

4. **查询测试用例列表** - `GET /api/test-cases`
   - 支持 `testType` 查询参数过滤
   - 响应列表中每项包含 `testType` 字段

5. **查询测试用例统计** - `GET /api/test-cases/summary`
   - 支持 `testType` 查询参数过滤

## 前端集成建议

### 1. 测试类型选择器

```javascript
const testTypes = [
  { value: 'functional', label: '功能测试' },
  { value: 'performance', label: '性能测试' },
  { value: 'security', label: '安全测试' },
  { value: 'compatibility', label: '兼容性测试' },
  { value: 'smoke', label: '冒烟测试' },
  { value: 'regression', label: '回归测试' }
];
```

### 2. 测试类型显示

```javascript
const testTypeLabels = {
  'functional': '功能',
  'performance': '性能',
  'security': '安全',
  'compatibility': '兼容性',
  'smoke': '冒烟',
  'regression': '回归'
};

// 使用
const displayLabel = testTypeLabels[testCase.testType] || '未知';
```

### 3. 测试类型图标/颜色

```javascript
const testTypeStyles = {
  'functional': { icon: '⚙️', color: '#1890ff' },
  'performance': { icon: '⚡', color: '#faad14' },
  'security': { icon: '🔒', color: '#f5222d' },
  'compatibility': { icon: '🔄', color: '#52c41a' },
  'smoke': { icon: '💨', color: '#722ed1' },
  'regression': { icon: '🔁', color: '#13c2c2' }
};
```

## 数据迁移

### 现有数据处理

由于字段有默认值 `functional`，所有现有的测试用例会自动设置为 `functional` 类型。

如需批量更新现有数据，可执行：

```sql
-- 示例：将所有冒烟测试标记为smoke类型
UPDATE TestCases 
SET test_type = 'smoke' 
WHERE name LIKE '%冒烟%' 
  OR description LIKE '%冒烟%';

-- 示例：将所有性能测试标记为performance类型
UPDATE TestCases 
SET test_type = 'performance' 
WHERE name LIKE '%性能%' 
  OR description LIKE '%性能%'
  OR tags LIKE '%性能%';
```

## 验证测试

### 1. 数据库验证

```sql
-- 查看字段是否添加成功
DESCRIBE TestCases;

-- 查看索引是否创建成功
SHOW INDEX FROM TestCases WHERE Key_name = 'idx_test_type';

-- 查看现有数据的test_type分布
SELECT test_type, COUNT(*) as count 
FROM TestCases 
WHERE is_deleted = FALSE 
GROUP BY test_type;
```

### 2. API测试

```bash
# 创建测试用例（包含testType）
curl -X POST http://localhost:8080/api/test-cases \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "apiId": 1,
    "name": "性能测试用例",
    "testType": "performance",
    "priority": "P1"
  }'

# 按testType查询
curl "http://localhost:8080/api/test-cases?testType=performance" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 注意事项

1. **默认值**: 新创建的测试用例如果不指定 `testType`，默认为 `functional`

2. **枚举值**: 只能使用预定义的6种测试类型，传入其他值会导致数据库错误

3. **向后兼容**: 现有API调用如果不传 `testType` 字段，不会影响功能

4. **查询优化**: 已为 `test_type` 字段添加索引，按测试类型查询性能较好

5. **前端验证**: 建议在前端也添加测试类型的枚举验证，避免无效请求

## 完成状态

✅ 所有修改已完成，无编译错误

- ✅ 数据库字段添加
- ✅ 数据库索引创建
- ✅ 实体类修改
- ✅ DTO类修改
- ✅ 查询类修改
- ✅ MyBatis映射文件修改
- ✅ 编译检查通过

## 下一步建议

1. 重启应用服务器
2. 执行API测试验证功能
3. 更新前端代码以支持测试类型选择和显示
4. 更新API文档
5. 如有需要，批量更新现有测试用例的类型

