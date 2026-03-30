# SettlementReportProcessor

決済レポートの各レコード（CSV 行）を解析し、エンティティに変換するプロセッサークラスです。

## 実装ファイル

- [SettlementReportProcessor.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/importer/processor/SettlementReportProcessor.java)

## 主な機能

### 1. レコードの処理

- **メソッド:** `process(CSVRecord csvRecord, Map<SettlementId, Settlement> settlementMap)`
- **処理内容:**
    - サマリー行（ヘッダー等）をスキップする。
    - CSV レコードから `Settlement` エンティティを構築する。
    - レコード内の SKU に基づき、`SkuNameService` を介して SKU 名情報を更新または新規作成する。
    - 構築したエンティティを `settlementMap` にマージする（同一キーの場合は金額を加算）。

### 2. エンティティの構築

- **メソッド:** `buildSettlement(CSVRecord csvRecord)`
- **処理内容:** `ReportConstants` に定義されたヘッダー名を使用して、CSV の各列から値を抽出し、`Settlement` オブジェクトにセットする。

## 依存コンポーネント

- `SkuNameService` (Domain Service)
