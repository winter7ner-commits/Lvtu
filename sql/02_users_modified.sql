USE lvtu;

-- ===================================================================
-- 修改用户系统：添加 userid 作为业务主键
-- userid：按注册顺序自动编号，不可更改，不显示
-- username：可修改，保持唯一
-- ===================================================================

-- 1. 修改 users 表
-- 添加 userid 字段和自动编号触发器

ALTER TABLE users
ADD COLUMN userid BIGINT UNIQUE NOT NULL COMMENT '用户编号，按注册顺序递增，不可修改' AFTER user_id;

-- 为现有数据赋予 userid（按 user_id 的顺序，从 1 开始）
SET @userid_counter := 0;
UPDATE users SET userid = (@userid_counter := @userid_counter + 1) ORDER BY user_id;

-- 创建触发器：新用户插入时自动分配 userid
DELIMITER $$
CREATE TRIGGER tr_users_insert_userid 
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.userid IS NULL THEN
        SELECT COALESCE(MAX(userid), 0) + 1 INTO @max_userid FROM users;
        SET NEW.userid := @max_userid;
    END IF;
END$$
DELIMITER ;

-- 2. 修改 users_verfications 表外键
ALTER TABLE users_verfications
DROP FOREIGN KEY `fk_user_id`;

ALTER TABLE users_verfications
CHANGE COLUMN user_id user_id BIGINT NOT NULL COMMENT '关联用户ID（userid）';

ALTER TABLE users_verfications
ADD CONSTRAINT `fk_user_id` FOREIGN KEY (user_id) REFERENCES users (userid) ON DELETE CASCADE;

-- 3. 修改 application 表外键
ALTER TABLE application
DROP FOREIGN KEY fk_application_user;

-- 更新 application 表中的 user_id 为 userid
UPDATE application a
SET a.user_id = (SELECT u.userid FROM users u WHERE u.user_id = a.user_id);

ALTER TABLE application
CHANGE COLUMN user_id user_id BIGINT NOT NULL COMMENT '申请人用户ID（userid）';

ALTER TABLE application
ADD CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES users (userid);

-- 4. 修改 lawyer 表外键
ALTER TABLE lawyer
DROP FOREIGN KEY fk_lawyer_user;

-- 更新 lawyer 表中的 user_id 为 userid
UPDATE lawyer l
SET l.user_id = (SELECT u.userid FROM users u WHERE u.user_id = l.user_id);

ALTER TABLE lawyer
CHANGE COLUMN user_id user_id BIGINT NOT NULL UNIQUE COMMENT '关联用户ID（userid）';

ALTER TABLE lawyer
ADD CONSTRAINT fk_lawyer_user FOREIGN KEY (user_id) REFERENCES users (userid);

-- 5. 修改 order 表外键
ALTER TABLE `order`
DROP FOREIGN KEY fk_order_user;

-- 更新 order 表中的 user_id 为 userid
UPDATE `order` o
SET o.user_id = (SELECT u.userid FROM users u WHERE u.user_id = o.user_id);

ALTER TABLE `order`
CHANGE COLUMN user_id user_id BIGINT NOT NULL COMMENT '用户ID（userid）';

ALTER TABLE `order`
ADD CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users (userid);

-- 6. 修改 evaluation 表外键
ALTER TABLE evaluation
DROP FOREIGN KEY fk_eval_user;

-- 更新 evaluation 表中的 user_id 为 userid
UPDATE evaluation e
SET e.user_id = (SELECT u.userid FROM users u WHERE u.user_id = e.user_id);

ALTER TABLE evaluation
CHANGE COLUMN user_id user_id BIGINT NOT NULL COMMENT '评价者用户ID（userid）';

ALTER TABLE evaluation
ADD CONSTRAINT fk_eval_user FOREIGN KEY (user_id) REFERENCES users (userid);

-- 7. 添加索引以支持 userid 查询
ALTER TABLE users
ADD INDEX idx_userid (userid);

-- 8. 更新 username 的注释，说明可以修改但必须唯一（已有唯一约束）
ALTER TABLE users
MODIFY COLUMN username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，可修改但必须唯一';

-- 9. 将 userid 设置为隐藏列（MySQL 8.0.23+）
-- 注意：如果数据库版本低于 8.0.23，请删除或注释此行
-- ALTER TABLE users MODIFY COLUMN userid BIGINT HIDDEN;

-- 查询验证修改
SELECT '===== users 表结构 =====' AS info;
DESC users;

SELECT '===== 用户数据 userid 映射 =====' AS info;
SELECT userid, user_id, username, user_type, created_time FROM users ORDER BY userid;

SELECT '===== 外键关系验证 =====' AS info;
SELECT 
    CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'lvtu' AND REFERENCED_TABLE_NAME = 'users'
ORDER BY TABLE_NAME;
