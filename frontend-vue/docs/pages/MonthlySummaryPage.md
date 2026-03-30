# MonthlySummaryPage.vue - 詳細設計

親 SKU ごとの月次収益サマリーを表示する画面コンポーネントです。

## 基本情報

- **パス:** `frontend-vue/src/pages/MonthlySummaryPage.vue`
- **役割:** バックエンドから取得した月次収益データをテーブル形式で表示。

## 実装ファイル

- [MonthlySummaryPage.vue](../../src/pages/MonthlySummaryPage.vue)

## データ構造 (Interfaces)

### ParentSkuRevenueForMonth

個別の親 SKU ごとの収益データ。

- `parentSku`: 親 SKU コード
- `parentSkuJapaneseName`: 親 SKU の日本語名
- `totalSales`: 総売上
- `totalFees`: 手数料・その他費用
- `totalAdCost`: 広告費
- `productCost`: 商品原価
- `grossProfit`: 粗利益
- `orderCount`: 注文数

### ParentSkuMonthlySummary

月単位の集計データ。

- `year`: 年
- `month`: 月
- `parentSkuRevenues`: `ParentSkuRevenueForMonth` のリスト
- `monthlyTotal`: その月の合計値 (`ParentSkuRevenueForMonth`)

## 状態管理 (Reactive State)

| 変数名 | 型 | 説明 |
| :--- | :--- | :--- |
| `summary` | `ref<ParentSkuMonthlySummary[]>` | API から取得した月次サマリーデータのリスト。 |

## 主な機能

### 1. データ取得 (`onMounted`)

- **処理内容:**
  - コンポーネントのマウント時に `/api/monthly-summary` へ GET リクエストを送信。
  - 取得したデータを `summary.value` に格納。

## UI 構成

- **テーブル表示:**
  - `v-for` を使用して年月ごとにグループ化して表示。
  - 各年月の先頭行に `rowspan` を使用して「年月」を表示。
  - 各年月の末尾に「合計」行を表示（太字）。
  - 数値データは `toLocaleString()` を使用してカンマ区切りで表示。

## 依存ライブラリ

- `vue` (ref, onMounted)
- `axios`
