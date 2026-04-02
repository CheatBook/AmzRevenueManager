# AWS Lambda + TypeScript 完全ガイド (構築・開発・デプロイ)

AWS Lambda を TypeScript で開発するために必要な、環境構築からプロジェクトの初期化、実務的なデプロイ方法までの一貫した解説。

---

## 1. ローカル開発ツールのインストール

開発に必要な基本ツールのインストール。

### ① Node.js のインストール
- **役割**: Lambda (JavaScript/TypeScript) の実行環境。
- **手順**: [Node.js 公式サイト](https://nodejs.org/)から LTS（推奨版）をダウンロードしてインストール。
- **確認**: ターミナルで `node -v` を実行し、バージョンが表示されれば OK。

### ② AWS CLI のインストール
- **役割**: ローカル PC から AWS を操作するためのツール。
- **手順**: [AWS CLI MSI インストーラー](https://awscli.amazonaws.com/AWSCLIV2.msi)を実行。
- **確認**: ターミナルを**開き直し**、`aws --version` を実行して確認。

---

## 2. AWS アカウントとの紐付け（認証設定）

ローカル PC から AWS を操作できるように、IAM ユーザーの認証情報を設定。

### ① IAM アクセスキーの取得
1. AWS コンソールの **IAM** サービスに移動。
2. 「ユーザー」から対象のユーザーを選択し、**「セキュリティ認証情報」** タブを開く。
3. 「アクセスキー」セクションで **「アクセスキーを作成」** をクリック。
4. **「コマンドラインインターフェイス (CLI)」** を選択し、確認のチェックを入れて次へ。
5. 作成完了画面で以下の 2 つを必ず保存（CSV のダウンロードを推奨）。
   - **アクセスキー ID**
   - **シークレットアクセスキー**

### ② 認証情報の設定
ターミナルで以下のコマンドを実行し、取得した情報を入力。
```powershell
aws configure
```
- **AWS Access Key ID**: 保存した ID を入力
- **AWS Secret Access Key**: 保存したシークレットキーを入力
- **Default region name**: `ap-northeast-1` (東京リージョン)
- **Default output format**: `json`

---

## 3. プロジェクトの初期化 (TypeScript)

開発プロジェクトを作成し、ESM (ECMAScript Modules) 形式で設定。

### ① プロジェクト作成と ESM 有効化
```bash
mkdir my-lambda-project
cd my-lambda-project
npm init -y
```
- **重要**: `package.json` を開き、`"type": "module"` を追記（または `commonjs` から変更）。

### ② 必要なパッケージのインストール
```bash
# TypeScript 本体と Lambda 用の型定義
npm install --save-dev typescript @types/node @types/aws-lambda

# SAM ビルド (esbuild) に必要なパッケージ
npm install --save-dev esbuild

# AWS SDK (例: S3 を使う場合)
npm install @aws-sdk/client-s3
```

### ③ TypeScript の設定 (`tsconfig.json`)
```bash
npx tsc --init
```
- `tsconfig.json` 内の `verbatimModuleSyntax` を `true` に設定することを推奨。

---

## 4. 最初の Lambda ハンドラー作成

`index.ts` を作成し、以下のコードを記述。

```typescript
import type { Context, APIGatewayProxyResult, APIGatewayProxyEvent } from 'aws-lambda';

export const handler = async (event: APIGatewayProxyEvent, context: Context): Promise<APIGatewayProxyResult> => {
  console.log(`Event: ${JSON.stringify(event, null, 2)}`);
  
  return {
    statusCode: 200,
    body: JSON.stringify({ message: 'Hello from Lambda!' }),
  };
};
```

> **注意**: `verbatimModuleSyntax: true` の場合、型のみのインポートには必ず `import type` を使用。

---

## 5. デプロイ戦略 (SAM & CDK)

実務で一般的に利用されるデプロイ方法。利用には事前にツールのインストールが必要。

### ツールのインストール手順

#### A. AWS SAM CLI のインストール
1. **インストーラーのダウンロード**: [AWS SAM CLI 公式ページ](https://docs.aws.amazon.com/ja_jp/serverless-application-model/latest/developerguide/install-sam-cli.html)から Windows 用の MSI インストーラーをダウンロード。
2. **インストール**: インストーラーを実行し、完了。
3. **確認**: ターミナルを**開き直し**、`sam --version` を実行してバージョンが表示されれば OK。

#### B. AWS CDK のインストール
CDK は Node.js のパッケージとしてインストール。
1. **インストール**: ターミナルで以下のコマンドを実行。
   ```bash
   npm install -g aws-cdk
   ```
2. **確認**: `cdk --version` を実行してバージョンが表示されれば OK。

---

### 各ツールのデプロイ方法

#### A. AWS SAM (Serverless Application Model)
サーバーレスに特化したフレームワーク。デプロイには **`template.yaml`** という設計図ファイルが必要。

> **Windows での推奨構成**: Windows 環境では `sam build` が `esbuild` を見つけられないエラーが発生しやすいため、以下の「手動ビルド方式」を推奨。

##### 1. `package.json` にビルドスクリプトを追加
SAM に頼らず、確実にビルドを行うための設定を `package.json` に追記。

```json
  "scripts": {
    "build": "esbuild index.ts --bundle --minify --sourcemap --platform=node --target=es2020 --outfile=dist/index.js"
  }
```

##### 2. `template.yaml` の作成
プロジェクトのルートディレクトリに `template.yaml` を作成。**`Metadata` セクションは含めない**のがポイント。

```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Description: my-lambda-project

Resources:
  MyHelloWorldFunction:
    Type: AWS::Serverless::Function
    Properties:
      # 手動ビルドで生成される dist フォルダを指定
      CodeUri: ./dist
      # dist 内の index.js を参照
      Handler: index.handler
      Runtime: nodejs20.x
      Architectures:
        - x86_64
      MemorySize: 128
      Timeout: 3
      Events:
        HelloWorld:
          Type: Api
          Properties:
            Path: /hello
            Method: get
```

##### 3. デプロイ手順
1. **ビルド (JS生成)**: ターミナルで以下を実行し、`dist/index.js` を作成。
   ```bash
   npm run build
   ```
2. **SAM ビルド (パッケージ化)**:
   ```bash
   sam build
   ```
   - `Metadata` がないため、SAM は `CodeUri` の内容をそのままデプロイ用パッケージとして集約。
3. **デプロイ**:
   ```bash
   sam deploy --guided
   ```

##### 4. `sam deploy --guided` の対話項目の意味
初回デプロイ時に聞かれる各項目の意味。

- **Stack Name [sam-app]**:
  - AWS 上でのプロジェクト名。任意の名前（例: `my-lambda-project`）を入力。
- **AWS Region [ap-northeast-1]**:
  - デプロイ先の地域。通常はそのまま（東京リージョン）で OK。
- **Confirm changes before deploy [y/N]**:
  - デプロイ前の変更内容確認。慣れるまでは `y` を推奨。
  - `y` の場合、デプロイ途中で **`Deploy this changeset? [y/N]:`** との確認あり。`y` を押さない限りリソースは未作成。
  - 表示される `+ Add` の表は「作成予定リソースのリスト（チェンジセット）」。
- **Allow SAM CLI IAM role creation [Y/n]**:
  - SAM による Lambda 用権限（IAM ロール）の自動作成許可。必ず `Y` を選択。
- **Disable rollback [y/N]**:
  - デプロイ失敗時のロールバック無効化設定。デバッグ時は `y` 推奨。
- **MyHelloWorldFunction has no authentication. Is this okay? [y/N]**:
  - API の認証なし状態の確認。テスト用なら `y` で進行。
- **Save arguments to configuration file [Y/n]**:
  - 設定の `samconfig.toml` への保存。`Y` 推奨（次回から `sam deploy` のみで実行可能）。

##### 5. 2回目以降のデプロイ（コード修正時）
コード修正後の再デプロイ手順。

1. **JS の再生成**:
   ```bash
   npm run build
   ```
2. **ビルドとデプロイを一括実行**:
   ```bash
   sam build && sam deploy
   ```
   - 初回の設定が `samconfig.toml` に保存されているため、再度の対話は不要。

---

#### B. AWS CDK (Cloud Development Kit)
TypeScript でインフラを定義。
- **ブートストラップ**: `cdk bootstrap` (初回のみ)
- **デプロイ**: `cdk deploy`

---

## 6. AWS コンソールでの確認

デプロイ完了後の確認項目。

1. **Lambda コンソール**: 関数の作成状況、コード、IAM ロールの設定確認。
2. **テスト実行**: 「テスト」タブからの実行と成功確認。
3. **CloudWatch Logs**: `console.log` の出力内容確認。
