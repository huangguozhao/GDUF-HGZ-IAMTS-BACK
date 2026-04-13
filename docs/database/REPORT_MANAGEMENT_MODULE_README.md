# 报告管理模块实现说明

## 概述

报告管理模块是接口自动化管理平台的第五个核心模块，负责管理测试报告的生成、查询、更新和删除等操作。该模块基于SpringBoot + MyBatis技术栈实现，提供了完整的RESTful API接口。

## 功能特性

### 1. 报告列表查询
- 支持分页查询
- 多条件过滤（项目ID、报告类型、环境、状态、文件格式等）
- 时间范围查询
- 成功率范围过滤
- 标签过滤
- 关键字搜索
- 多种排序方式
- 统计摘要信息

### 2. 报告详情管理
- 根据ID查询报告详情
- 根据项目ID查询报告列表
- 根据执行ID查询报告
- 创建新报告
- 更新报告信息
- 删除报告（支持单个和批量删除）

### 3. 报告状态管理
- 更新报告状态（生成中、已完成、失败）
- 更新报告文件信息（文件路径、大小、下载地址）

## 技术实现

### 1. 实体类设计

#### PO实体类
- `TestReportSummary`: 测试报告汇总表实体类，对应数据库表TestReportSummaries

#### DTO类
- `ReportListQueryDTO`: 报告列表查询参数DTO
- `ReportListResponseDTO`: 报告列表响应DTO
- `ReportSummaryDTO`: 报告统计摘要DTO
- `ReportPageResultDTO`: 报告分页结果DTO

#### 枚举类
- `ReportSortFieldEnum`: 报告排序字段枚举
- `ReportTypeEnum`: 报告类型枚举（已存在）
- `ReportStatusEnum`: 报告状态枚举（已存在）
- `ReportFormatEnum`: 报告格式枚举（已存在）

### 2. 数据访问层

#### Mapper接口
- `ReportMapper`: 报告管理数据访问接口

#### XML映射文件
- `ReportMapper.xml`: MyBatis映射文件，包含所有SQL语句

### 3. 业务逻辑层

#### Service接口
- `ReportService`: 报告管理服务接口

#### Service实现
- `ReportServiceImpl`: 报告管理服务实现类

### 4. 控制层

#### Controller
- `ReportController`: 报告管理控制器，提供RESTful API

## API接口说明

### 1. 获取报告列表
```
GET /api/reports
```

**请求参数：**
- `project_id`: 项目ID过滤
- `report_type`: 报告类型过滤
- `environment`: 环境过滤
- `report_status`: 报告状态过滤
- `file_format`: 文件格式过滤
- `start_time_begin`: 开始时间范围查询（开始）
- `start_time_end`: 开始时间范围查询（结束）
- `success_rate_min`: 最小成功率过滤
- `success_rate_max`: 最大成功率过滤
- `tags`: 标签过滤
- `search_keyword`: 关键字搜索
- `sort_by`: 排序字段
- `sort_order`: 排序顺序
- `include_deleted`: 是否包含已删除的报告
- `page`: 页码
- `page_size`: 每页条数

**响应数据：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 45,
    "items": [...],
    "page": 1,
    "page_size": 20,
    "summary": {
      "total_reports": 45,
      "by_type": {...},
      "by_status": {...},
      "by_environment": {...},
      "avg_success_rate": 92.5
    }
  }
}
```

### 2. 其他API接口
- `GET /api/reports/{reportId}`: 根据ID查询报告详情
- `GET /api/reports/project/{projectId}`: 根据项目ID查询报告列表
- `GET /api/reports/execution/{executionId}`: 根据执行ID查询报告
- `POST /api/reports`: 创建报告
- `PUT /api/reports/{reportId}`: 更新报告
- `DELETE /api/reports/{reportId}`: 删除报告
- `DELETE /api/reports/batch`: 批量删除报告
- `PATCH /api/reports/{reportId}/status`: 更新报告状态
- `PATCH /api/reports/{reportId}/file`: 更新报告文件信息

## 数据库设计

### TestReportSummaries表
该表存储测试报告的汇总信息，包括：
- 基本信息：报告ID、名称、类型、项目ID等
- 执行信息：执行ID、环境、开始时间、结束时间、耗时等
- 统计信息：总用例数、执行用例数、通过用例数、失败用例数等
- 文件信息：文件格式、路径、大小、下载地址等
- 元数据：创建时间、更新时间、删除标记等

## 权限控制

所有API接口都需要通过`@GlobalInterceptor(checkLogin = true)`注解进行认证检查，确保只有登录用户才能访问。

## 错误处理

实现了完整的错误处理机制：
- 参数校验错误（HTTP 400）
- 认证失败（HTTP 401）
- 权限不足（HTTP 403）
- 资源不存在（HTTP 404）
- 服务器内部错误（HTTP 500）

## 测试

提供了完整的API测试脚本`test_report_apis.bat`，包含所有接口的测试用例。

## 注意事项

1. **JSON字段处理**: 使用`JsonTypeHandler`处理JSON字段的序列化和反序列化
2. **时间格式**: 所有时间字段使用ISO 8601格式
3. **分页限制**: 每页最大条数限制为50条
4. **软删除**: 采用逻辑删除方式，不会物理删除数据
5. **权限验证**: 所有接口都需要JWT token认证

## 扩展性

该模块设计具有良好的扩展性：
- 可以轻松添加新的报告类型
- 支持自定义报告格式
- 可以扩展更多的过滤条件
- 支持自定义排序字段

## 性能优化

- 数据库索引优化
- 分页查询减少数据传输
- 统计信息缓存
- 异步报告生成（可扩展）

## 总结

报告管理模块提供了完整的测试报告管理功能，包括查询、创建、更新、删除等操作。该模块遵循了项目的整体架构设计，使用了统一的响应格式、错误处理机制和权限控制。代码结构清晰，易于维护和扩展。
