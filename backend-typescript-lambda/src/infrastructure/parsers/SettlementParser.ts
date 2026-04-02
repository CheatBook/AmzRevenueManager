import { parse } from 'csv-parse/sync';
import { Settlement } from '../../domain/models/Settlement';
import { Logger } from '../../shared/logger';

/**
 * 決済レポートのパースを行うクラス
 */
export class SettlementParser {
  /**
   * Amazon決済レポート(TSV)をSettlementオブジェクトの配列に変換します。
   * @param tsvContent 生のTSV文字列
   * @returns Settlementオブジェクトの配列
   */
  public static parseCsv(tsvContent: string): Settlement[] {
    Logger.info('決済レポートの解析を開始します。');

    try {
      const records = parse(tsvContent, {
        columns: true,
        delimiter: '\t', // TSV形式
        skip_empty_lines: true,
        trim: true,
        relax_column_count: true,
      });

      // 2行目のサマリー行（transaction-typeが空）を除外してマッピング
      const results = records
        .filter((record: any) => record['transaction-type'] && record['transaction-type'].trim() !== '')
        .map((record: any, index: number) => ({
          settlementId: record['settlement-id'],
          lineId: `L${index + 1}`, // 一意性を確保するための連番
          transactionType: record['transaction-type'],
          orderId: record['order-id'],
          amazonOrderId: record['amazon-order-id'],
          orderItemCode: record['order-item-code'],
          amountType: record['amount-type'],
          amountDescription: record['amount-description'],
          amount: parseFloat(record['amount']) || 0,
          postedDateTime: record['posted-date-time'],
          sku: record['sku'],
          quantityPurchased: parseInt(record['quantity-purchased'], 10) || 0,
        }));

      Logger.info(`解析完了: ${results.length} 件のレコードを抽出しました。`);
      return results;
    } catch (error) {
      Logger.error('決済レポートの解析中にエラーが発生しました。', error);
      throw error;
    }
  }
}
