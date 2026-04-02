/**
 * SKUごとの収益サマリーインターフェース
 */
export interface SkuRevenueSummary {
  /** SKU */
  sku: string;
  /** 日本語名 */
  japaneseName: string;
  /** 総売上 */
  totalRevenue: number;
  /** 手数料合計 */
  totalCommission: number;
  /** 配送料合計 */
  totalShipping: number;
  /** 税金合計 */
  totalTax: number;
  /** 粗利益 */
  grossProfit: number;
  /** 決済件数 (注文件数) */
  settlementCount: number;
  /** 合計購入数量 */
  totalQuantityPurchased: number;
}
