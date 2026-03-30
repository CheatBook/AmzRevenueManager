# SkuNameController - クラス詳細設計

SKU 名および親 SKU の管理に関するコントローラークラスです。

## エンドポイント一覧

- [全 SKU 名取得 (GET /api/sku-names)](./api-SkuName-GetAll.md)
- [決済情報からのユニーク SKU 取得 (GET /api/sku-names/distinct-skus)](./api-SkuName-GetDistinct.md)
- [SKU 名保存 (POST /api/sku-names)](./api-SkuName-Save.md)
- [親 SKU 作成 (POST /api/sku-names/parent-sku)](./api-SkuName-CreateParent.md)
- [親 SKU リスト取得 (GET /api/sku-names/parent-skus)](./api-SkuName-GetParents.md)

## 実装ファイル

- [SkuNameController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/SkuNameController.java)

## 共通仕様

### 1. アノテーション

- `@RestController`: RESTful Web サービスのエンドポイントとして定義。
- `@RequestMapping("/api/sku-names")`: ベースパスを設定。
- `@RequiredArgsConstructor`: Lombok によるコンストラクタ注入。
- `@Slf4j`: ロギングを有効化。
- `@CrossOrigin(origins = "*")`: 全オリジンからのリクエストを許可。

### 2. 依存サービス

- `SkuNameApplicationService`: SKU 名管理のビジネスロジックを担当。
- `MessageLocalizationService`: 多言語対応メッセージの取得を担当。

## 関連ドキュメント

- [SkuNameApplicationService](../../application/services/skuname/detail-SkuNameApplicationService.md)
- [MessageLocalizationService](../../application/services/report/detail-MessageLocalizationService.md)
