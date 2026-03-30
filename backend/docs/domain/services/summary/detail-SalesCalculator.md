# SalesCalculator

月次サマリーにおいて、売上および手数料の計算を担当するクラスです。

## 実装ファイル

- [SalesCalculator.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/summary/calculator/SalesCalculator.java)

## 主な機能

### 1. 売上の計算

- **メソッド:** `calculate(List<Settlement> settlements, List<SkuName> skuNames, Map<String, String> parentSkuToJapaneseNameMap)`
- **処理内容:**
    - 決済リストをループし、各決済の金額を売上または手数料に振り分ける。
    - `AmountDescription`（Principal, Tax, Shipping 等）に基づき、売上として集計するか手数料として集計するかを判定する。
    - 親 SKU 単位で注文数（Order ID のユニーク数）をカウントする。
    - トランザクションタイプが `Other` の場合は「その他」として集計する。

## 依存コンポーネント

- [MessageLocalizationService](../../../application/services/report/detail-MessageLocalizationService.md) (Application Service)
