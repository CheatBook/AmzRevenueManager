# FileUploadController - クラス詳細設計

ファイルアップロードに関するコントローラークラスです。
各エンドポイントの詳細は、以下の機能別ドキュメントを参照してください。

## エンドポイント一覧

- [決済レポートアップロード (POST /api/sales/upload)](./api-FileUpload-Settlement.md)
- [広告レポートアップロード (POST /api/sales/upload-advertisement)](./api-FileUpload-Advertisement.md)
- [販売日管理レポートアップロード (POST /api/sales/upload-sales-date)](./api-FileUpload-SalesDate.md)

## 実装ファイル

- [FileUploadController.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/FileUploadController.java)

## 共通仕様

### 1. アノテーション

- `@RestController`: RESTful Web サービスのエンドポイントとして定義。
- `@RequestMapping("/api/sales")`: ベースパスを設定。
- `@RequiredArgsConstructor`: Lombok によるコンストラクタ注入（`final` フィールド）。
- `@Slf4j`: ロギング（SLF4J）を有効化。
- `@CrossOrigin(origins = "*")`: 全オリジンからのリクエストを許可（開発用）。

### 2. 依存サービス

- `ReportApplicationService`: レポート処理のビジネスロジックを担当。
- `MessageLocalizationService`: 多言語対応メッセージの取得を担当。

## 関連ドキュメント

- [ReportApplicationService](../service/detail-ReportApplicationService.md)
- [MessageLocalizationService](../service/detail-MessageLocalizationService.md)
