import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, PutCommand, QueryCommand, ScanCommand } from "@aws-sdk/lib-dynamodb";
import { Settlement } from "../../domain/models/Settlement";
import { Logger } from "../../shared/logger";

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);

/**
 * 決済情報のDynamoDBリポジトリ実装
 */
export class SettlementRepository {
  private readonly tableName = process.env.SETTLEMENT_TABLE_NAME || "Settlements";

  /**
   * 決済レコードを保存します。
   */
  public async save(settlement: Settlement): Promise<void> {
    const command = new PutCommand({
      TableName: this.tableName,
      Item: {
        PK: settlement.settlementId,
        SK: settlement.lineId,
        ...settlement,
        updatedAt: new Date().toISOString(),
      },
    });

    await docClient.send(command);
  }

  /**
   * 複数の決済レコードを一括保存します。
   */
  public async saveAll(settlements: Settlement[]): Promise<void> {
    Logger.info(`${settlements.length} 件の決済レコードを保存します。`);
    // 本来は BatchWriteItem を使用すべきですが、簡単のためループで実装
    for (const settlement of settlements) {
      await this.save(settlement);
    }
    Logger.info('一括保存が完了しました。');
  }

  /**
   * 指定された決済IDに紐づくすべてのレコードを取得します。
   */
  public async findBySettlementId(settlementId: string): Promise<Settlement[]> {
    Logger.info(`決済ID: ${settlementId} のデータを取得します。`);
    const command = new QueryCommand({
      TableName: this.tableName,
      KeyConditionExpression: "PK = :pk",
      ExpressionAttributeValues: {
        ":pk": settlementId,
      },
    });

    const response = await docClient.send(command);
    return (response.Items as Settlement[]) || [];
  }

  /**
   * すべての決済レコードを取得します（ページネーション対応）。
   */
  public async findAll(): Promise<Settlement[]> {
    Logger.info('全決済データの取得を開始します。');
    let allItems: Settlement[] = [];
    let lastEvaluatedKey: Record<string, any> | undefined = undefined;

    do {
      const command: ScanCommand = new ScanCommand({
        TableName: this.tableName,
        ExclusiveStartKey: lastEvaluatedKey,
      });

      const response = await docClient.send(command);
      if (response.Items) {
        allItems = allItems.concat(response.Items as Settlement[]);
      }
      lastEvaluatedKey = response.LastEvaluatedKey;
    } while (lastEvaluatedKey);

    Logger.info(`全データの取得が完了しました。合計: ${allItems.length} 件`);
    return allItems;
  }
}
