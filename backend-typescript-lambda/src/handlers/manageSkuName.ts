import { APIGatewayProxyEventV2, APIGatewayProxyResultV2 } from 'aws-lambda';
import { SkuNameRepository } from '../infrastructure/persistence/SkuNameRepository';
import { Logger } from '../shared/logger';
import { SkuName } from '../domain/models/SkuName';

const repository = new SkuNameRepository();

/**
 * SKU名マッピングを管理する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEventV2): Promise<APIGatewayProxyResultV2> => {
  const method = event.requestContext.http.method;
  Logger.info(`SKU名管理リクエストを受信しました: ${method}`);

  try {
    if (method === 'OPTIONS') {
      return createResponse(200, { message: 'OK' });
    }

    switch (method) {
      case 'GET':
        const sku = event.queryStringParameters?.sku;
        if (sku) {
          const result = await repository.findBySku(sku);
          return createResponse(200, result);
        } else {
          const results = await repository.findAll();
          return createResponse(200, results);
        }

      case 'POST':
        if (!event.body) return createResponse(400, { message: 'リクエストボディが空です。' });
        const skuName: SkuName = JSON.parse(event.body);
        await repository.save(skuName);
        return createResponse(200, { message: 'SKU名マッピングを保存しました。' });

      case 'DELETE':
        const skuToDelete = event.queryStringParameters?.sku;
        if (!skuToDelete) return createResponse(400, { message: 'SKUが指定されていません。' });
        await repository.delete(skuToDelete);
        return createResponse(200, { message: 'SKU名マッピングを削除しました。' });

      default:
        return createResponse(405, { message: '許可されていないメソッドです。' });
    }
  } catch (error) {
    Logger.error('SKU名管理中にエラーが発生しました。', error);
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
