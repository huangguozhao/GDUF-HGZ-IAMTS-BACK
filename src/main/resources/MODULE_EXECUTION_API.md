# 模块执行测试 API 文档

## 接口概述

本文档描述了模块执行测试相关的API接口，包括同步执行、异步执行、任务状态查询和任务取消等功能。

## 基础信息

- **基础路径**: `/api`
- **认证方式**: Bearer Token
- **内容类型**: `application/json`

## 接口列表

### 1. 执行模块测试（同步）

**接口描述**: 同步执行指定模块下的所有测试用例

**请求信息**:
- **路径**: `POST /modules/{module_id}/execute`
- **认证**: 需要
- **权限**: `module:execute`

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| module_id | Integer | 是 | 要执行测试的模块ID |

**请求体参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| environment | String | 否 | 执行环境标识（如: dev, test, prod） |
| base_url | String | 否 | 覆盖所有接口的基础URL |
| timeout | Integer | 否 | 全局超时时间（秒） |
| auth_override | Object | 否 | 全局认证信息覆盖配置 |
| variables | Object | 否 | 全局执行变量 |
| async | Boolean | 否 | 是否异步执行，默认: true |
| callback_url | String | 否 | 异步执行完成后的回调URL |
| concurrency | Integer | 否 | 并发执行数，默认: 5 |
| case_filter | Object | 否 | 用例过滤条件 |
| case_filter.priority | String[] | 否 | 优先级过滤，如: ["P0", "P1"] |
| case_filter.tags | String[] | 否 | 标签过滤，如: ["冒烟测试"] |
| case_filter.enabled_only | Boolean | 否 | 是否只执行启用的用例，默认: true |

**请求示例**:
```json
{
  "environment": "test",
  "base_url": "https://test-api.example.com",
  "timeout": 60,
  "async": false,
  "concurrency": 10,
  "case_filter": {
    "priority": ["P0", "P1"],
    "tags": ["冒烟测试"],
    "enabled_only": true
  },
  "variables": {
    "env": "test",
    "version": "1.2.0"
  }
}
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "模块测试执行完成",
  "data": {
    "execution_id": 20001,
    "module_id": 5,
    "module_name": "用户管理模块",
    "start_time": "2024-09-16T10:30:00.000Z",
    "end_time": "2024-09-16T10:35:15.000Z",
    "total_duration": 315000,
    "total_cases": 15,
    "passed": 12,
    "failed": 2,
    "skipped": 1,
    "broken": 0,
    "success_rate": 80.0,
    "details": {
      "by_priority": {
        "P0": { "total": 5, "passed": 5, "failed": 0, "skipped": 0, "broken": 0 },
        "P1": { "total": 10, "passed": 7, "failed": 2, "skipped": 1, "broken": 0 }
      },
      "by_api": {
        "用户登录接口": { "total": 3, "passed": 3, "failed": 0, "skipped": 0, "broken": 0 },
        "用户注册接口": { "total": 5, "passed": 4, "failed": 1, "skipped": 0, "broken": 0 }
      }
    },
    "report_id": 6001,
    "summary_url": "/api/reports/6001/summary"
  }
}
```

### 2. 异步执行模块测试

**接口描述**: 异步执行指定模块下的所有测试用例，立即返回任务信息

**请求信息**:
- **路径**: `POST /modules/{module_id}/execute-async`
- **认证**: 需要
- **权限**: `module:execute`

**请求参数**: 与同步执行相同

**响应示例**:
```json
{
  "code": 1,
  "msg": "模块测试执行任务已提交",
  "data": {
    "task_id": "module_task_abc123def456",
    "module_id": 5,
    "module_name": "用户管理模块",
    "total_cases": 25,
    "filtered_cases": 15,
    "status": "queued",
    "concurrency": 10,
    "estimated_duration": 120,
    "queue_position": 2,
    "monitor_url": "/api/tasks/module_task_abc123def456/status",
    "report_url": "/api/reports/module/5/executions/latest"
  }
}
```

### 3. 查询任务状态

**接口描述**: 查询异步执行任务的状态

**请求信息**:
- **路径**: `GET /tasks/{task_id}/status`
- **认证**: 需要
- **权限**: `module:view`

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| task_id | String | 是 | 任务ID |

**响应示例**:
```json
{
  "code": 1,
  "msg": "获取任务状态成功",
  "data": {
    "task_id": "module_task_abc123def456",
    "module_id": 5,
    "module_name": "用户管理模块",
    "status": "running",
    "total_cases": 15,
    "passed": 8,
    "failed": 1,
    "skipped": 0,
    "broken": 0,
    "success_rate": 53.3,
    "monitor_url": "/api/tasks/module_task_abc123def456/status"
  }
}
```

### 4. 取消任务执行

**接口描述**: 取消正在执行的异步任务

**请求信息**:
- **路径**: `POST /tasks/{task_id}/cancel`
- **认证**: 需要
- **权限**: `module:execute`

**路径参数**:
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| task_id | String | 是 | 任务ID |

**响应示例**:
```json
{
  "code": 1,
  "msg": "任务取消成功",
  "data": true
}
```

## 错误响应

### 常见错误码

| 错误码 | HTTP状态码 | 描述 | 处理建议 |
|--------|------------|------|----------|
| 1 | 200 | 成功 | - |
| 0 | 200 | 业务逻辑失败 | 展示msg给用户 |
| -2 | 403 | 权限不足 | 提示用户"权限不足" |
| -3 | 400 | 参数校验失败 | 提示用户检查输入 |
| -4 | 404 | 资源不存在 | 提示用户"请求的资源不存在" |
| -5 | 500 | 服务器内部异常 | 提示用户"系统繁忙，请稍后再试" |

### 错误响应示例

```json
// 模块不存在
{
  "code": -4,
  "msg": "模块不存在",
  "data": null
}

// 模块已禁用
{
  "code": 0,
  "msg": "模块已禁用，无法执行测试",
  "data": null
}

// 无可用用例
{
  "code": 0,
  "msg": "该模块下没有可执行的测试用例",
  "data": null
}

// 并发数超限
{
  "code": -3,
  "msg": "并发数不能超过50",
  "data": null
}
```

## 任务状态说明

| 状态 | 描述 |
|------|------|
| queued | 排队中 |
| running | 执行中 |
| completed | 已完成 |
| failed | 执行失败 |
| cancelled | 已取消 |
| timeout | 执行超时 |

## 注意事项

1. **认证要求**: 所有接口都需要在请求头中携带有效的Bearer Token
2. **权限控制**: 不同接口需要不同的权限，请确保用户具有相应权限
3. **并发限制**: 并发执行数不能超过50，建议根据系统性能调整
4. **超时设置**: 建议设置合理的超时时间，避免长时间等待
5. **异步执行**: 对于大型模块，建议使用异步执行模式
6. **任务监控**: 异步执行后可通过任务状态接口监控执行进度
7. **结果查看**: 执行完成后可通过报告ID查看详细的测试结果
