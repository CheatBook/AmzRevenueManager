package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDataDto {

    private String settlementId;

    private String sku;

    private String amountDescription;

    private Integer quantityPurchased;

    private LocalDateTime purchaseDate;

    private LocalDateTime postedDateTime;

    private BigDecimal amount;
}