## 6.18. 分页获取最近编辑的项目

#### 6.18.1. 基本信息

**请求路径**: `/recent-projects`
**请求方式**: `GET`
**接口描述**: 分页查询当前用户最近编辑或访问的项目列表。**此接口需要认证。**

##### 6.18.2. 请求参数

**请求头 (Headers)**:

| 参数名          | 类型   | 是否必须 | 备注                             |
| :-------------- | :----- | :------- | :------------------------------- |
| `Authorization` | string | 必须     | 认证令牌，格式: `Bearer {token}` |

**查询参数 (Query String)**:

| 参数名称        | 是否必须 | 示例            | 备注                                                         |
| :-------------- | :------- | :-------------- | :----------------------------------------------------------- |
| `time_range`    | 否       | `7d`            | 时间范围。可选: `1d`, `7d`, `30d`，默认: `7d`                |
| `include_stats` | 否       | `true`          | 是否包含项目统计信息，默认: `false`                          |
| `sort_by`       | 否       | `last_accessed` | 排序字段。可选: `last_accessed`, `updated_at`, `created_at`，默认: `last_accessed` |
| `sort_order`    | 否       | `desc`          | 排序顺序。可选: `asc`, `desc`，默认: `desc`                  |
| `page`          | 否       | `1`             | 分页查询的页码，默认为 `1`                                   |
| `page_size`     | 否       | `10`            | 分页查询的每页记录数，默认为 `10`，最大 `20`                 |

**请求数据样例**:

```
GET /recent-projects
GET /recent-projects?time_range=30d&include_stats=true&page=1&page_size=15
GET /recent-projects?sort_by=updated_at&sort_order=desc
```

##### 6.18.3. 响应数据

**参数格式**: `application/json`

**通用参数说明**:

| 参数名 | 类型   | 是否必须 | 备注                    |
| :----- | :----- | :------- | :---------------------- |
| `code` | number | 必须     | 业务状态码。`1`代表成功 |
| `msg`  | string | 必须     | 操作的详细结果消息      |
| `data` | object | 必须     | 分页数据对象            |

**分页数据对象 (`data`) 结构**:

| 参数名       | 类型     | 说明                 |
| :----------- | :------- | :------------------- |
| `total`      | number   | 符合条件的数据总条数 |
| `items`      | object[] | 当前页的项目数据列表 |
| `page`       | number   | 当前页码             |
| `page_size`  | number   | 当前每页条数         |
| `time_range` | object   | 时间范围信息         |

**时间范围信息 (`time_range`) 结构**:

| 参数名       | 类型   | 说明     |
| :----------- | :----- | :------- |
| `start_time` | string | 开始时间 |
| `end_time`   | string | 结束时间 |
| `days`       | number | 天数     |

**项目对象 (`items[]`) 结构**:

| 参数名          | 类型   | 说明         |
| :-------------- | :----- | :----------- |
| `project_id`    | number | 项目ID       |
| `name`          | string | 项目名称     |
| `description`   | string | 项目描述     |
| `creator_info`  | object | 创建人信息   |
| `last_accessed` | string | 最后访问时间 |
| `access_count`  | number | 访问次数     |
| `module_count`  | number | 模块数量     |
| `api_count`     | number | 接口数量     |
| `case_count`    | number | 用例数量     |
| `last_activity` | object | 最后活动信息 |
| `created_at`    | string | 创建时间     |
| `updated_at`    | string | 更新时间     |

**最后活动信息 (`last_activity`) 结构**:

| 参数名        | 类型   | 说明         |
| :------------ | :----- | :----------- |
| `type`        | string | 活动类型     |
| `description` | string | 活动描述     |
| `timestamp`   | string | 活动时间     |
| `user_id`     | number | 操作用户ID   |
| `user_name`   | string | 操作用户姓名 |

**成功响应数据样例 (HTTP 200)**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 8,
    "items": [
      {
        "project_id": 1,
        "name": "电商平台项目",
        "description": "新一代电商平台开发项目",
        "creator_info": {
          "user_id": 123,
          "name": "张管理员",
          "avatar_url": "/avatars/zhang.jpg"
        },
        "last_accessed": "2024-09-16T14:30:00.000Z",
        "access_count": 15,
        "module_count": 8,
        "api_count": 156,
        "case_count": 420,
        "last_activity": {
          "type": "case_execution",
          "description": "执行了测试用例",
          "timestamp": "2024-09-16T14:25:00.000Z",
          "user_id": 456,
          "user_name": "李测试"
        },
        "created_at": "2024-01-15T10:30:00.000Z",
        "updated_at": "2024-09-10T16:45:00.000Z"
      },
      {
        "project_id": 2,
        "name": "后台管理系统",
        "description": "企业内部后台管理系统",
        "creator_info": {
          "user_id": 123,
          "name": "张管理员",
          "avatar_url": "/avatars/zhang.jpg"
        },
        "last_accessed": "2024-09-15T16:20:00.000Z",
        "access_count": 8,
        "module_count": 6,
        "api_count": 89,
        "case_count": 235,
        "last_activity": {
          "type": "api_creation",
          "description": "创建了新接口",
          "timestamp": "2024-09-15T15:30:00.000Z",
          "user_id": 789,
          "user_name": "王开发"
        },
        "created_at": "2024-02-20T14:15:00.000Z",
        "updated_at": "2024-09-12T09:30:00.000Z"
      },
      {
        "project_id": 3,
        "name": "移动端API服务",
        "description": "移动端应用后端API服务",
        "creator_info": {
          "user_id": 456,
          "name": "李技术",
          "avatar_url": "/avatars/li.jpg"
        },
        "last_accessed": "2024-09-14T11:15:00.000Z",
        "access_count": 5,
        "module_count": 5,
        "api_count": 67,
        "case_count": 180,
        "last_activity": {
          "type": "module_update",
          "description": "更新了模块信息",
          "timestamp": "2024-09-14T10:45:00.000Z",
          "user_id": 456,
          "user_name": "李技术"
        },
        "created_at": "2024-03-10T09:00:00.000Z",
        "updated_at": "2024-08-25T13:20:00.000Z"
      }
    ],
    "page": 1,
    "page_size": 10,
    "time_range": {
      "start_time": "2024-09-09T00:00:00.000Z",
      "end_time": "2024-09-16T23:59:59.000Z",
      "days": 7
    }
  }
}
```

**失败响应数据样例**:

```json
// 认证失败 (HTTP 401)
{
  "code": -1,
  "msg": "认证失败，请重新登录",
  "data": null
}

// 权限不足 (HTTP 403)
{
  "code": -2,
  "msg": "权限不足，无法查看最近编辑项目列表",
  "data": null
}

// 参数错误 (HTTP 400)
{
  "code": -3,
  "msg": "时间范围参数错误",
  "data": null
}
```

**可能的HTTP状态码**:

*   `HTTP 200`: 查询成功。
*   `HTTP 400`: 参数错误（如时间范围错误）。
*   `HTTP 401`: 未提供Token或Token无效/过期。
*   `HTTP 403`: 权限不足。
*   `HTTP 500`: 服务器内部错误。

**接口逻辑说明**:

1.  **认证与授权**: 验证 Token 并获取当前用户信息。
2.  **时间范围计算**: 根据 `time_range` 参数计算时间范围。
3.  **查询用户项目访问记录**:
    *   查询项目访问日志表（假设存在 `ProjectAccessLogs` 表）
    *   获取用户最近访问的项目ID列表
4.  **构建查询**: 
    *   查询 `Projects` 表，过滤用户有权限访问的项目
    *   关联查询项目统计信息（模块数、接口数、用例数）
    *   获取最后活动信息
5.  **排序处理**: 根据 `sort_by` 和 `sort_order` 参数进行排序。
6.  **分页处理**: 根据 `page` 和 `page_size` 计算分页偏移量。
7.  **返回结果**: 返回分页的项目列表和时间范围信息。

**注意**: 

*   该接口需要项目访问日志表的支持来记录用户访问行为
*   最后活动信息可能需要从操作日志表中查询
*   应该只返回用户有权限访问的项目
*   对于统计信息，可以使用缓存提高性能

**项目访问日志表示例**:

```sql
CREATE TABLE ProjectAccessLogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action_type VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    INDEX idx_project_user (project_id, user_id),
    INDEX idx_access_time (access_time),
    INDEX idx_user_access (user_id, access_time)
) COMMENT='项目访问日志表';
```

**推荐实现**:

```javascript
// 伪代码示例
async function getRecentProjects(currentUser, queryParams) {
  // 1. 计算时间范围
  const timeRange = calculateTimeRange(queryParams.time_range);
  
  // 2. 查询用户最近访问的项目ID
  const recentProjectIds = await ProjectAccessLogs.getRecentProjectIds(
    currentUser.id, 
    timeRange.startTime, 
    timeRange.endTime
  );
  
  // 3. 查询项目详细信息
  const projects = await Project.find({
    where: {
      project_id: { in: recentProjectIds },
      is_deleted: false
    },
    include: [
      { model: User, as: 'creator' },
      { model: ProjectStats, as: 'stats' }
    ],
    order: getSortOrder(queryParams.sort_by, queryParams.sort_order),
    limit: queryParams.page_size,
    offset: (queryParams.page - 1) * queryParams.page_size
  });
  
  // 4. 获取最后活动信息
  for (const project of projects) {
    project.last_activity = await ActivityLog.getLastActivity(project.project_id);
  }
  
  return {
    total: recentProjectIds.length,
    items: projects,
    page: queryParams.page,
    page_size: queryParams.page_size,
    time_range: timeRange
  };
}
```

**性能优化建议**:

- 为项目访问日志表建立合适的索引
- 使用缓存存储项目统计信息
- 限制返回的项目数量，避免性能问题
- 考虑实现异步数据加载，先返回基本数据再加载统计信息







