# MonthlyRevenueSummaryService

月次収益サマリーの計算および集計を行うドメインサービスです。

## 実装ファイル

- [MonthlyRevenueSummaryService.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/service/MonthlyRevenueSummaryService.java)

## 主な機能

### 1. 月次収益サマリーの計算

- **メソッド:** `getMonthlyRevenueSummary()`
- **処理内容:**
    - 決済、SKU名、広告、仕入れ、販売日の全データを取得する。
    - 仕入れ情報から親 SKU ごとの平均単価を計算する。
    - データを月ごとにグループ化する（販売日がある場合は販売日、ない場合は決済日基準）。
    - 各月について以下の計算を行う：
        - `SalesCalculator` による売上・手数料の計算。
        - `AdvertisementCostCalculator` による広告費の集計。
        - `ProductCostCalculator` による商品原価（平均単価 × 注文数）の計算。
        - `SummaryAggregator` による月次合計の集計。
    - 結果を年月の降順でソートして返却する。

## 依存コンポーネント

- [SettlementRepository](../../infrastructure/repositories/report/detail-SettlementRepository.md) (Repository)
- [CacheableDataService](../../skuname/detail-CacheableDataService.md) (Domain Service)
- [AdvertisementRepository](../../infrastructure/repositories/summary/detail-AdvertisementRepository.md) (Repository)
- [PurchaseRepository](../../infrastructure/repositories/purchase/detail-PurchaseRepository.md) (Repository)
- [SalesDateRepository](../../infrastructure/repositories/summary/detail-SalesDateRepository.md) (Repository)
- [SalesCalculator](detail-SalesCalculator.md) (Calculator)
- [AdvertisementCostCalculator](detail-AdvertisementCostCalculator.md) (Calculator)
- [ProductCostCalculator](detail-ProductCostCalculator.md) (Calculator)
- [SummaryAggregator](detail-SummaryAggregator.md) (Aggregator)
