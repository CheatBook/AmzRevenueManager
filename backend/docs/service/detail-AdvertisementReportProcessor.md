# AdvertisementReportProcessor

広告レポートの各レコード（CSV 行）を解析し、エンティティに変換・集計するプロセッサークラスです。

## 実装ファイル

- [AdvertisementReportProcessor.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/importer/processor/AdvertisementReportProcessor.java)

## 主な機能

### 1. レコードの処理

- **メソッド:** `process(CSVRecord csvRecord, Map<String, Advertisement> advertisementMap)`
- **処理内容:**
    - CSV レコードから日付、キャンペーン名、広告費を抽出する。
    - キャンペーン名に基づき、`SkuNameRepository` から対応する SKU を取得する。
    - 同一の親 SKU および日付のデータが `advertisementMap` または DB（`AdvertisementRepository`）に存在するか確認する。
    - 既存データがある場合は広告費を加算し、ない場合は新規エンティティを作成してマップに追加する。

### 2. エラーハンドリング

- 日付のパースに失敗した場合は、エラーログを記録してその行の処理をスキップする。
- キャンペーン名に対応する SKU が見つからない場合は、`SkuNameNotFoundException` をスローする。

## 依存コンポーネント

- `SkuNameRepository` (Repository)
- `AdvertisementRepository` (Repository)
- `MessageLocalizationService` (Application Service)
