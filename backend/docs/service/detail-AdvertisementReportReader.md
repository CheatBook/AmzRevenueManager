# AdvertisementReportReader

広告レポート（CSV ファイル）を読み込み、解析するためのリーダークラスです。

## 実装ファイル

- [AdvertisementReportReader.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/importer/reader/AdvertisementReportReader.java)

## 主な機能

### 1. CSV ファイルの解析

- **コンストラクタ:** `AdvertisementReportReader(MultipartFile file)`
- **処理内容:**
    - `BOMInputStream` を使用して、BOM 付き UTF-8 ファイルを正しく処理する。
    - `Apache Commons CSV` の `CSVParser` を構成し、ヘッダーの自動認識、トリミング、大文字小文字の無視を設定する。

### 2. リソース管理

- `AutoCloseable` を実装しており、`try-with-resources` 文による確実なクローズを保証する。

## 依存ライブラリ

- `Apache Commons CSV`
- `Apache Commons IO`
