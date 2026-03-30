# MonthlyRevenueSummaryController - 月次収益サマリー取得

親 SKU ごとの月次収益サマリー（売上、手数料、広告費、原価、粗利等）を取得するためのエンドポイントです。

## 基本情報

- **URI:** `/api/monthly-summary`
- **メソッド:** `GET`
- **実装メソッド:** `MonthlyRevenueSummaryController.getMonthlyRevenueSummary`

## 実装ファイル

- [MonthlyRevenueSummaryController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/MonthlyRevenueSummaryController.java)

## 処理フロー

1. `MonthlySummaryApplicationService.getMonthlyRevenueSummary()` を呼び出す。
2. 取得した `ParentSkuMonthlySummaryDto` のリストを返却する。

## 依存サービス

- [MonthlySummaryApplicationService](../../application/services/summary/detail-MonthlySummaryApplicationService.md)
