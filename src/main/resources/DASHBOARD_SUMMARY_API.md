# 个人测试概况接口 API 文档

## 接口信息

**接口路径**: `/api/dashboard/summary`
**请求方式**: `GET`
**接口描述**: 获取当前用户的个人测试概况，包括执行统计、待办事项、最近活动等。**此接口需要认证。**

## 请求参数

### 请求头 (Headers)

| 参数名          | 类型   | 是否必须 | 备注                             |
| :-------------- | :----- | :------- | :------------------------------- |
| `Authorization` | string | 必须     | 认证令牌，格式: `Bearer {token}` |

### 查询参数 (Query String)

| 参数名称                  | 是否必须 | 类型    | 示例   | 备注                                          |
| :------------------------ | :------- | :------ | :----- | :-------------------------------------------- |
| `time_range`              | 否       | string  | `7d`   | 时间范围。可选: `1d`, `7d`, `30d`，默认: `7d` |
| `include_recent_activity` | 否       | boolean | `true` | 是否包含最近活动，默认: `true`                |
| `include_pending_tasks`   | 否       | boolean | `true` | 是否包含待办事项，默认: `true`                |
| `include_quick_actions`   | 否       | boolean | `true` | 是否包含快捷操作，默认: `true`                |

### 请求示例

```
GET /api/dashboard/summary
GET /api/dashboard/summary?time_range=30d
GET /api/dashboard/summary?include_recent_activity=true&include_pending_tasks=true
```

## 响应数据

### 成功响应示例 (HTTP 200)

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "user_info": {
      "user_id": 123,
      "name": "张测试",
      "avatar_url": "/avatars/zhangceshi.jpg",
      "department": "质量保障部",
      "position": "高级测试工程师",
      "last_login": "2024-09-16T08:30:00.000Z",
      "member_since": "2023-03-15T00:00:00.000Z"
    },
    "execution_stats": {
      "total_executions": 856,
      "success_rate": 82.5,
      "avg_duration": 1450,
      "cases_created": 45,
      "cases_maintained": 120,
      "bugs_found": 28,
      "trend": "up",
      "change_percent": 3.2
    },
    "project_stats": [
      {
        "project_id": 1,
        "project_name": "电商平台",
        "executions": 420,
        "success_rate": 85.2,
        "change": 3.1,
        "avg_duration": 1250
      },
      {
        "project_id": 2,
        "project_name": "后台管理系统",
        "executions": 236,
        "success_rate": 78.8,
        "change": -1.2,
        "avg_duration": 1680
      }
    ],
    "recent_activity": [
      {
        "activity_id": 1001,
        "type": "case_execution",
        "description": "执行了测试用例",
        "target_id": 101,
        "target_name": "用户登录-成功场景",
        "timestamp": "2024-09-16T14:30:00.000Z",
        "details": {
          "status": "passed",
          "duration": 1245
        }
      },
      {
        "activity_id": 1002,
        "type": "case_creation",
        "description": "创建了新的测试用例",
        "target_id": 205,
        "target_name": "订单支付-超时处理",
        "timestamp": "2024-09-16T11:20:00.000Z",
        "details": {
          "priority": "P1",
          "module": "支付系统"
        }
      }
    ],
    "pending_tasks": [
      {
        "task_id": 5001,
        "type": "case_review",
        "title": "评审新功能测试用例",
        "priority": "high",
        "due_date": "2024-09-18T23:59:59.000Z",
        "assigner": "李经理",
        "progress": 0
      },
      {
        "task_id": 5002,
        "type": "bug_verification",
        "title": "验证缺陷修复 #BUG-1024",
        "priority": "medium",
        "due_date": "2024-09-17T18:00:00.000Z",
        "assigner": "王开发",
        "progress": 50
      }
    ],
    "quick_actions": [
      {
        "name": "快速执行",
        "icon": "play-circle",
        "url": "/quick-execute",
        "description": "快速执行测试用例"
      },
      {
        "name": "创建用例",
        "icon": "plus-circle",
        "url": "/test-cases/create",
        "description": "创建新的测试用例"
      },
      {
        "name": "查看报告",
        "icon": "bar-chart",
        "url": "/reports",
        "description": "查看测试报告"
      },
      {
        "name": "数据统计",
        "icon": "pie-chart",
        "url": "/statistics",
        "description": "查看数据统计"
      }
    ],
    "system_status": {
      "total_cases": 1250,
      "active_projects": 8,
      "today_executions": 156,
      "system_health": "good"
    },
    "health_score": {
      "overall": 86,
      "execution_quality": 92,
      "case_coverage": 78,
      "defect_density": 15,
      "trend": "improving"
    }
  }
}
```

### 失败响应示例

```json
// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}

// 用户信息不存在 (HTTP 404)
{
  "code": -4,
  "msg": "用户信息不存在",
  "data": null
}
```

## 接口逻辑说明

1. **认证与授权**: 验证 Token 并获取当前用户信息
2. **多数据源查询**: 
   - 查询用户基本信息
   - 统计用户的测试执行数据
   - 获取用户相关的项目信息
   - 查询最近的活动记录
   - 获取待办任务列表
3. **数据处理**: 
   - 计算各种统计指标和评分
   - 格式化时间和数字字段
   - 生成趋势和变化数据
4. **性能优化**: 
   - 使用并行查询提高效率
   - 对静态数据进行缓存
5. **返回结果**: 返回结构化的个人测试概况

## 使用示例

### curl测试

```bash
# 获取个人测试概况（默认）
curl http://localhost:8080/api/dashboard/summary

# 获取最近30天的概况
curl "http://localhost:8080/api/dashboard/summary?time_range=30d"

# 不包含待办事项
curl "http://localhost:8080/api/dashboard/summary?include_pending_tasks=false"

# 不包含最近活动
curl "http://localhost:8080/api/dashboard/summary?include_recent_activity=false"
```

## 技术实现

### Mapper层
- `getUserInfo()` - 用户基本信息
- `getUserExecutionStats()` - 用户执行统计
- `getUserProjectStats()` - 用户项目统计
- `getUserRecentActivity()` - 最近活动记录
- `getUserPendingTasks()` - 待办事项
- `getSystemStatus()` - 系统状态
- `getUserHealthScore()` - 质量健康评分

### Service层
- `getDashboardSummary()` - 主方法
- `buildQuickActions()` - 构建快捷操作

### Controller层
- `GET /api/dashboard/summary` - REST接口

## 性能优化

1. **数据库优化**
   - 使用聚合函数减少数据传输
   - 建立必要的索引

2. **缓存策略**
   - 用户基本信息可缓存
   - 系统状态信息可缓存

3. **并行查询**
   - 多个数据源并行查询
   - 减少响应时间

## 注意事项

- 该接口主要用于Dashboard首页展示，应该保证响应速度
- 建议使用缓存机制，避免频繁查询数据库
- 应该确保数据的安全性，只返回用户有权限访问的信息
- 接口应该支持个性化配置，允许用户定制Dashboard内容

## 权限控制

使用 `@GlobalInterceptor` 注解：
- `checkLogin = true` - 需要登录
- `checkPermission = {"testcase:view"}` - 需要测试用例查看权限


