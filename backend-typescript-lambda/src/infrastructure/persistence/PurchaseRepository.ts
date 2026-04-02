import { PutCommand, QueryCommand, ScanCommand } from "@aws-sdk/lib-dynamodb";
import { docClient } from "./dynamoClient";
import { Purchase } from "../../domain/models/Purchase";
import { Logger } from "../../shared/logger";

/**
 * 仕入れ原価データのDynamoDBリポジトリ実装
 */
export class PurchaseRepository {
  private readonly tableName = process.env.PURCHASE_TABLE_NAME || "Purchases";

  /**
   * 仕入れ情報を保存します。
   */
  public async save(purchase: Purchase): Promise<void> {
    Logger.info(`仕入れ情報を保存します: SKU=${purchase.sku}, 日付=${purchase.purchaseDate}`);
    const command = new PutCommand({
      TableName: this.tableName,
      Item: {
        PK: purchase.sku,
        SK: purchase.purchaseDate,
        ...purchase,
        updatedAt: new Date().toISOString(),
      },
    });

    await docClient.send(command);
  }

  /**
   * SKUに紐づくすべての仕入れ情報を取得します。
   */
  public async findBySku(sku: string): Promise<Purchase[]> {
    Logger.info(`SKU: ${sku} の仕入れ情報を取得します。`);
    const command = new QueryCommand({
      TableName: this.tableName,
      KeyConditionExpression: "PK = :sku",
      ExpressionAttributeValues: {
        ":sku": sku,
      },
    });

    const response = await docClient.send(command);
    return (response.Items as Purchase[]) || [];
  }

  /**
   * すべての仕入れ情報を取得します。
   */
  public async findAll(): Promise<Purchase[]> {
    Logger.info('全仕入れ情報の取得を開始します。');
    let allItems: Purchase[] = [];
    let lastEvaluatedKey: Record<string, any> | undefined = undefined;

    do {
      const command: ScanCommand = new ScanCommand({
        TableName: this.tableName,
        ExclusiveStartKey: lastEvaluatedKey,
      });

      const response = await docClient.send(command);
      if (response.Items) {
        allItems = allItems.concat(response.Items as Purchase[]);
      }
      lastEvaluatedKey = response.LastEvaluatedKey;
    } while (lastEvaluatedKey);

    return allItems;
  }
}
