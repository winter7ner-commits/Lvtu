INSERT INTO legal_category (id, name, status) VALUES (1, '宪法', 1);
INSERT INTO legal_category (id, name, status) VALUES (2, '民法', 1);
INSERT INTO legal_category (id, name, status) VALUES (3, '刑法', 1);
INSERT INTO legal_category (id, name, status) VALUES (4, '行政法', 1);
INSERT INTO legal_category (id, name, status) VALUES (5, '商法', 1);
INSERT INTO legal_category (id, name, status) VALUES (6, '民事诉讼法', 1);
INSERT INTO legal_category (id, name, status) VALUES (7, '刑事诉讼法', 1);
INSERT INTO legal_category (id, name, status) VALUES (8, '行政诉讼法', 1);

INSERT INTO legal_document (id, name, category_id) VALUES (1, '中华人民共和国宪法', 1);
INSERT INTO legal_document (id, name, category_id) VALUES (2, '中华人民共和国民法典', 2);
INSERT INTO legal_document (id, name, category_id) VALUES (3, '中华人民共和国刑法', 3);
INSERT INTO legal_document (id, name, category_id) VALUES (4, '中华人民共和国行政许可法', 4);
INSERT INTO legal_document (id, name, category_id) VALUES (5, '中华人民共和国公司法', 5);
INSERT INTO legal_document (id, name, category_id) VALUES (6, '中华人民共和国民事诉讼法', 6);
INSERT INTO legal_document (id, name, category_id) VALUES (7, '中华人民共和国刑事诉讼法', 7);
INSERT INTO legal_document (id, name, category_id) VALUES (8, '中华人民共和国行政诉讼法', 8);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (1, 1, '第一条', '第一章 总则', '中华人民共和国是工人阶级领导的、以工农联盟为基础的人民民主专政的社会主义国家。', 1);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (2, 1, '第二条', '第一章 总则', '中华人民共和国的一切权力属于人民。', 2);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (3, 1, '第三条', '第一章 总则', '中华人民共和国的国家机构实行民主集中制的原则。', 3);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (4, 2, '第一条', '第一章 基本规定', '为了保护民事主体的合法权益，调整民事关系，维护社会和经济秩序，适应中国特色社会主义发展要求，弘扬社会主义核心价值观，根据宪法，制定本法。', 1);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (5, 2, '第二条', '第一章 基本规定', '民法调整平等主体的自然人、法人和非法人组织之间的人身关系和财产关系。', 2);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (6, 2, '第三条', '第一章 基本规定', '民事主体的人身权利、财产权利以及其他合法权益受法律保护，任何组织或者个人不得侵犯。', 3);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (7, 2, '第四条', '第一章 基本规定', '民事主体在民事活动中的法律地位一律平等。', 4);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (8, 2, '第五条', '第一章 基本规定', '民事主体从事民事活动，应当遵循自愿原则，按照自己的意思设立、变更、终止民事法律关系。', 5);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (9, 3, '第一条', '第一章 刑法的任务、基本原则和适用范围', '为了惩罚犯罪，保护人民，根据宪法，结合我国同犯罪作斗争的具体经验及实际情况，制定本法。', 1);
INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (10, 3, '第二条', '第一章 刑法的任务、基本原则和适用范围', '中华人民共和国刑法的任务，是用刑罚同一切犯罪行为作斗争，以保卫国家安全，保卫人民民主专政的政权和社会主义制度，保护国有财产和劳动群众集体所有的财产，保护公民私人所有的财产，保护公民的人身权利、民主权利和其他权利，维护社会秩序、经济秩序，保障社会主义建设事业的顺利进行。', 2);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (11, 4, '第一条', '第一章 总则', '为了规范行政许可的设定和实施，保护公民、法人和其他组织的合法权益，维护公共利益和社会秩序，保障和监督行政机关有效实施行政管理，根据宪法，制定本法。', 1);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (12, 5, '第一条', '第一章 总则', '为了规范公司的组织和行为，保护公司、股东和债权人的合法权益，维护社会经济秩序，促进社会主义市场经济的发展，制定本法。', 1);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (13, 6, '第一条', '第一章 任务、适用范围和基本原则', '中华人民共和国民事诉讼法以宪法为根据，结合我国民事审判工作的经验和实际情况制定。', 1);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (14, 7, '第一条', '第一章 任务和基本原则', '为了保证刑法的正确实施，惩罚犯罪，保护人民，保障国家安全和社会公共安全，维护社会主义社会秩序，根据宪法，制定本法。', 1);

INSERT INTO legal_article (id, document_id, article_no, chapter_title, content, sort_order) VALUES (15, 8, '第一条', '第一章 总则', '为保证人民法院公正、及时审理行政案件，解决行政争议，保护公民、法人和其他组织的合法权益，监督行政机关依法行使职权，根据宪法，制定本法。', 1);
