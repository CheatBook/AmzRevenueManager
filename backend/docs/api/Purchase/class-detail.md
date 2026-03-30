# PurchaseController - クラス詳細設計

仕入れ情報の管理に関するコントローラークラスです。

## エンドポイント一覧

- [仕入れ情報保存 (POST /api/purchases)](./api-Purchase-Save.md)
- [全仕入れ情報取得 (GET /api/purchases)](./api-Purchase-GetAll.md)
- [仕入れ情報更新 (PUT /api/purchases/{parentSku}/{purchaseDate})](./api-Purchase-Update.md)

## 実装ファイル

- [PurchaseController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/PurchaseController.java)

## 共通仕様

### 1. アノテーション

- `@RestController`: RESTful Web サービスのエンドポイントとして定義。
- `@RequestMapping("/api/purchases")`: ベースパスを設定。
- `@RequiredArgsConstructor`: Lombok によるコンストラクタ注入。
- `@Slf4j`: ロギングを有効化。
- `@CrossOrigin(origins = "*")`: 全オリジンからのリクエストを許可。

### 2. 依存サービス

- `PurchaseApplicationService`: 仕入れ処理のビジネスロジックを担当。
- `MessageLocalizationService`: 多言語対応メッセージの取得を担当。

## 関連ドキュメント

- [PurchaseApplicationService](../../application/services/purchase/detail-PurchaseApplicationService.md)
- [MessageLocalizationService](../../application/services/report/detail-MessageLocalizationService.md)
