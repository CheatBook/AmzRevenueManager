# CacheableDataService

頻繁に参照され、更新頻度が低いデータをキャッシュ付きで提供するサービスです。

## 実装ファイル

- [CacheableDataService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/service/CacheableDataService.java)

## 主な機能

### 1. 全 SKU 名の取得

- **メソッド:** `findAllSkuNames()`
- **処理内容:** `SkuNameRepository` からすべての SKU 名を取得する。
- **キャッシュ:** `@Cacheable("skuNames")` により、結果をキャッシュする。

## 依存コンポーネント

- [SkuNameRepository](../../infrastructure/repositories/skuname/detail-SkuNameRepository.md) (Repository)
