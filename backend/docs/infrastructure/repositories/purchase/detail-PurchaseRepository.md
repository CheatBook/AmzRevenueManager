# PurchaseRepository

仕入れ情報（`Purchase` エンティティ）の永続化を担当するリポジトリです。

## 実装ファイル

- [PurchaseRepository.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/repository/PurchaseRepository.java)

## 主な機能

### 1. 基本的な CRUD 操作

- `JpaRepository` を継承し、標準的な保存・検索機能を提供。

### 2. 期間指定検索

- `findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate)`: 指定された期間内の仕入れデータを取得する。

## 関連エンティティ

- `Purchase`
- `PurchaseId` (複合主キー)
