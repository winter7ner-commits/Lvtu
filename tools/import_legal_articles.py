#!/usr/bin/env python3
"""
Generate full legal article SQL from official law text files.

Usage:
  python3 tools/import_legal_articles.py \
    --manifest tools/legal_documents_manifest.json \
    --source-dir data/legal_sources \
    --output /tmp/legal_full_data.sql

Source files:
  Put official law text files downloaded from the National Laws and Regulations
  Database into source-dir. Supported formats: .txt, .docx.

File naming:
  The importer matches files by law name. For example:
    data/legal_sources/中华人民共和国民法典.txt
    data/legal_sources/中华人民共和国劳动合同法.docx

Output:
  SQL INSERT statements for legal_category, legal_document,
  legal_article and legal_article_explanation.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
import zipfile
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable
from xml.etree import ElementTree


ARTICLE_RE = re.compile(r"(第[零〇一二三四五六七八九十百千万两]+条)")
CHAPTER_RE = re.compile(r"^第[零〇一二三四五六七八九十百千万两]+章\s*(.+)?$")
SECTION_RE = re.compile(r"^第[零〇一二三四五六七八九十百千万两]+节\s*(.+)?$")


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


def normalize_text(text: str) -> str:
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    text = re.sub(r"[ \t\u3000]+", " ", text)
    text = re.sub(r"\n{3,}", "\n\n", text)
    return text.strip()


def read_txt(path: Path) -> str:
    for encoding in ("utf-8", "utf-8-sig", "gb18030"):
        try:
            return path.read_text(encoding=encoding)
        except UnicodeDecodeError:
            continue
    raise UnicodeDecodeError("unknown", b"", 0, 1, f"Cannot decode {path}")


def read_docx(path: Path) -> str:
    with zipfile.ZipFile(path) as archive:
        xml = archive.read("word/document.xml")
    root = ElementTree.fromstring(xml)
    namespace = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}
    paragraphs: list[str] = []
    for paragraph in root.findall(".//w:p", namespace):
        parts = [node.text or "" for node in paragraph.findall(".//w:t", namespace)]
        line = "".join(parts).strip()
        if line:
            paragraphs.append(line)
    return "\n".join(paragraphs)


def read_source(path: Path) -> str:
    if path.suffix.lower() == ".txt":
        return read_txt(path)
    if path.suffix.lower() == ".docx":
        return read_docx(path)
    raise ValueError(f"Unsupported file type: {path}")


def split_article_lines(text: str) -> list[str]:
    lines: list[str] = []
    for raw in normalize_text(text).split("\n"):
        line = raw.strip()
        if not line:
            continue
        lines.append(line)
    return lines


def parse_articles(text: str) -> list[Article]:
    lines = split_article_lines(text)
    articles: list[Article] = []
    current_chapter: str | None = None
    current_section: str | None = None
    current_number: str | None = None
    current_content: list[str] = []
    order = 0

    def flush() -> None:
        nonlocal current_number, current_content, order
        if current_number is None:
            return
        content = normalize_text("\n".join(current_content))
        if not content:
            return
        order += 1
        title = current_section or current_chapter
        articles.append(
            Article(
                number=current_number,
                title=title,
                chapter_title=current_chapter,
                section_title=current_section,
                content=content,
                sort_order=order,
            )
        )
        current_number = None
        current_content = []

    for line in lines:
        chapter_match = CHAPTER_RE.match(line)
        if chapter_match:
            flush()
            current_chapter = line
            current_section = None
            continue

        section_match = SECTION_RE.match(line)
        if section_match:
            flush()
            current_section = line
            continue

        article_match = ARTICLE_RE.match(line)
        if article_match:
            flush()
            current_number = article_match.group(1)
            remaining = line[article_match.end():].strip()
            current_content = [remaining] if remaining else []
            continue

        if current_number is not None:
            current_content.append(line)

    flush()
    return articles


def find_source_file(source_dir: Path, law_name: str) -> Path | None:
    candidates = [
        source_dir / f"{law_name}.txt",
        source_dir / f"{law_name}.docx",
    ]
    for candidate in candidates:
        if candidate.exists():
            return candidate
    compact = re.sub(r"\s+", "", law_name)
    for path in source_dir.glob("*"):
        if path.suffix.lower() not in {".txt", ".docx"}:
            continue
        if re.sub(r"\s+", "", path.stem) == compact:
            return path
    return None


def explanation_for(article: Article) -> str:
    plain = re.sub(r"\s+", "", article.content)
    short = plain[:80]
    if len(plain) > 80:
        short += "..."
    return (
        f"本条主要规定“{short}”。理解时应结合所属法律、章节和具体案件事实判断。"
        "该解释为平台根据公开法条整理的通俗说明，仅供学习参考，不构成具体法律意见。"
    )


def iter_documents(manifest: dict) -> Iterable[tuple[int, str, int]]:
    seen: set[tuple[int, str]] = set()
    for category in manifest["categories"]:
        category_id = int(category["id"])
        for index, law_name in enumerate(category["documents"], start=1):
            key = (category_id, law_name)
            if key in seen:
                continue
            seen.add(key)
            yield category_id, law_name, index


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", default="tools/legal_documents_manifest.json")
    parser.add_argument("--source-dir", default="data/legal_sources")
    parser.add_argument("--output", required=True)
    args = parser.parse_args()

    manifest_path = Path(args.manifest)
    source_dir = Path(args.source_dir)
    output_path = Path(args.output)

    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    output_path.parent.mkdir(parents=True, exist_ok=True)

    document_id = 1
    article_id = 1
    explanation_id = 1
    lines: list[str] = [
        "USE lvtu;",
        "",
        "-- Generated by tools/import_legal_articles.py",
        "-- Review generated SQL before running in a production database.",
        "",
    ]

    lines.append("INSERT INTO legal_category (id, name, parent_id, sort_order, status) VALUES")
    category_values = []
    for category in manifest["categories"]:
        category_values.append(
            f"({int(category['id'])}, {sql_string(category['name'])}, 0, {int(category['id'])}, 1)"
        )
    lines.append(",\n".join(category_values) + "\nON DUPLICATE KEY UPDATE name = VALUES(name), sort_order = VALUES(sort_order), status = VALUES(status);")
    lines.append("")

    missing: list[str] = []
    doc_values: list[str] = []
    article_values: list[str] = []
    explanation_values: list[str] = []

    for category_id, law_name, sort_order in iter_documents(manifest):
        source_file = find_source_file(source_dir, law_name)
        doc_values.append(
            "("
            f"{document_id}, {sql_string(law_name)}, {category_id}, NULL, '法律', '现行有效', "
            f"{sql_string(manifest.get('source_name'))}, {sql_string(manifest.get('source_url'))}, NULL, "
            f"{sort_order}, 1"
            ")"
        )

        if source_file is None:
            missing.append(law_name)
            document_id += 1
            continue

        articles = parse_articles(read_source(source_file))
        if not articles:
            missing.append(f"{law_name}（未解析到法条）")
            document_id += 1
            continue

        for article in articles:
            article_values.append(
                "("
                f"{article_id}, {document_id}, {sql_string(article.number)}, {sql_string(article.title)}, "
                f"{sql_string(article.chapter_title)}, {sql_string(article.section_title)}, "
                f"{sql_string(article.content)}, {sql_string(manifest.get('source_url'))}, "
                f"{article.sort_order}, 1"
                ")"
            )
            explanation_values.append(
                "("
                f"{explanation_id}, {article_id}, {sql_string(explanation_for(article))}, '平台整理', 1"
                ")"
            )
            article_id += 1
            explanation_id += 1

        document_id += 1

    lines.append("INSERT INTO legal_document (id, name, category_id, publish_date, law_level, effective_status, source_name, source_url, summary, sort_order, status) VALUES")
    lines.append(",\n".join(doc_values) + "\nON DUPLICATE KEY UPDATE category_id = VALUES(category_id), source_name = VALUES(source_name), source_url = VALUES(source_url), sort_order = VALUES(sort_order), status = VALUES(status);")
    lines.append("")

    if article_values:
        lines.append("INSERT INTO legal_article (id, document_id, article_number, title, chapter_title, section_title, content, source_url, sort_order, status) VALUES")
        lines.append(",\n".join(article_values) + "\nON DUPLICATE KEY UPDATE title = VALUES(title), chapter_title = VALUES(chapter_title), section_title = VALUES(section_title), content = VALUES(content), source_url = VALUES(source_url), sort_order = VALUES(sort_order), status = VALUES(status);")
        lines.append("")

    if explanation_values:
        lines.append("INSERT INTO legal_article_explanation (id, article_id, content, source, status) VALUES")
        lines.append(",\n".join(explanation_values) + "\nON DUPLICATE KEY UPDATE content = VALUES(content), source = VALUES(source), status = VALUES(status);")
        lines.append("")

    if missing:
        lines.append("-- Missing source files or parse failures:")
        for name in missing:
            lines.append(f"-- - {name}")

    output_path.write_text("\n".join(lines) + "\n", encoding="utf-8")

    print(f"Generated: {output_path}")
    print(f"Documents: {document_id - 1}")
    print(f"Articles: {article_id - 1}")
    print(f"Explanations: {explanation_id - 1}")
    if missing:
        print("Missing source files:")
        for name in missing:
            print(f"  - {name}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
