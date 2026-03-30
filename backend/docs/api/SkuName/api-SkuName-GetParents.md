# SkuNameController - 親 SKU リスト取得

登録されているすべての親 SKU（`parentSku` が null のもの）を取得するためのエンドポイントです。

## 基本情報

- **URI:** `/api/sku-names/parent-skus`
- **メソッド:** `GET`
- **実装メソッド:** `SkuNameController.getParentSkus`

## 実装ファイル

- [SkuNameController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/SkuNameController.java)

## 処理フロー

1. `SkuNameApplicationService.findParentSkus()` を呼び出す。
2. 取得した親 SKU のリストを `200 OK` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [SkuNameApplicationService](../../application/services/skuname/detail-SkuNameApplicationService.md)
