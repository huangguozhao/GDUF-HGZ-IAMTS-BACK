-- ==================================================================================
-- Projects表结构升级脚本
-- 版本: 2.0
-- 日期: 2024-10-26
-- 说明: 为Projects表添加项目编码、类型、状态、头像等字段，完善项目管理功能
-- ==================================================================================

-- 1. 添加项目编码（唯一标识）
ALTER TABLE Projects 
ADD COLUMN project_code VARCHAR(50) NOT NULL UNIQUE COMMENT '项目编码，唯一';

-- 2. 添加项目类型
ALTER TABLE Projects 
ADD COLUMN project_type ENUM('WEB', 'MOBILE', 'API', 'DESKTOP', 'HYBRID') DEFAULT 'API' COMMENT '项目类型';

-- 3. 添加项目状态
ALTER TABLE Projects 
ADD COLUMN status ENUM('ACTIVE', 'INACTIVE', 'ARCHIVED') DEFAULT 'ACTIVE' COMMENT '项目状态';

-- 4. 添加项目头像
ALTER TABLE Projects 
ADD COLUMN avatar_url VARCHAR(255) COMMENT '项目头像URL';

-- 5. 添加软删除相关字段（如果不存在）
-- 注意：如果这些字段已存在，请注释掉以下语句
-- ALTER TABLE Projects 
-- ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
-- ADD COLUMN deleted_at TIMESTAMP NULL COMMENT '删除时间',
-- ADD COLUMN deleted_by INT NULL COMMENT '删除人ID';

-- 6. 添加索引以优化查询性能
ALTER TABLE Projects 
ADD INDEX IF NOT EXISTS idx_project_code (project_code),
ADD INDEX IF NOT EXISTS idx_creator_id (creator_id),
ADD INDEX IF NOT EXISTS idx_status (status),
ADD INDEX IF NOT EXISTS idx_is_deleted (is_deleted);

-- ==================================================================================
-- 数据迁移脚本
-- 为现有项目生成项目编码和设置默认值
-- ==================================================================================

-- 为现有项目生成唯一项目编码
-- 格式：PROJ-{project_id的6位数字}，例如：PROJ-000001
UPDATE Projects 
SET project_code = CONCAT('PROJ-', LPAD(project_id, 6, '0'))
WHERE project_code IS NULL OR project_code = '';

-- 为现有项目设置默认类型
UPDATE Projects 
SET project_type = 'API'
WHERE project_type IS NULL;

-- 为现有项目设置默认状态
UPDATE Projects 
SET status = 'ACTIVE'
WHERE status IS NULL;

-- 为未删除的项目确保 is_deleted = FALSE
UPDATE Projects 
SET is_deleted = FALSE
WHERE is_deleted IS NULL;

-- ==================================================================================
-- 字段说明和使用示例
-- ==================================================================================

/*
1. project_code（项目编码）
   - 唯一标识项目的短代码
   - 建议格式：PROJ-XXXXXX（6位数字）或 {前缀}-{编号}
   - 用途：URL友好的项目标识、报告引用、API路径等
   - 示例：'PROJ-000001', 'WEB-2024001', 'API-USER-SYS'

2. project_type（项目类型）
   - WEB: Web应用项目
   - MOBILE: 移动应用项目（iOS/Android）
   - API: API/后端服务项目
   - DESKTOP: 桌面应用项目
   - HYBRID: 混合型项目（跨平台）
   
3. status（项目状态）
   - ACTIVE: 活跃状态，正常开发/测试中
   - INACTIVE: 非活跃状态，暂停或待启动
   - ARCHIVED: 已归档，项目已完成或终止
   
4. avatar_url（项目头像）
   - 存储项目Logo或标识图片的URL
   - 可以是相对路径或完整URL
   - 示例：'/uploads/projects/project-001.png'
           'https://cdn.example.com/avatars/project-001.png'
*/

-- ==================================================================================
-- 验证脚本
-- ==================================================================================

-- 查看表结构
-- DESC Projects;

-- 查看新增字段和索引
-- SHOW FULL COLUMNS FROM Projects 
-- WHERE Field IN ('project_code', 'project_type', 'status', 'avatar_url');

-- SHOW INDEX FROM Projects WHERE Column_name IN ('project_code', 'creator_id', 'status', 'is_deleted');

-- 验证项目编码唯一性
-- SELECT project_code, COUNT(*) as count
-- FROM Projects
-- WHERE is_deleted = FALSE
-- GROUP BY project_code
-- HAVING count > 1;

-- 查看项目类型分布
-- SELECT 
--     project_type,
--     COUNT(*) as count,
--     ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Projects WHERE is_deleted = FALSE), 2) as percentage
-- FROM Projects
-- WHERE is_deleted = FALSE
-- GROUP BY project_type
-- ORDER BY count DESC;

-- 查看项目状态分布
-- SELECT 
--     status,
--     COUNT(*) as count,
--     ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Projects WHERE is_deleted = FALSE), 2) as percentage
-- FROM Projects
-- WHERE is_deleted = FALSE
-- GROUP BY status
-- ORDER BY count DESC;

-- 查看最近创建的项目
-- SELECT 
--     project_id,
--     name,
--     project_code,
--     project_type,
--     status,
--     CASE WHEN avatar_url IS NOT NULL THEN 'YES' ELSE 'NO' END as has_avatar,
--     created_at
-- FROM Projects
-- WHERE is_deleted = FALSE
-- ORDER BY created_at DESC
-- LIMIT 10;

-- ==================================================================================
-- 项目编码生成函数（可选）
-- ==================================================================================

-- 创建函数生成唯一的项目编码
DELIMITER //

CREATE FUNCTION IF NOT EXISTS generate_project_code(prefix VARCHAR(10))
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE next_id INT;
    DECLARE new_code VARCHAR(50);
    
    -- 获取最大编号
    SELECT COALESCE(MAX(CAST(SUBSTRING(project_code, LENGTH(prefix) + 2) AS UNSIGNED)), 0) + 1
    INTO next_id
    FROM Projects
    WHERE project_code LIKE CONCAT(prefix, '-%');
    
    -- 生成新编码
    SET new_code = CONCAT(prefix, '-', LPAD(next_id, 6, '0'));
    
    RETURN new_code;
END //

DELIMITER ;

-- 使用示例：
-- INSERT INTO Projects (name, description, creator_id, project_code, project_type, status)
-- VALUES ('新项目', '项目描述', 1, generate_project_code('PROJ'), 'API', 'ACTIVE');

-- ==================================================================================
-- 项目编码规范建议
-- ==================================================================================

/*
推荐的项目编码命名规范：

1. 格式1：简单递增
   - PROJ-000001, PROJ-000002, ...
   - 适用于项目数量不多的情况

2. 格式2：类型前缀+编号
   - WEB-000001, MOB-000001, API-000001
   - 适用于需要区分项目类型的情况

3. 格式3：年份+类型+编号
   - 2024-WEB-001, 2024-API-001
   - 适用于按年度管理项目的情况

4. 格式4：业务前缀+编号
   - USER-SYS-001, ORDER-SYS-001
   - 适用于多业务线的情况

选择原则：
- 保持一致性
- 易于识别和记忆
- 长度适中（建议不超过20字符）
- 避免特殊字符（推荐使用字母、数字、连字符）
*/

-- ==================================================================================
-- 数据完整性检查
-- ==================================================================================

-- 检查是否有项目缺少必填字段
-- SELECT 
--     project_id,
--     name,
--     CASE WHEN project_code IS NULL OR project_code = '' THEN 'Missing' ELSE 'OK' END as code_status,
--     CASE WHEN project_type IS NULL THEN 'Missing' ELSE 'OK' END as type_status,
--     CASE WHEN status IS NULL THEN 'Missing' ELSE 'OK' END as status_status
-- FROM Projects
-- WHERE is_deleted = FALSE
--   AND (project_code IS NULL OR project_code = '' 
--        OR project_type IS NULL 
--        OR status IS NULL);

-- ==================================================================================
-- 回滚脚本（如需回滚，请谨慎使用）
-- ==================================================================================

/*
-- 删除新增的字段
ALTER TABLE Projects 
DROP COLUMN project_code,
DROP COLUMN project_type,
DROP COLUMN status,
DROP COLUMN avatar_url;

-- 删除新增的索引
ALTER TABLE Projects 
DROP INDEX IF EXISTS idx_project_code,
DROP INDEX IF EXISTS idx_status;

-- 删除生成函数
DROP FUNCTION IF EXISTS generate_project_code;
*/

-- ==================================================================================
-- 性能优化建议
-- ==================================================================================

/*
1. 项目编码查询优化
   - 已添加唯一索引 idx_project_code
   - 确保通过 project_code 查询时使用等值匹配
   
2. 项目状态筛选优化
   - 已添加索引 idx_status
   - 适用于按状态筛选项目列表的场景
   
3. 软删除查询优化
   - 已添加索引 idx_is_deleted
   - 建议所有查询都添加 is_deleted = FALSE 条件
   
4. 复合索引建议
   - 如果经常按状态和删除标记组合查询，可以考虑：
     CREATE INDEX idx_status_deleted ON Projects(status, is_deleted);
*/

-- ==================================================================================
-- 应用层使用建议
-- ==================================================================================

/*
1. 创建项目时
   - 自动生成 project_code（使用函数或应用层逻辑）
   - 设置合适的 project_type
   - 默认 status 为 'ACTIVE'
   - 可选：上传并设置 avatar_url

2. 更新项目时
   - project_code 一般不允许修改
   - 根据项目生命周期更新 status
   - 允许更换 avatar_url

3. 删除项目时
   - 使用软删除（设置 is_deleted = TRUE）
   - 记录 deleted_at 和 deleted_by
   - 考虑将 status 更改为 'ARCHIVED'

4. 归档项目时
   - 设置 status = 'ARCHIVED'
   - 保留所有数据，不删除
   - 可选：移动相关文件到归档存储
*/

-- ==================================================================================
-- 完成
-- ==================================================================================

SELECT 'Projects表升级完成！' as status,
       COUNT(*) as total_projects,
       SUM(CASE WHEN project_code IS NOT NULL AND project_code != '' THEN 1 ELSE 0 END) as with_code,
       SUM(CASE WHEN project_type IS NOT NULL THEN 1 ELSE 0 END) as with_type,
       SUM(CASE WHEN status IS NOT NULL THEN 1 ELSE 0 END) as with_status
FROM Projects
WHERE is_deleted = FALSE;

