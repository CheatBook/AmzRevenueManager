/**
 * 決済情報インターフェース
 */
export interface Settlement {
  /** 決済ID */
  settlementId: string;
  /** 行ID (一意性を確保するための連番) */
  lineId: string;
  /** トランザクションの種類 (Order, Refund など) */
  transactionType: string;
  /** 注文ID */
  orderId: string;
  /** Amazon注文ID */
  amazonOrderId: string;
  /** 注文商品コード */
  orderItemCode: string;
  /** 金額の種類 (ItemPrice, ItemFees など) */
  amountType: string;
  /** 金額の説明 (Principal, Commission など) */
  amountDescription: string;
  /** 金額 */
  amount: number;
  /** 計上日時 */
  postedDateTime: string;
  /** SKU */
  sku: string;
  /** 購入数量 */
  quantityPurchased: number;
}
