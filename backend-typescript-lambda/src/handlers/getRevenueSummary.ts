import { APIGatewayProxyEventV2, APIGatewayProxyResultV2 } from 'aws-lambda';
import { SettlementRepository } from '../infrastructure/persistence/SettlementRepository';
import { RevenueSummaryService } from '../domain/services/RevenueSummaryService';
import { Logger } from '../shared/logger';
import { createResponse, handleOptions } from '../shared/response';

// ハンドラーの外で初期化（再利用される）
const repository = new SettlementRepository();

/**
 * 収益サマリーを取得する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEventV2): Promise<APIGatewayProxyResultV2> => {
  const method = event.requestContext.http.method;

  // OPTIONS リクエストの共通処理
  if (method === 'OPTIONS') return handleOptions()!;

  Logger.info('収益サマリーの取得リクエストを受信しました。');

  try {
    const settlementId = event.queryStringParameters?.settlementId;
    const settlements = settlementId 
      ? await repository.findBySettlementId(settlementId) 
      : await repository.findAll();

    const summaries = RevenueSummaryService.calculateSkuRevenueSummaries(settlements);
    return createResponse(200, summaries);
  } catch (error) {
    Logger.error('収益サマリーの取得中にエラーが発生しました。', error);
    return createResponse(500, { 
      message: '内部サーバーエラーが発生しました。', 
      error: error instanceof Error ? error.message : String(error) 
    });
  }
};
