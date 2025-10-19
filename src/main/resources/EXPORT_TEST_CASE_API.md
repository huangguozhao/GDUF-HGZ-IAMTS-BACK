# 测试用例导出接口文档

## 接口信息

**请求路径**: `/apis/{api_id}/test-cases/export`  
**请求方式**: `GET`  
**接口描述**: 导出指定接口下的测试用例为Excel或CSV文件

## 请求参数

### 请求头
- `Authorization`: Bearer {token} (必须)

### 路径参数
- `api_id`: 接口ID (必须)

### 查询参数
- `format`: 导出格式，可选值: `excel`, `csv`，默认: `excel`
- `include_disabled`: 是否包含已禁用的用例，默认: `false`
- `include_templates`: 是否包含模板用例，默认: `false`
- `fields`: 指定导出的字段，多个字段用逗号分隔
- `filename`: 导出文件的名称（不包含扩展名）

## 支持的导出格式

### Excel格式 (.xlsx)
- 包含多个工作表：
  - **测试用例**: 主要用例数据
  - **数据字典**: 字段说明和枚举值
  - **导入模板**: 空的导入模板

### CSV格式 (.csv)
- UTF-8编码，包含BOM
- 包含表头行
- 支持逗号分隔和引号转义

## 可导出的字段

### 默认字段
- `case_code`: 用例编码
- `name`: 用例名称
- `description`: 用例描述
- `priority`: 优先级
- `severity`: 严重程度
- `tags`: 标签
- `expected_http_status`: 预期HTTP状态码
- `is_enabled`: 是否启用
- `created_at`: 创建时间

### 完整字段列表
- `case_code`: 用例编码
- `name`: 用例名称
- `description`: 用例描述
- `priority`: 优先级 (P0, P1, P2, P3)
- `severity`: 严重程度 (critical, high, medium, low)
- `tags`: 标签 (逗号分隔)
- `pre_conditions`: 前置条件
- `test_steps`: 测试步骤
- `request_override`: 请求参数覆盖
- `expected_http_status`: 预期HTTP状态码
- `expected_response_schema`: 预期响应Schema
- `expected_response_body`: 预期响应体
- `assertions`: 断言规则
- `extractors`: 响应提取规则
- `validators`: 验证器配置
- `is_enabled`: 是否启用
- `is_template`: 是否为模板
- `version`: 版本号
- `created_by`: 创建人ID
- `creator_name`: 创建人姓名
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 响应数据

### 成功响应
直接返回文件流，响应头包含：
- `Content-Type`: 根据格式返回相应的MIME类型
- `Content-Disposition`: 附件下载头，包含文件名
- `Content-Length`: 文件大小

### 错误响应

#### 接口不存在 (HTTP 404)
```json
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}
```

#### 无用例数据 (HTTP 400)
```json
{
  "code": 0,
  "msg": "该接口下没有可导出的测试用例",
  "data": null
}
```

#### 不支持的格式 (HTTP 400)
```json
{
  "code": -3,
  "msg": "不支持的导出格式",
  "data": null
}
```

#### 权限不足 (HTTP 403)
```json
{
  "code": -2,
  "msg": "权限不足，无法导出测试用例",
  "data": null
}
```

#### 系统错误 (HTTP 500)
```json
{
  "code": -5,
  "msg": "文件导出失败",
  "data": null
}
```

## 使用示例

### 基本导出
```bash
GET /apis/101/test-cases/export
Authorization: Bearer {token}
```

### 导出为CSV格式
```bash
GET /apis/101/test-cases/export?format=csv
Authorization: Bearer {token}
```

### 包含已禁用和模板用例
```bash
GET /apis/101/test-cases/export?include_disabled=true&include_templates=true
Authorization: Bearer {token}
```

### 指定导出字段
```bash
GET /apis/101/test-cases/export?fields=case_code,name,priority,severity,tags
Authorization: Bearer {token}
```

### 自定义文件名
```bash
GET /apis/101/test-cases/export?filename=login_api_testcases
Authorization: Bearer {token}
```

## 导出文件示例

### Excel文件内容示例
| 用例编码 | 用例名称 | 用例描述 | 优先级 | 严重程度 | 标签 | 预期HTTP状态码 | 是否启用 | 创建时间 |
|---------|---------|---------|--------|---------|------|---------------|---------|---------|
| TC-API-101-001 | 用户登录-成功场景 | 测试成功登录场景 | P0 | high | 冒烟测试,登录功能 | 200 | true | 2024-06-15 10:30:00 |
| TC-API-101-002 | 用户登录-密码错误 | 测试密码错误场景 | P1 | medium | 登录功能,异常测试 | 401 | true | 2024-06-16 09:15:00 |

### CSV文件内容示例
```csv
"用例编码","用例名称","用例描述","优先级","严重程度","标签","预期HTTP状态码","是否启用","创建时间"
"TC-API-101-001","用户登录-成功场景","测试成功登录场景","P0","high","冒烟测试,登录功能","200","true","2024-06-15 10:30:00"
"TC-API-101-002","用户登录-密码错误","测试密码错误场景","P1","medium","登录功能,异常测试","401","true","2024-06-16 09:15:00"
```

## 注意事项

1. **权限要求**: 需要 `testcase:view` 权限
2. **文件格式**: 支持Excel (.xlsx) 和CSV (.csv) 格式
3. **数据过滤**: 默认只导出启用的非模板用例
4. **字段选择**: 可以指定要导出的字段，无效字段会被忽略
5. **文件名**: 自动添加时间戳，避免文件名冲突
6. **编码**: CSV文件使用UTF-8编码，包含BOM
7. **模板功能**: Excel文件包含数据字典和导入模板工作表
8. **大数据量**: 对于大量数据，建议分批导出或实现异步导出

## 技术实现

- **Excel生成**: 使用Apache POI库
- **CSV生成**: 自定义实现，支持UTF-8编码和引号转义
- **文件下载**: 直接写入HttpServletResponse输出流
- **错误处理**: 统一的错误响应格式
- **权限控制**: 使用@GlobalInterceptor注解进行权限验证
