# クラス詳細設計書: FileUploadController

## 1. 概要
Amazonの各種レポート（決済、広告、販売日管理）のアップロードを受け付け、アプリケーションサービスへ処理を委譲するRESTコントローラー。

- **パッケージ**: `io.github.cheatbook.amzrevenuemanager.interfaces.web`
- **ベースURL**: `/api/sales`
- **主な責務**: 
    - HTTPリクエストの受付とパラメータ（ファイル）のバリデーション
    - サービス層（`ReportApplicationService`）の呼び出し
    - 処理結果に応じたHTTPステータスコードとメッセージの返却

## 2. メソッド詳細

### 2.1 handleFileUpload (決済レポートアップロード)
決済レポート（複数可）を処理します。

- **パス**: `POST /upload`
- **引数**: `List<MultipartFile> files`
- **処理フロー**:
    1. **空チェック**: `files.isEmpty()` の場合、`400 Bad Request` ("ファイルが空です。") を返却。
    2. **ループ処理**: 各ファイルに対して以下の処理を実行。
        - `reportApplicationService.importSettlementReport(file)` を呼び出し。
        - **分岐（警告メッセージ）**: 戻り値（警告）がある場合、ファイル名と共に警告内容を保持。
        - **正常系**: 処理成功メッセージを保持。
    3. **例外ハンドリング**:
        - `DuplicateSettlementIdException`: `409 Conflict` と例外メッセージを返却し、処理を中断。
        - `Exception`: `500 Internal Server Error` とエラーメッセージを返却し、処理を中断。
    4. **レスポンス**: すべてのファイル処理後、警告があれば末尾に付与して `200 OK` を返却。

### 2.2 handleAdvertisementUpload (広告レポートアップロード)
広告レポート（単一）を処理します。

- **パス**: `POST /upload-advertisement`
- **引数**: `MultipartFile file`
- **処理フロー**:
    1. **空チェック**: `file.isEmpty()` の場合、`400 Bad Request` ("ファイルが空です。") を返却。
    2. **サービス呼び出し**: `reportApplicationService.importAdvertisementReport(file)` を実行。
    3. **正常系**: `200 OK` ("広告レポートが正常にアップロードされ、処理されました。") を返却。
    4. **例外ハンドリング**:
        - `SkuNameNotFoundException`: `400 Bad Request` と例外メッセージを返却。
        - `Exception`: `500 Internal Server Error` とエラーメッセージを返却。

### 2.3 handleSalesDateUpload (販売日管理レポートアップロード)
販売日管理レポート（複数可）を処理します。

- **パス**: `POST /upload-sales-date`
- **引数**: `List<MultipartFile> files`
- **処理フロー**:
    1. **空チェック**: `files.isEmpty()` の場合、`400 Bad Request` ("ファイルが空です。") を返却。
    2. **ループ処理**: 各ファイルに対して以下の処理を実行。
        - `reportApplicationService.importSalesDateReport(file)` を呼び出し。
        - **正常系**: 処理成功メッセージを保持。
    3. **例外ハンドリング**:
        - `Exception`: `500 Internal Server Error` とエラーメッセージを返却し、処理を中断。
    4. **レスポンス**: すべてのファイル処理後、`200 OK` と各ファイルの処理結果を返却。

## 3. 呼び出し先サービスの詳細

### ReportApplicationService
コントローラーから呼び出されるアプリケーション層のサービスです。

| メソッド名 | 役割 | コントローラーでの利用箇所 |
| :--- | :--- | :--- |
| `importSettlementReport` | 決済レポートの解析・保存。重複チェックを行い、警告があれば文字列で返す。 | `handleFileUpload` |
| `importAdvertisementReport` | 広告レポートの解析・保存。SKU名の存在チェックを伴う。 | `handleAdvertisementUpload` |
| `importSalesDateReport` | 販売日管理レポートの解析・保存。 | `handleSalesDateUpload` |

## 4. エラーハンドリングとステータスコード

| ステータスコード | 意味 | 発生条件 |
| :--- | :--- | :--- |
| `200 OK` | 成功 | すべてのファイルが正常に処理された場合。 |
| `400 Bad Request` | 不正なリクエスト | ファイルが未選択、または広告レポートでSKU名が見つからない場合。 |
| `409 Conflict` | 競合 | 決済レポートで既にインポート済みの決済IDが含まれている場合。 |
| `500 Internal Server Error` | サーバーエラー | ファイル読み込みエラーや予期せぬ実行時例外が発生した場合。 |
