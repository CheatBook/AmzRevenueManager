# AdvertisementRepository

広告情報（`Advertisement` エンティティ）の永続化を担当するリポジトリです。

## 実装ファイル

- [AdvertisementRepository.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/repository/AdvertisementRepository.java)

## 主な機能

### 1. 基本的な CRUD 操作

- `JpaRepository` を継承し、標準的な保存・検索機能を提供。

### 2. 期間指定検索

- `findByIdDateBetween(LocalDate startDate, LocalDate endDate)`: 指定された期間内の広告データを取得する。

## 関連エンティティ

- `Advertisement`
- `AdvertisementId` (複合主キー)
