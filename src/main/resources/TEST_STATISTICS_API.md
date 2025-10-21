# 测试统计信息接口 API 文档

## 接口信息

**接口路径**: `/api/test-results/statistics`
**请求方式**: `GET`
**接口描述**: 获取测试执行的统计信息，支持按时间范围、项目、模块等多维度统计。**此接口需要认证。**

## 请求参数

### 请求头 (Headers)

| 参数名          | 类型   | 是否必须 | 备注                             |
| :-------------- | :----- | :------- | :------------------------------- |
| `Authorization` | string | 必须     | 认证令牌，格式: `Bearer {token}` |

### 查询参数 (Query String)

| 参数名称             | 是否必须 | 类型    | 示例                   | 备注                                                         |
| :------------------- | :------- | :------ | :--------------------- | :----------------------------------------------------------- |
| `time_range`         | 否       | string  | `7d`                   | 时间范围。可选: `1d`, `7d`, `30d`, `90d`, `custom`，默认: `7d` |
| `start_time`         | 否       | string  | `2024-09-01T00:00:00Z` | 自定义开始时间（ISO格式），time_range=custom时有效           |
| `end_time`           | 否       | string  | `2024-09-16T23:59:59Z` | 自定义结束时间（ISO格式），time_range=custom时有效           |
| `project_id`         | 否       | integer | `1`                    | 按项目ID过滤                                                 |
| `module_id`          | 否       | integer | `5`                    | 按模块ID过滤                                                 |
| `api_id`             | 否       | integer | `101`                  | 按接口ID过滤                                                 |
| `environment`        | 否       | string  | `test`                 | 按环境过滤                                                   |
| `group_by`           | 否       | string  | `day`                  | 分组方式。可选: `hour`, `day`, `week`, `month`, `priority`, `severity`，默认: `day` |
| `include_trend`      | 否       | boolean | `true`                 | 是否包含趋势数据，默认: `true`                               |
| `include_comparison` | 否       | boolean | `false`                | 是否包含同比环比数据，默认: `false`                          |

### 请求示例

```
GET /api/test-results/statistics
GET /api/test-results/statistics?time_range=30d&group_by=priority
GET /api/test-results/statistics?project_id=1&module_id=5&group_by=day
GET /api/test-results/statistics?start_time=2024-09-01T00:00:00Z&end_time=2024-09-16T23:59:59Z&time_range=custom
GET /api/test-results/statistics?environment=test&include_trend=true&include_comparison=true
```

## 响应数据

### 成功响应示例 (HTTP 200)

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "summary": {
      "total_executions": 125,
      "total_cases": 85,
      "passed": 98,
      "failed": 20,
      "broken": 2,
      "skipped": 5,
      "success_rate": 78.40,
      "avg_duration": 1567,
      "max_duration": 12500,
      "min_duration": 120,
      "start_time": "2024-09-09T00:00:00.000Z",
      "end_time": "2024-09-16T00:00:00.000Z"
    },
    "trend_data": [
      {
        "time_period": "2024-09-09",
        "label": "09-09",
        "total": 15,
        "passed": 12,
        "failed": 2,
        "broken": 1,
        "skipped": 0,
        "success_rate": 80.00,
        "avg_duration": 1420
      },
      {
        "time_period": "2024-09-10",
        "label": "09-10",
        "total": 18,
        "passed": 15,
        "failed": 2,
        "broken": 0,
        "skipped": 1,
        "success_rate": 83.33,
        "avg_duration": 1380
      }
    ],
    "group_data": [
      {
        "group_key": "P0",
        "group_name": "P0-最高优先级",
        "total": 32,
        "passed": 31,
        "failed": 1,
        "broken": 0,
        "skipped": 0,
        "success_rate": 96.88,
        "avg_duration": 1250
      },
      {
        "group_key": "P1",
        "group_name": "P1-高优先级",
        "total": 56,
        "passed": 45,
        "failed": 9,
        "broken": 1,
        "skipped": 1,
        "success_rate": 80.36,
        "avg_duration": 1680
      }
    ],
    "comparison_data": {
      "previous_period": {
        "success_rate": 75.20,
        "change_percent": 3.20,
        "trend": "up"
      },
      "year_over_year": {
        "success_rate": 70.80,
        "change_percent": 7.60,
        "trend": "up"
      }
    },
    "top_issues": [
      {
        "failure_type": "timeout",
        "count": 8,
        "percentage": 40.00,
        "avg_duration": 8500
      },
      {
        "failure_type": "assertion_error",
        "count": 7,
        "percentage": 35.00,
        "avg_duration": 1650
      },
      {
        "failure_type": "connection_error",
        "count": 5,
        "percentage": 25.00,
        "avg_duration": 3200
      }
    ],
    "execution_metrics": {
      "total_duration": 195875,
      "avg_concurrency": 8.5,
      "peak_concurrency": 15,
      "throughput": 73.24,
      "reliability": 78.40
    }
  }
}
```

### 失败响应示例

```json
// 时间范围参数错误 (HTTP 400)
{
  "code": -3,
  "msg": "自定义时间范围时，必须提供start_time和end_time参数",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看统计信息",
  "data": null
}
```

## 接口逻辑说明

1. **时间范围解析**: 支持预设时间范围（1d/7d/30d/90d）和自定义时间范围
2. **数据查询**: 执行复杂的聚合查询和统计计算
3. **趋势分析**: 按指定时间粒度（小时/天/周/月）生成趋势数据
4. **维度分组**: 支持按优先级、严重程度等维度分组统计
5. **同比环比**: 计算与上一周期和去年同期的对比数据
6. **问题分析**: 统计主要失败类型和占比
7. **性能指标**: 计算吞吐量、可靠性等指标

## 使用示例

### curl测试

```bash
# 获取最近7天的统计（默认）
curl http://localhost:8080/api/test-results/statistics

# 获取最近30天按优先级分组的统计
curl "http://localhost:8080/api/test-results/statistics?time_range=30d&group_by=priority"

# 自定义时间范围
curl "http://localhost:8080/api/test-results/statistics?time_range=custom&start_time=2024-09-01T00:00:00&end_time=2024-09-16T23:59:59"

# 包含同比环比数据
curl "http://localhost:8080/api/test-results/statistics?include_comparison=true"

# 按项目过滤
curl "http://localhost:8080/api/test-results/statistics?project_id=1&group_by=day"
```

## 技术实现

### Mapper层
- `getStatisticsSummary()` - 总体统计
- `getTrendData()` - 趋势数据（按时间分组）
- `getGroupData()` - 分组数据（按维度分组）
- `getTopIssues()` - 主要问题统计

### Service层
- `getTestStatistics()` - 主方法
- `parseTimeRange()` - 时间范围解析
- `buildComparisonData()` - 同比环比计算
- `buildExecutionMetrics()` - 执行指标计算

### Controller层
- `GET /api/test-results/statistics` - REST接口

## 性能优化

1. **数据库优化**
   - 使用聚合函数减少数据传输
   - 建立必要的索引

2. **缓存策略**
   - 统计结果可缓存5-10分钟
   - 使用Redis缓存热点数据

3. **异步计算**
   - 大数据量时使用异步计算
   - 提供任务查询接口

## 注意事项

- 统计查询可能较慢，建议增加缓存
- 同比环比数据计算需要历史数据支持
- 默认包含趋势数据，不包含同比环比数据
- 分组维度会影响返回的数据结构

## 权限控制

使用 `@GlobalInterceptor` 注解：
- `checkLogin = true` - 需要登录
- `checkPermission = {"testcase:view"}` - 需要测试用例查看权限



