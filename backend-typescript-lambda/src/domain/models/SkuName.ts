/**
 * SKU名マッピングインターフェース
 */
export interface SkuName {
  /** SKU */
  sku: string;
  /** 日本語名 */
  japaneseName: string;
  /** 親SKU */
  parentSku?: string;
  /** 更新日時 */
  updatedAt?: string;
}
