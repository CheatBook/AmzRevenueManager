package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class TransactionDto {

    @CsvBindByName(column = "settlement-id")
    private String settlementId;

    @CsvBindByName(column = "transaction-type")
    private String transactionType;

    @CsvBindByName(column = "order-id")
    private String orderId;

    @CsvBindByName(column = "amount-type")
    private String amountType;

    @CsvBindByName(column = "amount-description")
    private String amountDescription;

    @CsvBindByName(column = "amount")
    private String amount;

    @CsvBindByName(column = "posted-date-time")
    private String postedDateTime;

    @CsvBindByName(column = "sku")
    private String sku;

    @CsvBindByName(column = "quantity-purchased")
    private String quantityPurchased;
}