/**
 * DynamoDB Table Schema:
 * 
 * Table Name: Settlements
 * Partition Key (PK): settlementId (String)
 * Sort Key (SK): lineId (String) - e.g., "L1", "L2" or UUID to ensure uniqueness
 */
export interface Settlement {
  settlementId: string;
  lineId: string; // Added for unique Sort Key
  transactionType: string;
  orderId: string;
  amazonOrderId: string;
  orderItemCode: string; // Changed to string as it can be large
  amountType: string;
  amountDescription: string;
  amount: number;
  postedDateTime: string;
  sku: string;
  quantityPurchased: number;
}
