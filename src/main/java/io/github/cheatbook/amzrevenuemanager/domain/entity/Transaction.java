package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String settlementId;
    private String transactionType;
    private String orderId;
    private String amountType;
    private String amountDescription;
    private BigDecimal amount;
    private LocalDateTime postedDateTime;
    private String sku;
    private Integer quantityPurchased;

    @Transient
    private String parentSku;

    public String getParentSku() {
        return this.parentSku;
    }

    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }
}