# SalesDateService

売上日管理レポート（TSV等）のインポートおよび保存を担当するドメインサービスです。

## 実装ファイル

- [SalesDateService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/service/SalesDateService.java)

## 主な機能

### 1. 売上日情報の保存

- **メソッド:** `saveSalesDates(MultipartFile file)`
- **処理内容:**
    - ファイルを 1 行ずつ読み込み、タブ区切りで分割する。
    - `Amazon Order ID` と `Purchase Date`（注文日時）を抽出する。
    - 日時文字列（OffsetDateTime形式）を `LocalDateTime` に変換する。
    - `SalesDate` エンティティのリストを作成し、`SalesDateRepository` で一括保存する。
- **トランザクション:** `@Transactional`

## 依存コンポーネント

- [SalesDateRepository](../../infrastructure/repositories/summary/detail-SalesDateRepository.md) (Repository)
