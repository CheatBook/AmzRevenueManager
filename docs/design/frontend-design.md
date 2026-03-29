# フロントエンド設計

## 1. 概要
Vue 3 (Composition API) を用いたシングルページアプリケーション (SPA) です。
Viteをビルドツールとして使用し、TypeScriptで型安全な開発を行っています。

## 2. ディレクトリ構造

### 2.1 Vue フロントエンド (frontend-vue)
```text
frontend-vue/
├── src/
│   ├── assets/         # 静的資産（画像、グローバルCSS等）
│   ├── components/     # 共通コンポーネント
│   ├── pages/          # 画面単位のコンポーネント
│   ├── router/         # Vue Routerによるルーティング定義
│   ├── types/          # TypeScriptの型定義
│   ├── App.vue         # ルートコンポーネント
│   ├── main.ts         # エントリーポイント
│   └── style.css       # 共通スタイル
├── index.html          # HTMLテンプレート
└── vite.config.ts      # Vite/プロキシ設定
```

### 2.2 React フロントエンド (frontend-react)
既存の React 実装は `frontend-react` ディレクトリに保持されています。

## 3. 画面一覧とルーティング

| パス | ページコンポーネント | 説明 |
| :--- | :--- | :--- |
| `/` | `UploadPage.vue` | 各種レポートのアップロード画面 |
| `/monthly-summary` | `MonthlySummaryPage.vue` | 親SKU別の月次収益集計画面 |
| `/sku-names` | `SkuNamePage.vue` | SKUと日本語名・親SKUの紐付け管理 |
| `/parent-sku-names` | `ParentSkuPage.vue` | 親SKU自体の登録・管理 |
| `/purchase` | `PurchasePage.vue` | 親SKUごとの仕入れ情報登録 |
| `/transaction-data` | `TransactionDataPage.vue` | インポートされた全データの生リスト表示 |

## 4. コンポーネント設計方針

### 4.1 Composition API (`<script setup>`)
- ロジックとテンプレートを分離しやすくするため、すべてのコンポーネントで `<script setup>` を採用。
- 状態管理は `ref` および `reactive` を使用。

### 4.2 スタイル管理
- `src/style.css` にて CSS Variables (変数) を定義し、テーマカラーやフォントを一括管理。
- 各コンポーネントでは `scoped` スタイルを使用し、スタイルの干渉を防止。
- レスポンシブデザインに対応し、モバイル端末でもテーブルやフォームが崩れないよう調整。

### 4.3 通信 (Axios)
- `vite.config.ts` のプロキシ設定により、`/api` へのリクエストをバックエンドサーバーに転送。
- 非同期処理中のローディング状態 (`isLoading`) を各ページで管理し、ユーザー体験を向上。
