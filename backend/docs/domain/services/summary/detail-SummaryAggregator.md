# SummaryAggregator

親 SKU ごとの収益データを集計し、月次合計を算出するクラスです。

## 実装ファイル

- [SummaryAggregator.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/domain/summary/SummaryAggregator.java)

## 主な機能

### 1. 月次合計の集計

- **メソッド:** `aggregate(Map<String, ParentSkuRevenueForMonthDto> parentSkuSummaryMap)`
- **処理内容:**
    - 各親 SKU の粗利（売上 + 手数料 + 広告費 + 商品原価）を計算する。
    - 各数値を四捨五入（整数）する。
    - 全親 SKU の数値を合算し、その月の「合計」行データを作成する。

## 依存コンポーネント

- [MessageLocalizationService](../../../application/services/report/detail-MessageLocalizationService.md) (Application Service)
