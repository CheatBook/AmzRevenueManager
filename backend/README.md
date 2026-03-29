# Backend - AmzRevenueManager

Spring Boot 3 を使用したバックエンドアプリケーションです。

## 1. 技術スタック
- **Java**: 17
- **Framework**: Spring Boot 3.5.4
- **Build Tool**: Maven
- **Database**: MySQL (Connector/J), H2 (Development)
- **Library**: Lombok, Commons CSV, OpenCSV, Apache POI

## 2. ディレクトリ構造
```text
backend/
├── src/
│   ├── main/
│   │   ├── java/io/github/cheatbook/amzrevenuemanager/
│   │   │   ├── application/    # アプリケーション層 (Service)
│   │   │   ├── domain/         # ドメイン層 (Entity, Service, Repository)
│   │   │   ├── interfaces/     # インターフェース層 (Controller, DTO)
│   │   │   └── AmzrevenuemanagerApplication.java
│   │   └── resources/          # 設定ファイル (application.properties)
│   └── test/                   # テストコード
└── pom.xml                     # Maven設定
```

## 3. 開発手順

### ビルド
```bash
./mvnw clean compile
```

### テスト実行
```bash
./mvnw test
```

### 実行
```bash
./mvnw spring-boot:run
```

## 4. 設計詳細
クラス単位の詳細設計については以下のドキュメントを参照してください。
- [FileUploadController 詳細設計](./docs/detail/class-detail-FileUploadController.md)
