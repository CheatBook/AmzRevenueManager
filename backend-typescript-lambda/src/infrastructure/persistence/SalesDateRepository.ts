import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, PutCommand, GetCommand, ScanCommand } from "@aws-sdk/lib-dynamodb";
import { SalesDate } from "../../domain/models/SalesDate";
import { Logger } from "../../shared/logger";

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);

/**
 * 販売日管理データのDynamoDBリポジトリ実装
 */
export class SalesDateRepository {
  private readonly tableName = process.env.SALES_DATE_TABLE_NAME || "SalesDates";

  /**
   * 販売日情報を保存します。
   */
  public async save(salesDate: SalesDate): Promise<void> {
    const command = new PutCommand({
      TableName: this.tableName,
      Item: {
        PK: salesDate.orderId,
        SK: salesDate.sku,
        ...salesDate,
        updatedAt: new Date().toISOString(),
      },
    });

    await docClient.send(command);
  }

  /**
   * 複数の販売日情報を一括保存します。
   */
  public async saveAll(salesDates: SalesDate[]): Promise<void> {
    Logger.info(`${salesDates.length} 件の販売日情報を保存します。`);
    for (const item of salesDates) {
      await this.save(item);
    }
    Logger.info('販売日情報の一括保存が完了しました。');
  }

  /**
   * 注文IDとSKUから販売日情報を取得します。
   */
  public async find(orderId: string, sku: string): Promise<SalesDate | null> {
    const command = new GetCommand({
      TableName: this.tableName,
      Key: {
        PK: orderId,
        SK: sku,
      },
    });

    const response = await docClient.send(command);
    return (response.Item as SalesDate) || null;
  }

  /**
   * すべての販売日情報を取得します。
   */
  public async findAll(): Promise<SalesDate[]> {
    Logger.info('全販売日情報の取得を開始します。');
    let allItems: SalesDate[] = [];
    let lastEvaluatedKey: Record<string, any> | undefined = undefined;

    do {
      const command: ScanCommand = new ScanCommand({
        TableName: this.tableName,
        ExclusiveStartKey: lastEvaluatedKey,
      });

      const response = await docClient.send(command);
      if (response.Items) {
        allItems = allItems.concat(response.Items as SalesDate[]);
      }
      lastEvaluatedKey = response.LastEvaluatedKey;
    } while (lastEvaluatedKey);

    return allItems;
  }
}
