# MessageLocalizationService

システム内のメッセージを多言語対応（i18n）させるためのサービスです。
ブラウザのロケール設定に基づき、適切な言語のメッセージを返却します。

## 実装ファイル

- [MessageLocalizationService.java](../../src/main/java/io/github/cheatbook/amzrevenuemanager/application/service/MessageLocalizationService.java)

## 主な機能

### 1. メッセージの取得（引数なし）

- **メソッド:** `getMessage(String key)`
- **処理内容:** 指定されたキーに対応するメッセージを現在のロケールで取得する。

### 2. メッセージの取得（引数あり）

- **メソッド:** `getMessage(String key, Object[] args)`
- **処理内容:** プレースホルダ（{0}, {1}等）を含むメッセージに引数を埋め込んで取得する。

## 実装詳細

- `org.springframework.context.MessageSource` を利用。
- `LocaleContextHolder.getLocale()` により、リクエストごとのロケールを自動判別。
- デフォルトロケールは `I18nConfig` で日本語（`Locale.JAPANESE`）に設定。

## 関連ファイル

- `src/main/resources/messages.properties` (日本語)
- `src/main/resources/messages_en.properties` (英語)
