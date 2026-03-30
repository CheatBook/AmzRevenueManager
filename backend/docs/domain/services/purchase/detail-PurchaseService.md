# PurchaseService

仕入れ情報のビジネスロジックおよび永続化を担当するドメインサービスです。

## 実装ファイル

- [PurchaseService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/service/PurchaseService.java)

## 主な機能

### 1. 仕入れ情報の保存

- **メソッド:** `savePurchase(PurchaseDto purchaseDto)`
- **処理内容:**
    - `PurchaseDto` から `Purchase` エンティティを作成する。
    - `(金額 + 関税) / 数量` により平均単価（`unitPrice`）を計算する。
    - `PurchaseRepository` を使用して保存する。
- **トランザクション:** `@Transactional`

### 2. 仕入れ情報の更新

- **メソッド:** `updatePurchase(String parentSku, LocalDate purchaseDate, PurchaseDto purchaseDto)`
- **処理内容:**
    - `parentSku` と `purchaseDate` をキーに既存データを取得する。
    - 数量、金額、関税を更新し、平均単価を再計算する。
    - `PurchaseRepository` を使用して保存する。
- **トランザクション:** `@Transactional`

## 依存コンポーネント

- [PurchaseRepository](../../infrastructure/repositories/purchase/detail-PurchaseRepository.md) (Repository)
