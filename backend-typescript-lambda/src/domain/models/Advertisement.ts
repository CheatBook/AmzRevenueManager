/**
 * 広告費データインターフェース
 */
export interface Advertisement {
  /** 日付 (YYYY-MM-DD) */
  date: string;
  /** キャンペーン名 */
  campaignName: string;
  /** 親SKU (キャンペーン名などから紐付け) */
  parentSku: string;
  /** 広告費 */
  adCost: number;
  /** 更新日時 */
  updatedAt?: string;
}
