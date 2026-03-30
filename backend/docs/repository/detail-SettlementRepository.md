# SettlementRepository

決済情報（`Settlement` エンティティ）の永続化および集計クエリを担当するリポジトリです。

## 実装ファイル

- [SettlementRepository.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/repository/SettlementRepository.java)

## 主な機能

### 1. 基本的な CRUD 操作

- `JpaRepository` を継承し、標準的な保存・検索機能を提供。

### 2. 重複チェック

- `existsBySettlementId(String settlementId)`: 指定された決済 ID が既に存在するか確認。

### 3. 集計・抽出クエリ

- `findDistinctSkus()`: 登録されている全 SKU の重複なしリストを取得。
- `findDistinctMonths()`: 決済が発生した月の重複なしリストを取得。
- `findParentSkuSummaryData()`: 親 SKU ごとの売上、手数料、配送料、税金、数量を集計。
- `findTransactionData()`: `Settlement` と `SalesDate` を結合し、トランザクション一覧データを取得。

## 関連エンティティ

- `Settlement`
- `SettlementId` (複合主キー)
