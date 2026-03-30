# SkuNameController - 決済情報からのユニーク SKU 取得

インポート済みの決済情報に含まれる、重複しない SKU のリストを取得するためのエンドポイントです。

## 基本情報

- **URI:** `/api/sku-names/distinct-skus`
- **メソッド:** `GET`
- **実装メソッド:** `SkuNameController.getDistinctSkusFromSettlements`

## 実装ファイル

- [SkuNameController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/SkuNameController.java)

## 処理フロー

1. `SkuNameApplicationService.findDistinctSkusFromSettlements()` を呼び出す。
2. 取得した SKU 文字列のリストを `200 OK` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [SkuNameApplicationService](../../application/services/skuname/detail-SkuNameApplicationService.md)
