import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, PutCommand, QueryCommand, ScanCommand } from "@aws-sdk/lib-dynamodb";
import { Advertisement } from "../../domain/models/Advertisement";
import { Logger } from "../../shared/logger";

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);

/**
 * 広告費データのDynamoDBリポジトリ実装
 */
export class AdvertisementRepository {
  private readonly tableName = process.env.ADVERTISEMENT_TABLE_NAME || "Advertisements";

  /**
   * 広告費レコードを保存します。
   */
  public async save(ad: Advertisement): Promise<void> {
    const command = new PutCommand({
      TableName: this.tableName,
      Item: {
        PK: ad.date,
        SK: ad.campaignName,
        ...ad,
        updatedAt: new Date().toISOString(),
      },
    });

    await docClient.send(command);
  }

  /**
   * 複数の広告費レコードを一括保存します。
   */
  public async saveAll(ads: Advertisement[]): Promise<void> {
    Logger.info(`${ads.length} 件の広告費レコードを保存します。`);
    for (const ad of ads) {
      await this.save(ad);
    }
    Logger.info('広告費の一括保存が完了しました。');
  }

  /**
   * 指定された期間の広告費データを取得します。
   */
  public async findByDateRange(startDate: string, endDate: string): Promise<Advertisement[]> {
    Logger.info(`期間: ${startDate} 〜 ${endDate} の広告費データを取得します。`);
    const command = new ScanCommand({
      TableName: this.tableName,
      FilterExpression: "#date BETWEEN :start AND :end",
      ExpressionAttributeNames: {
        "#date": "date",
      },
      ExpressionAttributeValues: {
        ":start": startDate,
        ":end": endDate,
      },
    });

    const response = await docClient.send(command);
    return (response.Items as Advertisement[]) || [];
  }
}
