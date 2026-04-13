# DTO、VO、Query类更新总结

## 更新概述

根据 `Projects` 表和 `TestReportSummaries` 表的数据库结构升级，系统地更新了所有相关的DTO（Data Transfer Object）、Query类，确保前后端数据交互的完整性和一致性。

---

## Projects 表相关类更新

### 1. 输入DTO（请求）

#### 1.1 AddProjectDTO（添加项目请求DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/AddProjectDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码（可选，不提供则自动生成）
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
private String avatarUrl;         // 项目头像URL
```

**使用场景**: 
- 创建项目接口的请求体
- `projectCode` 为可选字段，不提供时由系统自动生成

#### 1.2 UpdateProjectDTO（编辑项目请求DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/UpdateProjectDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码（一般不建议修改）
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
private String avatarUrl;         // 项目头像URL
```

**使用场景**: 
- 更新项目接口的请求体
- 支持动态更新，字段为null时不修改

### 2. 输出DTO（响应）

#### 2.1 ProjectListResponseDTO（项目列表响应DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/ProjectListResponseDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
private String avatarUrl;         // 项目头像URL
```

**使用场景**: 
- 项目列表查询接口的响应
- 前端展示项目列表时使用

#### 2.2 AddProjectResponseDTO（添加项目响应DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/AddProjectResponseDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
private String avatarUrl;         // 项目头像URL
```

**使用场景**: 
- 创建项目成功后的响应
- 返回新创建项目的完整信息

#### 2.3 UpdateProjectResponseDTO（编辑项目响应DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/UpdateProjectResponseDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
private String avatarUrl;         // 项目头像URL
```

**使用场景**: 
- 更新项目成功后的响应
- 返回更新后项目的完整信息

#### 2.4 RecentProjectItemDTO（最近编辑的项目项DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/RecentProjectItemDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
private String avatarUrl;         // 项目头像URL
```

**使用场景**: 
- 最近编辑项目列表接口的响应
- Dashboard展示最近访问的项目

### 3. 查询DTO

#### 3.1 ProjectListQueryDTO（项目列表查询DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/ProjectListQueryDTO.java`

**新增字段**:
```java
private String projectCode;      // 项目编码（精确查询）
private String projectType;       // 项目类型：WEB, MOBILE, API, DESKTOP, HYBRID
private String status;            // 项目状态：ACTIVE, INACTIVE, ARCHIVED
```

**使用场景**: 
- 项目列表查询的过滤条件
- 支持按编码、类型、状态筛选项目

**查询示例**:
```java
ProjectListQueryDTO query = new ProjectListQueryDTO();
query.setProjectType("API");           // 只查询API类型项目
query.setStatus("ACTIVE");             // 只查询活跃项目
query.setProjectCode("PROJ-000001");   // 按编码精确查询
```

---

## TestReportSummaries 表相关类更新

### 1. 输出DTO（响应）

#### 1.1 ReportListResponseDTO（报告列表响应DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/ReportListResponseDTO.java`

**新增字段**:
```java
private Integer reportConfigId;           // 报告配置ID
private String executiveSummary;          // 执行摘要
private String conclusionRecommendation;  // 结论建议
```

**使用场景**: 
- 报告列表查询接口的响应
- 前端展示报告列表时使用
- `executiveSummary` 和 `conclusionRecommendation` 用于快速预览报告内容

#### 1.2 ReportExportResponseDTO（报告导出响应DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/ReportExportResponseDTO.java`

##### 1.2.1 ReportSummaryInfoDTO（报告摘要信息）
**新增字段**:
```java
private Integer reportConfigId;           // 报告配置ID
private String executiveSummary;          // 执行摘要
private String conclusionRecommendation;  // 结论建议
```

##### 1.2.2 TestCaseResultDTO（测试用例结果）
**新增字段**:
```java
private String moduleName;          // 模块名称
private String apiName;             // 接口名称
private String suiteName;           // 测试套件名称
private String testLayer;           // 测试层级：UNIT, INTEGRATION, API, E2E, PERFORMANCE, SECURITY
private String testType;            // 测试类型：POSITIVE, NEGATIVE, BOUNDARY, SECURITY, PERFORMANCE, USABILITY
private Integer flakyCount;         // 不稳定次数
private String impactAssessment;    // 影响评估：HIGH, MEDIUM, LOW
private String retestResult;        // 复测结果：PASSED, FAILED, NOT_RETESTED
```

**使用场景**: 
- 报告导出接口的响应
- 生成PDF、Excel、HTML等格式的报告
- 包含更详细的测试用例信息

### 2. 查询DTO

#### 2.1 ReportListQueryDTO（报告列表查询DTO）
**文件路径**: `src/main/java/com/victor/iatms/entity/dto/ReportListQueryDTO.java`

**新增字段**:
```java
private Integer reportConfigId;    // 报告配置ID过滤
private Boolean isIsoReport;        // 是否只查询ISO报告（有iso_metrics的报告）
```

**使用场景**: 
- 报告列表查询的过滤条件
- 支持按报告配置筛选
- 支持筛选ISO标准报告

**查询示例**:
```java
ReportListQueryDTO query = new ReportListQueryDTO();
query.setReportConfigId(1);        // 按配置ID筛选
query.setIsIsoReport(true);        // 只查询ISO标准报告
query.setReportType("execution");  // 执行类报告
query.setEnvironment("production"); // 生产环境
```

---

## 字段详细说明

### Projects 表新增字段说明

#### 1. projectCode（项目编码）
- **类型**: String
- **约束**: 唯一，NOT NULL
- **格式建议**:
  - 简单递增：`PROJ-000001`, `PROJ-000002`
  - 类型前缀：`WEB-000001`, `API-000001`
  - 年份编号：`2024-WEB-001`
  - 业务前缀：`USER-SYS-001`
- **用途**:
  - URL友好的项目标识
  - API路径参数
  - 报告引用
  - 外部系统集成

#### 2. projectType（项目类型）
- **类型**: ENUM
- **枚举值**:
  - `WEB`: Web应用项目
  - `MOBILE`: 移动应用项目（iOS/Android）
  - `API`: API/后端服务项目
  - `DESKTOP`: 桌面应用项目
  - `HYBRID`: 混合型项目（跨平台）
- **默认值**: `API`
- **用途**:
  - 项目分类统计
  - 测试策略选择
  - 报告模板选择

#### 3. status（项目状态）
- **类型**: ENUM
- **枚举值**:
  - `ACTIVE`: 活跃状态，正常开发/测试中
  - `INACTIVE`: 非活跃状态，暂停或待启动
  - `ARCHIVED`: 已归档，项目已完成或终止
- **默认值**: `ACTIVE`
- **用途**:
  - 项目生命周期管理
  - 筛选活跃项目
  - 归档历史项目

#### 4. avatarUrl（项目头像）
- **类型**: String
- **格式**: URL路径
- **示例**:
  - 相对路径：`/uploads/projects/project-001.png`
  - 完整URL：`https://cdn.example.com/avatars/project-001.png`
- **用途**:
  - 项目视觉标识
  - 提升项目辨识度
  - UI美化

### TestReportSummaries 表新增字段说明

#### 1. reportConfigId（报告配置ID）
- **类型**: Integer
- **说明**: 关联报告配置表，用于报告模板和配置管理
- **用途**:
  - 报告模板选择
  - 报告生成配置
  - 自定义报告格式

#### 2. executiveSummary（执行摘要）
- **类型**: TEXT
- **说明**: 面向管理层的简要总结
- **内容示例**:
  ```
  本次测试于2024年10月26日执行完成，共执行100个测试用例，
  通过率为95%。发现2个高优先级缺陷，建议立即修复后再发布。
  ```
- **用途**:
  - 管理层快速了解测试结果
  - 报告预览
  - Dashboard展示

#### 3. conclusionRecommendation（结论建议）
- **类型**: TEXT
- **说明**: 测试结论和改进建议
- **内容示例**:
  ```
  结论：系统整体质量良好，可以发布。
  建议：
  1. 优化支付模块的性能
  2. 加强安全测试覆盖
  3. 完善错误日志机制
  ```
- **用途**:
  - 测试结论总结
  - 改进方向建议
  - 风险提示

#### 4. ISO相关JSON字段（不在DTO中直接展示）
这些字段存储为JSON，通过专门的ISO报告DTO处理：
- `isoMetrics`: ISO标准指标数据
- `riskAssessment`: 风险评估数据
- `defectAnalysis`: 缺陷分析数据
- `environmentDetails`: 环境详细信息
- `testScopeDetails`: 测试范围详情

---

## 前后端接口影响分析

### 1. 需要更新的API接口

#### Projects 相关接口

##### 1.1 创建项目接口
**接口**: `POST /api/projects`

**请求示例**:
```json
{
  "name": "用户管理系统",
  "description": "用户管理和权限控制系统",
  "projectCode": "USER-SYS-001",
  "projectType": "API",
  "status": "ACTIVE",
  "avatarUrl": "/uploads/projects/user-system.png"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "projectId": 1,
    "name": "用户管理系统",
    "description": "用户管理和权限控制系统",
    "projectCode": "USER-SYS-001",
    "projectType": "API",
    "status": "ACTIVE",
    "avatarUrl": "/uploads/projects/user-system.png",
    "creatorId": 1,
    "creatorName": "张三",
    "createdAt": "2024-10-26T10:00:00",
    "updatedAt": "2024-10-26T10:00:00"
  }
}
```

##### 1.2 项目列表查询接口
**接口**: `GET /api/projects`

**请求参数**:
```
GET /api/projects?projectType=API&status=ACTIVE&page=1&pageSize=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "projectId": 1,
        "name": "用户管理系统",
        "description": "用户管理和权限控制系统",
        "projectCode": "USER-SYS-001",
        "projectType": "API",
        "status": "ACTIVE",
        "avatarUrl": "/uploads/projects/user-system.png",
        "creatorInfo": {
          "userId": 1,
          "name": "张三",
          "avatarUrl": "/uploads/users/zhangsan.png"
        },
        "createdAt": "2024-10-26T10:00:00",
        "updatedAt": "2024-10-26T10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "pageSize": 10
  }
}
```

##### 1.3 更新项目接口
**接口**: `PUT /api/projects/{projectId}`

**请求示例**:
```json
{
  "name": "用户管理系统V2",
  "projectType": "WEB",
  "status": "ACTIVE",
  "avatarUrl": "/uploads/projects/user-system-v2.png"
}
```

#### Reports 相关接口

##### 2.1 报告列表查询接口
**接口**: `GET /api/reports`

**请求参数**:
```
GET /api/reports?reportConfigId=1&isIsoReport=true&reportType=execution
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "reportId": 1,
        "reportName": "用户系统测试报告",
        "reportType": "execution",
        "projectId": 1,
        "projectName": "用户管理系统",
        "environment": "production",
        "totalCases": 100,
        "passedCases": 95,
        "failedCases": 5,
        "successRate": 95.00,
        "reportConfigId": 1,
        "executiveSummary": "测试执行完成，整体质量良好",
        "conclusionRecommendation": "建议修复5个失败用例后发布",
        "reportStatus": "completed",
        "createdAt": "2024-10-26T10:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 10
  }
}
```

##### 2.2 报告导出接口
**接口**: `GET /api/reports/{reportId}/export`

**响应字段变更**:
- `reportSummary` 增加 `reportConfigId`, `executiveSummary`, `conclusionRecommendation`
- `testResults` 中每个用例增加 `moduleName`, `apiName`, `suiteName`, `testLayer`, `testType`, `flakyCount`, `impactAssessment`, `retestResult`

---

## 前端开发指南

### 1. 项目管理界面更新

#### 1.1 项目列表展示
```typescript
interface Project {
  projectId: number;
  name: string;
  description: string;
  projectCode: string;          // 新增
  projectType: string;           // 新增
  status: string;                // 新增
  avatarUrl: string;             // 新增
  creatorInfo: {
    userId: number;
    name: string;
    avatarUrl: string;
  };
  createdAt: string;
  updatedAt: string;
}
```

#### 1.2 项目筛选组件
```typescript
interface ProjectFilter {
  name?: string;
  creatorId?: number;
  projectCode?: string;     // 新增
  projectType?: string;     // 新增：WEB, MOBILE, API, DESKTOP, HYBRID
  status?: string;          // 新增：ACTIVE, INACTIVE, ARCHIVED
  includeDeleted?: boolean;
}
```

#### 1.3 项目表单组件
```vue
<template>
  <el-form :model="projectForm">
    <el-form-item label="项目名称" prop="name">
      <el-input v-model="projectForm.name" />
    </el-form-item>
    
    <el-form-item label="项目描述" prop="description">
      <el-input type="textarea" v-model="projectForm.description" />
    </el-form-item>
    
    <!-- 新增字段 -->
    <el-form-item label="项目编码" prop="projectCode">
      <el-input v-model="projectForm.projectCode" placeholder="不填写则自动生成" />
    </el-form-item>
    
    <el-form-item label="项目类型" prop="projectType">
      <el-select v-model="projectForm.projectType">
        <el-option label="Web应用" value="WEB" />
        <el-option label="移动应用" value="MOBILE" />
        <el-option label="API服务" value="API" />
        <el-option label="桌面应用" value="DESKTOP" />
        <el-option label="混合应用" value="HYBRID" />
      </el-select>
    </el-form-item>
    
    <el-form-item label="项目状态" prop="status">
      <el-select v-model="projectForm.status">
        <el-option label="活跃" value="ACTIVE" />
        <el-option label="非活跃" value="INACTIVE" />
        <el-option label="已归档" value="ARCHIVED" />
      </el-select>
    </el-form-item>
    
    <el-form-item label="项目头像" prop="avatarUrl">
      <el-upload
        action="/api/upload"
        :on-success="handleAvatarSuccess"
      >
        <img v-if="projectForm.avatarUrl" :src="projectForm.avatarUrl" />
        <i v-else class="el-icon-plus" />
      </el-upload>
    </el-form-item>
  </el-form>
</template>
```

### 2. 报告管理界面更新

#### 2.1 报告列表展示
```typescript
interface Report {
  reportId: number;
  reportName: string;
  reportType: string;
  projectId: number;
  projectName: string;
  environment: string;
  totalCases: number;
  passedCases: number;
  failedCases: number;
  successRate: number;
  reportConfigId: number;          // 新增
  executiveSummary: string;        // 新增
  conclusionRecommendation: string; // 新增
  reportStatus: string;
  createdAt: string;
}
```

#### 2.2 报告筛选组件
```typescript
interface ReportFilter {
  projectId?: number;
  reportType?: string;
  environment?: string;
  reportStatus?: string;
  reportConfigId?: number;    // 新增
  isIsoReport?: boolean;      // 新增
}
```

---

## 数据库迁移注意事项

### 1. Projects 表迁移
- 执行 `update_projects_table.sql` 脚本
- 自动为现有项目生成 `project_code`
- 默认 `project_type` 为 `API`
- 默认 `status` 为 `ACTIVE`

### 2. TestReportSummaries 表迁移
- 执行 `update_test_report_summaries_table.sql` 脚本
- 新字段允许NULL，现有报告不受影响
- ISO相关字段为扩展功能，可逐步完善

---

## 测试建议

### 1. 单元测试
```java
@Test
public void testProjectDTOMapping() {
    Project project = new Project();
    project.setProjectId(1);
    project.setName("测试项目");
    project.setProjectCode("TEST-001");
    project.setProjectType("API");
    project.setStatus("ACTIVE");
    project.setAvatarUrl("/test.png");
    
    ProjectListResponseDTO dto = mapToDTO(project);
    assertEquals("TEST-001", dto.getProjectCode());
    assertEquals("API", dto.getProjectType());
    assertEquals("ACTIVE", dto.getStatus());
}
```

### 2. 集成测试
- 测试创建项目时新字段的保存
- 测试项目列表查询时新字段的返回
- 测试按新字段筛选的功能
- 测试报告导出包含新字段

### 3. 前端测试
- 测试项目表单新字段的输入
- 测试项目列表新字段的展示
- 测试项目筛选新条件的使用
- 测试报告页面新字段的显示

---

## 完成状态

✅ **所有DTO/VO/Query类已更新完成**

### Projects 相关 (7个类)
- ✅ AddProjectDTO
- ✅ UpdateProjectDTO
- ✅ ProjectListResponseDTO
- ✅ AddProjectResponseDTO
- ✅ UpdateProjectResponseDTO
- ✅ RecentProjectItemDTO
- ✅ ProjectListQueryDTO

### Reports 相关 (3个类)
- ✅ ReportListResponseDTO
- ✅ ReportListQueryDTO
- ✅ ReportExportResponseDTO (包含内部类)

---

## 相关文档

- `PROJECTS_TABLE_UPDATE_SUMMARY.md` - Projects表升级详细文档
- `TEST_REPORT_SUMMARIES_UPDATE_SUMMARY.md` - TestReportSummaries表升级详细文档
- `update_projects_table.sql` - Projects表升级SQL脚本
- `update_test_report_summaries_table.sql` - TestReportSummaries表升级SQL脚本

---

## 版本历史

- **v2.0 (2024-10-26)**: 完整更新所有DTO/VO/Query类，支持新的数据库表结构
- **v1.0**: 初始版本

