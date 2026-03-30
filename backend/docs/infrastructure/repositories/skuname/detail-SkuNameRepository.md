# SkuNameRepository

SKU 名情報（`SkuName` エンティティ）の永続化を担当するリポジトリです。

## 実装ファイル

- [SkuNameRepository.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/repository/SkuNameRepository.java)

## 主な機能

### 1. 基本的な CRUD 操作

- `JpaRepository` を継承し、標準的な保存・検索機能を提供。

### 2. カスタム検索

- `findByParentSkuIsNull()`: 親 SKU が設定されていない（自身が親である）レコードを取得する。
- `findByParentSku(String parentSku)`: 特定の親 SKU に紐付く子 SKU のリストを取得する。
- `findByJapaneseName(String japaneseName)`: 日本語名で検索する。

## 関連エンティティ

- `SkuName`
