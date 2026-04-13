# 🔧 故障排查指南

## ❌ 当前问题

所有接口返回：`{"code":-5,"msg":"系统异常，请稍后重试"}`

这说明后端代码抛出了异常。

---

## 🔍 立即排查步骤

### 步骤1：重启应用并查看完整日志

1. **停止应用** (Ctrl+C)
2. **重新启动**
```bash
mvn spring-boot:run
```

3. **观察启动日志**，查找以下关键信息：
   - `Started IatmsApplication in xxx seconds` ← 启动成功标志
   - 数据库连接信息
   - 任何 ERROR 或 Exception 信息

4. **重要：把启动过程中的所有ERROR日志发给我！**

---

### 步骤2：测试健康检查接口

我已经创建了一个健康检查接口，先测试基础功能是否正常：

#### 测试命令：
```bash
# 运行健康检查脚本
test_health.bat
```

或手动测试：
```bash
# Test 1: 最简单的接口
curl http://localhost:8080/api/health

# Test 2: 详细健康检查（包括数据库连接）
curl http://localhost:8080/api/health/detail

# Test 3: JSON序列化测试
curl http://localhost:8080/api/test/json
```

#### 预期结果：
```json
// Test 1
{"code":1,"msg":"OK","data":"System is running"}

// Test 2
{"code":1,"msg":"Health check completed","data":{"application":"running","database":"connected",...}}

// Test 3
{"code":1,"msg":"success","data":{"message":"JSON serialization works",...}}
```

如果这3个测试都成功，说明基础功能正常，问题在TestExecutionController的实现。

---

### 步骤3：检查数据库表

#### 方法1：运行SQL检查脚本
```bash
# 连接到MySQL
mysql -u root -p

# 运行检查脚本
source check_database.sql
```

#### 方法2：手动检查
```sql
USE iatmsdb_dev;

-- 检查表是否存在
SHOW TABLES LIKE 'TestCaseResults';

-- 如果不存在，运行创建脚本
source create_test_tables.sql
```

---

## 🐛 常见错误及解决方案

### 错误1：NullPointerException

**现象：** 控制台显示空指针异常

**可能原因：**
- Mapper 注入失败
- Service 依赖注入失败
- 数据库连接失败

**解决方案：**
```java
// 检查 TestExecutionServiceImpl 中的依赖是否都正确注入
@Autowired
private TestExecutionMapper testExecutionMapper;  // 可能为null

@Autowired
private ObjectMapper objectMapper;  // 可能为null
```

---

### 错误2：Table doesn't exist

**现象：** 控制台显示 `Table 'TestCaseResults' doesn't exist`

**解决方案：**
```bash
# 运行建表脚本
mysql -u root -p < create_test_tables.sql
```

---

### 错误3：Jackson序列化失败

**现象：** 控制台显示 JSON 序列化错误

**可能原因：**
- DTO类缺少getter/setter
- 循环引用

**临时解决方案：**
我已经临时禁用了 WebLogAspect，重启应用后再测试。

---

### 错误4：SQL语法错误

**现象：** 控制台显示 SQL 语法错误

**可能原因：**
- MyBatis XML 中的 SQL 语法问题
- 表字段名不匹配

**解决方案：**
检查 `src/main/resources/mapper/TestExecutionMapper.xml` 中新增的SQL语句。

---

## 📋 快速诊断清单

按顺序检查：

- [ ] **应用是否成功启动？**
  - 查看控制台是否有 `Started IatmsApplication`
  - 没有ERROR日志

- [ ] **健康检查接口是否正常？**
  ```bash
  curl http://localhost:8080/api/health
  # 应该返回：{"code":1,"msg":"OK","data":"System is running"}
  ```

- [ ] **数据库是否连接成功？**
  ```bash
  curl http://localhost:8080/api/health/detail
  # 检查 database 字段是否为 "connected"
  ```

- [ ] **TestCaseResults表是否存在？**
  ```sql
  SHOW TABLES LIKE 'TestCaseResults';
  ```

- [ ] **拦截器是否已禁用？**
  ```java
  // GlobalOperationAspect.java
  boolean ENABLE_INTERCEPTOR = false;  // 应该是 false
  ```

- [ ] **WebLogAspect是否已禁用？**
  ```java
  // WebLogAspect.java
  // @Component  // 应该被注释掉
  ```

---

## 🎯 推荐的排查顺序

### 1️⃣ 先测试健康检查
```bash
test_health.bat
```

如果失败 → 应用启动有问题，查看启动日志

如果成功 → 进入步骤2

### 2️⃣ 查看控制台完整错误
```
请求一个接口后，在控制台查找：
- ERROR
- Exception
- 堆栈跟踪
```

把完整的错误信息发给我！

### 3️⃣ 检查数据库
```sql
-- 运行检查脚本
source check_database.sql

-- 如果表不存在，创建表
source create_test_tables.sql
```

### 4️⃣ 重启应用再测试
```bash
# 停止应用
Ctrl+C

# 重新启动
mvn spring-boot:run

# 测试
simple_test.bat
```

---

## 💡 如果还是失败

请提供以下信息：

1. **控制台完整错误日志**（从发起请求到异常的所有日志）
2. **健康检查测试结果**
```bash
test_health.bat
```

3. **数据库检查结果**
```sql
SHOW TABLES;
```

4. **application-dev.yml配置**（数据库连接部分）

---

## 🚀 最快的解决方案

运行以下命令，然后把输出发给我：

```bash
# 1. 健康检查
test_health.bat > health_check_result.txt

# 2. 简单测试
simple_test.bat > simple_test_result.txt

# 3. 查看启动日志
# 复制控制台中的 ERROR 和 Exception 信息
```

然后告诉我这3个测试的结果，我就能快速定位问题！

---

## 📞 常见问题快速索引

| 问题 | 检查命令 | 解决方案 |
|------|----------|----------|
| 应用未启动 | `jps \| grep Iatms` | 运行 `mvn spring-boot:run` |
| 数据库未连接 | `curl /api/health/detail` | 检查 application-dev.yml |
| 表不存在 | `SHOW TABLES` | 运行 create_test_tables.sql |
| 端口被占用 | `netstat -ano \| findstr 8080` | 修改端口或关闭占用程序 |

---

## ⚡ 一键自动诊断（推荐）

```bash
# 运行完整的诊断流程
test_health.bat
```

根据输出结果判断：
- ✅ 如果都成功 → 问题在业务代码
- ❌ 如果失败 → 问题在基础设施（数据库、配置等）

