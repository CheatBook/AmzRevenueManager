import { parse } from "csv-parse/sync";
import { Settlement } from "../models/Settlement";

export class SettlementParser {
  /**
   * Parses Amazon Settlement Report TSV content into Settlement objects.
   * @param tsvContent Raw TSV string
   * @returns Array of Settlement objects
   */
  public static parseCsv(tsvContent: string): Settlement[] {
    const records = parse(tsvContent, {
      columns: true,
      delimiter: "\t", // TSV format
      skip_empty_lines: true,
      trim: true,
      relax_column_count: true,
    });

    // The first record (line 2) is often a summary row.
    // We skip it if it doesn't have a transaction-type.
    return records
      .filter(
        (record: any) =>
          record["transaction-type"] &&
          record["transaction-type"].trim() !== "",
      )
      .map((record: any, index: number) => ({
        settlementId: record["settlement-id"],
        lineId: `L${index + 1}`, // Sequence number for unique Sort Key
        transactionType: record["transaction-type"],
        orderId: record["order-id"],
        amazonOrderId: record["amazon-order-id"],
        orderItemCode: record["order-item-code"],
        amountType: record["amount-type"],
        amountDescription: record["amount-description"],
        amount: parseFloat(record["amount"]) || 0,
        postedDateTime: record["posted-date-time"],
        sku: record["sku"],
        quantityPurchased: parseInt(record["quantity-purchased"], 10) || 0,
      }));
  }
}
