-- 创建数据库
CREATE DATABASE IF NOT EXISTS lvt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lvt;

-- 法律条文表
CREATE TABLE IF NOT EXISTS law_articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50),
    sub_category VARCHAR(50),
    keyword VARCHAR(255),
    source VARCHAR(255),
    effective_date DATE,
    status VARCHAR(20),
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME
);

-- 法律条文章节表
CREATE TABLE IF NOT EXISTS law_sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT,
    section_number VARCHAR(50),
    title VARCHAR(255),
    content TEXT,
    FOREIGN KEY (article_id) REFERENCES law_articles(id)
);

-- 案例表
CREATE TABLE IF NOT EXISTS cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    case_number VARCHAR(100),
    court VARCHAR(255),
    judgment_date DATE,
    content TEXT,
    related_article_id BIGINT,
    keyword VARCHAR(255),
    view_count INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (related_article_id) REFERENCES law_articles(id)
);

-- 法律解释表
CREATE TABLE IF NOT EXISTS law_explanations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT,
    title VARCHAR(255),
    content TEXT,
    explanation_type VARCHAR(50),
    source VARCHAR(255),
    publish_date DATE,
    FOREIGN KEY (article_id) REFERENCES law_articles(id)
);

-- 插入示例数据
-- 法律条文示例
INSERT INTO law_articles (title, content, category, sub_category, keyword, source, effective_date, status, created_at, updated_at)
VALUES 
('中华人民共和国宪法', '中华人民共和国宪法是国家的根本大法...', '宪法', '根本法', '宪法,根本法,国家制度', '全国人民代表大会', '1982-12-04', '有效', NOW(), NOW()),
('中华人民共和国民法典', '中华人民共和国民法典是新中国第一部以法典命名的法律...', '民法', '民法典', '民法典,民事权利,合同', '全国人民代表大会', '2021-01-01', '有效', NOW(), NOW()),
('中华人民共和国刑法', '中华人民共和国刑法是规定犯罪和刑罚的法律...', '刑法', '刑事法律', '刑法,犯罪,刑罚', '全国人民代表大会', '1997-10-01', '有效', NOW(), NOW());

-- 法律条文章节示例
INSERT INTO law_sections (article_id, section_number, title, content)
VALUES 
(1, '第一条', '立法目的', '为了维护国家统一和民族团结，保障公民的基本权利和自由...'),
(1, '第二条', '国家性质', '中华人民共和国是工人阶级领导的、以工农联盟为基础的人民民主专政的社会主义国家...'),
(2, '第一条', '立法目的', '为了保护民事主体的合法权益，调整民事关系...'),
(2, '第二条', '调整范围', '民法调整平等主体的自然人、法人和非法人组织之间的人身关系和财产关系...');

-- 案例示例
INSERT INTO cases (title, content, case_number, court, judgment_date, related_article_id, keyword, created_at, updated_at)
VALUES 
('张三诉李四合同纠纷案', '原告张三与被告李四签订了一份买卖合同...', '(2023)京0101民初1234号', '北京市东城区人民法院', '2023-06-15', 2, '合同纠纷,买卖合同,违约责任', NOW(), NOW()),
('王五故意伤害案', '被告人王五因琐事与被害人赵六发生争执...', '(2023)沪0104刑初5678号', '上海市徐汇区人民法院', '2023-08-20', 3, '故意伤害,刑事犯罪,量刑', NOW(), NOW());

-- 法律解释示例
INSERT INTO law_explanations (article_id, title, content, explanation_type, source, publish_date)
VALUES 
(1, '关于宪法第一条的解释', '宪法第一条规定了国家的根本制度...', '立法解释', '全国人大常委会', '2022-05-10'),
(2, '关于民法典合同编的解释', '民法典合同编对合同的订立、履行、变更和解除等作出了详细规定...', '司法解释', '最高人民法院', '2021-03-01');
