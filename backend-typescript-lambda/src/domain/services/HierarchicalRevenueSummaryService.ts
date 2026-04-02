import { Settlement } from "../models/Settlement";
import { SkuRevenueSummary } from "../models/RevenueSummary";
import { SkuName } from "../models/SkuName";
import { HierarchicalSkuRevenueSummary } from "../models/HierarchicalRevenueSummary";
import { RevenueSummaryService } from "./RevenueSummaryService";
import { Logger } from "../../shared/logger";

/**
 * 階層的な収益サマリーの計算を行うドメインサービス
 */
export class HierarchicalRevenueSummaryService {
  /**
   * 決済データとSKU名マスタから階層的な収益サマリーを計算します。
   */
  public static calculate(
    settlements: Settlement[],
    skuNames: SkuName[]
  ): HierarchicalSkuRevenueSummary[] {
    Logger.info('階層的収益サマリーの計算を開始します。');

    // 1. SKUごとの基本サマリーを計算
    const skuSummaries = RevenueSummaryService.calculateSkuRevenueSummaries(settlements);
    const skuSummaryMap = new Map(skuSummaries.map(s => [s.sku, s]));
    
    // SKU名マッピングの作成
    const skuToNameMap = new Map(skuNames.map(s => [s.sku, s.japaneseName]));
    
    // 2. 親SKU -> 子SKUリストのマップ作成
    const parentToChildrenMap = new Map<string, string[]>();
    for (const skuName of skuNames) {
      if (skuName.parentSku) {
        const children = parentToChildrenMap.get(skuName.parentSku) || [];
        children.push(skuName.sku);
        parentToChildrenMap.set(skuName.parentSku, children);
      }
    }

    const hierarchicalSummaries: HierarchicalSkuRevenueSummary[] = [];
    const processedSkus = new Set<string>();

    // 3. 親SKUごとに集計
    for (const [parentSku, childSkus] of parentToChildrenMap.entries()) {
      const parentSummary: SkuRevenueSummary = {
        sku: parentSku,
        japaneseName: skuToNameMap.get(parentSku) || "",
        totalRevenue: 0,
        totalCommission: 0,
        totalShipping: 0,
        totalTax: 0,
        grossProfit: 0,
        settlementCount: 0,
        totalQuantityPurchased: 0,
      };

      const childrenSummaries: SkuRevenueSummary[] = [];
      for (const childSku of childSkus) {
        const childSummary = skuSummaryMap.get(childSku) || this.createEmptySummary(childSku, skuToNameMap.get(childSku) || "");
        childrenSummaries.push(childSummary);
        processedSkus.add(childSku);

        // 子の値を親に加算
        parentSummary.totalRevenue += childSummary.totalRevenue;
        parentSummary.totalCommission += childSummary.totalCommission;
        parentSummary.totalShipping += childSummary.totalShipping;
        parentSummary.totalTax += childSummary.totalTax;
        parentSummary.grossProfit += childSummary.grossProfit;
        parentSummary.settlementCount += childSummary.settlementCount;
        parentSummary.totalQuantityPurchased += childSummary.totalQuantityPurchased;
      }

      childrenSummaries.sort((a, b) => a.sku.localeCompare(b.sku));
      hierarchicalSummaries.push({ parentSummary, childrenSummaries });
      processedSkus.add(parentSku);
    }

    // 4. 親を持たない（かつ親でもない）SKUを追加
    for (const summary of skuSummaries) {
      if (!processedSkus.has(summary.sku)) {
        hierarchicalSummaries.push({
          parentSummary: summary,
          childrenSummaries: [],
        });
      }
    }

    // 親SKUでソート
    hierarchicalSummaries.sort((a, b) => a.parentSummary.sku.localeCompare(b.parentSummary.sku));

    Logger.info(`階層的収益サマリーの計算が完了しました。合計: ${hierarchicalSummaries.length} 件`);
    return hierarchicalSummaries;
  }

  private static createEmptySummary(sku: string, japaneseName: string): SkuRevenueSummary {
    return {
      sku,
      japaneseName,
      totalRevenue: 0,
      totalCommission: 0,
      totalShipping: 0,
      totalTax: 0,
      grossProfit: 0,
      settlementCount: 0,
      totalQuantityPurchased: 0,
    };
  }
}
