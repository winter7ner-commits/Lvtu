USE lvtu;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lvt DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '律途法律平台数据库';
USE lvt;

-- 法规分类表
CREATE TABLE IF NOT EXISTS legal_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称，如民法、行政法、刑法',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='法规分类表';

-- 法规文件表
CREATE TABLE IF NOT EXISTS legal_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '法规文件ID',
    category_id BIGINT NOT NULL COMMENT '所属法规分类ID',
    title VARCHAR(200) NOT NULL COMMENT '法规文件名称，如中华人民共和国民法典',
    short_title VARCHAR(100) COMMENT '简称，如民法典',
    issuing_authority VARCHAR(100) COMMENT '发布机关，如全国人大常委会',
    publish_date DATE COMMENT '发布日期',
    effective_date DATE COMMENT '生效日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1现行有效, 0失效',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES legal_category(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='法规文件表';

-- 法条表
CREATE TABLE IF NOT EXISTS legal_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '法条ID',
    document_id BIGINT NOT NULL COMMENT '所属法规文件ID',
    article_no VARCHAR(50) NOT NULL COMMENT '条文编号，如第一条、第二条',
    chapter_title VARCHAR(100) COMMENT '章节标题，如第一章基本规定',
    content TEXT NOT NULL COMMENT '条文内容',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '条文排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (document_id) REFERENCES legal_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='法条表';

-- 插入示例数据
-- 法规分类示例
INSERT INTO legal_category (name, parent_id, sort_order, status) VALUES
('宪法', 0, 1, 1),
('民法', 0, 2, 1),
('刑法', 0, 3, 1),
('行政法', 0, 4, 1),
('商法', 0, 5, 1),
('诉讼法', 0, 6, 1),
('经济法', 0, 7, 1),
('社会法', 0, 8, 1),
('知识产权法', 0, 9, 1),
('婚姻家庭法', 0, 10, 1),
('民法典', 2, 1, 1),
('刑法分则', 3, 1, 1);

-- 法规文件示例
INSERT INTO legal_document (category_id, title, short_title, issuing_authority, publish_date, effective_date, status) VALUES
(1, '中华人民共和国宪法', '宪法', '全国人民代表大会', '1982-12-04', '1982-12-04', 1),
(11, '中华人民共和国民法典', '民法典', '全国人民代表大会', '2020-05-28', '2021-01-01', 1),
(3, '中华人民共和国刑法', '刑法', '全国人民代表大会', '1997-03-14', '1997-10-01', 1),
(4, '中华人民共和国行政许可法', '行政许可法', '全国人大常委会', '2003-08-27', '2004-07-01', 1),
(5, '中华人民共和国公司法', '公司法', '全国人大常委会', '2018-10-26', '2018-10-26', 1),
(6, '中华人民共和国民事诉讼法', '民事诉讼法', '全国人大常委会', '2023-09-01', '2024-01-01', 1);

-- 法条示例 - 宪法
INSERT INTO legal_article (document_id, article_no, chapter_title, content, sort_order) VALUES
(1, '第一条', '第一章 总纲', '中华人民共和国是工人阶级领导的、以工农联盟为基础的人民民主专政的社会主义国家。', 1),
(1, '第二条', '第一章 总纲', '中华人民共和国的一切权力属于人民。', 2),
(1, '第三条', '第一章 总纲', '中华人民共和国的国家机构实行民主集中制的原则。', 3),
(1, '第四条', '第一章 总纲', '中华人民共和国各民族一律平等。', 4),
(1, '第五条', '第一章 总纲', '中华人民共和国实行依法治国，建设社会主义法治国家。', 5),
(1, '第六条', '第一章 总纲', '中华人民共和国的社会主义经济制度的基础是生产资料的社会主义公有制。', 6);

-- 法条示例 - 民法典
INSERT INTO legal_article (document_id, article_no, chapter_title, content, sort_order) VALUES
(2, '第一条', '第一编 总则 第一章 基本规定', '为了保护民事主体的合法权益，调整民事关系，维护社会和经济秩序，适应中国特色社会主义发展要求，弘扬社会主义核心价值观，根据宪法，制定本法。', 1),
(2, '第二条', '第一编 总则 第一章 基本规定', '民法调整平等主体的自然人、法人和非法人组织之间的人身关系和财产关系。', 2),
(2, '第三条', '第一编 总则 第一章 基本规定', '民事主体的人身权利、财产权利以及其他合法权益受法律保护，任何组织或者个人不得侵犯。', 3),
(2, '第四条', '第一编 总则 第一章 基本规定', '民事主体在民事活动中的法律地位一律平等。', 4),
(2, '第五条', '第一编 总则 第一章 基本规定', '民事主体从事民事活动，应当遵循自愿原则，按照自己的意思设立、变更、终止民事法律关系。', 5),
(2, '第六条', '第一编 总则 第一章 基本规定', '民事主体从事民事活动，应当遵循公平原则，合理确定各方的权利和义务。', 6),
(2, '第七条', '第一编 总则 第一章 基本规定', '民事主体从事民事活动，应当遵循诚信原则，秉持诚实，恪守承诺。', 7),
(2, '第八条', '第一编 总则 第一章 基本规定', '民事主体从事民事活动，不得违反法律，不得违背公序良俗。', 8);

-- 法条示例 - 刑法
INSERT INTO legal_article (document_id, article_no, chapter_title, content, sort_order) VALUES
(3, '第一条', '第一编 总则 第一章 刑法的任务、基本原则和适用范围', '为了惩罚犯罪，保护人民，根据宪法，结合我国同犯罪作斗争的具体经验及实际情况，制定本法。', 1),
(3, '第二条', '第一编 总则 第一章 刑法的任务、基本原则和适用范围', '中华人民共和国刑法的任务，是用刑罚同一切犯罪行为作斗争，以保卫国家安全，保卫人民民主专政的政权和社会主义制度，保护国有财产和劳动群众集体所有的财产，保护公民私人所有的财产，保护公民的人身权利、民主权利和其他权利，维护社会秩序、经济秩序，保障社会主义建设事业的顺利进行。', 2),
(3, '第三条', '第一编 总则 第一章 刑法的任务、基本原则和适用范围', '法律明文规定为犯罪行为的，依照法律定罪处刑；法律没有明文规定为犯罪行为的，不得定罪处刑。', 3),
(3, '第四条', '第一编 总则 第一章 刑法的任务、基本原则和适用范围', '对任何人犯罪，在适用法律上一律平等。不允许任何人有超越法律的特权。', 4),
(3, '第五条', '第一编 总则 第一章 刑法的任务、基本原则和适用范围', '刑罚的轻重，应当与犯罪分子所犯罪行和承担的刑事责任相适应。', 5);

-- 法条示例 - 行政许可法
INSERT INTO legal_article (document_id, article_no, chapter_title, content, sort_order) VALUES
(4, '第一条', '第一章 总则', '为了规范行政许可的设定和实施，保护公民、法人和其他组织的合法权益，维护公共利益和社会秩序，保障和监督行政机关有效实施行政管理，根据宪法，制定本法。', 1),
(4, '第二条', '第一章 总则', '本法所称行政许可，是指行政机关根据公民、法人或者其他组织的申请，经依法审查，准予其从事特定活动的行为。', 2),
(4, '第三条', '第一章 总则', '行政许可的设定和实施，适用本法。有关行政机关对其他机关或者对其直接管理的事业单位的人事、财务、外事等事项的审批，不适用本法。', 3);

-- 法条示例 - 公司法
INSERT INTO legal_article (document_id, article_no, chapter_title, content, sort_order) VALUES
(5, '第一条', '第一章 总则', '为了规范公司的组织和行为，保护公司、股东和债权人的合法权益，维护社会经济秩序，促进社会主义市场经济的发展，制定本法。', 1),
(5, '第二条', '第一章 总则', '本法所称公司是指依照本法在中国境内设立的有限责任公司和股份有限公司。', 2),
(5, '第三条', '第一章 总则', '公司是企业法人，有独立的法人财产，享有法人财产权。公司以其全部财产对公司的债务承担责任。', 3);

-- 法条示例 - 民事诉讼法
INSERT INTO legal_article (document_id, article_no, chapter_title, content, sort_order) VALUES
(6, '第一条', '第一编 总则 第一章 任务、适用范围和基本原则', '中华人民共和国民事诉讼法以宪法为根据，结合我国民事审判工作的经验和实际情况制定。', 1),
(6, '第二条', '第一编 总则 第一章 任务、适用范围和基本原则', '中华人民共和国民事诉讼法的任务，是保护当事人行使诉讼权利，保证人民法院查明事实，分清是非，正确适用法律，及时审理民事案件，确认民事权利义务关系，制裁民事违法行为，保护当事人的合法权益，教育公民自觉遵守法律，维护社会秩序、经济秩序，保障社会主义建设事业顺利进行。', 2),
(6, '第三条', '第一编 总则 第一章 任务、适用范围和基本原则', '人民法院受理公民之间、法人之间、其他组织之间以及他们相互之间因财产关系和人身关系提起的民事诉讼，适用本法的规定。', 3);
