# PurchaseController - 全仕入れ情報取得

登録されているすべての仕入れ情報を取得するためのエンドポイントです。

## 基本情報

- **URI:** `/api/purchases`
- **メソッド:** `GET`
- **実装メソッド:** `PurchaseController.getAllPurchases`

## 実装ファイル

- [PurchaseController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/PurchaseController.java)

## 処理フロー

1. `PurchaseApplicationService.getAllPurchases()` を呼び出す。
2. 取得した `Purchase` リストを `200 OK` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [PurchaseApplicationService](../../application/services/purchase/detail-PurchaseApplicationService.md)
