# SkuNamePage.vue - 詳細設計

SKU 名と親 SKU の紐付けを管理する画面コンポーネントです。

## 基本情報

- **パス:** `frontend-vue/src/pages/SkuNamePage.vue`
- **役割:** SKU に対する日本語名の登録、および親 SKU への紐付け。

## 実装ファイル

- [SkuNamePage.vue](../../src/pages/SkuNamePage.vue)

## 状態管理 (Reactive State)

| 変数名 | 型 | 説明 |
| :--- | :--- | :--- |
| `skuNames` | `ref<SkuName[]>` | 登録済みの SKU 名一覧。 |
| `selectedSku` | `ref<string>` | フォームで選択された SKU。 |
| `newJapaneseName` | `ref<string>` | 入力された日本語名。 |
| `parentSkus` | `ref<SkuName[]>` | 選択肢として表示する親 SKU 一覧。 |
| `distinctSkus` | `ref<string[]>` | 決済データから抽出された未登録を含むユニークな SKU 一覧。 |
| `selectedParentSku` | `ref<string>` | 選択された親 SKU。 |
| `message` | `ref<string>` | 処理結果メッセージ。 |
| `isLoading` | `ref<boolean>` | 処理中の状態。 |

## 主な機能

### 1. 初期データ取得 (`onMounted`)

- `fetchSkuNames()`: 全 SKU 名一覧を取得。
- `fetchParentSkus()`: 親 SKU リストを取得。
- `fetchDistinctSkus()`: 決済データからユニークな SKU リストを取得（セレクトボックス用）。

### 2. SKU 名保存 (`handleSubmit`)

- **処理内容:**
  - バリデーション（SKU と日本語名の入力チェック）。
  - `/api/sku-names` へ POST リクエストを送信。
  - 成功時、フォームをクリアし一覧を再取得。

## UI 構成

- **登録フォーム:**
  - SKU 選択（`distinctSkus` から選択）
  - 日本語名入力
  - 親 SKU 選択（任意）
  - 保存ボタン
- **一覧表示:**
  - 登録済みの SKU、日本語名、親 SKU（日本語名を表示）をテーブル表示。

## 依存ライブラリ

- `vue` (ref, onMounted)
- `axios`
