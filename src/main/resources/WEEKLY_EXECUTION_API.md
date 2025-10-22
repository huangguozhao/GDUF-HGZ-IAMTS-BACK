# 近七天测试执行情况接口 API 文档

## 接口信息

**接口路径**: `/api/weekly-execution`
**请求方式**: `GET`
**接口描述**: 获取最近七天的测试执行情况统计，用于Dashboard展示和快速概览。**此接口需要认证。**

## 请求参数

### 请求头 (Headers)

| 参数名          | 类型   | 是否必须 | 备注                             |
| :-------------- | :----- | :------- | :------------------------------- |
| `Authorization` | string | 必须     | 认证令牌，格式: `Bearer {token}` |

### 查询参数 (Query String)

| 参数名称               | 是否必须 | 类型    | 示例    | 备注                               |
| :--------------------- | :------- | :------ | :------ | :--------------------------------- |
| `project_id`           | 否       | integer | `1`     | 按项目ID过滤                       |
| `module_id`            | 否       | integer | `5`     | 按模块ID过滤                       |
| `environment`          | 否       | string  | `test`  | 按环境过滤                         |
| `include_daily_trend`  | 否       | boolean | `true`  | 是否包含每日趋势数据，默认: `true` |
| `include_top_failures` | 否       | boolean | `true`  | 是否包含主要失败原因，默认: `true` |
| `include_performance`  | 否       | boolean | `false` | 是否包含性能指标，默认: `false`    |

### 请求示例

```
GET /api/weekly-execution
GET /api/weekly-execution?project_id=1
GET /api/weekly-execution?environment=test&include_top_failures=true
GET /api/weekly-execution?module_id=5&include_daily_trend=true&include_performance=true
```

## 响应数据

### 成功响应示例 (HTTP 200)

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "date_range": {
      "start_date": "2024-09-09",
      "end_date": "2024-09-16",
      "days": 7
    },
    "summary": {
      "total_executions": 856,
      "total_cases": 125,
      "passed": 682,
      "failed": 142,
      "broken": 18,
      "skipped": 14,
      "success_rate": 79.7,
      "avg_duration": 1450,
      "change_from_last_week": {
        "success_rate_change": 2.5,
        "trend": "up",
        "executions_change": 15.2
      }
    },
    "daily_trend": [
      {
        "date": "2024-09-10",
        "day_of_week": "周二",
        "total": 120,
        "passed": 98,
        "failed": 18,
        "broken": 2,
        "skipped": 2,
        "success_rate": 81.7,
        "avg_duration": 1380
      },
      {
        "date": "2024-09-11",
        "day_of_week": "周三",
        "total": 135,
        "passed": 110,
        "failed": 22,
        "broken": 3,
        "skipped": 0,
        "success_rate": 81.5,
        "avg_duration": 1420
      },
      {
        "date": "2024-09-12",
        "day_of_week": "周四",
        "total": 128,
        "passed": 102,
        "failed": 20,
        "broken": 4,
        "skipped": 2,
        "success_rate": 79.7,
        "avg_duration": 1480
      }
    ],
    "project_stats": [
      {
        "project_id": 1,
        "project_name": "电商平台",
        "executions": 420,
        "success_rate": 82.4,
        "change": 3.1
      },
      {
        "project_id": 2,
        "project_name": "后台管理系统",
        "executions": 236,
        "success_rate": 78.8,
        "change": -1.2
      },
      {
        "project_id": 3,
        "project_name": "移动端API",
        "executions": 200,
        "success_rate": 75.5,
        "change": 0.5
      }
    ],
    "module_stats": [
      {
        "module_id": 5,
        "module_name": "用户管理",
        "executions": 185,
        "success_rate": 85.9,
        "avg_duration": 1250
      },
      {
        "module_id": 8,
        "module_name": "订单管理",
        "executions": 162,
        "success_rate": 80.2,
        "avg_duration": 1680
      },
      {
        "module_id": 12,
        "module_name": "支付系统",
        "executions": 145,
        "success_rate": 78.6,
        "avg_duration": 1950
      }
    ],
    "top_failures": [
      {
        "failure_type": "timeout_error",
        "count": 45,
        "percentage": 31.7,
        "avg_duration": 8200,
        "trend": "up"
      },
      {
        "failure_type": "assertion_failed",
        "count": 38,
        "percentage": 26.8,
        "main_cases": ["用户登录-并发测试", "订单创建-库存验证"]
      },
      {
        "failure_type": "connection_error",
        "count": 25,
        "percentage": 17.6,
        "environment": "test"
      }
    ],
    "performance_metrics": {
      "p95_duration": 2850,
      "p99_duration": 5200,
      "max_duration": 12500,
      "min_duration": 120,
      "throughput": 68.5
    },
    "quality_trend": {
      "current_week_success": [81.7, 81.5, 79.7, 78.2, 80.1, 76.8, 79.3],
      "last_week_success": [79.2, 78.5, 77.8, 76.4, 78.9, 75.2, 77.6],
      "improvement": true
    }
  }
}
```

### 失败响应示例

```json
// 无执行数据 (HTTP 200)
{
  "code": 0,
  "msg": "近七天无测试执行数据",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看执行统计",
  "data": null
}

// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

## 接口逻辑说明

1. **时间范围计算**: 自动计算最近7天的日期范围
2. **数据查询**: 查询 `TestCaseResults` 表中最近7天的执行记录
3. **趋势分析**: 生成每日执行趋势折线图数据
4. **排行统计**: 计算项目、模块执行排行（前5）
5. **失败分析**: 列出主要失败原因，便于问题定位
6. **性能指标**: 包含执行时长分布和吞吐量指标
7. **质量对比**: 提供与上周的对比数据，显示改善情况

## 使用示例

### curl测试

```bash
# 获取最近7天执行情况（默认）
curl http://localhost:8080/api/weekly-execution

# 按项目过滤
curl "http://localhost:8080/api/weekly-execution?project_id=1"

# 按环境过滤
curl "http://localhost:8080/api/weekly-execution?environment=test"

# 包含性能指标
curl "http://localhost:8080/api/weekly-execution?include_performance=true"

# 不包含失败原因分析
curl "http://localhost:8080/api/weekly-execution?include_top_failures=false"
```

## 技术实现

### Mapper层
- `getWeeklySummary()` - 近七天总体统计
- `getWeeklyDailyTrend()` - 每日趋势数据
- `getWeeklyProjectStats()` - 项目统计排行
- `getWeeklyModuleStats()` - 模块统计排行
- `getWeeklyTopFailures()` - 主要失败原因
- `getWeeklyPerformanceMetrics()` - 性能指标
- `getLastWeekSummary()` - 上周同期数据

### Service层
- `getWeeklyExecution()` - 主方法
- `buildQualityTrend()` - 质量趋势对比

### Controller层
- `GET /api/weekly-execution` - REST接口

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

- 该接口主要用于Dashboard展示，应该保证响应速度
- 建议使用缓存机制，避免频繁查询数据库
- 对于大数据量，可以考虑使用预聚合表或物化视图
- 返回的数据结构应该便于前端图表组件直接使用

## 权限控制

使用 `@GlobalInterceptor` 注解：
- `checkLogin = true` - 需要登录
- `checkPermission = {"testcase:view"}` - 需要测试用例查看权限



