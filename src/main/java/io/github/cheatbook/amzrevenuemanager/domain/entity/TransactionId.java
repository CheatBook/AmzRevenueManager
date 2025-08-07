package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionId implements Serializable {
    private String settlementId;
    private String transactionType;
    private String orderId;
    private Long orderItemCode;
    private String amountDescription;
    private LocalDateTime postedDateTime;
}