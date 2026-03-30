# SkuNameController - 全 SKU 名取得

登録されているすべての SKU 名情報を取得するためのエンドポイントです。

## 基本情報

- **URI:** `/api/sku-names`
- **メソッド:** `GET`
- **実装メソッド:** `SkuNameController.getAllSkuNames`

## 実装ファイル

- [SkuNameController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/SkuNameController.java)

## 処理フロー

1. `SkuNameApplicationService.getAllSkuNames()` を呼び出す。
2. 取得した `SkuName` リストを `200 OK` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [SkuNameApplicationService](../../application/services/skuname/detail-SkuNameApplicationService.md)
