# PurchaseController - 仕入れ情報保存

新しい仕入れ情報をシステムに登録するためのエンドポイントです。

## 基本情報

- **URI:** `/api/purchases`
- **メソッド:** `POST`
- **実装メソッド:** `PurchaseController.savePurchase`

## 実装ファイル

- [PurchaseController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/PurchaseController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `purchaseDto` | `PurchaseDto` | ○ | 保存する仕入れ情報（JSON）。親 SKU、仕入れ日、数量、金額、関税等を含む。 |

## 処理フロー

1. `PurchaseApplicationService.savePurchase(purchaseDto)` を呼び出す。
2. 正常終了時、保存された `Purchase` エンティティを `201 Created` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [PurchaseApplicationService](../../application/services/purchase/detail-PurchaseApplicationService.md)
