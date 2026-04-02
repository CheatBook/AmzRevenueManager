import { APIGatewayProxyEventV2, APIGatewayProxyResultV2 } from 'aws-lambda';
import Busboy from 'busboy';
import { AdvertisementParser } from '../infrastructure/parsers/AdvertisementParser';
import { AdvertisementRepository } from '../infrastructure/persistence/AdvertisementRepository';
import { Logger } from '../shared/logger';

const repository = new AdvertisementRepository();

/**
 * 広告レポートをアップロード・処理する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEventV2): Promise<APIGatewayProxyResultV2> => {
  const method = event.requestContext.http.method;
  if (method === 'OPTIONS') {
    return createResponse(200, { message: 'OK' });
  }

  Logger.info('広告レポートのアップロードリクエストを受信しました。');

  try {
    const { fileContent, filename } = await parseMultipart(event);
    Logger.info(`処理対象ファイル: ${filename}`);

    const ads = AdvertisementParser.parseCsv(fileContent);
    await repository.saveAll(ads);

    return createResponse(200, { message: '広告レポートの処理が完了しました。', count: ads.length });
  } catch (error) {
    Logger.error('広告レポートの処理中にエラーが発生しました。', error);
    return createResponse(500, { message: '内部サーバーエラーが発生しました。', error: error instanceof Error ? error.message : String(error) });
  }
};

const parseMultipart = (event: APIGatewayProxyEventV2): Promise<{ fileContent: string; filename: string }> => {
  return new Promise((resolve, reject) => {
    const contentType = event.headers['content-type'] || event.headers['Content-Type'];
    if (!contentType) return reject(new Error('Content-Type ヘッダーが見つかりません。'));

    const busboy = Busboy({ headers: { 'content-type': contentType } });
    let fileContent = '';
    let filename = '';

    busboy.on('file', (_fieldname, file, info) => {
      filename = info.filename;
      file.on('data', (data) => { fileContent += data.toString(); });
    });

    busboy.on('finish', () => {
      if (!fileContent) reject(new Error('ファイルが見つかりません。'));
      else resolve({ fileContent, filename });
    });

    busboy.on('error', (err) => reject(err));
    const body = event.isBase64Encoded ? Buffer.from(event.body || '', 'base64') : event.body || '';
    busboy.end(body);
  });
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
