import { APIGatewayProxyEventV2, APIGatewayProxyResultV2 } from 'aws-lambda';
import { SettlementRepository } from '../infrastructure/persistence/SettlementRepository';
import { SkuNameRepository } from '../infrastructure/persistence/SkuNameRepository';
import { AdvertisementRepository } from '../infrastructure/persistence/AdvertisementRepository';
import { PurchaseRepository } from '../infrastructure/persistence/PurchaseRepository';
import { SalesDateRepository } from '../infrastructure/persistence/SalesDateRepository';
import { MonthlyRevenueSummaryService } from '../domain/services/MonthlyRevenueSummaryService';
import { Logger } from '../shared/logger';
import { createResponse, handleOptions } from '../shared/response';

// ハンドラーの外で初期化（再利用される）
const settlementRepo = new SettlementRepository();
const skuNameRepo = new SkuNameRepository();
const adRepo = new AdvertisementRepository();
const purchaseRepo = new PurchaseRepository();
const salesDateRepo = new SalesDateRepository();

/**
 * 月次収益サマリーを取得する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEventV2): Promise<APIGatewayProxyResultV2> => {
  const method = event.requestContext.http.method;

  // OPTIONS リクエストの共通処理
  if (method === 'OPTIONS') return handleOptions()!;

  Logger.info('月次収益サマリーの取得リクエストを受信しました。');

  try {
    // 必要なデータを並列で取得
    const [settlements, skuNames, ads, purchases, salesDates] = await Promise.all([
      settlementRepo.findAll(),
      skuNameRepo.findAll(),
      adRepo.findByDateRange('2000-01-01', '2099-12-31'), 
      purchaseRepo.findAll(),
      salesDateRepo.findAll()
    ]);

    const summaries = MonthlyRevenueSummaryService.calculate(
      settlements,
      skuNames,
      ads,
      purchases,
      salesDates
    );

    return createResponse(200, summaries);
  } catch (error) {
    Logger.error('月次収益サマリーの取得中にエラーが発生しました。', error);
    return createResponse(500, { message: '内部サーバーエラーが発生しました。' });
  }
};
