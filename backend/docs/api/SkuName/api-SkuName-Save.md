# SkuNameController - SKU 名保存

SKU 名情報（SKU と日本語名の紐付け、親 SKU の設定等）を保存するためのエンドポイントです。

## 基本情報

- **URI:** `/api/sku-names`
- **メソッド:** `POST`
- **実装メソッド:** `SkuNameController.saveSkuName`

## 実装ファイル

- [SkuNameController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/SkuNameController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `skuName` | `SkuName` | ○ | 保存する SKU 名エンティティ（JSON）。 |

## 処理フロー

1. `SkuNameApplicationService.saveSkuName(skuName)` を呼び出す。
2. 正常終了時、保存された `SkuName` エンティティを `201 Created` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [SkuNameApplicationService](../../application/services/skuname/detail-SkuNameApplicationService.md)
