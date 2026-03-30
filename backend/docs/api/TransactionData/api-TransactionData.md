# TransactionDataController - トランザクションデータ取得

決済情報と販売日情報を結合した、詳細なトランザクションリストを取得するためのエンドポイントです。

## 基本情報

- **URI:** `/api/transaction-data`
- **メソッド:** `GET`
- **実装メソッド:** `TransactionDataController.getTransactionData`

## 実装ファイル

- [TransactionDataController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/TransactionDataController.java)

## 処理フロー

1. `ReportApplicationService.getTransactionData()` を呼び出す。
2. 取得した `TransactionDataDto` のリストを返却する。

## 依存サービス

- [ReportApplicationService](../../application/services/report/detail-ReportApplicationService.md)
