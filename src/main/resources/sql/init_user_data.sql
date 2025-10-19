-- 初始化测试用户数据
-- 密码都是: 123456 (使用BCrypt加密后的值)

INSERT INTO Users (name, email, avatar_url, phone, password, department_id, employee_id, creator_id, position, description, status, created_at, updated_at, is_deleted) 
VALUES 
('管理员', 'admin@example.com', 'https://example.com/avatars/admin.jpg', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1, 'EMP001', 1, '系统管理员', '系统初始管理员账户', 'active', NOW(), NOW(), FALSE),
('测试用户', 'test@example.com', 'https://example.com/avatars/test.jpg', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1, 'EMP002', 1, '测试员', '测试用户账户', 'active', NOW(), NOW(), FALSE),
('待审核用户', 'pending@example.com', 'https://example.com/avatars/pending.jpg', '13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1, 'EMP003', 1, '实习生', '待审核用户账户', 'pending', NOW(), NOW(), FALSE),
('禁用用户', 'disabled@example.com', 'https://example.com/avatars/disabled.jpg', '13800138003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 1, 'EMP004', 1, '前员工', '已禁用用户账户', 'inactive', NOW(), NOW(), FALSE);
