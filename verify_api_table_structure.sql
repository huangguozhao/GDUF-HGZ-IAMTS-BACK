-- 验证接口表结构是否包含所有必要字段
DESCRIBE Apis;

-- 检查是否有示例数据
SELECT 
    api_id,
    api_code,
    name,
    method,
    path,
    base_url,
    full_url,
    request_parameters,
    path_parameters,
    request_headers,
    request_body,
    auth_config,
    examples,
    tags
FROM Apis 
WHERE module_id = 1 
LIMIT 3;

-- 检查字段是否存在
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Apis' 
AND TABLE_SCHEMA = DATABASE()
AND COLUMN_NAME IN (
    'base_url',
    'request_parameters', 
    'path_parameters',
    'request_headers',
    'request_body',
    'auth_config',
    'examples'
)
ORDER BY ORDINAL_POSITION;
