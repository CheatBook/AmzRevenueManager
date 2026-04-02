import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { SettlementRepository } from '../infrastructure/persistence/SettlementRepository';
import { SkuNameRepository } from '../infrastructure/persistence/SkuNameRepository';
import { AdvertisementRepository } from '../infrastructure/persistence/AdvertisementRepository';
import { PurchaseRepository } from '../infrastructure/persistence/PurchaseRepository';
import { SalesDateRepository } from '../infrastructure/persistence/SalesDateRepository';
import { MonthlyRevenueSummaryService } from '../domain/services/MonthlyRevenueSummaryService';
import { Logger } from '../shared/logger';

const settlementRepo = new SettlementRepository();
const skuNameRepo = new SkuNameRepository();
const adRepo = new AdvertisementRepository();
const purchaseRepo = new PurchaseRepository();
const salesDateRepo = new SalesDateRepository();

/**
 * 月次収益サマリーを取得する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEvent): Promise<APIGatewayProxyResult> => {
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

const createResponse = (statusCode: number, body: any): APIGatewayProxyResult => ({
  statusCode,
  headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' },
  body: JSON.stringify(body),
});
