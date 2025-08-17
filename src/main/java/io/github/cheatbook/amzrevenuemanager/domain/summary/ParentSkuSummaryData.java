package io.github.cheatbook.amzrevenuemanager.domain.summary;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParentSkuSummaryData {
    private String parentSku;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal totalShipping;
    private BigDecimal totalTax;
    private Long totalQuantityPurchased;
}