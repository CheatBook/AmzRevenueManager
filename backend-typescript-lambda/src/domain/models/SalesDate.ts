/**
 * 販売日管理データインターフェース
 */
export interface SalesDate {
  /** 注文ID */
  orderId: string;
  /** SKU */
  sku: string;
  /** 販売日 (YYYY-MM-DD) */
  salesDate: string;
  /** 更新日時 */
  updatedAt?: string;
}
