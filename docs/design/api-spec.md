# API仕様書

## 1. 概要
バックエンド（Spring Boot）が提供するREST APIの仕様です。ベースURLは `/api` です。

## 2. エンドポイント一覧

### 2.1 レポートアップロード (`/api/sales`)

| メソッド | パス | 説明 | リクエスト形式 |
| :--- | :--- | :--- | :--- |
| POST | `/upload` | 決済レポートのアップロード | multipart/form-data (files) |
| POST | `/upload-advertisement` | 広告レポートのアップロード | multipart/form-data (file) |
| POST | `/upload-sales-date` | 販売日管理レポートのアップロード | multipart/form-data (files) |

### 2.2 SKU名管理 (`/api/sku-names`)

| メソッド | パス | 説明 | リクエスト/レスポンス |
| :--- | :--- | :--- | :--- |
| GET | `/` | 登録済みSKU名一覧の取得 | JSON Array (SkuName) |
| POST | `/` | SKU名の新規登録/更新 | JSON Object (SkuName) |
| GET | `/distinct-skus` | トランザクション内のユニークなSKU取得 | JSON Array (String) |
| GET | `/parent-skus` | 親SKU一覧の取得 | JSON Array (SkuName) |
| POST | `/parent-sku` | 親SKUの新規登録 | JSON Object (japaneseName) |

### 2.3 仕入れ管理 (`/api/purchases`)

| メソッド | パス | 説明 | リクエスト/レスポンス |
| :--- | :--- | :--- | :--- |
| GET | `/` | 仕入れ情報一覧の取得 | JSON Array (Purchase) |
| POST | `/` | 仕入れ情報の新規登録 | JSON Object (Purchase) |
| PUT | `/{parentSku}/{date}` | 仕入れ情報の更新 | JSON Object (Purchase) |

### 2.4 集計・一覧 (`/api/monthly-summary`, `/api/transaction-data`)

| メソッド | パス | 説明 | レスポンス形式 |
| :--- | :--- | :--- | :--- |
| GET | `/monthly-summary` | 月次親SKU別収益サマリーの取得 | JSON Array (ParentSkuMonthlySummary) |
| GET | `/transaction-data` | トランザクションデータ一覧の取得 | JSON Array (TransactionData) |

## 3. 主要なデータモデル (JSON)

### SkuName
```json
{
  "sku": "string",
  "japaneseName": "string",
  "parentSku": "string (optional)"
}
```

### Purchase
```json
{
  "parentSku": "string",
  "purchaseDate": "YYYY-MM-DD",
  "quantity": number,
  "amount": number,
  "tariff": number,
  "unitPrice": number (readonly)
}
```
