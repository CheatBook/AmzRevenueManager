import { APIGatewayProxyEventV2, APIGatewayProxyResultV2 } from 'aws-lambda';
import { PurchaseRepository } from '../infrastructure/persistence/PurchaseRepository';
import { Logger } from '../shared/logger';
import { Purchase } from '../domain/models/Purchase';
import { createResponse, handleOptions } from '../shared/response';

// ハンドラーの外で初期化（再利用される）
const repository = new PurchaseRepository();

/**
 * 仕入れ情報を管理する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEventV2): Promise<APIGatewayProxyResultV2> => {
  const method = event.requestContext.http.method;

  // OPTIONS リクエストの共通処理
  if (method === 'OPTIONS') return handleOptions()!;

  Logger.info(`仕入れ情報管理リクエストを受信しました: ${method}`);

  try {
    switch (method) {
      case 'GET':
        const sku = event.queryStringParameters?.sku;
        const results = sku ? await repository.findBySku(sku) : await repository.findAll();
        return createResponse(200, results);

      case 'POST':
        if (!event.body) return createResponse(400, { message: 'リクエストボディが空です。' });
        const purchase: Purchase = JSON.parse(event.body);
        await repository.save(purchase);
        return createResponse(200, { message: '仕入れ情報を保存しました。' });

      default:
        return createResponse(405, { message: '許可されていないメソッドです。' });
    }
  } catch (error) {
    Logger.error('仕入れ情報管理中にエラーが発生しました。', error);
    return createResponse(500, { message: '内部サーバーエラーが発生しました。' });
  }
};
