package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuRevenueSummaryDto {
    private String sku;
    private String japaneseName; // 日本語名を追加
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal totalShipping;
    private BigDecimal totalTax;
    private BigDecimal grossProfit;
    private Integer settlementCount; // order_idのユニーク数
    private Integer totalQuantityPurchased; // quantity_purchasedの合計
}