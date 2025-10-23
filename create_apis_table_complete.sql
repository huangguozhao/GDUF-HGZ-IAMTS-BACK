-- 创建完整的接口表（Apis）
-- 如果表已存在，先删除
DROP TABLE IF EXISTS Apis;

-- 创建接口表
CREATE TABLE Apis (
    api_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '接口ID，自增主键',
    api_code VARCHAR(50) NOT NULL COMMENT '接口编码，模块内唯一',
    module_id INT NOT NULL COMMENT '模块ID，关联Modules表的主键',
    name VARCHAR(255) NOT NULL COMMENT '接口名称',
    method ENUM('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS') NOT NULL COMMENT '请求方法',
    path VARCHAR(500) NOT NULL COMMENT '接口路径',
    base_url VARCHAR(255) COMMENT '基础URL',
    full_url VARCHAR(500) GENERATED ALWAYS AS (CONCAT(COALESCE(base_url, ''), path)) STORED COMMENT '完整URL',
    request_parameters JSON COMMENT '查询参数，JSON格式',
    path_parameters JSON COMMENT '路径参数，JSON格式',
    request_headers JSON COMMENT '请求头信息，JSON格式',
    request_body TEXT COMMENT '请求体内容',
    request_body_type ENUM('json', 'form-data', 'x-www-form-urlencoded', 'xml', 'raw') COMMENT '请求体类型',
    response_body_type ENUM('json', 'xml', 'html', 'text') COMMENT '响应体类型',
    description TEXT COMMENT '接口描述',
    status ENUM('active', 'inactive', 'deprecated') DEFAULT 'active' COMMENT '接口状态',
    version VARCHAR(20) DEFAULT '1.0' COMMENT '版本号',
    timeout_seconds INT DEFAULT 30 COMMENT '超时时间(秒)',
    auth_type ENUM('none', 'basic', 'bearer', 'api_key', 'oauth2') DEFAULT 'none' COMMENT '认证类型',
    auth_config JSON COMMENT '认证配置',
    tags JSON COMMENT '标签，JSON数组格式',
    examples JSON COMMENT '请求示例',
    created_by INT NOT NULL COMMENT '创建人ID，关联用户表',
    updated_by INT NULL COMMENT '更新人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted_by INT NULL COMMENT '删除人ID',
    
    -- 索引
    INDEX idx_module_id (module_id),
    INDEX idx_method_path (method, path),
    INDEX idx_api_code (api_code),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_is_deleted(is_deleted),
    UNIQUE KEY uk_module_api_code (module_id, api_code)
    
) COMMENT='接口信息表';

-- 插入测试数据
INSERT INTO Apis (
    api_code, module_id, name, method, path, base_url,
    request_parameters, path_parameters, request_headers, request_body,
    request_body_type, response_body_type, description, status, version,
    timeout_seconds, auth_type, auth_config, tags, examples,
    created_by
) VALUES 
(
    'USER_LOGIN', 1, '用户登录接口', 'POST', '/api/user/login', 'https://api.example.com',
    '{"page": "integer", "size": "integer"}',
    '{"userId": "integer"}',
    '{"Content-Type": "application/json", "Authorization": "Bearer {token}"}',
    '{"username": "string", "password": "string"}',
    'json', 'json', '用户登录接口，支持用户名密码登录', 'active', '1.0',
    30, 'bearer', '{"tokenType": "Bearer", "headerName": "Authorization"}',
    '["用户管理", "认证"]',
    '[{"name": "正常登录", "request": {"username": "admin", "password": "123456"}, "response": {"code": 200, "data": {"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}}}]',
    1
),
(
    'USER_INFO', 1, '获取用户信息', 'GET', '/api/user/{userId}', 'https://api.example.com',
    '{"include": "string"}',
    '{"userId": "integer"}',
    '{"Authorization": "Bearer {token}"}',
    NULL,
    NULL, 'json', '获取指定用户的详细信息', 'active', '1.0',
    15, 'bearer', '{"tokenType": "Bearer", "headerName": "Authorization"}',
    '["用户管理", "查询"]',
    '[{"name": "获取用户信息", "request": {"userId": 123}, "response": {"code": 200, "data": {"id": 123, "name": "张三", "email": "zhangsan@example.com"}}}]',
    1
),
(
    'USER_UPDATE', 1, '更新用户信息', 'PUT', '/api/user/{userId}', 'https://api.example.com',
    NULL,
    '{"userId": "integer"}',
    '{"Content-Type": "application/json", "Authorization": "Bearer {token}"}',
    '{"name": "string", "email": "string", "phone": "string"}',
    'json', 'json', '更新用户的基本信息', 'active', '1.0',
    30, 'bearer', '{"tokenType": "Bearer", "headerName": "Authorization"}',
    '["用户管理", "更新"]',
    '[{"name": "更新用户信息", "request": {"name": "李四", "email": "lisi@example.com"}, "response": {"code": 200, "data": {"success": true}}}]',
    1
);

-- 验证数据插入
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
    tags,
    examples
FROM Apis 
WHERE module_id = 1;
