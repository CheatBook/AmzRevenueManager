/**
 * 仕入れ原価データインターフェース
 */
export interface Purchase {
  /** SKU */
  sku: string;
  /** 仕入れ日 (YYYY-MM-DD) */
  purchaseDate: string;
  /** 仕入れ単価 */
  unitCost: number;
  /** 更新日時 */
  updatedAt?: string;
}
