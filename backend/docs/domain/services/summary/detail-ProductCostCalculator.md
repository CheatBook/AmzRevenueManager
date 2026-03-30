# ProductCostCalculator

月次サマリーにおいて、商品原価の計算を担当するクラスです。

## 実装ファイル

- [ProductCostCalculator.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/summary/calculator/ProductCostCalculator.java)

## 主な機能

### 1. 商品原価の計算

- **メソッド:** `calculate(Map<String, ParentSkuRevenueForMonthDto> parentSkuSummaryMap, Map<String, Double> averageUnitPriceByParentSku)`
- **処理内容:**
    - 親 SKU ごとのサマリーマップをループする。
    - 平均単価マップから該当する親 SKU の単価を取得する。
    - `平均単価 × 注文数` を計算し、商品原価としてサマリーにセットする。
