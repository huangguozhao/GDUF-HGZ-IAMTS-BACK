# 项目启动指南

## 问题解决

### Quartz启动错误解决方案

如果遇到以下错误：
```
Table 'iatmsdb.qrtz_locks' doesn't exist
```

**解决方案1：暂时禁用Quartz（推荐）**
- 当前配置已经禁用了Quartz，可以直接启动项目
- 如果后续需要定时任务功能，请使用解决方案2

**解决方案2：启用Quartz并创建数据库表**
1. 执行 `src/main/resources/sql/quartz_tables.sql` 脚本创建Quartz所需的数据库表
2. 在 `application.yml` 中取消注释Quartz配置部分
3. 重新启动项目

## 启动步骤

### 1. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE iatmsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户表
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，自增主键',
    name VARCHAR(100) NOT NULL COMMENT '用户姓名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '用户邮箱，唯一',
    avatar_url VARCHAR(255) COMMENT '用户头像URL',
    phone VARCHAR(20) COMMENT '用户手机号码，允许为空',
    password VARCHAR(255) NOT NULL COMMENT '用户密码，加密存储',
    department_id INT COMMENT '部门ID，关联部门表',
    employee_id VARCHAR(50) COMMENT '员工工号',
    creator_id INT COMMENT '创建人ID，关联用户表',
    last_login_time TIMESTAMP COMMENT '最后登录时间',
    position VARCHAR(100) COMMENT '职位',
    description TEXT COMMENT '备注/描述',
    status ENUM('active', 'inactive', 'pending') NOT NULL DEFAULT 'pending' COMMENT '用户状态：激活、非激活、待审核',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted_by INT NULL COMMENT '删除人ID'
) COMMENT='用户信息表';
```

### 2. 初始化测试数据
执行 `src/main/resources/sql/init_user_data.sql` 脚本

### 3. 启动项目
运行 `com.victor.iatms.IatmsApplication` 主类

### 4. 测试登录接口
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "123456"
  }'
```

## 测试用户

| 邮箱 | 密码 | 状态 | 说明 |
|------|------|------|------|
| admin@example.com | 123456 | active | 系统管理员 |
| test@example.com | 123456 | active | 普通用户 |
| pending@example.com | 123456 | pending | 待审核用户 |
| disabled@example.com | 123456 | inactive | 禁用用户 |

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库连接配置是否正确
- 确认数据库用户权限

### 2. Redis连接失败
- 检查Redis服务是否启动
- 确认Redis连接配置是否正确

### 3. JWT配置问题
- 确认JWT密钥配置正确
- 检查JWT过期时间设置

## 技术栈

- Java 17
- Spring Boot 3.5.5
- MyBatis 3.0.3
- MySQL 8.0
- Redis
- JWT (jjwt 0.12.3)
- Lombok
