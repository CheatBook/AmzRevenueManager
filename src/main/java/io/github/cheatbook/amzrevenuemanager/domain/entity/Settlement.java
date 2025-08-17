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

/**
 * 決済エンティティです。
 */
@Entity
@Data
@Table(name = "settlement")
@IdClass(SettlementId.class)
public class Settlement {

    /**
     * 決済ID
     */
    @Id
    @Column(length = 30)
    private String settlementId;

    /**
     * トランザクションの種類
     */
    @Column(length = 50)
    private String transactionType;

    /**
     * 注文ID
     */
    @Id
    @Column(length = 50)
    private String orderId;

    /**
     * 注文商品コード
     */
    @Id
    private Long orderItemCode;

    /**
     * 金額の種類
     */
    @Column(length = 50)
    private String amountType;

    /**
     * 金額の説明
     */
    @Id
    @Column(length = 50)
    private String amountDescription;

    /**
     * 金額
     */
    private BigDecimal amount;

    /**
     * 計上日時
     */
    @Id
    private LocalDateTime postedDateTime;

    /**
     * SKU
     */
    @Column(length = 50)
    private String sku;

    /**
     * 購入数量
     */
    private Integer quantityPurchased;

    /**
     * 親SKU（DBテーブル外）
     */
    @Transient
    private String parentSku;

    /**
     * 親SKUを取得します。
     *
     * @return 親SKU
     */
    public String getParentSku() {
        return this.parentSku;
    }

    /**
     * 親SKUを設定します。
     *
     * @param parentSku 親SKU
     */
    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }
}