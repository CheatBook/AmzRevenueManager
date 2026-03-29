package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * 決済情報DTOです。
 */
@Data
public class SettlementDto {

    /**
     * 決済ID
     */
    @CsvBindByName(column = "settlement-id")
    private String settlementId;

    /**
     * トランザクションの種類
     */
    @CsvBindByName(column = "transaction-type")
    private String transactionType;

    /**
     * 注文ID
     */
    @CsvBindByName(column = "order-id")
    private String orderId;

    /**
     * 金額の種類
     */
    @CsvBindByName(column = "amount-type")
    private String amountType;

    /**
     * 金額の説明
     */
    @CsvBindByName(column = "amount-description")
    private String amountDescription;

    /**
     * 金額
     */
    @CsvBindByName(column = "amount")
    private String amount;

    /**
     * 計上日時
     */
    @CsvBindByName(column = "posted-date-time")
    private String postedDateTime;

    /**
     * SKU
     */
    @CsvBindByName(column = "sku")
    private String sku;

    /**
     * 購入数量
     */
    @CsvBindByName(column = "quantity-purchased")
    private String quantityPurchased;
}