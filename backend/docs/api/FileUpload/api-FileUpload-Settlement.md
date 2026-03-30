# FileUploadController - 決済レポートアップロード

決済レポート（CSV）をアップロードし、システムにインポートするためのエンドポイントです。

## 基本情報

- **URI:** `/api/sales/upload`
- **メソッド:** `POST`
- **Content-Type:** `multipart/form-data`
- **実装メソッド:** `FileUploadController.handleFileUpload`

## 実装ファイル

- [FileUploadController.java](../../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/FileUploadController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `files` | `List<MultipartFile>` | ○ | アップロードする決済レポートファイルのリスト。複数ファイルの一括アップロードに対応。 |

## 処理フロー

1. **バリデーション:**
    - ファイルリストが空の場合は、`400 Bad Request` を返却する。
    - メッセージ: `error.file_empty` (多言語対応)
2. **ファイル処理ループ:**
    - 各ファイルに対して `ReportApplicationService.importSettlementReport(file)` を呼び出す。
    - 正常終了した場合、成功メッセージをビルドする。
    - 警告メッセージ（合計金額の不一致など）がある場合は、警告リストに追加する。
3. **例外ハンドリング:**
    - `DuplicateSettlementIdException`: `409 Conflict` を返却し、処理を中断する。
    - `Exception` (その他): `500 Internal Server Error` を返却し、エラーログを記録する。
4. **レスポンス生成:**
    - 全ファイルの処理結果（成功メッセージ + 警告がある場合は警告内容）を結合して `200 OK` で返却する。

## 依存サービス

- [ReportApplicationService](../../application/services/report/detail-ReportApplicationService.md)
- [MessageLocalizationService](../../application/services/report/detail-MessageLocalizationService.md)

## 備考

- 開発環境では CORS を全許可（`@CrossOrigin(origins = "*")`）しているが、本番環境では制限が必要。
- 決済レポートの重複チェックは Settlement ID 単位で行われる。
