# 定时测试任务 API 文档

## 概述

定时测试任务功能允许用户创建自动执行的测试计划，支持多种触发方式（Cron表达式、每日、每周、每月等），可以定时执行测试用例、模块、项目或API测试。

## API 列表

### 1. 创建定时任务

**POST** `/api/scheduled-tasks`

**请求头：**
- Authorization: Bearer {token}

**请求体：**
```json
{
  "taskName": "每日API测试",
  "description": "每天早上9点执行用户管理模块的所有测试",
  "taskType": "module",  // single_case, module, project, test_suite, api
  "targetId": 1,
  "triggerType": "daily",  // cron, simple, daily, weekly, monthly
  "dailyHour": 9,
  "dailyMinute": 0,
  "executionEnvironment": "test",  // dev, test, prod, staging
  "concurrency": 3,
  "executionStrategy": "by_module",
  "retryEnabled": true,
  "maxRetryAttempts": 2,
  "notifyOnFailure": true,
  "notificationRecipients": "test@example.com"
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "定时任务创建成功",
  "data": {
    "taskId": 1,
    "taskName": "每日API测试",
    "isEnabled": true,
    "nextTriggerTime": "2024-09-16T09:00:00",
    "createdAt": "2024-09-15T14:30:00"
  }
}
```

### 2. 更新定时任务

**PUT** `/api/scheduled-tasks/{taskId}`

**请求体：** 同创建接口

### 3. 删除定时任务

**DELETE** `/api/scheduled-tasks/{taskId}`

### 4. 获取任务详情

**GET** `/api/scheduled-tasks/{taskId}`

### 5. 分页查询任务列表

**GET** `/api/scheduled-tasks?page=1&pageSize=10&taskType=module&isEnabled=true`

**查询参数：**
- page: 页码（默认1）
- pageSize: 每页大小（默认20）
- taskName: 任务名称（模糊查询）
- taskType: 任务类型
- targetId: 目标ID
- triggerType: 触发器类型
- isEnabled: 是否启用
- executionEnvironment: 执行环境

### 6. 启用任务

**POST** `/api/scheduled-tasks/{taskId}/enable`

### 7. 禁用任务

**POST** `/api/scheduled-tasks/{taskId}/disable`

### 8. 立即执行任务

**POST** `/api/scheduled-tasks/{taskId}/execute`

### 9. 获取执行历史

**GET** `/api/scheduled-tasks/{taskId}/history?page=1&pageSize=10`

### 10. 获取执行统计

**GET** `/api/scheduled-tasks/{taskId}/statistics`

### 11. 获取执行记录详情

**GET** `/api/scheduled-tasks/executions/{executionId}`

## 触发器类型说明

### 1. Cron表达式
```json
{
  "triggerType": "cron",
  "cronExpression": "0 0 9 * * ?"  // 每天上午9点
}
```

常见Cron表达式：
- `0 0 9 * * ?` - 每天上午9点
- `0 30 9 * * ?` - 每天上午9点30分
- `0 0 */2 * * ?` - 每两小时执行一次
- `0 0 9 ? * MON` - 每周一早上9点
- `0 0 9 1 * ?` - 每月1号早上9点
- `0 0 0 * * ?` - 每天午夜12点

### 2. 每日执行
```json
{
  "triggerType": "daily",
  "dailyHour": 9,
  "dailyMinute": 30
}
```

### 3. 每周执行
```json
{
  "triggerType": "weekly",
  "weeklyDays": "1,3,5",  // 1=周一, 3=周三, 5=周五
  "dailyHour": 9,
  "dailyMinute": 0
}
```

### 4. 每月执行
```json
{
  "triggerType": "monthly",
  "monthlyDay": 15,
  "dailyHour": 9,
  "dailyMinute": 0
}
```

### 5. 简单重复
```json
{
  "triggerType": "simple",
  "simpleRepeatInterval": 3600000,  // 间隔（毫秒）
  "simpleRepeatCount": 10  // 重复次数，-1表示无限
}
```

## 使用步骤

### 步骤1：执行数据库脚本
```bash
# 登录MySQL
mysql -u root -p

# 选择数据库
use iatmsdb;

# 执行创建表脚本
source create_scheduled_task_tables.sql
```

### 步骤2：启动Spring Boot应用
```bash
cd iatms
mvn spring-boot:run
```

### 步骤3：创建定时任务
使用Postman或curl创建定时任务。

### 步骤4：测试API
运行 `test_scheduled_task_apis.bat` 脚本测试各个API。

## 前端集成示例

### Vue.js 示例
```javascript
// 创建定时任务
async function createScheduledTask(taskData) {
  const response = await fetch('/api/scheduled-tasks', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(taskData)
  });
  return response.json();
}

// 获取任务列表
async function getTaskList(page, pageSize) {
  const response = await fetch(`/api/scheduled-tasks?page=${page}&pageSize=${pageSize}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
}
```

### React 示例
```jsx
// 创建定时任务表单
function CreateTaskForm() {
  const [taskData, setTaskData] = useState({
    taskName: '',
    taskType: 'module',
    triggerType: 'daily',
    dailyHour: 9,
    dailyMinute: 0,
    executionEnvironment: 'test'
  });

  const handleSubmit = async () => {
    await fetch('/api/scheduled-tasks', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(taskData)
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* 表单字段 */}
    </form>
  );
}
```

## 定时任务管理界面设计建议

### 1. 任务列表页面
- 任务名称和描述
- 任务类型图标
- 触发方式显示
- 执行状态（启用/禁用）
- 下次执行时间
- 最后执行结果
- 操作按钮（编辑、删除、立即执行、启用/禁用）

### 2. 创建/编辑任务页面
- 基本信息：任务名称、描述
- 执行范围：任务类型（用例/模块/项目/API）、选择目标
- 触发方式：Cron表达式选择器或快捷选项
- 执行配置：环境、超时时间、并发数
- 通知配置：成功/失败通知、接收邮箱

### 3. 任务详情页面
- 任务基本信息
- 执行历史列表
- 统计图表（成功率、执行次数趋势）
- 日志查看

## 注意事项

1. **数据库依赖**：确保已执行 `create_scheduled_task_tables.sql` 创建所需表
2. **Quartz表**：Spring Boot会自动创建Quartz相关表
3. **权限控制**：需要相应的权限才能创建/编辑/删除任务
4. **Token认证**：所有API都需要有效的JWT Token

