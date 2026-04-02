import { parse } from 'csv-parse/sync';
import { Advertisement } from '../../domain/models/Advertisement';
import { Logger } from '../../shared/logger';

/**
 * 広告レポートのパースを行うクラス
 */
export class AdvertisementParser {
  /**
   * 広告レポート(CSV)をAdvertisementオブジェクトの配列に変換します。
   * @param csvContent 生のCSV文字列
   * @returns Advertisementオブジェクトの配列
   */
  public static parseCsv(csvContent: string): Advertisement[] {
    Logger.info('広告レポートの解析を開始します。');

    try {
      const records = parse(csvContent, {
        columns: true,
        skip_empty_lines: true,
        trim: true,
      });

      const results = records.map((record: any) => ({
        date: record['日付'],
        campaignName: record['キャンペーン名'],
        parentSku: '', // 後でマッピングされるため、初期値は空
        adCost: parseFloat(record['広告費']) || 0,
      }));

      Logger.info(`解析完了: ${results.length} 件のレコードを抽出しました。`);
      return results;
    } catch (error) {
      Logger.error('広告レポートの解析中にエラーが発生しました。', error);
      throw error;
    }
  }
}
