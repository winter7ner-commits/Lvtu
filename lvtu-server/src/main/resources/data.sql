INSERT INTO legal_category (id, name, parent_id, sort_order) VALUES
(1, '宪法', NULL, 1),
(2, '民法', NULL, 2),
(3, '刑法', NULL, 3),
(4, '行政法', NULL, 4),
(5, '商法', NULL, 5),
(6, '民事诉讼法', NULL, 6),
(7, '刑事诉讼法', NULL, 7),
(8, '行政诉讼法', NULL, 8);

INSERT INTO legal_document (id, name, category_id, publish_date, sort_order, status) VALUES
(1, '中华人民共和国宪法', 1, DATE '1982-12-04', 1, 1),
(2, '中华人民共和国民法典', 2, DATE '2020-05-28', 1, 1),
(3, '中华人民共和国刑法', 3, DATE '2020-12-26', 1, 1),
(4, '中华人民共和国行政许可法', 4, DATE '2019-04-23', 1, 1),
(5, '中华人民共和国公司法', 5, DATE '2018-10-26', 1, 1),
(6, '中华人民共和国民事诉讼法', 6, DATE '2021-12-24', 1, 1),
(7, '中华人民共和国刑事诉讼法', 7, DATE '2018-10-26', 1, 1),
(8, '中华人民共和国行政诉讼法', 8, DATE '2017-06-27', 1, 1);

INSERT INTO legal_article (id, document_id, article_number, title, content, sort_order) VALUES
(1, 1, '第1条', '宪法基本原则', '中华人民共和国是工人阶级领导的、以工农联盟为基础的人民民主专政的社会主义国家。', 1),
(2, 1, '第2条', '社会主义制度', '社会主义制度是中华人民共和国的根本制度。中国共产党领导是中国特色社会主义最本质的特征。', 2),
(3, 2, '第1条', '民法调整范围', '中华人民共和国民法调整平等主体的自然人、法人和非法人组织之间的人身关系和财产关系。', 1),
(4, 2, '第2条', '民事主体地位平等', '民事主体在民事活动中的法律地位一律平等。', 2),
(5, 3, '第1条', '刑法任务', '中华人民共和国刑法的任务，是用刑罚同一切犯罪行为作斗争，以保卫国家安全，保卫人民民主专政的政权和社会主义制度，保护国有财产和劳动群众集体所有的财产，保护公民私人所有的财产，保护公民的人身权利、民主权利和其他权利，维护社会秩序、经济秩序，保障社会主义建设事业的顺利进行。', 1);

INSERT INTO users (id, username, password, email, user_type, avatar_url, status, created_at, updated_at) VALUES
(1, 'admin', 'admin123', 'admin@lvtu.com', 3, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'user', 'user123', 'user@lvtu.com', 1, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);