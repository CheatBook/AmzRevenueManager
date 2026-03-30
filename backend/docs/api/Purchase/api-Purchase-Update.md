# PurchaseController - 仕入れ情報更新

既存の仕入れ情報を更新するためのエンドポイントです。

## 基本情報

- **URI:** `/api/purchases/{parentSku}/{purchaseDate}`
- **メソッド:** `PUT`
- **実装メソッド:** `PurchaseController.updatePurchase`

## 実装ファイル

- [PurchaseController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/PurchaseController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `parentSku` | `String` | ○ | パス変数。更新対象の親 SKU。 |
| `purchaseDate` | `LocalDate` | ○ | パス変数。更新対象の仕入れ日（ISO 形式: YYYY-MM-DD）。 |
| `purchaseDto` | `PurchaseDto` | ○ | 更新後の仕入れ情報（JSON）。 |

## 処理フロー

1. `PurchaseApplicationService.updatePurchase(parentSku, purchaseDate, purchaseDto)` を呼び出す。
2. 正常終了時、更新された `Purchase` エンティティを `200 OK` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [PurchaseApplicationService](../../application/services/purchase/detail-PurchaseApplicationService.md)
