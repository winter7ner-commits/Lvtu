# 法条完整数据导入说明

## 目标

法律法规模块采用三级结构：

```text
法律分类 -> 法律文件 -> 具体法条
```

如果要做到“每个分类都有多部法律，并且每部法律都有完整全部法条”，不建议手写 SQL。正确做法是从官方来源下载法律全文，再由导入脚本自动解析并生成 SQL。

## 数据来源

建议使用国家法律法规数据库：

```text
https://flk.npc.gov.cn/
```

不要直接复制第三方 App、商业法律数据库或付费平台的解释内容。

## 文件准备

在国家法律法规数据库中搜索法律名称，下载或复制官方正文，保存到：

```text
data/legal_sources/
```

文件名必须尽量和法律名称一致，例如：

```text
data/legal_sources/中华人民共和国民法典.txt
data/legal_sources/中华人民共和国劳动合同法.docx
data/legal_sources/中华人民共和国消费者权益保护法.txt
```

支持格式：

```text
.txt
.docx
```

## 法律清单

法律清单在：

```text
tools/legal_documents_manifest.json
```

里面已经按分类列了多部常用法律。后续想增加法律，只需要在对应分类的 `documents` 数组里追加名称。

## 生成 SQL

执行：

```bash
python3 tools/import_legal_articles.py \
  --manifest tools/legal_documents_manifest.json \
  --source-dir data/legal_sources \
  --output /tmp/legal_full_data.sql
```

脚本会生成：

```text
legal_category
legal_document
legal_article
legal_article_explanation
```

其中 `legal_article` 会按“第X条”自动拆分完整法条；`legal_article_explanation` 会为每条法条生成一段通俗解释草稿。

## 执行 SQL

如果是新数据库，先执行项目原来的 8 个初始化 SQL：

```text
01_database.sql
02_users.sql
03_legal_article.sql
04_application.sql
05_service_order.sql
06_result.sql
07_evaluation.sql
08_china_region.sql
```

然后执行生成文件：

```text
/tmp/legal_full_data.sql
```

如果你仍然坚持“只保留 8 个 SQL 文件”，可以把 `/tmp/legal_full_data.sql` 的内容合并进 `sql/03_legal_article.sql` 的数据插入区。

## 注意事项

1. 完整法条数据量会很大，民法典一部法律就有一千多条。
2. 解释内容是平台整理的通俗说明，不构成法律意见。
3. 导入前应检查 SQL 文件，避免下载文本中包含目录、修订说明或无关页眉页脚。
4. 如果某部法律没有生成法条，通常是源文件格式不规范，需要清理文本后重新运行。
