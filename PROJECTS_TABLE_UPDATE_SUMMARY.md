# Projects表结构升级总结

## 更新概述

根据新的数据库表结构需求，对`Projects`表进行了重大升级，添加了4个新字段以完善项目管理功能，支持项目编码、类型分类、状态管理和视觉标识。

## 新增字段说明

### 1. 项目编码（project_code）
- **类型**: VARCHAR(50) NOT NULL UNIQUE
- **说明**: 项目的唯一短代码标识
- **特点**: 
  - 唯一约束，不可重复
  - URL友好的标识符
  - 适合用于API路径、报告引用等场景
- **格式建议**:
  - 简单递增：PROJ-000001, PROJ-000002
  - 类型前缀：WEB-000001, API-000001
  - 年份编号：2024-WEB-001, 2024-API-001
  - 业务前缀：USER-SYS-001, ORDER-SYS-001

### 2. 项目类型（project_type）
- **类型**: ENUM('WEB', 'MOBILE', 'API', 'DESKTOP', 'HYBRID')
- **默认值**: 'API'
- **说明**: 标识项目的技术类型
- **枚举值**:
  - `WEB`: Web应用项目
  - `MOBILE`: 移动应用项目（iOS/Android）
  - `API`: API/后端服务项目
  - `DESKTOP`: 桌面应用项目
  - `HYBRID`: 混合型项目（跨平台）

### 3. 项目状态（status）
- **类型**: ENUM('ACTIVE', 'INACTIVE', 'ARCHIVED')
- **默认值**: 'ACTIVE'
- **说明**: 项目的生命周期状态
- **枚举值**:
  - `ACTIVE`: 活跃状态，正常开发/测试中
  - `INACTIVE`: 非活跃状态，暂停或待启动
  - `ARCHIVED`: 已归档，项目已完成或终止

### 4. 项目头像（avatar_url）
- **类型**: VARCHAR(255)
- **说明**: 项目Logo或标识图片的URL
- **用途**: 
  - 项目视觉标识
  - 提升项目辨识度
  - UI展示
- **示例**:
  - 相对路径：`/uploads/projects/project-001.png`
  - 完整URL：`https://cdn.example.com/avatars/project-001.png`

## 代码修改清单

### 1. 实体类更新
- ✅ `Project.java` - 添加4个新字段的属性定义，并增加详细注释

### 2. Mapper XML更新

#### ProjectMapper.xml
- ✅ `ProjectMap` resultMap - 更新基础结果映射，添加新字段映射
- ✅ `ProjectListResponseMap` resultMap - 更新列表响应映射
- ✅ `selectProjectList` - 更新项目列表查询
- ✅ `selectById` - 更新按ID查询
- ✅ `insert` - 更新INSERT语句
- ✅ `updateById` - 更新UPDATE语句（支持动态更新新字段）
- ✅ `selectProjectDetailById` - 更新项目详情查询
- ✅ `selectProjectForUpdate` - 更新编辑项目查询
- ✅ `selectByCode` - 更新按编码查询

### 3. 其他Mapper文件
涉及Projects表的其他Mapper（ReportMapper.xml, TestExecutionMapper.xml等）中的JOIN查询会自动获得新字段，无需修改。

### 4. Service实现
Service层使用实体类映射，自动支持新字段，无需特殊修改。只需在业务逻辑中：
- 创建项目时生成并设置`project_code`
- 根据项目类型设置`project_type`
- 根据业务流程更新`status`
- 处理项目头像上传并设置`avatar_url`

## 数据库索引优化

新增索引以提升查询性能：
```sql
idx_project_code (project_code)  -- 项目编码唯一索引
idx_creator_id (creator_id)      -- 创建人查询
idx_status (status)              -- 状态筛选
idx_is_deleted (is_deleted)      -- 软删除查询
```

## 数据迁移策略

### 自动迁移脚本
升级脚本包含了自动数据迁移逻辑：

1. **项目编码生成**
   ```sql
   UPDATE Projects 
   SET project_code = CONCAT('PROJ-', LPAD(project_id, 6, '0'))
   WHERE project_code IS NULL OR project_code = '';
   ```
   自动为现有项目生成格式为 `PROJ-000001` 的编码

2. **默认类型设置**
   ```sql
   UPDATE Projects 
   SET project_type = 'API'
   WHERE project_type IS NULL;
   ```
   现有项目默认设置为API类型

3. **默认状态设置**
   ```sql
   UPDATE Projects 
   SET status = 'ACTIVE'
   WHERE status IS NULL;
   ```
   现有项目默认设置为活跃状态

## 向后兼容性

- `project_code`字段虽然是NOT NULL，但迁移脚本会自动生成
- 所有新字段都有默认值或在迁移时自动填充
- 现有查询和功能不受影响
- `avatar_url`字段允许NULL，可逐步完善

## 使用场景和最佳实践

### 1. 创建新项目
```java
Project project = new Project();
project.setName("用户管理系统");
project.setDescription("用户管理和权限控制系统");
project.setCreatorId(userId);

// 生成项目编码
project.setProjectCode(generateProjectCode("PROJ"));

// 设置项目类型
project.setProjectType("API");

// 设置初始状态
project.setStatus("ACTIVE");

// 可选：设置头像
project.setAvatarUrl("/uploads/projects/user-system.png");

projectMapper.insert(project);
```

### 2. 更新项目
```java
Project project = projectMapper.selectById(projectId);
project.setName("新项目名称");
project.setProjectType("WEB");  // 更改类型
project.setAvatarUrl(newAvatarUrl);  // 更新头像
projectMapper.updateById(project);
```

### 3. 项目状态转换
```java
// 暂停项目
project.setStatus("INACTIVE");
projectMapper.updateById(project);

// 归档项目
project.setStatus("ARCHIVED");
projectMapper.updateById(project);
```

### 4. 按编码查询
```java
Project project = projectMapper.selectByCode("PROJ-000001");
```

### 5. 按类型筛选
```sql
SELECT * FROM Projects 
WHERE project_type = 'WEB' 
  AND status = 'ACTIVE' 
  AND is_deleted = FALSE;
```

## 项目编码生成工具

### 方案1：数据库函数（已提供）
```sql
SELECT generate_project_code('PROJ');  -- 返回 'PROJ-000001'
```

### 方案2：Java工具类
```java
public class ProjectCodeGenerator {
    private static final String PREFIX = "PROJ";
    
    public String generateProjectCode() {
        // 查询最大编号
        Integer maxNumber = projectMapper.getMaxProjectNumber(PREFIX);
        int nextNumber = (maxNumber == null ? 0 : maxNumber) + 1;
        
        // 生成编码：PROJ-000001
        return String.format("%s-%06d", PREFIX, nextNumber);
    }
    
    public String generateProjectCodeByType(String type) {
        // 根据类型生成：WEB-000001, API-000001
        return String.format("%s-%06d", type, getNextNumber(type));
    }
    
    public String generateProjectCodeByYear(String type) {
        // 按年份生成：2024-WEB-001
        int year = LocalDate.now().getYear();
        return String.format("%d-%s-%03d", year, type, getNextNumber(type, year));
    }
}
```

## 升级步骤

### 1. 备份数据库
```bash
mysqldump -u username -p database_name Projects > backup_projects.sql
```

### 2. 执行升级脚本
```bash
mysql -u username -p database_name < update_projects_table.sql
```

### 3. 验证升级结果
```sql
-- 检查表结构
DESC Projects;

-- 验证新字段
SELECT 
    project_id,
    name,
    project_code,
    project_type,
    status,
    CASE WHEN avatar_url IS NOT NULL THEN 'YES' ELSE 'NO' END as has_avatar
FROM Projects
WHERE is_deleted = FALSE
LIMIT 10;

-- 验证索引
SHOW INDEX FROM Projects 
WHERE Column_name IN ('project_code', 'status');

-- 检查项目编码唯一性
SELECT project_code, COUNT(*) as count
FROM Projects
WHERE is_deleted = FALSE
GROUP BY project_code
HAVING count > 1;
```

### 4. 部署新代码
- 更新实体类
- 更新Mapper XML
- 重启应用服务

### 5. 功能测试
- 测试创建项目（验证编码自动生成）
- 测试更新项目（验证新字段可更新）
- 测试按编码查询
- 测试按类型和状态筛选
- 测试项目头像上传和显示

## 数据统计和分析

### 项目类型分布
```sql
SELECT 
    project_type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Projects WHERE is_deleted = FALSE), 2) as percentage
FROM Projects
WHERE is_deleted = FALSE
GROUP BY project_type
ORDER BY count DESC;
```

### 项目状态分布
```sql
SELECT 
    status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Projects WHERE is_deleted = FALSE), 2) as percentage
FROM Projects
WHERE is_deleted = FALSE
GROUP BY status
ORDER BY count DESC;
```

### 头像使用率
```sql
SELECT 
    COUNT(*) as total_projects,
    SUM(CASE WHEN avatar_url IS NOT NULL THEN 1 ELSE 0 END) as with_avatar,
    ROUND(SUM(CASE WHEN avatar_url IS NOT NULL THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as avatar_usage_rate
FROM Projects
WHERE is_deleted = FALSE;
```

## 注意事项

### 1. 项目编码管理
- **唯一性**: 确保编码生成逻辑能产生唯一值
- **不可变性**: 创建后不建议修改project_code
- **规范性**: 制定并遵守编码命名规范
- **验证**: 在创建前验证编码是否已存在

### 2. 项目类型选择
- 根据实际技术栈选择合适的类型
- 混合项目建议使用HYBRID类型
- 类型选择影响后续的测试策略

### 3. 项目状态管理
- 状态转换应有明确的业务规则
- ARCHIVED状态的项目通常不再活跃操作
- 考虑添加状态转换日志

### 4. 头像图片管理
- 限制文件大小（建议≤2MB）
- 支持常见图片格式（jpg, png, svg）
- 考虑CDN加速
- 提供默认头像

### 5. 性能考虑
- 充分利用已添加的索引
- 避免全表扫描
- 考虑分页查询大量数据
- 定期分析慢查询

## 测试验证

### 1. 单元测试
```java
@Test
public void testInsertProjectWithNewFields() {
    Project project = new Project();
    project.setName("测试项目");
    project.setDescription("测试描述");
    project.setCreatorId(1);
    project.setProjectCode("TEST-000001");
    project.setProjectType("API");
    project.setStatus("ACTIVE");
    project.setAvatarUrl("/test/avatar.png");
    project.setCreatedAt(LocalDateTime.now());
    project.setUpdatedAt(LocalDateTime.now());
    project.setIsDeleted(false);
    
    int result = projectMapper.insert(project);
    assertEquals(1, result);
    assertNotNull(project.getProjectId());
}

@Test
public void testSelectByCode() {
    Project project = projectMapper.selectByCode("PROJ-000001");
    assertNotNull(project);
    assertEquals("PROJ-000001", project.getProjectCode());
}

@Test
public void testUpdateProjectFields() {
    Project project = projectMapper.selectById(1);
    project.setProjectType("WEB");
    project.setStatus("INACTIVE");
    project.setAvatarUrl("/new/avatar.png");
    
    int result = projectMapper.updateById(project);
    assertEquals(1, result);
    
    Project updated = projectMapper.selectById(1);
    assertEquals("WEB", updated.getProjectType());
    assertEquals("INACTIVE", updated.getStatus());
}
```

### 2. 集成测试
- 测试完整的项目创建流程
- 测试项目列表查询包含新字段
- 测试项目编码唯一性约束
- 测试项目状态转换逻辑

### 3. 数据库测试
```sql
-- 测试插入
INSERT INTO Projects (
    name, description, creator_id, 
    project_code, project_type, status, avatar_url,
    created_at, updated_at, is_deleted
) VALUES (
    '测试项目', '测试描述', 1,
    'TEST-001', 'API', 'ACTIVE', '/test/avatar.png',
    NOW(), NOW(), FALSE
);

-- 测试查询
SELECT * FROM Projects WHERE project_code = 'TEST-001';

-- 测试更新
UPDATE Projects 
SET project_type = 'WEB', status = 'INACTIVE'
WHERE project_code = 'TEST-001';

-- 测试约束
-- 以下语句应该失败（重复编码）
INSERT INTO Projects (name, creator_id, project_code)
VALUES ('重复项目', 1, 'TEST-001');
```

## 完成状态

✅ 所有计划任务已完成
- 实体类更新
- ProjectMapper.xml 完整更新
- SQL升级脚本（包含数据迁移）
- 详细文档编写
- 使用示例和最佳实践

## 相关文件

- `update_projects_table.sql` - 数据库升级脚本
- `src/main/java/com/victor/iatms/entity/po/Project.java` - 实体类
- `src/main/resources/mapper/ProjectMapper.xml` - 项目Mapper
- 其他涉及Projects表的Mapper（ReportMapper.xml, TestExecutionMapper.xml等）

## 后续工作建议

1. **项目编码管理服务**
   - 实现统一的编码生成服务
   - 支持多种编码规范
   - 提供编码验证接口

2. **项目分类优化**
   - 考虑增加项目标签系统
   - 支持自定义项目分类
   - 提供项目模板功能

3. **项目状态流转**
   - 实现状态机管理
   - 添加状态转换日志
   - 支持状态转换权限控制

4. **项目头像管理**
   - 实现头像上传接口
   - 添加图片处理（裁剪、压缩）
   - 提供默认头像库

5. **数据分析和报表**
   - 项目类型分布分析
   - 项目状态趋势分析
   - 项目活跃度统计

## 版本历史

- **v2.0 (2024-10-26)**: 添加项目编码、类型、状态、头像字段，完善项目管理功能
- **v1.0**: 初始版本，基础项目功能

