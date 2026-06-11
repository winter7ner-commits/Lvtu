USE lvtu;

-- 001: 添加 deactivated_phone 字段，用于逻辑删除时临时保存手机号
ALTER TABLE users
ADD COLUMN deactivated_phone VARCHAR(20) DEFAULT NULL COMMENT '注销时临时保存的手机号';

-- 为安全起见，确保在 phone 字段上继续存在唯一索引，deactivated_phone 不需要唯一索引
-- 运行后请验证：
-- SHOW COLUMNS FROM users LIKE 'deactivated_phone';
-- SELECT COUNT(*) FROM users WHERE deactivated_phone IS NOT NULL;