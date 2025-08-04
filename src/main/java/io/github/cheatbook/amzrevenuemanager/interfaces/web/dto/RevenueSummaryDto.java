package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueSummaryDto {

    private BigDecimal totalRevenue; // 総売上
    private BigDecimal totalCommission; // 総手数料
    private BigDecimal totalShipping; // 総送料
    private BigDecimal totalTax; // 総税額
    private BigDecimal grossProfit; // 粗利益 (売上 - 手数料 - 送料)
    private long transactionCount; // 取引件数
}