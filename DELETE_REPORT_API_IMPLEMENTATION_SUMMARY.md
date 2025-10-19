# 报告删除接口实现说明

## 概述

本文档描述了报告管理模块中删除测试报告接口的实现，包括软删除和硬删除两种模式。

## 接口信息

- **请求路径**: `DELETE /reports/{report_id}`
- **请求方式**: `DELETE`
- **接口描述**: 删除指定ID的测试报告（支持软删除和硬删除）
- **认证要求**: 需要认证，且需要删除权限

## 请求参数

### 路径参数
- `report_id`: 要删除的报告ID（必须）

### 查询参数
- `force`: 是否强制删除（物理删除），可选，默认 `false`

### 请求头
- `Authorization`: 认证令牌，格式 `Bearer {token}`

## 响应格式

### 成功响应 (HTTP 200)
```json
{
  "code": 1,
  "msg": "报告删除成功",
  "data": {
    "report_id": 1001,
    "report_name": "电商平台回归测试报告-20240916",
    "deleted": true,
    "deletion_type": "soft_delete",
    "deleted_at": "2024-09-16T14:30:25.000Z",
    "affected_rows": 1,
    "deleted_by": 123
  }
}
```

### 错误响应

#### 报告不存在 (HTTP 404)
```json
{
  "code": -4,
  "msg": "报告不存在",
  "data": null
}
```

#### 报告已被删除 (HTTP 410)
```json
{
  "code": -6,
  "msg": "报告已被删除，无法重复删除",
  "data": null
}
```

#### 存在依赖关系 (HTTP 409)
```json
{
  "code": -7,
  "msg": "报告被其他数据引用，无法删除",
  "data": {
    "report_id": 1003,
    "dependencies": [
      {
        "type": "analysis",
        "count": 2,
        "description": "关联分析报告"
      }
    ]
  }
}
```

## 实现组件

### 1. DTO类
- `DeleteReportResponseDTO`: 删除响应数据传输对象
- `ReportDependencyDTO`: 报告依赖信息
- `ReportDependencyCheckDTO`: 依赖检查结果

### 2. Mapper接口
- `ReportMapper`: 数据访问层接口
  - `selectByIdForDelete()`: 检查报告是否存在
  - `checkReportDependencies()`: 检查依赖关系
  - `softDeleteReport()`: 软删除报告
  - `hardDeleteReport()`: 硬删除报告
  - `deleteReportTestResults()`: 删除相关测试结果

### 3. Service层
- `ReportService`: 业务逻辑接口
- `ReportServiceImpl`: 业务逻辑实现
  - `deleteTestReport()`: 主要删除方法
  - `checkReportExists()`: 检查报告存在性
  - `checkReportDependencies()`: 检查依赖关系
  - `softDeleteReport()`: 软删除实现
  - `hardDeleteReport()`: 硬删除实现

### 4. Controller层
- `ReportController`: REST控制器
  - `deleteReport()`: 删除接口端点

## 业务逻辑

### 删除流程
1. **参数验证**: 检查report_id和用户ID的有效性
2. **存在性检查**: 确认报告存在且未被删除
3. **依赖检查**: 检查报告是否被其他数据引用（仅软删除时）
4. **权限验证**: 验证用户是否有删除权限
5. **执行删除**: 根据force参数选择软删除或硬删除
6. **返回结果**: 返回删除操作的结果

### 软删除 (force=false 或默认)
- 更新 `TestReportSummaries` 表的 `is_deleted` 字段为 `true`
- 设置 `deleted_at` 为当前时间
- 设置 `deleted_by` 为当前用户ID
- 保留数据，可恢复

### 硬删除 (force=true)
- 从 `TestReportSummaries` 表永久删除记录
- 同时删除相关的测试结果数据（`TestCaseResults`）
- 数据不可恢复

## 权限控制

- 普通用户只能删除自己创建的报告
- 项目管理员可以删除其管理项目下的所有报告
- 系统管理员可以删除任何报告和执行物理删除操作

## 安全考虑

- 防止越权删除：确保用户只能删除有权限访问的报告
- 限制物理删除频率，避免误操作
- 考虑实现回收站机制，允许恢复误删除的报告
- 对于重要报告，可以考虑二次确认机制

## 测试

使用 `test_delete_report_api.bat` 脚本进行接口测试，包括：
- 软删除测试
- 硬删除测试
- 错误场景测试
- 权限验证测试

## 数据库操作

### 软删除SQL
```sql
UPDATE TestReportSummaries 
SET is_deleted = TRUE, 
    deleted_at = NOW(), 
    deleted_by = :current_user_id,
    updated_at = NOW()
WHERE report_id = :report_id 
AND is_deleted = FALSE;
```

### 硬删除SQL
```sql
DELETE FROM TestReportSummaries 
WHERE report_id = :report_id;
```

## 注意事项

- 默认采用软删除机制，避免数据永久丢失
- 物理删除操作需要谨慎使用，建议仅限于管理员权限
- 删除操作应该记录详细的审计日志
- 对于大型报告，删除时需要考虑关联文件的清理
