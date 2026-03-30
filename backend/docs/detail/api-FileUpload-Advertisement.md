# FileUploadController - 広告レポートアップロード

Amazon 広告レポート（CSV）をアップロードし、システムにインポートするためのエンドポイントです。

## 基本情報

- **URI:** `/api/sales/upload-advertisement`
- **メソッド:** `POST`
- **Content-Type:** `multipart/form-data`
- **実装メソッド:** `FileUploadController.handleAdvertisementUpload`

## 実装ファイル

- [FileUploadController.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/interfaces/web/FileUploadController.java)

## リクエストパラメータ

| パラメータ名 | 型 | 必須 | 説明 |
| :--- | :--- | :--- | :--- |
| `file` | `MultipartFile` | ○ | アップロードする広告レポートファイル（単一）。 |

## 処理フロー

1. **バリデーション:**
    - ファイルが空の場合は、`400 Bad Request` を返却する。
    - メッセージ: `error.file_empty` (多言語対応)
2. **インポート実行:**
    - `ReportApplicationService.importAdvertisementReport(file)` を呼び出す。
3. **例外ハンドリング:**
    - `SkuNameNotFoundException`: キャンペーン名に対応する SKU が登録されていない場合、`400 Bad Request` を返却する。
    - `Exception` (その他): `500 Internal Server Error` を返却し、エラーログを記録する。
4. **レスポンス生成:**
    - 正常終了時、成功メッセージ（`upload.advertisement.success`）を `200 OK` で返却する。

## 依存サービス

- [ReportApplicationService](../service/detail-ReportApplicationService.md)
- [MessageLocalizationService](../service/detail-MessageLocalizationService.md)
