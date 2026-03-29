package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 親SKUごとの月次サマリーDTOです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSkuMonthlySummaryDto {
    /**
     * 年
     */
    private int year;

    /**
     * 月
     */
    private int month;

    /**
     * 親SKUごとの収益リスト
     */
    private List<ParentSkuRevenueForMonthDto> parentSkuRevenues;

    /**
     * 月次合計
     */
    private ParentSkuRevenueForMonthDto monthlyTotal;

    /**
     * 親SKUごとの月次収益DTOです。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParentSkuRevenueForMonthDto {
        /**
         * 親SKU
         */
        private String parentSku;

        /**
         * 親SKUの日本語名
         */
        private String parentSkuJapaneseName;

        /**
         * 合計売上
         */
        private BigDecimal totalSales;

        /**
         * 合計手数料
         */
        private BigDecimal totalFees;

        /**
         * 合計広告費
         */
        private BigDecimal totalAdCost;

        /**
         * 売上総利益
         */
        private BigDecimal grossProfit;

        /**
         * 注文件数
         */
        private int orderCount;

        /**
         * 商品原価
         */
        private BigDecimal productCost;
    }
}