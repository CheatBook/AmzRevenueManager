# MonthlySummaryApplicationService

月次収益サマリーの取得に関するアプリケーションサービスです。

## 実装ファイル

- [MonthlySummaryApplicationService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/application/service/MonthlySummaryApplicationService.java)

## 主な機能

### 1. 月次収益サマリーの取得

- **メソッド:** `getMonthlyRevenueSummary()`
- **処理内容:** `MonthlyRevenueSummaryService` を呼び出して月次収益サマリーを取得する。
- **戻り値:** `List<ParentSkuMonthlySummaryDto>`

## 依存コンポーネント

- [MonthlyRevenueSummaryService](../../domain/services/summary/detail-MonthlyRevenueSummaryService.md) (Domain Service)
