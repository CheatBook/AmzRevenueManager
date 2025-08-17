package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKUごとの収益サマリーDTOです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuRevenueSummaryDto {
    /**
     * SKU
     */
    private String sku;

    /**
     * 日本語名
     */
    private String japaneseName;

    /**
     * 合計収益
     */
    private BigDecimal totalRevenue;

    /**
     * 合計手数料
     */
    private BigDecimal totalCommission;

    /**
     * 合計送料
     */
    private BigDecimal totalShipping;

    /**
     * 合計税金
     */
    private BigDecimal totalTax;

    /**
     * 売上総利益
     */
    private BigDecimal grossProfit;

    /**
     * 注文件数 (order_idのユニーク数)
     */
    private Integer settlementCount;

    /**
     * 合計購入数量 (quantity_purchasedの合計)
     */
    private Integer totalQuantityPurchased;
}