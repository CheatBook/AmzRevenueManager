# AdvertisementCostCalculator

月次サマリーにおいて、広告費の計算および集計を担当するクラスです。

## 実装ファイル

- [AdvertisementCostCalculator.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/summary/calculator/AdvertisementCostCalculator.java)

## 主な機能

### 1. 広告費の計算

- **メソッド:** `calculate(List<Advertisement> advertisements, Map<String, ParentSkuRevenueForMonthDto> parentSkuSummaryMap, Map<String, String> parentSkuToJapaneseNameMap)`
- **処理内容:**
    - 広告リストをループし、親 SKU ごとに広告費を `parentSkuSummaryMap` に加算する。
    - マップに対象の親 SKU が存在しない場合は、新規に空のサマリーを作成する。
