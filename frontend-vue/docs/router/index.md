# router/index.ts - 詳細設計

アプリケーションのルーティングを定義する設定ファイルです。

## 基本情報

- **パス:** `frontend-vue/src/router/index.ts`
- **役割:** URI パスとページコンポーネントの紐付け。

## 実装ファイル

- [router/index.ts](../../src/router/index.ts)

## ルーティング定義

| パス | コンポーネント | 説明 |
| :--- | :--- | :--- |
| `/` | `UploadPage.vue` | レポートアップロード画面（デフォルト） |
| `/monthly-summary` | `MonthlySummaryPage.vue` | 月次収益サマリー画面 |
| `/sku-names` | `SkuNamePage.vue` | SKU名管理画面 |
| `/parent-sku-names` | `ParentSkuPage.vue` | 親SKU管理画面 |
| `/purchase` | `PurchasePage.vue` | 仕入れ情報管理画面 |
| `/transaction-data` | `TransactionDataPage.vue` | トランザクションデータ一覧画面 |

## 設定内容

- **History Mode:** `createWebHistory()` を使用。
- **ベースライブラリ:** `vue-router`
