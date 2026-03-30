# AdvertisementImportService

広告レポート（CSV）のインポート処理を担当するドメインサービスです。

## 実装ファイル

- [AdvertisementImportService.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/importer/AdvertisementImportService.java)

## 主な機能

### 1. 広告レポートのインポート

- **メソッド:** `importReport(MultipartFile file)`
- **処理内容:**
    - `AdvertisementReportReader` を使用して CSV ファイルを読み込む。
    - 各レコードを `AdvertisementReportProcessor` で処理し、`Advertisement` エンティティに変換する。
    - 変換されたデータを `AdvertisementRepository` を使用して一括保存する。
- **トランザクション:** 
    - `@Transactional(rollbackFor = SkuNameNotFoundException.class)`
    - エラー（SKU未登録等）が発生した場合はロールバックを行う。

## 依存コンポーネント

- [AdvertisementRepository](../repository/detail-AdvertisementRepository.md) (Repository)
- [AdvertisementReportProcessor](../service/detail-AdvertisementReportProcessor.md) (Processor)
- [AdvertisementReportReader](../service/detail-AdvertisementReportReader.md) (Reader)
