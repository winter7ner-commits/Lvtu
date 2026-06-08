USE lvtu;

-- ===================================================================
-- 添加 deactivated_phone 字段
-- 注销时将原手机号暂存到此字段，释放 phone 列供新用户注册
-- 恢复账号时从 deactivated_phone 取回手机号
-- ===================================================================

-- 1. 添加 deactivated_phone 列
ALTER TABLE users
ADD COLUMN deactivated_phone VARCHAR(20) DEFAULT NULL COMMENT '注销时暂存的原手机号，用于恢复账号验证身份'
AFTER phone;

-- 2. 修复已有的已注销用户：将 phone 中的手机号迁移到 deactivated_phone，然后清空 phone
UPDATE users
SET deactivated_phone = phone, phone = NULL
WHERE status = 0 AND phone IS NOT NULL;

-- 3. 修复已有的已注销用户：将用户名改为 _deactivated_ 前缀，释放原用户名
UPDATE users
SET username = CONCAT('_deactivated_', user_id, '_', UNIX_TIMESTAMP(updated_time))
WHERE status = 0 AND username NOT LIKE '_deactivated_%';

-- 验证
SELECT '===== 已注销用户数据修复验证 =====' AS info;
SELECT user_id, username, phone, deactivated_phone, status
FROM users
WHERE status = 0;
