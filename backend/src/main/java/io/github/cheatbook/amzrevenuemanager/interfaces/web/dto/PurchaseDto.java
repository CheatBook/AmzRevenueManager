package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * 仕入れ情報DTOです。
 */
@Data
public class PurchaseDto {

    /**
     * 親SKU
     */
    private String parentSku;

    /**
     * 仕入れ日
     */
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
}