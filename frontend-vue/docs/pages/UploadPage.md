# UploadPage.vue - 詳細設計

各種レポートファイルをアップロードするための画面コンポーネントです。

## 基本情報

- **パス:** `frontend-vue/src/pages/UploadPage.vue`
- **役割:** 決済、広告、販売日の各レポートファイルの選択およびバックエンドへの送信。

## 実装ファイル

- [UploadPage.vue](../../src/pages/UploadPage.vue)

## 状態管理 (Reactive State)

| 変数名 | 型 | 説明 |
| :--- | :--- | :--- |
| `selectedFiles` | `reactive<{ [key: string]: FileList \| null }>` | 選択されたファイルをアップロード種別ごとに保持。 |
| `messages` | `reactive<{ [key: string]: string }>` | 成功メッセージを保持。 |
| `errors` | `reactive<{ [key: string]: string }>` | エラーメッセージを保持。 |
| `isLoading` | `reactive<{ [key: string]: boolean }>` | アップロード処理中の状態を保持。 |

## 主な機能

### 1. ファイル選択ハンドリング (`handleFileChange`)

- **引数:** `event: Event`, `uploadType: string`
- **処理内容:**
  - `input[type="file"]` の変更イベントを受け取り、選択されたファイルを `selectedFiles` に格納する。
  - 該当する種別のメッセージとエラーをクリアする。

### 2. アップロード実行 (`handleUpload`)

- **引数:** `uploadType: "payment" | "advertisement" | "salesDate"`
- **処理内容:**
  - 選択されたファイルの存在チェックを行う。
  - `FormData` オブジェクトを作成し、ファイルをセットする。
    - `payment`, `salesDate` は複数ファイル（`files`）に対応。
    - `advertisement` は単一ファイル（`file`）に対応。
  - 種別に応じた API エンドポイントへ `axios.post` を実行する。
    - 決済: `/api/sales/upload`
    - 広告: `/api/sales/upload-advertisement`
    - 販売日: `/api/sales/upload-sales-date`
  - レスポンスメッセージを表示し、エラー時はエラー内容を表示する。

## UI 構成

- **カード形式のレイアウト:**
  - 決済レポートアップロード
  - 広告レポートアップロード
  - 販売日管理レポートアップロード
- **共通要素:**
  - ファイル選択ボタン（隠し `input` を `label` で装飾）
  - 選択済みファイル名の表示
  - アップロード実行ボタン（処理中は無効化）
  - メッセージ/エラー表示エリア

## 依存ライブラリ

- `vue` (ref, reactive)
- `axios`
