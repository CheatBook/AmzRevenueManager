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
@Table(name = "settlement")
@IdClass(SettlementId.class)
public class Settlement {

    @Id
    @Column(length = 30)
    private String settlementId;
    @Column(length = 50)
    private String transactionType;
    @Id
    @Column(length = 50)
    private String orderId;
    @Id
    private Long orderItemCode;
    @Column(length = 50)
    private String amountType;
    @Id
    @Column(length = 50)
    private String amountDescription;
    private BigDecimal amount;
    @Id
    private LocalDateTime postedDateTime;
    @Column(length = 50)
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