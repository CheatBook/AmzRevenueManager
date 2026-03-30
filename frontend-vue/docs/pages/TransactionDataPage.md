# TransactionDataPage.vue - 詳細設計

インポートされた全トランザクションデータを一覧表示する画面コンポーネントです。

## 基本情報

- **パス:** `frontend-vue/src/pages/TransactionDataPage.vue`
- **役割:** システムに取り込まれた生データの確認。

## 実装ファイル

- [TransactionDataPage.vue](../../src/pages/TransactionDataPage.vue)

## 状態管理 (Reactive State)

| 変数名 | 型 | 説明 |
| :--- | :--- | :--- |
| `transactionData` | `ref<TransactionData[]>` | API から取得したトランザクションデータのリスト。 |

## 主な機能

### 1. データ取得 (`onMounted`)

- `/api/transaction-data` へ GET リクエストを送信。

## UI 構成

- **テーブル表示:**
  - 登録親番号（Settlement ID）、SKU、分類、個数、購入日、確定日、金額を表示。
  - 大量のデータを表示するため、スクロール可能なコンテナ内に配置。

## 依存ライブラリ

- `vue` (ref, onMounted)
- `axios`
