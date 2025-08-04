package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRevenueSummaryDto {
    private Integer year;
    private Integer month;
    private BigDecimal totalSales;
    private BigDecimal totalSalesByCard;
    private BigDecimal totalPromotionalRebates;
    private BigDecimal totalAmazonFees;
    private BigDecimal totalOtherTransactionFees;
    private BigDecimal totalOther;
    private BigDecimal total;
    private Long totalQuantity;
}