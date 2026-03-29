# AmzRevenueManager

Amazonの各種レポートをアップロードし、親SKU単位での月次収益を自動集計・管理するシステムです。

## 1. プロジェクト構成

本プロジェクトは、バックエンド（Spring Boot）とフロントエンド（Vue.js / React）で構成されています。

- [backend/](./backend/README.md): Spring Boot 3 + Java 17
- [frontend-vue/](./frontend-vue/README.md): Vue 3 + Vite + TypeScript (推奨)
- [frontend-react/](./frontend-react/README.md): React (既存実装)
- [docs/](./docs/design/architecture.md): システム全体の設計ドキュメント

## 2. ドキュメント一覧

システム全体の共通設計については以下のドキュメントを参照してください。

- [全体アーキテクチャ](./docs/design/architecture.md)
- [API仕様書](./docs/design/api-spec.md)
- [データベース設計](./docs/design/database-design.md)
- [フロントエンド設計](./docs/design/frontend-design.md)

## 3. クイックスタート

### データベースの起動 (Docker)
本システムは MySQL を使用します。以下のコマンドでデータベースコンテナを起動してください。
```bash
docker-compose up -d
```

### バックエンドの起動
```bash
cd backend
./mvnw spring-boot:run
```

### フロントエンド(Vue)の起動
```bash
cd frontend-vue
npm install
npm run dev
```
