# MonthlyRevenueSummaryController - クラス詳細設計

月次収益サマリーの取得に関するコントローラークラスです。

## エンドポイント一覧

- [月次収益サマリー取得 (GET /api/monthly-summary)](./api-MonthlyRevenueSummary.md)

## 実装ファイル

- [MonthlyRevenueSummaryController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/MonthlyRevenueSummaryController.java)

## 共通仕様

### 1. アノテーション

- `@RestController`: RESTful Web サービスのエンドポイントとして定義。
- `@RequestMapping("/api/monthly-summary")`: ベースパスを設定。
- `@RequiredArgsConstructor`: Lombok によるコンストラクタ注入。

### 2. 依存サービス

- `MonthlySummaryApplicationService`: 月次サマリー処理のビジネスロジックを担当。

## 関連ドキュメント

- [MonthlySummaryApplicationService](../../application/services/summary/detail-MonthlySummaryApplicationService.md)
