package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 親SKUごとの日次収益DTOです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentSkuRevenueForDailyDto {
    /**
     * 親SKU名
     */
    private String parentSkuName;

    /**
     * 合計収益
     */
    private BigDecimal totalRevenue;

    /**
     * 合計手数料
     */
    private BigDecimal totalCommission;

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
    private long settlementCount;
}