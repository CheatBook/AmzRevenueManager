import { SkuRevenueSummary } from "./RevenueSummary";

/**
 * 階層的なSKU収益サマリーインターフェース
 */
export interface HierarchicalSkuRevenueSummary {
  /** 親SKUのサマリー */
  parentSummary: SkuRevenueSummary;
  /** 子SKUのサマリーリスト */
  childrenSummaries: SkuRevenueSummary[];
}
