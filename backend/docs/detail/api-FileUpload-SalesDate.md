# FileUploadController - 販売日管理レポートアップロード

販売日管理レポート（CSV）をアップロードし、システムにインポートするためのエンドポイントです。

## 基本情報

- **URI:** `/api/sales/upload-sales-date`
- **メソッド:** `POST`
- **Content-Type:** `multipart/form-data`
- **実装メソッド:** `FileUploadController.handleSalesDateUpload`

## 実装ファイル

- [FileUploadController.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/FileUploadController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `files` | `List<MultipartFile>` | ○ | アップロードする販売日管理レポートファイルのリスト。 |

## 処理フロー

1. **バリデーション:**
    - ファイルリストが空の場合は、`400 Bad Request` を返却する。
    - メッセージ: `error.file_empty` (多言語対応)
2. **ファイル処理ループ:**
    - 各ファイルに対して `ReportApplicationService.importSalesDateReport(file)` を呼び出す。
3. **例外ハンドリング:**
    - `Exception`: `500 Internal Server Error` を返却し、エラーログを記録する。
4. **レスポンス生成:**
    - 正常終了時、各ファイルの処理結果メッセージを結合して `200 OK` で返却する。

## 依存サービス

- [ReportApplicationService](../service/detail-ReportApplicationService.md)
- [MessageLocalizationService](../service/detail-MessageLocalizationService.md)
