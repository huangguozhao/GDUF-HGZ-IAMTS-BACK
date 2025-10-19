# 测试用例导入接口文档

## 接口信息

**请求路径**: `/apis/{api_id}/test-cases/import`  
**请求方式**: `POST`  
**接口描述**: 通过Excel或CSV文件批量导入测试用例到指定接口

## 请求参数

### 请求头
- `Authorization`: Bearer {token} (必须)
- `Content-Type`: multipart/form-data

### 路径参数
- `api_id`: 接口ID (必须)

### 查询参数
- `import_mode`: 导入模式，可选值: `insert`(仅新增), `upsert`(新增或更新)，默认: `insert`
- `conflict_strategy`: 冲突处理策略，可选值: `skip`(跳过), `overwrite`(覆盖), `rename`(重命名)，默认: `skip`
- `template_type`: 模板类型，可选值: `simple`, `standard`, `advanced`，默认: `standard`

### 请求体
- `file`: Excel或CSV文件 (必须)

## 支持的文件格式

- Excel文件: `.xlsx`, `.xls`
- CSV文件: `.csv`
- 最大文件大小: 10MB

## Excel/CSV文件格式

### 必需列
- `case_code`: 用例编码
- `name`: 用例名称

### 可选列
- `description`: 用例描述
- `priority`: 优先级 (P0, P1, P2, P3)
- `severity`: 严重程度 (critical, high, medium, low)
- `tags`: 标签 (多个标签用逗号分隔，如: "冒烟测试,登录功能")
- `pre_conditions`: 前置条件
- `test_steps`: 测试步骤
- `request_override`: 请求参数覆盖
- `expected_http_status`: 预期HTTP状态码
- `expected_response_body`: 预期响应体
- `assertions`: 断言规则
- `extractors`: 提取规则
- `validators`: 验证器配置
- `is_enabled`: 是否启用 (true/false)
- `is_template`: 是否为模板用例 (true/false)
- `version`: 版本号

### 示例数据

```csv
case_code,name,description,priority,severity,tags,expected_http_status
TC-API-101-001,用户登录-成功场景,测试成功登录场景,P0,high,"冒烟测试,登录功能",200
TC-API-101-002,用户登录-密码错误,测试密码错误场景,P1,medium,"登录功能,异常测试",401
TC-API-101-003,用户登录-用户不存在,测试用户不存在场景,P1,medium,"登录功能,异常测试",404
```

## 响应数据

### 成功响应 (HTTP 200)

```json
{
  "code": 1,
  "msg": "用例导入完成",
  "data": {
    "total_count": 15,
    "success_count": 12,
    "failure_count": 2,
    "skip_count": 1,
    "import_id": "imp_20240916103000",
    "imported_cases": [
      {
        "case_id": 1001,
        "case_code": "TC-API-101-001",
        "name": "用户登录-成功场景"
      }
    ],
    "failed_records": [
      {
        "row_number": 5,
        "case_code": "TC-API-101-005",
        "name": "用户登录-异常场景",
        "error": "用例编码已存在"
      }
    ]
  }
}
```

### 错误响应

#### 文件格式不支持 (HTTP 400)
```json
{
  "code": -3,
  "msg": "不支持的文件格式，请上传Excel或CSV文件",
  "data": null
}
```

#### 文件过大 (HTTP 400)
```json
{
  "code": -3,
  "msg": "文件大小超过限制(10MB)",
  "data": null
}
```

#### 接口不存在 (HTTP 404)
```json
{
  "code": -4,
  "msg": "接口不存在",
  "data": null
}
```

#### 权限不足 (HTTP 403)
```json
{
  "code": -2,
  "msg": "权限不足，无法导入测试用例",
  "data": null
}
```

## 使用说明

1. **权限要求**: 需要 `testcase:create` 权限
2. **文件格式**: 支持Excel (.xlsx, .xls) 和CSV (.csv) 格式
3. **数据验证**: 系统会自动验证用例编码和名称的必填性，以及优先级和严重程度的有效性
4. **冲突处理**: 当用例编码已存在时，根据 `conflict_strategy` 参数决定处理方式
5. **事务处理**: 导入过程使用事务，确保数据一致性
6. **错误报告**: 详细的错误信息会包含在响应中，包括失败的行号和具体错误原因

## 注意事项

- 用例编码在同一接口下必须唯一
- 优先级必须是 P0, P1, P2, P3 中的一个
- 严重程度必须是 critical, high, medium, low 中的一个
- 标签支持多个，用逗号分隔
- 布尔值字段支持 true/false 或 1/0
- 建议先下载标准模板文件，确保导入格式正确
