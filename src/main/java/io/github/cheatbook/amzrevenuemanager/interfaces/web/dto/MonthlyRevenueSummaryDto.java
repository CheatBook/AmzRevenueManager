package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 月次収益サマリーDTOです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRevenueSummaryDto {
    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     */
    private Integer month;

    /**
     * 合計売上
     */
    private BigDecimal totalSales;

    /**
     * カードによる合計売上
     */
    private BigDecimal totalSalesByCard;

    /**
     * 合計プロモーションリベート
     */
    private BigDecimal totalPromotionalRebates;

    /**
     * 合計Amazon手数料
     */
    private BigDecimal totalAmazonFees;

    /**
     * 合計その他トランザクション手数料
     */
    private BigDecimal totalOtherTransactionFees;

    /**
     * 合計その他
     */
    private BigDecimal totalOther;

    /**
     * 合計
     */
    private BigDecimal total;

    /**
     * 合計数量
     */
    private Long totalQuantity;
}