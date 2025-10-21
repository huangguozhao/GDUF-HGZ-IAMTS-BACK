-- 修复测试用例数据结构
-- 将错误存储在pre_conditions中的请求参数移到request_override中

-- 1. 备份当前数据（可选）
-- CREATE TABLE TestCases_backup AS SELECT * FROM TestCases;

-- 2. 示例：修复登录测试用例
-- 假设当前数据错误地把请求参数放在了pre_conditions中

-- 错误的数据结构（需要修复）：
-- pre_conditions: {"username": "johndoe", "password": "Test@123456"}
-- request_override: null

-- 正确的数据结构（修复后）：
-- pre_conditions: {"description": "用户johndoe必须已注册且状态为激活"}
-- request_override: {"body": {"username": "johndoe", "password": "Test@123456"}}

-- 批量更新SQL（示例）
UPDATE TestCases 
SET 
    -- 将pre_conditions改为描述性质
    pre_conditions = JSON_OBJECT(
        'description', '测试用户必须已注册且状态为激活',
        'data_requirements', JSON_OBJECT(
            'user_exists', TRUE,
            'user_status', 'active'
        )
    ),
    
    -- 将实际的请求参数放到request_override中
    request_override = JSON_OBJECT(
        'body', JSON_OBJECT(
            'username', JSON_UNQUOTE(JSON_EXTRACT(pre_conditions, '$.username')),
            'password', JSON_UNQUOTE(JSON_EXTRACT(pre_conditions, '$.password'))
        )
    )

WHERE 
    -- 只更新那些错误地把请求参数放在pre_conditions中的用例
    JSON_CONTAINS_PATH(pre_conditions, 'one', '$.username', '$.password')
    AND (request_override IS NULL OR request_override = '{}');

-- 3. 示例：正确的测试用例数据

-- 登录成功用例
INSERT INTO TestCases (
    case_code, api_id, name, description, priority, severity,
    tags, pre_conditions, test_steps, request_override,
    expected_http_status, expected_response_schema, expected_response_body,
    assertions, extractors, validators,
    is_enabled, is_template, version, created_by
) VALUES (
    'TC_AUTH001_001',
    1,  -- 登录接口ID
    '正常登录测试',
    '使用正确的用户名和密码进行登录',
    'P0',
    'critical',
    
    -- tags
    '["认证", "登录", "正向用例", "冒烟测试"]',
    
    -- ✅ pre_conditions - 仅描述前提条件
    '{
        "description": "测试用户johndoe必须已注册且状态为激活",
        "data_requirements": {
            "username": "johndoe",
            "user_status": "active",
            "account_verified": true
        },
        "environment_requirements": {
            "auth_service_available": true,
            "database_accessible": true
        }
    }',
    
    -- test_steps
    '[
        {"step": 1, "action": "发送登录请求", "expected": "返回200状态码"},
        {"step": 2, "action": "验证token存在", "expected": "响应中包含token字段"},
        {"step": 3, "action": "验证用户信息", "expected": "返回正确的用户名"}
    ]',
    
    -- ✅ request_override - 实际的请求参数
    '{
        "body": {
            "username": "johndoe",
            "password": "Test@123456"
        },
        "headers": {
            "X-Client-Type": "web",
            "X-Request-ID": "test-${timestamp}"
        }
    }',
    
    -- expected_http_status
    200,
    
    -- expected_response_schema
    '{
        "type": "object",
        "required": ["code", "message", "data"],
        "properties": {
            "code": {"type": "number"},
            "message": {"type": "string"},
            "data": {
                "type": "object",
                "required": ["token", "userId", "username"],
                "properties": {
                    "token": {"type": "string"},
                    "userId": {"type": "number"},
                    "username": {"type": "string"}
                }
            }
        }
    }',
    
    -- expected_response_body
    '{
        "code": 200,
        "message": "登录成功",
        "data": {
            "token": "*",
            "userId": "*",
            "username": "johndoe"
        }
    }',
    
    -- assertions
    '[
        {"type": "status_code", "expected": 200},
        {"type": "json_path", "path": "$.code", "expected": 200},
        {"type": "json_path", "path": "$.message", "expected": "登录成功"},
        {"type": "json_path", "path": "$.data.token", "operator": "exists"},
        {"type": "json_path", "path": "$.data.username", "expected": "johndoe"}
    ]',
    
    -- extractors
    '[
        {"name": "auth_token", "type": "json_path", "path": "$.data.token"},
        {"name": "user_id", "type": "json_path", "path": "$.data.userId"}
    ]',
    
    -- validators
    '[
        {"type": "response_time", "max_ms": 1000},
        {"type": "schema", "enabled": true}
    ]',
    
    TRUE,  -- is_enabled
    FALSE,  -- is_template
    '1.0',  -- version
    1  -- created_by
);

-- 4. 验证修复结果
SELECT 
    case_id,
    case_code,
    name,
    JSON_PRETTY(pre_conditions) AS pre_conditions,
    JSON_PRETTY(request_override) AS request_override,
    JSON_PRETTY(expected_response_body) AS expected_response_body
FROM TestCases
WHERE case_code = 'TC_AUTH001_001';

-- 5. 清理说明
-- 如果需要清理旧数据，可以执行：
-- DELETE FROM TestCases WHERE is_deleted = TRUE AND deleted_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

