# TransactionDataController - クラス詳細設計

トランザクション明細データの取得に関するコントローラークラスです。

## エンドポイント一覧

- [トランザクションデータ取得 (GET /api/transaction-data)](./api-TransactionData.md)

## 実装ファイル

- [TransactionDataController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/TransactionDataController.java)

## 共通仕様

### 1. アノテーション

- `@RestController`: RESTful Web サービスのエンドポイントとして定義。
- `@RequestMapping("/api/transaction-data")`: ベースパスを設定。
- `@RequiredArgsConstructor`: Lombok によるコンストラクタ注入。

### 2. 依存サービス

- `ReportApplicationService`: レポート処理のビジネスロジックを担当。

## 関連ドキュメント

- [ReportApplicationService](../../application/services/report/detail-ReportApplicationService.md)
