import { Settlement } from "../models/Settlement";
import { SkuRevenueSummary } from "../models/RevenueSummary";
import { Logger } from "../../shared/logger";

/**
 * 収益サマリーの計算を行うドメインサービス
 */
export class RevenueSummaryService {
  /**
   * 決済リストからSKUごとにグループ化された収益サマリーを計算します。
   */
  public static calculateSkuRevenueSummaries(settlements: Settlement[]): SkuRevenueSummary[] {
    Logger.info('SKU別収益サマリーの計算を開始します。');
    
    const skuSummaryMap: Record<string, SkuRevenueSummary> = {};
    const skuToOrderIdQuantityMap: Record<string, Record<string, number>> = {};
    const skuToOrderIds: Record<string, Set<string>> = {};

    // Order と Refund トランザクションのみを対象にする
    const orderSettlements = settlements.filter(
      (s) => s.transactionType === "Order" || s.transactionType === "Refund"
    );

    for (const settlement of orderSettlements) {
      const sku = settlement.sku;
      if (!sku) continue;

      if (!skuSummaryMap[sku]) {
        skuSummaryMap[sku] = {
          sku,
          japaneseName: "", // 将来的に SkuName テーブルから取得
          totalRevenue: 0,
          totalCommission: 0,
          totalShipping: 0,
          totalTax: 0,
          grossProfit: 0,
          settlementCount: 0,
          totalQuantityPurchased: 0,
        };
        skuToOrderIdQuantityMap[sku] = {};
        skuToOrderIds[sku] = new Set();
      }

      const summary = skuSummaryMap[sku];
      const amount = settlement.amount || 0;
      const desc = settlement.amountDescription;
      const orderId = settlement.orderId;
      const quantity = settlement.quantityPurchased || 0;

      if (orderId) {
        skuToOrderIds[sku].add(orderId);
      }

      switch (desc) {
        case "Principal":
          summary.totalRevenue += amount;
          if (orderId) {
            skuToOrderIdQuantityMap[sku][orderId] = Math.max(
              skuToOrderIdQuantityMap[sku][orderId] || 0,
              quantity
            );
          }
          break;
        case "Tax":
        case "ShippingTax":
          summary.totalTax += amount;
          break;
        case "Shipping":
          summary.totalShipping += amount;
          break;
        case "Commission":
        case "FBAPerUnitFulfillmentFee":
        case "ShippingChargeback":
        case "RefundCommission":
          summary.totalCommission += amount;
          break;
      }
    }

    // カウントと利益の最終計算
    const results = Object.values(skuSummaryMap).map((summary) => {
      const sku = summary.sku;
      summary.settlementCount = skuToOrderIds[sku].size;
      summary.totalQuantityPurchased = Object.values(skuToOrderIdQuantityMap[sku]).reduce(
        (sum, q) => sum + q,
        0
      );
      summary.grossProfit =
        summary.totalRevenue + summary.totalCommission + summary.totalShipping;
      return summary;
    });

    Logger.info(`計算完了: ${results.length} 件のSKUサマリーを生成しました。`);
    return results;
  }
}
