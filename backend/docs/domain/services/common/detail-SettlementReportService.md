# SettlementReportService

決済レポート（`SettlementReport` エンティティ）の保存処理を担当するドメインサービスです。

## 実装ファイル

- [SettlementReportService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/service/SettlementReportService.java)

## 主な機能

### 1. 決済レポートの保存

- **メソッド:** `save(SettlementReport settlementReport)`
- **処理内容:** `SettlementReportRepository` を使用して、決済レポートのサマリー情報（ID、期間、合計金額等）を保存する。
- **トランザクション:** `@Transactional`

## 依存コンポーネント

- [SettlementReportRepository](../../infrastructure/repositories/report/detail-SettlementReportRepository.md) (Repository)
