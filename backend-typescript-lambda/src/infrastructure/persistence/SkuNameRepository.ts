import { PutCommand, GetCommand, ScanCommand, DeleteCommand } from "@aws-sdk/lib-dynamodb";
import { docClient } from "./dynamoClient";
import { SkuName } from "../../domain/models/SkuName";
import { Logger } from "../../shared/logger";

/**
 * SKU名マッピングのDynamoDBリポジトリ実装
 */
export class SkuNameRepository {
  private readonly tableName = process.env.SKU_NAME_TABLE_NAME || "SkuNames";

  /**
   * SKU名を保存します。
   */
  public async save(skuName: SkuName): Promise<void> {
    Logger.info(`SKU名マッピングを保存します: ${skuName.sku} -> ${skuName.japaneseName}`);
    const command = new PutCommand({
      TableName: this.tableName,
      Item: {
        ...skuName,
        updatedAt: new Date().toISOString(),
      },
    });

    await docClient.send(command);
  }

  /**
   * SKU名を取得します。
   */
  public async findBySku(sku: string): Promise<SkuName | null> {
    Logger.info(`SKU: ${sku} のマッピングを取得します。`);
    const command = new GetCommand({
      TableName: this.tableName,
      Key: { sku },
    });

    const response = await docClient.send(command);
    return (response.Item as SkuName) || null;
  }

  /**
   * すべてのSKU名マッピングを取得します。
   */
  public async findAll(): Promise<SkuName[]> {
    Logger.info('全SKU名マッピングの取得を開始します。');
    let allItems: SkuName[] = [];
    let lastEvaluatedKey: Record<string, any> | undefined = undefined;

    do {
      const command: ScanCommand = new ScanCommand({
        TableName: this.tableName,
        ExclusiveStartKey: lastEvaluatedKey,
      });

      const response = await docClient.send(command);
      if (response.Items) {
        allItems = allItems.concat(response.Items as SkuName[]);
      }
      lastEvaluatedKey = response.LastEvaluatedKey;
    } while (lastEvaluatedKey);

    Logger.info(`全マッピングの取得が完了しました。合計: ${allItems.length} 件`);
    return allItems;
  }

  /**
   * SKU名を削除します。
   */
  public async delete(sku: string): Promise<void> {
    Logger.info(`SKU: ${sku} のマッピングを削除します。`);
    const command = new DeleteCommand({
      TableName: this.tableName,
      Key: { sku },
    });

    await docClient.send(command);
  }
}
