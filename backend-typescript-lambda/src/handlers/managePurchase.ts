import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { PurchaseRepository } from '../infrastructure/persistence/PurchaseRepository';
import { Logger } from '../shared/logger';
import { Purchase } from '../domain/models/Purchase';

const repository = new PurchaseRepository();

/**
 * 仕入れ情報を管理する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEvent): Promise<APIGatewayProxyResult> => {
  const method = event.requestContext.http.method;
  Logger.info(`仕入れ情報管理リクエストを受信しました: ${method}`);

  try {
    switch (method) {
      case 'GET':
        const sku = event.queryStringParameters?.sku;
        if (sku) {
          const results = await repository.findBySku(sku);
          return createResponse(200, results);
        } else {
          const results = await repository.findAll();
          return createResponse(200, results);
        }

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

const createResponse = (statusCode: number, body: any): APIGatewayProxyResult => ({
  statusCode,
  headers: { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' },
  body: JSON.stringify(body),
});
