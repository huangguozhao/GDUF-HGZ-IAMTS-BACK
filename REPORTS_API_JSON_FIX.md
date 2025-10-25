# Reports API JSON格式错误修复

## 问题描述

请求 `/api/reports` 接口时，返回的响应包含两个JSON对象拼接在一起：

```json
{...正常数据...}{"code":-5,"msg":"系统异常，请稍后重试"}
```

## 问题分析

### 1. 现象
- 使用curl和前端都出现相同问题
- 后端日志显示正常响应
- JSON格式不合法，包含双重响应

### 2. 根本原因

**MySQL的`JSON_OBJECT`函数在处理统计数据时返回了不完整的JSON**

原SQL（有问题）：
```xml
<select id="selectReportSummary" ...>
    SELECT 
        COUNT(*) AS total_reports,
        JSON_OBJECT(
            'execution', SUM(CASE WHEN report_type = 'execution' THEN 1 ELSE 0 END),
            'coverage', SUM(CASE WHEN report_type = 'coverage' THEN 1 ELSE 0 END),
            'trend', SUM(CASE WHEN report_type = 'trend' THEN 1 ELSE 0 END),
            ...
        ) AS by_type,
        ...
</select>
```

导致返回：`"byType":{"trend"}` 而不是 `"byType":{"trend":0}`

### 3. 触发流程
1. Mapper查询返回不完整的JSON字符串
2. `JsonTypeHandler`尝试解析失败
3. Jackson序列化ResponseVO时抛出异常
4. 全局异常处理器捕获异常，追加`{"code":-5,"msg":"系统异常，请稍后重试"}`
5. 最终形成双重JSON响应

## 解决方案

### 修改内容

1. **简化Mapper SQL** - 移除`JSON_OBJECT`，只查询基础统计
2. **添加分组统计查询** - 使用简单的`GROUP BY`
3. **在Service层构建Map** - Java代码更可靠

### 文件修改

#### 1. `ReportMapper.xml`
```xml
<!-- 修改前：使用JSON_OBJECT -->
<select id="selectReportSummary" ... resultMap="ReportSummaryResultMap">
    SELECT ..., JSON_OBJECT(...) AS by_type, ...
</select>

<!-- 修改后：简单查询 -->
<select id="selectReportSummary" ... resultType="ReportSummaryDTO">
    SELECT 
        COUNT(*) AS totalReports,
        AVG(success_rate) AS avgSuccessRate
    FROM TestReportSummaries trs
    ...
</select>

<!-- 新增：按类型统计 -->
<select id="countReportsByType" ... resultType="map">
    SELECT 
        report_type AS `key`,
        COUNT(*) AS `value`
    FROM TestReportSummaries trs
    ...
    GROUP BY report_type
</select>

<!-- 新增：按状态统计 -->
<select id="countReportsByStatus" ... resultType="map">
    ...
</select>

<!-- 新增：按环境统计 -->
<select id="countReportsByEnvironment" ... resultType="map">
    ...
</select>
```

#### 2. `ReportMapper.java`
```java
// 新增方法
Map<String, Long> countReportsByType(ReportListQueryDTO queryDTO);
Map<String, Long> countReportsByStatus(ReportListQueryDTO queryDTO);
Map<String, Long> countReportsByEnvironment(ReportListQueryDTO queryDTO);
```

#### 3. `ReportServiceImpl.java`
```java
// 在Service层构建统计Map
ReportSummaryDTO summary = reportMapper.selectReportSummary(queryDTO);

if (summary != null) {
    summary.setByType(reportMapper.countReportsByType(queryDTO));
    summary.setByStatus(reportMapper.countReportsByStatus(queryDTO));
    summary.setByEnvironment(reportMapper.countReportsByEnvironment(queryDTO));
}
```

## 修复效果

### 修复前
```json
{...data...}{"code":-5,"msg":"系统异常，请稍后重试"}
```

### 修复后
```json
{
  "code": 1,
  "msg": "查询报告列表成功",
  "data": {
    "total": 173,
    "items": [...],
    "summary": {
      "totalReports": 173,
      "byType": {
        "execution": 173,
        "coverage": 0,
        "trend": 0
      },
      "byStatus": {
        "completed": 61,
        "generating": 112
      },
      "byEnvironment": {
        "test": 21,
        "staging": 2,
        "production": 0
      },
      "avgSuccessRate": 58.16
    }
  }
}
```

## 重要经验

1. **避免在SQL中使用复杂的JSON函数** - 容易出现兼容性和格式问题
2. **在应用层构建复杂数据结构** - Java代码更可控、更易调试
3. **双重JSON响应通常是序列化异常导致的** - 检查数据格式和类型处理器
4. **使用curl直接测试API** - 排除前端干扰

## 测试步骤

1. 清理编译：`mvn clean compile -DskipTests`
2. 重启应用
3. 测试API：
```bash
curl -X GET "http://localhost:8080/api/reports?page=1&page_size=5" \
  -H "Authorization: Bearer test-token-123"
```
4. 验证返回的JSON格式正确

## 日期
2025-10-25

