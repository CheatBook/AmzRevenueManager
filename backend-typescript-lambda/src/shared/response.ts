import { APIGatewayProxyResultV2 } from 'aws-lambda';

/**
 * API Gateway 用の共通レスポンス生成ユーティリティ
 */
export const createResponse = (statusCode: number, body: any): APIGatewayProxyResultV2 => ({
  statusCode,
  headers: {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token',
  },
  body: JSON.stringify(body),
});

/**
 * OPTIONS リクエスト（プリフライト）に対する共通レスポンス
 */
export const handleOptions = (): APIGatewayProxyResultV2 | null => {
  return createResponse(200, { message: 'OK' });
};
