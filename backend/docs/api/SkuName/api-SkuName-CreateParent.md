# SkuNameController - 親 SKU 作成

新しい親 SKU を作成するためのエンドポイントです。

## 基本情報

- **URI:** `/api/sku-names/parent-sku`
- **メソッド:** `POST`
- **実装メソッド:** `SkuNameController.createParentSkuName`

## 実装ファイル

- [SkuNameController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/SkuNameController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `parentSkuRequest` | `SkuName` | ○ | 作成する親 SKU の情報。主に `japaneseName` が使用される。 |

## 処理フロー

1. `SkuNameApplicationService.saveSkuName(parentSku)` を呼び出す（内部で親 SKU として構築）。
2. 正常終了時、作成された `SkuName` エンティティを `201 Created` で返却する。
3. 例外発生時は `500 Internal Server Error` を返却し、エラーログを記録する。

## 依存サービス

- [SkuNameApplicationService](../../application/services/skuname/detail-SkuNameApplicationService.md)
