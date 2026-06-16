#!/usr/bin/env python3
"""
Fetch public law text from Chinese Wikisource and generate legal article SQL.

This script is intentionally separate from the local-file importer. It is for
project demo data: it fetches publicly available law text, resolves common
Wikisource transclusion pages, parses articles, and appends complete legal
article/explanation rows after the manually maintained document catalogue.
"""

from __future__ import annotations

import argparse
import json
import re
import ssl
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from pathlib import Path

SSL_CONTEXT = ssl._create_unverified_context()

ARTICLE_RE = re.compile(r"(?:\{\{gap\}\})?\s*(?:'''|’{3})?(第[零〇一二三四五六七八九十百千万两]+条)(?:'''|’{3})?\s*[　 ]*")
CHAPTER_RE = re.compile(r"^==+\s*(第[零〇一二三四五六七八九十百千万两]+章[^=\n]*)\s*==+")
SECTION_RE = re.compile(r"^==+\s*(第[零〇一二三四五六七八九十百千万两]+节[^=\n]*)\s*==+")
TRANSCLUDE_RE = re.compile(r"\{\{([:/][^}|]+)")
HEADER_RE = re.compile(r"\{\{header.*?\}\}", re.DOTALL)
TEMPLATE_RE = re.compile(r"\{\{[^{}\n]*(?:\n[^{}]*)?\}\}")
LINK_RE = re.compile(r"\[\[(?:[^|\]]+\|)?([^\]]+)\]\]")
TAG_RE = re.compile(r"<[^>]+>")


@dataclass
class Document:
    id: int
    name: str
    category_id: int


@dataclass
class Article:
    number: str
    title: str | None
    chapter_title: str | None
    section_title: str | None
    content: str
    sort_order: int


def sql_string(value: str | None) -> str:
    if value is None:
        return "NULL"
    return "'" + value.replace("\\", "\\\\").replace("'", "''") + "'"


def fetch_raw(title: str, timeout: int = 20) -> str | None:
    encoded = urllib.parse.urlencode({"title": title, "action": "raw"})
    url = f"https://zh.wikisource.org/w/index.php?{encoded}"
    request = urllib.request.Request(url, headers={"User-Agent": "LvtuLegalDataImporter/1.0"})
    try:
        with urllib.request.urlopen(request, timeout=timeout, context=SSL_CONTEXT) as response:
            text = response.read().decode("utf-8", errors="replace")
    except (urllib.error.URLError, TimeoutError):
        return None
    if "维基文库目前没有" in text or "#REDIRECT" in text.upper():
        return None
    return text


def search_titles(name: str) -> list[str]:
    query = urllib.parse.urlencode({
        "action": "opensearch",
        "search": name,
        "limit": "8",
        "namespace": "0",
        "format": "json",
    })
    url = f"https://zh.wikisource.org/w/api.php?{query}"
    request = urllib.request.Request(url, headers={"User-Agent": "LvtuLegalDataImporter/1.0"})
    try:
        with urllib.request.urlopen(request, timeout=20, context=SSL_CONTEXT) as response:
            payload = json.loads(response.read().decode("utf-8", errors="replace"))
    except (urllib.error.URLError, TimeoutError, json.JSONDecodeError):
        return []
    if not isinstance(payload, list) or len(payload) < 2:
        return []
    titles = payload[1]
    return [item for item in titles if isinstance(item, str)]


def transclusion_titles(raw: str, base_title: str) -> list[str]:
    titles: list[str] = []
    for matched in TRANSCLUDE_RE.findall(raw):
        candidate = matched.strip()
        if not candidate:
            continue
        if candidate.startswith(":"):
            candidate = candidate[1:].strip()
        elif candidate.startswith("/"):
            candidate = base_title + candidate
        else:
            continue
        if candidate and candidate not in titles:
            titles.append(candidate)
    return titles


def raw_with_articles(title: str) -> tuple[str | None, str | None]:
    raw = fetch_raw(title)
    if raw is None:
        return None, None
    if ARTICLE_RE.search(raw):
        return raw, title

    parts: list[str] = []
    source_title = title
    for candidate in transclusion_titles(raw, title):
        text = fetch_raw(candidate)
        if text and ARTICLE_RE.search(text):
            parts.append(text)
            source_title = candidate
        time.sleep(0.05)
    if parts:
        return "\n".join(parts), source_title
    return None, None


def resolve_text(name: str) -> tuple[str | None, str | None]:
    raw, source = raw_with_articles(name)
    if raw is not None:
        return raw, source

    # Some pages use latest-year subpages without a direct transclusion.
    for year in range(2026, 1978, -1):
        candidate = f"{name} ({year}年)"
        raw, source = raw_with_articles(candidate)
        if raw is not None:
            return raw, source
        time.sleep(0.05)

    for candidate in search_titles(name):
        raw, source = raw_with_articles(candidate)
        if raw is not None:
            return raw, source
        time.sleep(0.05)

    return None, None


def clean_line(line: str) -> str:
    line = line.strip()
    line = line.replace("{{gap}}", "")
    line = line.replace("'''", "")
    line = LINK_RE.sub(r"\1", line)
    line = TEMPLATE_RE.sub("", line)
    line = TAG_RE.sub("", line)
    line = line.replace("&nbsp;", " ")
    line = re.sub(r"[ \t\u3000]+", " ", line)
    return line.strip()


def normalize_content(parts: list[str]) -> str:
    text = "\n".join(part.strip() for part in parts if part.strip())
    text = re.sub(r"\n{3,}", "\n\n", text)
    return text.strip()


def parse_articles(raw: str) -> list[Article]:
    raw = HEADER_RE.sub("", raw)
    raw = raw.replace("<onlyinclude>", "").replace("</onlyinclude>", "")
    lines = raw.replace("\r\n", "\n").replace("\r", "\n").split("\n")

    articles: list[Article] = []
    chapter_title: str | None = None
    section_title: str | None = None
    current_number: str | None = None
    current_parts: list[str] = []
    order = 0

    def flush() -> None:
        nonlocal current_number, current_parts, order
        if current_number is None:
            return
        content = normalize_content(current_parts)
        if content:
            order += 1
            title = section_title or chapter_title
            articles.append(Article(current_number, title, chapter_title, section_title, content, order))
        current_number = None
        current_parts = []

    for raw_line in lines:
        line = raw_line.strip()
        if not line:
            continue

        section_match = SECTION_RE.match(line)
        if section_match:
            flush()
            section_title = clean_line(section_match.group(1))
            continue

        chapter_match = CHAPTER_RE.match(line)
        if chapter_match:
            flush()
            chapter_title = clean_line(chapter_match.group(1))
            section_title = None
            continue

        article_match = ARTICLE_RE.search(line)
        if article_match:
            flush()
            current_number = article_match.group(1)
            remaining = clean_line(line[article_match.end():])
            if remaining:
                current_parts = [remaining]
            continue

        if current_number is not None:
            cleaned = clean_line(line)
            if cleaned and not cleaned.startswith("[[Category:"):
                current_parts.append(cleaned)

    flush()
    return articles


def load_documents(sql_path: Path) -> list[Document]:
    text = sql_path.read_text(encoding="utf-8")
    start = text.find("INSERT INTO legal_document")
    end = text.find("-- 核心法条数据", start)
    if start < 0:
        return []
    text = text[start:end if end >= 0 else len(text)]
    pattern = re.compile(r"^\((\d+),\s*'((?:[^']|'')*)',\s*(\d+),", re.MULTILINE)
    docs: list[Document] = []
    seen: set[int] = set()
    for match in pattern.finditer(text):
        doc_id = int(match.group(1))
        name = match.group(2).replace("''", "'")
        category_id = int(match.group(3))
        if doc_id in seen:
            continue
        seen.add(doc_id)
        docs.append(Document(doc_id, name, category_id))
    return docs


def explanation_for(doc_name: str, article: Article) -> str:
    compact = re.sub(r"\s+", "", article.content)
    short = compact[:70] + ("..." if len(compact) > 70 else "")
    return (
        f"本条出自《{doc_name}》，核心内容是“{short}”。适用时应结合条文所在章节、案件事实和其他相关法律规定理解。"
        "本解释为平台根据公开法条整理的通俗说明，仅供学习参考，不构成具体法律意见。"
    )


def build_sql(rows: list[tuple[Document, str, list[Article]]]) -> str:
    article_values: list[str] = []
    explanation_values: list[str] = []
    article_id = 100000
    explanation_id = 100000

    for doc, source_title, articles in rows:
        deduped_by_number: dict[str, Article] = {}
        for article in articles:
            existing = deduped_by_number.get(article.number)
            if existing is None or len(article.content) > len(existing.content):
                deduped_by_number[article.number] = article

        deduped_articles = list(deduped_by_number.values())
        deduped_articles.sort(key=lambda item: item.sort_order)
        for index, article in enumerate(deduped_articles, start=1):
            article.sort_order = index

        for article in deduped_articles:
            article_values.append(
                "("
                f"{article_id}, {doc.id}, {sql_string(article.number)}, {sql_string(article.title)}, "
                f"{sql_string(article.chapter_title)}, {sql_string(article.section_title)}, "
                f"{sql_string(article.content)}, {sql_string('https://zh.wikisource.org/wiki/' + urllib.parse.quote(source_title))}, "
                f"{article.sort_order}, 1"
                ")"
            )
            explanation_values.append(
                "("
                f"{explanation_id}, {article_id}, {sql_string(explanation_for(doc.name, article))}, '平台整理', 1"
                ")"
            )
            article_id += 1
            explanation_id += 1

    if not article_values:
        return ""

    return "\n".join([
        "",
        "-- Wikisource full-text generated legal articles.",
        "-- Clear hand-written demo article rows first, otherwise (document_id, article_number) will conflict.",
        "DELETE FROM legal_article_explanation_feedback;",
        "DELETE FROM legal_article_favorite;",
        "DELETE FROM legal_article_comment;",
        "DELETE FROM legal_article_explanation;",
        "DELETE FROM legal_article;",
        "INSERT INTO legal_article (id, document_id, article_number, title, chapter_title, section_title, content, source_url, sort_order, status) VALUES",
        ",\n".join(article_values) + ";",
        "",
        "INSERT INTO legal_article_explanation (id, article_id, content, source, status) VALUES",
        ",\n".join(explanation_values) + ";",
        "",
    ])


def deduped_article_count(rows: list[tuple[Document, str, list[Article]]]) -> int:
    total = 0
    for _, _, articles in rows:
        total += len({article.number for article in articles})
    return total


def replace_generated_block(sql_path: Path, generated_sql: str) -> None:
    marker = "-- Wikisource full-text generated legal articles."
    text = sql_path.read_text(encoding="utf-8")
    index = text.find(marker)
    if index >= 0:
        text = text[:index].rstrip() + "\n"
    sql_path.write_text(text.rstrip() + "\n" + generated_sql, encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--sql", default="sql/03_legal_article.sql")
    parser.add_argument("--limit", type=int, default=0)
    args = parser.parse_args()

    sql_path = Path(args.sql)
    documents = load_documents(sql_path)
    if args.limit > 0:
        documents = documents[:args.limit]

    rows: list[tuple[Document, str, list[Article]]] = []
    misses: list[str] = []

    for doc in documents:
        raw, source_title = resolve_text(doc.name)
        if raw is None or source_title is None:
            misses.append(doc.name)
            print(f"MISS {doc.name}")
            continue
        articles = parse_articles(raw)
        if not articles:
            misses.append(doc.name)
            print(f"EMPTY {doc.name}")
            continue
        rows.append((doc, source_title, articles))
        print(f"OK {doc.id} {doc.name}: {len(articles)} articles")
        time.sleep(0.12)

    replace_generated_block(sql_path, build_sql(rows))
    print(f"Generated documents: {len(rows)}")
    print(f"Generated articles: {deduped_article_count(rows)}")
    if misses:
        print("Missing:")
        for name in misses:
            print(f"  - {name}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
