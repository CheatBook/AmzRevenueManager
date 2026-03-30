# ReportApplicationService

レポートのインポートおよびデータ取得に関するアプリケーションサービスです。
複数のドメインサービスやリポジトリを統合し、ユースケースを実現します。

## 実装ファイル

- [ReportApplicationService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/application/service/ReportApplicationService.java)

## 主な機能

### 1. 広告レポートのインポート

- **メソッド:** `importAdvertisementReport(MultipartFile file)`
- **処理内容:** `AdvertisementImportService` を呼び出して広告データをインポートする。
- **例外:** `IOException`, `SkuNameNotFoundException`

### 2. 決済レポートのインポート

- **メソッド:** `importSettlementReport(MultipartFile file)`
- **処理内容:** `SettlementImportService` を呼び出して決済データをインポートする。
- **戻り値:** インポート結果のメッセージ（警告等を含む）。
- **例外:** `IOException`, `DuplicateSettlementIdException`

### 3. 販売日管理レポートのインポート

- **メソッド:** `importSalesDateReport(MultipartFile file)`
- **処理内容:** `SalesDateService` を呼び出して販売日データを保存する。

### 4. トランザクションデータの取得

- **メソッド:** `getTransactionData()`
- **処理内容:** `SettlementRepository` からトランザクションデータのリストを取得する。
- **戻り値:** `List<TransactionDataDto>`

## 依存コンポーネント

- [AdvertisementImportService](../../domain/services/importer/detail-AdvertisementImportService.md) (Domain Service)
- [SettlementImportService](../../domain/services/importer/detail-SettlementImportService.md) (Domain Service)
- [SalesDateService](../../domain/services/common/detail-SalesDateService.md) (Domain Service)
- [SettlementRepository](../../infrastructure/repositories/report/detail-SettlementRepository.md) (Repository)
