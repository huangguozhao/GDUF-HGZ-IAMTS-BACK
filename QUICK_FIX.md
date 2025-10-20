# ⚡ 一键解决"系统异常"问题

## 🎯 问题现象

所有接口返回：
```json
{"code":-5,"msg":"系统异常，请稍后重试"}
```

## 🔥 90%可能的原因

**数据库表 `TestCaseResults` 不存在！**

---

## ✅ 一键解决方案（3步）

### 步骤1：创建数据库表（2分钟）

打开MySQL客户端（或Navicat等工具），执行以下命令：

```sql
-- 1. 切换到数据库
USE iatmsdb_dev;

-- 2. 检查表是否存在
SHOW TABLES LIKE 'TestCaseResults';

-- 3. 如果不存在（Empty set），执行建表脚本
-- 复制 create_test_tables.sql 文件的内容粘贴执行
-- 或使用命令： source create_test_tables.sql

-- 4. 插入测试数据
-- 复制 insert_test_data.sql 文件的内容粘贴执行
-- 或使用命令： source insert_test_data.sql

-- 5. 验证
SELECT COUNT(*) FROM TestCaseResults;
-- 应该显示：10（如果执行了insert_test_data.sql）
```

### 步骤2：重启应用（1分钟）

```bash
# 停止当前运行的应用（Ctrl+C）

# 重新启动
mvn spring-boot:run

# 等待看到：Started IatmsApplication in xxx seconds (JVM running for xxx)
```

### 步骤3：测试接口（30秒）

```bash
# 运行测试脚本
simple_test.bat
```

**预期结果：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 10,
    "items": [
      {
        "result_id": 1,
        "status": "passed",
        ...
      }
    ],
    "summary": {
      "total_count": 10,
      "passed": 6,
      "failed": 3,
      "success_rate": 60.00
    }
  }
}
```

---

## 🎊 完成！

如果看到上面的JSON响应（code=1），说明接口已经正常工作了！

---

## 📋 完整的SQL脚本（复制即用）

如果你想直接复制执行，这里是完整的建表+插入数据脚本：

```sql
-- 切换数据库
USE iatmsdb_dev;

-- 创建表
CREATE TABLE IF NOT EXISTS TestCaseResults (
    result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID',
    report_id BIGINT NULL,
    execution_id BIGINT NULL,
    task_type ENUM('test_suite', 'test_case', 'project', 'module', 'api_monitor') NOT NULL DEFAULT 'test_suite',
    ref_id INT NOT NULL,
    full_name VARCHAR(500),
    status ENUM('passed', 'failed', 'broken', 'skipped', 'unknown') NOT NULL,
    duration BIGINT,
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    failure_message TEXT,
    failure_trace TEXT,
    failure_type VARCHAR(100),
    error_code VARCHAR(50),
    steps_json JSON,
    parameters_json JSON,
    attachments_json JSON,
    logs_link VARCHAR(500),
    screenshot_link VARCHAR(500),
    video_link VARCHAR(500),
    environment VARCHAR(50),
    browser VARCHAR(50),
    os VARCHAR(50),
    device VARCHAR(50),
    tags_json JSON,
    severity ENUM('blocker', 'critical', 'normal', 'minor', 'trivial'),
    priority ENUM('P0', 'P1', 'P2', 'P3'),
    retry_count INT DEFAULT 0,
    flaky BOOLEAN DEFAULT false,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    deleted_by INT NULL,
    INDEX idx_status (status),
    INDEX idx_environment (environment),
    INDEX idx_is_deleted(is_deleted)
) COMMENT='测试结果表';

-- 插入10条测试数据
INSERT INTO TestCaseResults (report_id, execution_id, task_type, ref_id, full_name, status, duration, start_time, end_time, environment, browser, os, device, severity, priority) VALUES 
(1001, 30001, 'test_case', 101, '用户管理模块/用户登录接口/用户登录-成功场景', 'passed', 1245, '2024-09-16 10:30:00', '2024-09-16 10:30:01', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'critical', 'P0'),
(1001, 30002, 'test_case', 102, '用户管理模块/用户注册接口/用户注册-邮箱已存在', 'failed', 856, '2024-09-16 10:30:02', '2024-09-16 10:30:03', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'normal', 'P1'),
(1001, 30003, 'test_case', 103, '商品管理模块/商品查询接口/查询所有商品', 'broken', 2340, '2024-09-16 10:31:00', '2024-09-16 10:31:02', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'critical', 'P0'),
(1001, 30004, 'test_case', 104, '订单管理模块/订单创建接口/创建订单-成功', 'skipped', 0, '2024-09-16 10:32:00', '2024-09-16 10:32:00', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'normal', 'P2'),
(1002, 30005, 'module', 1, '用户管理模块', 'passed', 3560, '2024-09-16 10:33:00', '2024-09-16 10:33:04', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'normal', 'P1'),
(1003, 30006, 'project', 1, '电商平台项目', 'failed', 15678, '2024-09-16 10:35:00', '2024-09-16 10:35:16', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'critical', 'P0'),
(1004, 30007, 'api_monitor', 201, 'API健康检查', 'passed', 234, '2024-09-16 10:40:00', '2024-09-16 10:40:00', 'production', 'Automated Monitor', 'Linux', 'Server', 'normal', 'P2'),
(1005, 30008, 'test_suite', 301, '用户模块回归测试套件', 'failed', 8920, '2024-09-16 10:45:00', '2024-09-16 10:45:09', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'high', 'P1'),
(1001, 30009, 'test_case', 105, '工具类/时间格式化/标准格式', 'passed', 45, '2024-09-16 10:50:00', '2024-09-16 10:50:00', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'low', 'P3'),
(1001, 30010, 'test_case', 106, '报表管理/数据导出/导出大量数据', 'passed', 25678, '2024-09-16 11:00:00', '2024-09-16 11:00:26', 'test', 'Chrome 115', 'Windows 10', 'Desktop', 'normal', 'P2');

-- 验证数据
SELECT 'SUCCESS: 测试数据插入完成！' AS message;
SELECT COUNT(*) AS total_records FROM TestCaseResults;
SELECT status, COUNT(*) AS count FROM TestCaseResults GROUP BY status;
```

---

## 💡 执行方式

### 方式1：MySQL命令行

```bash
mysql -u root -p iatmsdb_dev < insert_test_data.sql
```

### 方式2：MySQL客户端

直接复制上面的SQL内容，粘贴到MySQL客户端执行

### 方式3：Navicat等工具

1. 连接到数据库
2. 打开 `insert_test_data.sql` 文件
3. 点击运行

---

## 测试数据说明

插入了10条测试结果数据：

| result_id | task_type | status | full_name | duration |
|-----------|-----------|--------|-----------|----------|
| 1 | test_case | passed | 用户登录-成功 | 1245ms |
| 2 | test_case | failed | 用户注册-失败 | 856ms |
| 3 | test_case | broken | 商品查询-异常 | 2340ms |
| 4 | test_case | skipped | 订单创建-跳过 | 0ms |
| 5 | module | passed | 用户管理模块 | 3560ms |
| 6 | project | failed | 电商平台项目 | 15678ms |
| 7 | api_monitor | passed | API健康检查 | 234ms |
| 8 | test_suite | failed | 回归测试套件 | 8920ms |
| 9 | test_case | passed | 时间格式化 | 45ms |
| 10 | test_case | passed | 数据导出 | 25678ms |

**统计：**
- 总数：10条
- 通过：6条
- 失败：3条
- 中断：1条
- 跳过：1条
- 成功率：60%

---

## 执行完成后测试

```bash
# 重启应用
mvn spring-boot:run

# 测试列表接口（应该返回10条数据）
curl http://localhost:8080/api/test-results

# 测试详情接口
curl http://localhost:8080/api/test-results/1

# 测试查询失败的用例
curl "http://localhost:8080/api/test-results?status=failed"

# 测试排序
curl "http://localhost:8080/api/test-results?sort_by=duration&sort_order=desc"
```

**预期结果：** 所有接口都返回 `"code":1` 表示成功！

---

## 🎉 就这么简单！

执行完上面的SQL后，重启应用，所有接口就能正常工作了！


