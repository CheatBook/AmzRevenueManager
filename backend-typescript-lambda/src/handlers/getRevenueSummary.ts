import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { SettlementRepository } from '../infrastructure/persistence/SettlementRepository';
import { RevenueSummaryService } from '../domain/services/RevenueSummaryService';
import { Logger } from '../shared/logger';

const repository = new SettlementRepository();

/**
 * 収益サマリーを取得する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEvent): Promise<APIGatewayProxyResult> => {
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

    return {
      statusCode: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
      body: JSON.stringify(summaries),
    };
  } catch (error) {
    Logger.error('収益サマリーの取得中にエラーが発生しました。', error);
    return {
      statusCode: 500,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
      body: JSON.stringify({
        message: '内部サーバーエラーが発生しました。',
        error: error instanceof Error ? error.message : String(error),
      }),
    };
  }
};
