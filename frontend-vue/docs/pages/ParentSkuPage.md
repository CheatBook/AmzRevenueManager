# ParentSkuPage.vue - 詳細設計

親 SKU 自体を登録・管理するための画面コンポーネントです。

## 基本情報

- **パス:** `frontend-vue/src/pages/ParentSkuPage.vue`
- **役割:** 新しい親 SKU（管理上のグループ単位）の作成。

## 実装ファイル

- [ParentSkuPage.vue](../../src/pages/ParentSkuPage.vue)

## 状態管理 (Reactive State)

| 変数名 | 型 | 説明 |
| :--- | :--- | :--- |
| `newJapaneseName` | `ref<string>` | 入力された親 SKU の日本語名。 |
| `parentSkus` | `ref<SkuName[]>` | 登録済みの親 SKU 一覧。 |
| `message` | `ref<string>` | 処理結果メッセージ。 |
| `isLoading` | `ref<boolean>` | 処理中の状態。 |

## 主な機能

### 1. 親 SKU 一覧取得 (`fetchParentSkus`)

- `/api/sku-names/parent-skus` からデータを取得。

### 2. 親 SKU 登録 (`handleSubmit`)

- **処理内容:**
  - 日本語名の入力チェック。
  - `/api/sku-names/parent-sku` へ POST リクエストを送信。
  - 成功時、一覧を更新。

## UI 構成

- **登録フォーム:**
  - 日本語名入力フィールド。
  - 保存ボタン。
- **一覧表示:**
  - 登録済みの親 SKU（自動生成された SKU コードと日本語名）を表示。

## 依存ライブラリ

- `vue` (ref, onMounted)
- `axios`
