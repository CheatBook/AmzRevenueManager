# PurchasePage.vue - 詳細設計

親 SKU ごとの仕入れ情報を登録・管理する画面コンポーネントです。

## 基本情報

- **パス:** `frontend-vue/src/pages/PurchasePage.vue`
- **役割:** 仕入れ日、数量、金額、関税の登録および編集。

## 実装ファイル

- [PurchasePage.vue](../../src/pages/PurchasePage.vue)

## 状態管理 (Reactive State)

| 変数名 | 型 | 説明 |
| :--- | :--- | :--- |
| `parentSkus` | `ref<SkuName[]>` | 選択肢用の親 SKU 一覧。 |
| `purchases` | `ref<Purchase[]>` | 登録済みの仕入れ情報一覧。 |
| `selectedParentSku` | `ref<string>` | フォームで選択された親 SKU。 |
| `purchaseDate` | `ref<string>` | 仕入れ日（デフォルトは当日）。 |
| `quantity` | `ref<number>` | 数量。 |
| `amount` | `ref<number>` | 金額（日本円）。 |
| `tariff` | `ref<number>` | 関税（日本円）。 |
| `editingPurchase` | `ref<Purchase \| null>` | 編集中のデータ（新規登録時は null）。 |

## 主な機能

### 1. 初期データ取得 (`onMounted`)

- `fetchParentSkus()`: 親 SKU リストを取得。
- `fetchPurchases()`: 登録済みの仕入れ一覧を取得。

### 2. 登録・更新 (`handleSubmit`)

- **処理内容:**
  - 必須項目の入力チェック。
  - `editingPurchase` がある場合は PUT (`/api/purchases/{sku}/{date}`)、ない場合は POST (`/api/purchases`) を実行。
  - 成功時、フォームをクリアし一覧を再取得。

### 3. 編集モード切り替え (`handleEdit`)

- 一覧の「編集」ボタン押下時に、選択された行のデータをフォームにセットし `editingPurchase` を更新する。

## UI 構成

- **登録/編集フォーム:**
  - 親 SKU 選択、仕入れ日（Date Picker）、数量、金額、関税の入力。
  - 登録/更新ボタン。
  - 編集キャンセル用のクリアボタン（編集時のみ）。
- **一覧表示:**
  - 仕入れ日、親 SKU 名、数量、金額、関税、計算された単価を表示。
  - 各行に「編集」ボタン。

## 依存ライブラリ

- `vue` (ref, onMounted)
- `axios`
