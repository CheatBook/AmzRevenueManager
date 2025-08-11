package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSkuMonthlySummaryDto {
    private int year;
    private int month;
    private List<ParentSkuRevenueForMonthDto> parentSkuRevenues;
    private ParentSkuRevenueForMonthDto monthlyTotal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParentSkuRevenueForMonthDto {
        private String parentSku;
        private String parentSkuJapaneseName;
        private BigDecimal totalSales;
        private BigDecimal totalFees;
        private BigDecimal totalAdCost;
        private BigDecimal grossProfit;
        private int orderCount;
        private BigDecimal productCost;
    }
}