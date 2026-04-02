import { APIGatewayProxyEventV2, APIGatewayProxyResultV2 } from 'aws-lambda';
import { SettlementRepository } from '../infrastructure/persistence/SettlementRepository';
import { RevenueSummaryService } from '../domain/services/RevenueSummaryService';
import { Logger } from '../shared/logger';

const repository = new SettlementRepository();

/**
 * 収益サマリーを取得する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEventV2): Promise<APIGatewayProxyResultV2> => {
  const method = event.requestContext.http.method;
  if (method === 'OPTIONS') {
    return createResponse(200, { message: 'OK' });
  }

  Logger.info('収益サマリーの取得リクエストを受信しました。');

  try {
    const settlementId = event.queryStringParameters?.settlementId;

    let settlements;
    if (settlementId) {
      Logger.info(`決済ID: ${settlementId} のサマリーを計算します。`);
      settlements = await repository.findBySettlementId(settlementId);
    } else {
      Logger.info('全データのサマリーを計算します。');
      settlements = await repository.findAll();
    }

    const summaries = RevenueSummaryService.calculateSkuRevenueSummaries(settlements);

    return createResponse(200, summaries);
  } catch (error) {
    Logger.error('収益サマリーの取得中にエラーが発生しました。', error);
    return createResponse(500, { message: '内部サーバーエラーが発生しました。', error: error instanceof Error ? error.message : String(error) });
  }
};

const createResponse = (statusCode: number, body: any): APIGatewayProxyResultV2 => ({
  statusCode,
  headers: {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token',
  },
  body: JSON.stringify(body),
});
