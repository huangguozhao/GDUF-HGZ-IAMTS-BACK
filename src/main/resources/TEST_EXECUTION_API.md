# 测试执行管理模块 API 文档

## 模块概述

测试执行管理模块提供了完整的测试用例执行功能，支持同步和异步执行模式，包含结果记录、报告生成和任务管理等功能。

## 接口列表

### 1. 执行单个测试用例

**接口路径**: `POST /api/test-cases/{case_id}/execute`  
**接口描述**: 执行指定的单个测试用例，并返回执行结果

#### 请求参数

**路径参数**:
- `case_id`: 测试用例ID (必填)

**请求体**:
```json
{
  "environment": "test",
  "base_url": "https://test-api.example.com",
  "timeout": 60,
  "variables": {
    "username": "testuser",
    "password": "Test123!"
  },
  "async": false
}
```

**参数说明**:
- `environment`: 执行环境标识 (可选，默认: test)
- `base_url`: 覆盖接口的基础URL (可选)
- `timeout`: 超时时间（秒） (可选，默认: 30)
- `variables`: 执行变量，用于参数化测试 (可选)
- `async`: 是否异步执行 (可选，默认: false)

#### 响应数据

**同步执行成功响应**:
```json
{
  "code": 1,
  "msg": "用例执行完成",
  "data": {
    "execution_id": 10001,
    "case_id": 1001,
    "case_name": "用户登录-成功场景",
    "status": "passed",
    "duration": 1250,
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:30:01.250Z",
    "response_status": 200,
    "assertions_passed": 3,
    "assertions_failed": 0,
    "failure_message": null,
    "logs_link": "/api/test-results/10001/logs",
    "report_id": 5001
  }
}
```

**异步执行成功响应**:
```json
{
  "code": 1,
  "msg": "用例执行任务已提交",
  "data": {
    "task_id": "task_abc123def456",
    "case_id": 1001,
    "case_name": "用户登录-成功场景",
    "status": "pending",
    "estimated_wait_time": 5,
    "queue_position": 3,
    "monitor_url": "/api/tasks/task_abc123def456/status"
  }
}
```

### 2. 异步执行测试用例

**接口路径**: `POST /api/test-cases/{case_id}/execute-async`  
**接口描述**: 异步执行指定的测试用例

#### 请求参数

与同步执行接口相同，但 `async` 参数会被自动设置为 `true`

#### 响应数据

返回任务信息，包含任务ID和监控URL

### 3. 查询任务状态

**接口路径**: `GET /api/tasks/{task_id}/status`  
**接口描述**: 查询异步任务的执行状态

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

**任务执行中**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "task_id": "task_abc123def456",
    "status": "running",
    "estimated_wait_time": 5
  }
}
```

**任务执行完成**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "execution_id": 10001,
    "case_id": 1001,
    "case_name": "用户登录-成功场景",
    "status": "passed",
    "duration": 1250,
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:30:01.250Z",
    "response_status": 200,
    "assertions_passed": 3,
    "assertions_failed": 0,
    "failure_message": null,
    "logs_link": "/api/test-results/10001/logs",
    "report_id": 5001
  }
}
```

### 4. 取消任务执行

**接口路径**: `POST /api/tasks/{task_id}/cancel`  
**接口描述**: 取消正在执行的异步任务

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "任务取消成功",
  "data": true
}
```

### 5. 获取执行结果详情

**接口路径**: `GET /api/test-results/{execution_id}`  
**接口描述**: 获取测试用例执行的详细结果

#### 请求参数

**路径参数**:
- `execution_id`: 执行ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "获取执行结果成功",
  "data": {
    "execution_id": 10001,
    "case_id": 1001,
    "case_name": "用户登录-成功场景",
    "status": "passed",
    "duration": 1250,
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:30:01.250Z",
    "failure_message": null,
    "logs_link": "/api/test-results/10001/logs",
    "report_id": 5001
  }
}
```

### 6. 获取执行日志

**接口路径**: `GET /api/test-results/{execution_id}/logs`  
**接口描述**: 获取测试用例执行的详细日志

#### 请求参数

**路径参数**:
- `execution_id`: 执行ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "获取执行日志成功",
  "data": "=== 测试用例执行日志 ===\n用例ID: 1001\n用例名称: 用户登录-成功场景\n执行时间: 2024-09-16T10:30:00\n请求方法: POST\n请求URL: https://test-api.example.com/api/login\n响应状态码: 200\n执行状态: passed"
}
```

### 7. 生成测试报告

**接口路径**: `POST /api/test-results/{execution_id}/report`  
**接口描述**: 为测试执行结果生成测试报告

#### 请求参数

**路径参数**:
- `execution_id`: 执行ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "测试报告生成成功",
  "data": 5001
}
```

### 8. 执行项目测试

**接口路径**: `POST /api/projects/{project_id}/execute`  
**接口描述**: 执行指定项目下的所有测试用例，支持按模块过滤和多种执行策略

#### 请求参数

**路径参数**:
- `project_id`: 项目ID (必填)

**请求体**:
```json
{
  "environment": "test",
  "base_url": "https://test-api.example.com",
  "timeout": 120,
  "async": true,
  "concurrency": 20,
  "execution_strategy": "by_module",
  "module_filter": {
    "module_ids": [1, 2, 3],
    "status": "active"
  },
  "case_filter": {
    "priority": ["P0", "P1"],
    "tags": ["冒烟测试"],
    "enabled_only": true
  },
  "variables": {
    "env": "test",
    "version": "2.0.0",
    "build_number": "456"
  },
  "report_config": {
    "detailed": true,
    "include_logs": true
  }
}
```

**参数说明**:
- `environment`: 执行环境标识 (可选，默认: test)
- `base_url`: 覆盖所有接口的基础URL (可选)
- `timeout`: 全局超时时间（秒） (可选，默认: 30)
- `auth_override`: 全局认证信息覆盖配置 (可选)
- `variables`: 全局执行变量 (可选)
- `async`: 是否异步执行 (可选，默认: true)
- `callback_url`: 异步执行完成后的回调URL (可选)
- `concurrency`: 并发执行数 (可选，默认: 10)
- `execution_strategy`: 执行策略 (可选，默认: by_module)
  - `sequential`: 顺序执行所有用例
  - `by_module`: 按模块分组执行
  - `by_priority`: 按优先级分组执行
- `module_filter`: 模块过滤条件 (可选)
  - `module_ids`: 指定要执行的模块ID列表
  - `status`: 模块状态过滤
- `case_filter`: 用例过滤条件 (可选)
  - `priority`: 优先级过滤
  - `tags`: 标签过滤
  - `enabled_only`: 是否只执行启用的用例
- `report_config`: 报告配置 (可选)
  - `detailed`: 是否生成详细报告
  - `include_logs`: 是否包含执行日志

#### 响应数据

**异步执行成功响应**:
```json
{
  "code": 1,
  "msg": "项目测试执行任务已提交",
  "data": {
    "task_id": "project_task_abc123def456",
    "project_id": 1,
    "project_name": "电商平台项目",
    "total_modules": 8,
    "filtered_modules": 3,
    "total_cases": 150,
    "filtered_cases": 45,
    "status": "queued",
    "concurrency": 20,
    "estimated_duration": 300,
    "queue_position": 1,
    "monitor_url": "/api/tasks/project_task_abc123def456/status",
    "report_url": "/api/reports/project/1/executions/latest",
    "cancel_url": "/api/tasks/project_task_abc123def456/cancel"
  }
}
```

**同步执行成功响应**:
```json
{
  "code": 1,
  "msg": "项目测试执行完成",
  "data": {
    "execution_id": 30001,
    "project_id": 1,
    "project_name": "电商平台项目",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:40:30.000Z",
    "total_duration": 630000,
    "total_modules": 3,
    "total_cases": 45,
    "passed": 38,
    "failed": 5,
    "skipped": 2,
    "success_rate": 84.4,
    "details": {
      "by_module": {
        "用户管理模块": { "total": 15, "passed": 14, "failed": 1, "success_rate": 93.3 },
        "订单管理模块": { "total": 20, "passed": 16, "failed": 3, "success_rate": 80.0 },
        "商品管理模块": { "total": 10, "passed": 8, "failed": 1, "success_rate": 80.0 }
      },
      "by_priority": {
        "P0": { "total": 20, "passed": 19, "failed": 1 },
        "P1": { "total": 25, "passed": 19, "failed": 4 }
      }
    },
    "report_id": 7001,
    "summary_url": "/api/reports/7001/summary",
    "download_url": "/api/reports/7001/export"
  }
}
```

### 9. 异步执行项目测试

**接口路径**: `POST /api/projects/{project_id}/execute-async`  
**接口描述**: 异步执行指定项目的测试用例

#### 请求参数

与同步执行接口相同，但 `async` 参数会被自动设置为 `true`

#### 响应数据

返回任务信息，包含任务ID和监控URL

### 10. 查询项目任务状态

**接口路径**: `GET /api/project-tasks/{task_id}/status`  
**接口描述**: 查询项目异步任务的执行状态

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

**任务执行中**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "task_id": "project_task_abc123def456",
    "status": "running",
    "estimated_wait_time": 5
  }
}
```

**任务执行完成**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "execution_id": 30001,
    "project_id": 1,
    "project_name": "电商平台项目",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:40:30.000Z",
    "total_duration": 630000,
    "total_cases": 45,
    "passed": 38,
    "failed": 5,
    "skipped": 2,
    "success_rate": 84.4,
    "report_id": 7001,
    "summary_url": "/api/reports/7001/summary",
    "download_url": "/api/reports/7001/export"
  }
}
```

### 11. 取消项目任务执行

**接口路径**: `POST /api/project-tasks/{task_id}/cancel`  
**接口描述**: 取消正在执行的项目异步任务

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "任务取消成功",
  "data": true
}
```

### 12. 执行接口测试

**接口路径**: `POST /api/apis/{api_id}/execute`  
**接口描述**: 执行指定接口下的所有测试用例，支持同步和异步执行模式

#### 请求参数

**路径参数**:
- `api_id`: 接口ID (必填)

**请求体**:
```json
{
  "environment": "test",
  "base_url": "https://test-api.example.com",
  "timeout": 60,
  "async": false,
  "concurrency": 5,
  "case_filter": {
    "priority": ["P0", "P1"],
    "tags": ["冒烟测试"],
    "enabled_only": true
  },
  "execution_order": "priority_desc",
  "variables": {
    "test_user": "test001",
    "test_password": "Pass123!"
  }
}
```

**参数说明**:
- `environment`: 执行环境标识 (可选，默认: test)
- `base_url`: 覆盖接口的基础URL (可选)
- `timeout`: 全局超时时间（秒） (可选，默认: 30)
- `auth_override`: 认证信息覆盖配置 (可选)
- `variables`: 执行变量，用于参数化测试 (可选)
- `async`: 是否异步执行 (可选，默认: false)
- `callback_url`: 异步执行完成后的回调URL (可选)
- `concurrency`: 并发执行数 (可选，默认: 3)
- `case_filter`: 用例过滤条件 (可选)
  - `priority`: 优先级过滤
  - `tags`: 标签过滤
  - `enabled_only`: 是否只执行启用的用例
- `execution_order`: 执行顺序 (可选，默认: priority_desc)
  - `priority_desc`: 按优先级降序
  - `priority_asc`: 按优先级升序
  - `name_asc`: 按名称升序
  - `name_desc`: 按名称降序

#### 响应数据

**同步执行成功响应**:
```json
{
  "code": 1,
  "msg": "接口测试执行完成",
  "data": {
    "execution_id": 40001,
    "api_id": 101,
    "api_name": "用户登录接口",
    "api_method": "POST",
    "api_path": "/auth/login",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:30:45.000Z",
    "total_duration": 45000,
    "total_cases": 8,
    "passed": 6,
    "failed": 1,
    "skipped": 1,
    "success_rate": 75.0,
    "case_results": [
      {
        "case_id": 1001,
        "case_code": "TC-API-101-001",
        "case_name": "用户登录-成功场景",
        "status": "passed",
        "duration": 1200,
        "response_status": 200
      },
      {
        "case_id": 1002,
        "case_code": "TC-API-101-002",
        "case_name": "用户登录-密码错误",
        "status": "passed",
        "duration": 800,
        "response_status": 401
      },
      {
        "case_id": 1003,
        "case_code": "TC-API-101-003",
        "case_name": "用户登录-用户不存在",
        "status": "failed",
        "duration": 750,
        "response_status": 404,
        "failure_message": "预期状态码为404，实际返回400"
      }
    ],
    "summary": {
      "by_priority": {
        "P0": { "total": 3, "passed": 3, "failed": 0 },
        "P1": { "total": 5, "passed": 3, "failed": 1 }
      },
      "by_status": {
        "passed": 6,
        "failed": 1,
        "skipped": 1
      }
    },
    "report_id": 8001,
    "detail_url": "/api/test-results/40001/details"
  }
}
```

**异步执行成功响应**:
```json
{
  "code": 1,
  "msg": "接口测试执行任务已提交",
  "data": {
    "task_id": "api_task_abc123def456",
    "api_id": 101,
    "api_name": "用户登录接口",
    "api_method": "POST",
    "api_path": "/auth/login",
    "total_cases": 8,
    "filtered_cases": 6,
    "status": "queued",
    "concurrency": 5,
    "estimated_duration": 30,
    "queue_position": 0,
    "monitor_url": "/api/tasks/api_task_abc123def456/status",
    "report_url": "/api/reports/api/101/executions/latest"
  }
}
```

### 13. 异步执行接口测试

**接口路径**: `POST /api/apis/{api_id}/execute-async`  
**接口描述**: 异步执行指定接口的测试用例

#### 请求参数

与同步执行接口相同，但 `async` 参数会被自动设置为 `true`

#### 响应数据

返回任务信息，包含任务ID和监控URL

### 14. 查询接口任务状态

**接口路径**: `GET /api/api-tasks/{task_id}/status`  
**接口描述**: 查询接口异步任务的执行状态

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

**任务执行中**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "task_id": "api_task_abc123def456",
    "status": "running",
    "estimated_wait_time": 3
  }
}
```

**任务执行完成**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "execution_id": 40001,
    "api_id": 101,
    "api_name": "用户登录接口",
    "api_method": "POST",
    "api_path": "/auth/login",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:30:45.000Z",
    "total_duration": 45000,
    "total_cases": 8,
    "passed": 6,
    "failed": 1,
    "skipped": 1,
    "success_rate": 75.0,
    "report_id": 8001,
    "detail_url": "/api/test-results/40001/details"
  }
}
```

### 15. 取消接口任务执行

**接口路径**: `POST /api/api-tasks/{task_id}/cancel`  
**接口描述**: 取消正在执行的接口异步任务

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "任务取消成功",
  "data": true
}
```

### 16. 执行测试套件

**接口路径**: `POST /api/test-suites/{suite_id}/execute`  
**接口描述**: 执行指定测试套件中的所有测试用例，支持复杂的执行策略和依赖管理

#### 请求参数

**路径参数**:
- `suite_id`: 测试套件ID (必填)

**请求体**:
```json
{
  "environment": "staging",
  "base_url": "https://staging-api.example.com",
  "timeout": 180,
  "async": true,
  "concurrency": 12,
  "execution_strategy": "smart",
  "stop_on_failure": false,
  "retry_config": {
    "enabled": true,
    "max_attempts": 2,
    "delay_ms": 2000
  },
  "case_filter": {
    "priority": ["P0", "P1", "P2"],
    "tags": ["回归测试"],
    "enabled_only": true
  },
  "variables": {
    "environment": "staging",
    "version": "2.1.0",
    "test_data_id": "dataset_001"
  },
  "report_config": {
    "detailed": true,
    "include_artifacts": true
  }
}
```

**参数说明**:
- `environment`: 执行环境标识 (可选，默认: test)
- `base_url`: 覆盖所有接口的基础URL (可选)
- `timeout`: 全局超时时间（秒） (可选，默认: 30)
- `auth_override`: 全局认证信息覆盖配置 (可选)
- `variables`: 全局执行变量 (可选)
- `async`: 是否异步执行 (可选，默认: true)
- `callback_url`: 异步执行完成后的回调URL (可选)
- `concurrency`: 并发执行数 (可选，默认: 8)
- `execution_strategy`: 执行策略 (可选，默认: smart)
  - `sequential`: 顺序执行所有用例
  - `parallel`: 完全并行执行（无依赖考虑）
  - `smart`: 智能依赖分析，按依赖关系分批次并行执行
- `stop_on_failure`: 失败时是否停止执行 (可选，默认: false)
- `retry_config`: 重试配置 (可选)
  - `enabled`: 是否启用重试 (可选，默认: false)
  - `max_attempts`: 最大重试次数 (可选，默认: 3)
  - `delay_ms`: 重试延迟时间（毫秒） (可选，默认: 1000)
- `case_filter`: 用例过滤条件 (可选)
  - `priority`: 优先级过滤
  - `tags`: 标签过滤
  - `enabled_only`: 是否只执行启用的用例
- `report_config`: 报告配置 (可选)
  - `detailed`: 是否生成详细报告 (可选，默认: true)
  - `include_artifacts`: 是否包含附件和日志 (可选，默认: false)

#### 响应数据

**异步执行成功响应**:
```json
{
  "code": 1,
  "msg": "测试套件执行任务已提交",
  "data": {
    "task_id": "suite_task_abc123def456",
    "suite_id": 10,
    "suite_name": "用户回归测试套件",
    "suite_code": "REGRESSION_USER_001",
    "total_cases": 85,
    "filtered_cases": 62,
    "estimated_cases": 62,
    "status": "preparing",
    "concurrency": 12,
    "estimated_duration": 420,
    "queue_position": 0,
    "execution_plan_url": "/api/tasks/suite_task_abc123def456/plan",
    "monitor_url": "/api/tasks/suite_task_abc123def456/status",
    "report_url": "/api/reports/suites/10/executions/latest",
    "cancel_url": "/api/tasks/suite_task_abc123def456/cancel"
  }
}
```

**同步执行成功响应**:
```json
{
  "code": 1,
  "msg": "测试套件执行完成",
  "data": {
    "execution_id": 50001,
    "suite_id": 10,
    "suite_name": "用户回归测试套件",
    "suite_code": "REGRESSION_USER_001",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:38:45.000Z",
    "total_duration": 525000,
    "total_cases": 62,
    "executed_cases": 62,
    "passed": 55,
    "failed": 5,
    "skipped": 2,
    "retried": 8,
    "success_rate": 88.7,
    "details": {
      "by_module": {
        "用户管理模块": { "total": 25, "passed": 23, "failed": 2 },
        "权限管理模块": { "total": 20, "passed": 18, "failed": 2 },
        "日志管理模块": { "total": 17, "passed": 14, "failed": 1 }
      },
      "by_priority": {
        "P0": { "total": 15, "passed": 15, "failed": 0 },
        "P1": { "total": 30, "passed": 27, "failed": 3 },
        "P2": { "total": 17, "passed": 13, "failed": 2 }
      },
      "retry_summary": {
        "total_retries": 8,
        "retry_success": 5,
        "retry_failed": 3
      }
    },
    "execution_plan": {
      "strategy": "smart",
      "concurrency": 12,
      "dependency_levels": 4,
      "batches": 8
    },
    "report_id": 9001,
    "summary_url": "/api/reports/9001/summary",
    "download_url": "/api/reports/9001/export",
    "artifacts_url": "/api/reports/9001/artifacts"
  }
}
```

### 17. 异步执行测试套件

**接口路径**: `POST /api/test-suites/{suite_id}/execute-async`  
**接口描述**: 异步执行指定测试套件的测试用例

#### 请求参数

与同步执行接口相同，但 `async` 参数会被自动设置为 `true`

#### 响应数据

返回任务信息，包含任务ID和监控URL

### 18. 查询测试套件任务状态

**接口路径**: `GET /api/suite-tasks/{task_id}/status`  
**接口描述**: 查询测试套件异步任务的执行状态

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

**任务执行中**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "task_id": "suite_task_abc123def456",
    "status": "running",
    "estimated_wait_time": 5
  }
}
```

**任务执行完成**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "execution_id": 50001,
    "suite_id": 10,
    "suite_name": "用户回归测试套件",
    "suite_code": "REGRESSION_USER_001",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:38:45.000Z",
    "total_duration": 525000,
    "total_cases": 62,
    "executed_cases": 62,
    "passed": 55,
    "failed": 5,
    "skipped": 2,
    "retried": 8,
    "success_rate": 88.7,
    "report_id": 9001,
    "summary_url": "/api/reports/9001/summary",
    "download_url": "/api/reports/9001/export",
    "artifacts_url": "/api/reports/9001/artifacts"
  }
}
```

### 19. 取消测试套件任务执行

**接口路径**: `POST /api/suite-tasks/{task_id}/cancel`  
**接口描述**: 取消正在执行的测试套件异步任务

#### 请求参数

**路径参数**:
- `task_id`: 任务ID (必填)

#### 响应数据

```json
{
  "code": 1,
  "msg": "任务取消成功",
  "data": true
}
```

## 错误响应

### 常见错误码

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| 1 | 200 | 成功 |
| 0 | 200 | 业务逻辑失败 |
| -1 | 401 | 认证失败 |
| -2 | 403 | 权限不足 |
| -3 | 400 | 参数校验失败 |
| -4 | 404 | 资源不存在 |
| -5 | 500 | 服务器内部异常 |

### 错误响应示例

**用例不存在**:
```json
{
  "code": -4,
  "msg": "测试用例不存在或未启用",
  "data": null
}
```

**接口不存在**:
```json
{
  "code": 0,
  "msg": "关联的接口不存在或已禁用",
  "data": null
}
```

**权限不足**:
```json
{
  "code": -2,
  "msg": "权限不足，无法执行测试用例",
  "data": null
}
```

**执行超时**:
```json
{
  "code": 0,
  "msg": "用例执行超时",
  "data": {
    "execution_id": 10001,
    "status": "failed",
    "failure_message": "执行超时（30秒）",
    "duration": 30000
  }
}
```

**项目不存在**:
```json
{
  "code": -4,
  "msg": "项目不存在",
  "data": null
}
```

**项目已禁用**:
```json
{
  "code": 0,
  "msg": "项目已禁用，无法执行测试",
  "data": null
}
```

**无可用用例**:
```json
{
  "code": 0,
  "msg": "该项目下没有可执行的测试用例",
  "data": null
}
```

**并发数超限**:
```json
{
  "code": -3,
  "msg": "并发数不能超过50",
  "data": null
}
```

**接口不存在**:
```json
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}
```

**接口已禁用**:
```json
{
  "code": 0,
  "msg": "接口已禁用，无法执行测试",
  "data": null
}
```

**无可用用例**:
```json
{
  "code": 0,
  "msg": "该接口下没有可执行的测试用例",
  "data": null
}
```

**接口并发数超限**:
```json
{
  "code": -3,
  "msg": "并发数不能超过10",
  "data": null
}
```

**测试套件不存在**:
```json
{
  "code": -4,
  "msg": "测试套件不存在",
  "data": null
}
```

**测试套件已禁用**:
```json
{
  "code": 0,
  "msg": "测试套件已禁用，无法执行",
  "data": null
}
```

**无可用用例**:
```json
{
  "code": 0,
  "msg": "该测试套件下没有可执行的测试用例",
  "data": null
}
```

**测试套件并发数超限**:
```json
{
  "code": -3,
  "msg": "并发数不能超过20",
  "data": null
}
```

**依赖分析失败**:
```json
{
  "code": 0,
  "msg": "用例依赖关系分析失败，存在循环依赖",
  "data": null
}
```

## 执行状态说明

### 执行状态枚举

| 状态 | 说明 |
|------|------|
| pending | 待执行 |
| running | 执行中 |
| passed | 通过 |
| failed | 失败 |
| broken | 中断 |
| skipped | 跳过 |
| unknown | 未知 |
| cancelled | 已取消 |

### 状态转换流程

1. **同步执行**: `pending` → `running` → `passed/failed/broken/skipped`
2. **异步执行**: `pending` → `running` → `passed/failed/broken/skipped`
3. **任务取消**: `pending/running` → `cancelled`

## 权限要求

### 所需权限

- `testcase:execute`: 执行测试用例
- `testcase:view`: 查看测试结果和日志
- `module:execute`: 执行模块测试
- `module:view`: 查看模块测试结果
- `project:execute`: 执行项目测试
- `project:view`: 查看项目测试结果
- `api:execute`: 执行接口测试
- `api:view`: 查看接口测试结果
- `suite:execute`: 执行测试套件
- `suite:view`: 查看测试套件测试结果

### 权限验证

所有接口都需要通过 `@GlobalInterceptor` 进行权限验证：
- 登录验证
- 权限检查
- 资源访问控制

## 使用示例

### 1. 同步执行测试用例

```bash
curl -X POST "http://localhost:8080/api/test-cases/1001/execute" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "timeout": 60,
    "variables": {
      "username": "testuser",
      "password": "Test123!"
    }
  }'
```

### 2. 异步执行测试用例

```bash
curl -X POST "http://localhost:8080/api/test-cases/1001/execute-async" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "variables": {
      "username": "testuser",
      "password": "Test123!"
    }
  }'
```

### 3. 查询任务状态

```bash
curl -X GET "http://localhost:8080/api/tasks/task_abc123def456/status" \
  -H "Authorization: Bearer your_token"
```

### 4. 获取执行结果

```bash
curl -X GET "http://localhost:8080/api/test-results/10001" \
  -H "Authorization: Bearer your_token"
```

### 5. 执行模块测试

```bash
curl -X POST "http://localhost:8080/api/modules/1/execute" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 5,
    "case_filter": {
      "priority": ["P0", "P1"],
      "enabled_only": true
    }
  }'
```

### 6. 查询模块任务状态

```bash
curl -X GET "http://localhost:8080/api/module-tasks/module_task_abc123/status" \
  -H "Authorization: Bearer your_token"
```

### 7. 执行项目测试

```bash
curl -X POST "http://localhost:8080/api/projects/1/execute" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 20,
    "execution_strategy": "by_module",
    "module_filter": {
      "module_ids": [1, 2, 3],
      "status": "active"
    },
    "case_filter": {
      "priority": ["P0", "P1"],
      "tags": ["冒烟测试"],
      "enabled_only": true
    },
    "variables": {
      "env": "test",
      "version": "2.0.0"
    }
  }'
```

### 8. 查询项目任务状态

```bash
curl -X GET "http://localhost:8080/api/project-tasks/project_task_abc123/status" \
  -H "Authorization: Bearer your_token"
```

### 9. 执行接口测试

```bash
curl -X POST "http://localhost:8080/api/apis/101/execute" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "test",
    "concurrency": 5,
    "case_filter": {
      "priority": ["P0", "P1"],
      "tags": ["冒烟测试"],
      "enabled_only": true
    },
    "execution_order": "priority_desc",
    "variables": {
      "test_user": "test001",
      "test_password": "Pass123!"
    }
  }'
```

### 10. 查询接口任务状态

```bash
curl -X GET "http://localhost:8080/api/api-tasks/api_task_abc123/status" \
  -H "Authorization: Bearer your_token"
```

### 11. 执行测试套件

```bash
curl -X POST "http://localhost:8080/api/test-suites/10/execute" \
  -H "Authorization: Bearer your_token" \
  -H "Content-Type: application/json" \
  -d '{
    "environment": "staging",
    "concurrency": 12,
    "execution_strategy": "smart",
    "stop_on_failure": false,
    "retry_config": {
      "enabled": true,
      "max_attempts": 2,
      "delay_ms": 2000
    },
    "case_filter": {
      "priority": ["P0", "P1", "P2"],
      "tags": ["回归测试"],
      "enabled_only": true
    },
    "variables": {
      "environment": "staging",
      "version": "2.1.0",
      "test_data_id": "dataset_001"
    },
    "report_config": {
      "detailed": true,
      "include_artifacts": true
    }
  }'
```

### 12. 查询测试套件任务状态

```bash
curl -X GET "http://localhost:8080/api/suite-tasks/suite_task_abc123/status" \
  -H "Authorization: Bearer your_token"
```

## 技术实现

### 核心组件

1. **TestCaseExecutor**: 测试用例执行器
2. **HttpClientUtils**: HTTP客户端工具
3. **AssertionUtils**: 断言工具
4. **TestExecutionService**: 执行服务
5. **TestExecutionMapper**: 数据访问层

### 执行流程

1. **验证阶段**: 验证用例存在性和权限
2. **准备阶段**: 构建HTTP请求参数
3. **执行阶段**: 发送HTTP请求
4. **验证阶段**: 执行断言和提取规则
5. **记录阶段**: 保存执行结果和生成报告

### 异步任务管理

- 使用 `CompletableFuture` 实现异步执行
- 内存中存储任务状态（生产环境建议使用Redis）
- 支持任务取消和状态查询

### 数据存储

- **TestCaseResults**: 存储测试执行结果
- **TestReportSummaries**: 存储测试报告汇总
- **Logs**: 存储操作日志

## 注意事项

1. **超时控制**: 默认30秒超时，最大300秒
2. **环境配置**: 支持dev、test、prod、staging环境
3. **变量替换**: 支持{{variable}}格式的变量替换
4. **断言支持**: 支持多种断言类型（equals、contains、regex等）
5. **错误处理**: 完善的异常处理和错误信息记录
6. **权限控制**: 严格的权限验证和资源访问控制
7. **日志记录**: 详细的执行日志和操作审计
8. **报告生成**: 自动生成测试报告和统计信息
