package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueSummaryDto {
    private LocalDate date;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal totalShipping;
    private BigDecimal totalTax;
    private BigDecimal totalAdCost;
    private BigDecimal grossProfit;
    private long settlementCount;
}