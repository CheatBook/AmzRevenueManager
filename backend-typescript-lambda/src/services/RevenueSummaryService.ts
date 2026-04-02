import { Settlement } from "../models/Settlement";
import { SkuRevenueSummary } from "../models/RevenueSummary";

export class RevenueSummaryService {
  /**
   * Calculates revenue summaries grouped by SKU from a list of settlements.
   */
  public static calculateSkuRevenueSummaries(
    settlements: Settlement[],
  ): SkuRevenueSummary[] {
    const skuSummaryMap: Record<string, SkuRevenueSummary> = {};
    const skuToOrderIdQuantityMap: Record<string, Record<string, number>> = {};
    const skuToOrderIds: Record<string, Set<string>> = {};

    // Filter only Order and Refund transactions
    const orderSettlements = settlements.filter(
      (s) => s.transactionType === "Order" || s.transactionType === "Refund",
    );

    for (const settlement of orderSettlements) {
      const sku = settlement.sku;
      if (!sku) continue;

      if (!skuSummaryMap[sku]) {
        skuSummaryMap[sku] = {
          sku,
          japaneseName: "", // In the future, fetch from SkuName table
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
              quantity,
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

    // Finalize counts and profit
    return Object.values(skuSummaryMap).map((summary) => {
      const sku = summary.sku;
      summary.settlementCount = skuToOrderIds[sku].size;
      summary.totalQuantityPurchased = Object.values(
        skuToOrderIdQuantityMap[sku],
      ).reduce((sum, q) => sum + q, 0);
      summary.grossProfit =
        summary.totalRevenue + summary.totalCommission + summary.totalShipping;
      return summary;
    });
  }
}
