# SkuNameService

SKU 名のビジネスロジックおよび永続化を担当するドメインサービスです。

## 実装ファイル

- [SkuNameService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/service/SkuNameService.java)

## 主な機能

### 1. SKU 名の保存

- **メソッド:** `saveSkuName(SkuName skuName)`
- **処理内容:** `SkuNameRepository` を使用して保存する。
- **トランザクション:** `@Transactional`

### 2. 親 SKU の作成

- **メソッド:** `createParentSkuName(String japaneseName)`
- **処理内容:**
    - `PARENT_` + UUID 形式でユニークな SKU を自動生成する。
    - `parentSku` フィールドを null に設定し、親 SKU として保存する。
- **トランザクション:** `@Transactional`

### 3. 特殊な検索

- `findParentSkus()`: `parentSku` が null のレコードを検索する。
- `findDistinctSkusFromSettlements()`: `SettlementRepository` を使用して決済データから SKU を抽出する。

## 依存コンポーネント

- [SkuNameRepository](../../infrastructure/repositories/skuname/detail-SkuNameRepository.md) (Repository)
- [SettlementRepository](../../infrastructure/repositories/report/detail-SettlementRepository.md) (Repository)
