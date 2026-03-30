# SettlementImportService

決済レポート（CSV）のインポート処理を担当するドメインサービスです。

## 実装ファイル

- [SettlementImportService.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/importer/SettlementImportService.java)

## 主な機能

### 1. 決済レポートのインポート

- **メソッド:** `importReport(MultipartFile file)`
- **処理内容:**
    - `SettlementReportReader` を使用して CSV ファイルを読み込む。
    - `Settlement ID` の重複チェックを `SettlementRepository` で行う。
    - サマリー情報（Settlement ID, 期間, 通貨, 合計金額）を `SettlementReportService` で保存する。
    - 各明細レコードを `SettlementReportProcessor` で処理し、`Settlement` エンティティに変換する。
    - 変換された明細データを `SettlementRepository` で一括保存する。
    - ファイル内の合計金額と、計算された明細の合計金額を検証する。
- **戻り値:** 合計金額が一致しない場合の警告メッセージ（一致時は null）。
- **トランザクション:** `@Transactional(rollbackFor = DuplicateSettlementIdException.class)`

## 依存コンポーネント

- [SettlementRepository](../repository/detail-SettlementRepository.md) (Repository)
- [SettlementReportService](../service/detail-SettlementReportService.md) (Domain Service)
- [SettlementReportProcessor](../service/detail-SettlementReportProcessor.md) (Processor)
- [MessageLocalizationService](../service/detail-MessageLocalizationService.md) (Application Service)
