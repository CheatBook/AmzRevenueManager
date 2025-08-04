package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentSkuRevenueForDailyDto {
    private String parentSkuName;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal grossProfit;
    private long transactionCount;
}