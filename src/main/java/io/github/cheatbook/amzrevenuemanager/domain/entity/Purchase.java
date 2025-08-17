package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕入れエンティティです。
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PurchaseId.class)
public class Purchase {

    /**
     * 親SKU
     */
    @Id
    private String parentSku;

    /**
     * 仕入れ日
     */
    @Id
    private LocalDate purchaseDate;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 金額
     */
    private Integer amount;

    /**
     * 関税
     */
    private Integer tariff;

    /**
     * 単価
     */
    private Double unitPrice;
}