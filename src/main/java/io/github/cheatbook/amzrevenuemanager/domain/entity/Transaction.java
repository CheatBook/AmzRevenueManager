package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table
@IdClass(TransactionId.class)
public class Transaction {

    @Id
    @Column(length = 20)
    private String settlementId;
    @Id
    @Column(length = 20)
    private String transactionType;
    @Id
    @Column(length = 30)
    private String orderId;
    @Id
    private Long orderItemCode;
    private String amountType;
    @Id
    @Column(length = 50)
    private String amountDescription;
    private BigDecimal amount;
    @Id
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