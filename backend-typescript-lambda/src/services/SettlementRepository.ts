import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import {
  DynamoDBDocumentClient,
  QueryCommand,
  ScanCommand,
} from "@aws-sdk/lib-dynamodb";
import { Settlement } from "../models/Settlement";

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);

export class SettlementRepository {
  private readonly tableName =
    process.env.SETTLEMENT_TABLE_NAME || "Settlements";

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

  public async saveAll(settlements: Settlement[]): Promise<void> {
    for (const settlement of settlements) {
      await this.save(settlement);
    }
  }

  /**
   * Retrieves all settlements for a specific settlementId.
   */
  public async findBySettlementId(settlementId: string): Promise<Settlement[]> {
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
   * Retrieves all settlements (Note: Use with caution on large tables)
   */
  public async findAll(): Promise<Settlement[]> {
    const command = new ScanCommand({
      TableName: this.tableName,
    });

    const response = await docClient.send(command);
    return (response.Items as Settlement[]) || [];
  }
}

import { PutCommand } from "@aws-sdk/lib-dynamodb";
