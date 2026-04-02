import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import Busboy from 'busboy';
import { SettlementParser } from '../infrastructure/parsers/SettlementParser';
import { SettlementRepository } from '../infrastructure/persistence/SettlementRepository';
import { Logger } from '../shared/logger';

const repository = new SettlementRepository();

/**
 * API Gateway イベントから multipart/form-data を解析します。
 */
const parseMultipart = (event: APIGatewayProxyEvent): Promise<{ fileContent: string; filename: string }> => {
  return new Promise((resolve, reject) => {
    const contentType = event.headers['content-type'] || event.headers['Content-Type'];
    if (!contentType) {
      return reject(new Error('Content-Type ヘッダーが見つかりません。'));
    }

    const busboy = Busboy({ headers: { 'content-type': contentType } });
    let fileContent = '';
    let filename = '';

    busboy.on('file', (_fieldname, file, info) => {
      const { filename: name } = info;
      filename = name;
      file.on('data', (data) => {
        fileContent += data.toString();
      });
    });

    busboy.on('finish', () => {
      if (!fileContent) {
        reject(new Error('リクエスト内にファイルが見つかりません。'));
      } else {
        resolve({ fileContent, filename });
      }
    });

    busboy.on('error', (err) => reject(err));

    const body = event.isBase64Encoded ? Buffer.from(event.body || '', 'base64') : event.body || '';
    busboy.end(body);
  });
};

/**
 * 決済レポートを処理する Lambda ハンドラー
 */
export const handler = async (event: APIGatewayProxyEvent): Promise<APIGatewayProxyResult> => {
  Logger.info('決済レポート処理を開始します。');

  try {
    const { fileContent, filename } = await parseMultipart(event);
    Logger.info(`処理対象ファイル: ${filename}`);

    const settlements = SettlementParser.parseCsv(fileContent);
    Logger.info(`パース完了: ${settlements.length} 件のレコード`);

    await repository.saveAll(settlements);

    return {
      statusCode: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
      body: JSON.stringify({
        message: '決済レポートの処理が正常に完了しました。',
        filename,
        count: settlements.length,
      }),
    };
  } catch (error) {
    Logger.error('決済レポートの処理中にエラーが発生しました。', error);
    return {
      statusCode: 500,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
      body: JSON.stringify({
        message: '内部サーバーエラーが発生しました。',
        error: error instanceof Error ? error.message : String(error),
        stack: process.env.DEBUG === 'true' && error instanceof Error ? error.stack : undefined,
      }),
    };
  }
};
