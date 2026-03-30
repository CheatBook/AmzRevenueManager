# PurchaseApplicationService

仕入れ情報の管理に関するアプリケーションサービスです。

## 実装ファイル

- [PurchaseApplicationService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/application/service/PurchaseApplicationService.java)

## 主な機能

### 1. 仕入れ情報の保存

- **メソッド:** `savePurchase(PurchaseDto purchaseDto)`
- **処理内容:** `PurchaseService` を呼び出して仕入れ情報を保存する。

### 2. 全仕入れ情報の取得

- **メソッド:** `getAllPurchases()`
- **処理内容:** `PurchaseService` を呼び出して全仕入れ情報を取得する。

### 3. 仕入れ情報の更新

- **メソッド:** `updatePurchase(String parentSku, LocalDate purchaseDate, PurchaseDto purchaseDto)`
- **処理内容:** `PurchaseService` を呼び出して特定の仕入れ情報を更新する。

## 依存コンポーネント

- [PurchaseService](../../domain/services/purchase/detail-PurchaseService.md) (Domain Service)
