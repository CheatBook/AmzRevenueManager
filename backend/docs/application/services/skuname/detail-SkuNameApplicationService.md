# SkuNameApplicationService

SKU 名および親 SKU の管理に関するアプリケーションサービスです。

## 実装ファイル

- [SkuNameApplicationService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/application/service/SkuNameApplicationService.java)

## 主な機能

### 1. 全 SKU 名の取得

- **メソッド:** `getAllSkuNames()`
- **処理内容:** `SkuNameService` を呼び出して全 SKU 名を取得する。

### 2. SKU 名の保存

- **メソッド:** `saveSkuName(SkuName skuName)`
- **処理内容:** `SkuNameService` を呼び出して SKU 名を保存する。

### 3. 親 SKU の取得

- **メソッド:** `findParentSkus()`
- **処理内容:** `SkuNameService` を呼び出して親 SKU のリストを取得する。

### 4. 決済情報からのユニーク SKU 取得

- **メソッド:** `findDistinctSkusFromSettlements()`
- **処理内容:** `SkuNameService` を呼び出して決済情報からユニークな SKU リストを取得する。

## 依存コンポーネント

- [SkuNameService](../../domain/services/skuname/detail-SkuNameService.md) (Domain Service)
