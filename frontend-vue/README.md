# Frontend Vue - AmzRevenueManager

Vue 3 を使用したフロントエンドアプリケーションです。

## 1. 技術スタック
- **Framework**: Vue 3 (Composition API)
- **Build Tool**: Vite
- **Language**: TypeScript
- **Router**: Vue Router
- **HTTP Client**: Axios

## 2. ディレクトリ構造
```text
frontend-vue/
├── src/
│   ├── pages/          # 画面コンポーネント
│   ├── router/         # ルーティング定義
│   ├── App.vue         # ルートコンポーネント
│   ├── main.ts         # エントリーポイント
│   └── style.css       # 共通スタイル
├── public/             # 静的ファイル
├── index.html          # HTMLテンプレート
└── vite.config.ts      # Vite設定 (APIプロキシ設定含む)
```

## 3. 開発手順

### セットアップ
```bash
npm install
```

### 開発サーバー起動
```bash
npm run dev
```

### ビルド
```bash
npm run build
```

## 4. 設計詳細
コンポーネントの設計方針やルーティングの詳細は、ルートの [frontend-design.md](../docs/design/frontend-design.md) を参照してください。
