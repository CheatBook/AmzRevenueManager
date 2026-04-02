import { Settlement } from "../models/Settlement";
import { SkuName } from "../models/SkuName";
import { Advertisement } from "../models/Advertisement";
import { Purchase } from "../models/Purchase";
import { SalesDate } from "../models/SalesDate";
import { Logger } from "../../shared/logger";

/**
 * 月次収益サマリーの計算結果インターフェース
 */
export interface MonthlyRevenueSummary {
  year: number;
  month: number;
  parentSkuRevenues: ParentSkuRevenueForMonth[];
  monthlyTotal: ParentSkuRevenueForMonth;
}

export interface ParentSkuRevenueForMonth {
  parentSku: string;
  parentSkuJapaneseName: string;
  totalSales: number;
  totalFees: number;
  totalAdCost: number;
  productCost: number;
  grossProfit: number;
  orderCount: number;
}

/**
 * 月次収益サマリーの計算を行うドメインサービス
 */
export class MonthlyRevenueSummaryService {
  /**
   * 各種データから月次収益サマリーを計算します。
   */
  public static calculate(
    settlements: Settlement[],
    skuNames: SkuName[],
    advertisements: Advertisement[],
    purchases: Purchase[],
    salesDates: SalesDate[]
  ): MonthlyRevenueSummary[] {
    Logger.info('月次収益サマリーの計算を開始します。');

    // 1. 準備: マップの作成
    const salesDateMap = new Map(salesDates.map(sd => [`${sd.orderId}#${sd.sku}`, sd.salesDate]));
    const parentSkuToNameMap = new Map<string, string>();
    const skuToParentSkuMap = new Map<string, string>();
    
    for (const sn of skuNames) {
      if (sn.parentSku) {
        skuToParentSkuMap.set(sn.sku, sn.parentSku);
        if (sn.japaneseName) parentSkuToNameMap.set(sn.parentSku, sn.japaneseName);
      }
    }

    // 親SKUごとの平均単価を計算
    const parentSkuPurchaseCosts = new Map<string, number[]>();
    for (const p of purchases) {
      // 仕入れ情報のSKUから親SKUを特定（マッピングがない場合は自身のSKUを親とする）
      const parentSku = skuToParentSkuMap.get(p.sku) || p.sku;
      const costs = parentSkuPurchaseCosts.get(parentSku) || [];
      costs.push(p.unitCost);
      parentSkuPurchaseCosts.set(parentSku, costs);
    }
    const averageUnitPriceByParentSku = new Map<string, number>();
    for (const [parentSku, costs] of parentSkuPurchaseCosts.entries()) {
      const avg = costs.reduce((a, b) => a + b, 0) / costs.length;
      averageUnitPriceByParentSku.set(parentSku, avg);
    }

    // 2. データを月ごとにグループ化
    const settlementsByMonth = new Map<string, Settlement[]>();
    for (const s of settlements) {
      let dateStr = s.postedDateTime;
      const salesDate = salesDateMap.get(`${s.orderId}#${s.sku}`);
      if (salesDate) dateStr = salesDate;
      
      const yearMonth = dateStr.substring(0, 7); // YYYY-MM
      const list = settlementsByMonth.get(yearMonth) || [];
      list.push(s);
      settlementsByMonth.set(yearMonth, list);
    }

    const adsByMonth = new Map<string, Advertisement[]>();
    for (const ad of advertisements) {
      const yearMonth = ad.date.substring(0, 7);
      const list = adsByMonth.get(yearMonth) || [];
      list.push(ad);
      adsByMonth.set(yearMonth, list);
    }

    // 3. 月ごとに集計
    const allMonths = Array.from(new Set([...settlementsByMonth.keys(), ...adsByMonth.keys()])).sort().reverse();
    const results: MonthlyRevenueSummary[] = [];

    for (const yearMonth of allMonths) {
      const [year, month] = yearMonth.split('-').map(Number);
      const monthlySettlements = settlementsByMonth.get(yearMonth) || [];
      const monthlyAds = adsByMonth.get(yearMonth) || [];

      const parentSkuSummaryMap = new Map<string, ParentSkuRevenueForMonth>();

      // A. 売上・手数料・注文数の集計
      for (const s of monthlySettlements) {
        const parentSku = skuToParentSkuMap.get(s.sku) || s.sku;
        const summary = parentSkuSummaryMap.get(parentSku) || this.createEmptyParentSummary(parentSku, parentSkuToNameMap.get(parentSku) || "");
        
        if (s.amountDescription === 'Principal') {
          summary.totalSales += s.amount;
          summary.orderCount += s.quantityPurchased || 1;
        } else if (['Commission', 'FBAPerUnitFulfillmentFee', 'ShippingChargeback', 'RefundCommission'].includes(s.amountDescription)) {
          summary.totalFees += s.amount;
        }
        
        parentSkuSummaryMap.set(parentSku, summary);
      }

      // B. 広告費の集計
      for (const ad of monthlyAds) {
        const parentSku = ad.parentSku;
        const summary = parentSkuSummaryMap.get(parentSku) || this.createEmptyParentSummary(parentSku, parentSkuToNameMap.get(parentSku) || "");
        summary.totalAdCost += ad.adCost;
        parentSkuSummaryMap.set(parentSku, summary);
      }

      // C. 商品原価の計算と最終利益の算出
      for (const summary of parentSkuSummaryMap.values()) {
        const avgCost = averageUnitPriceByParentSku.get(summary.parentSku) || 0;
        summary.productCost = avgCost * summary.orderCount;
        summary.grossProfit = summary.totalSales + summary.totalFees + summary.totalAdCost - summary.productCost;
      }

      const parentSkuRevenues = Array.from(parentSkuSummaryMap.values());
      const monthlyTotal = this.aggregateTotal(parentSkuRevenues);

      results.push({
        year: year!,
        month: month!,
        parentSkuRevenues,
        monthlyTotal
      });
    }

    Logger.info(`月次収益サマリーの計算が完了しました。合計: ${results.length} ヶ月分`);
    return results;
  }

  private static createEmptyParentSummary(parentSku: string, name: string): ParentSkuRevenueForMonth {
    return {
      parentSku,
      parentSkuJapaneseName: name,
      totalSales: 0,
      totalFees: 0,
      totalAdCost: 0,
      productCost: 0,
      grossProfit: 0,
      orderCount: 0
    };
  }

  private static aggregateTotal(revenues: ParentSkuRevenueForMonth[]): ParentSkuRevenueForMonth {
    const total = this.createEmptyParentSummary("合計", "");
    for (const r of revenues) {
      total.totalSales += r.totalSales;
      total.totalFees += r.totalFees;
      total.totalAdCost += r.totalAdCost;
      total.productCost += r.productCost;
      total.grossProfit += r.grossProfit;
      total.orderCount += r.orderCount;
    }
    return total;
  }
}
