package io.github.cheatbook.amzrevenuemanager.domain.summary;

import io.github.cheatbook.amzrevenuemanager.domain.constant.Miscellaneous;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class SummaryAggregator {

    public ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto aggregate(Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap) {
        ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto monthlyTotal = createEmptyMonthlyTotal();

        parentSkuSummaryMap.forEach((parentSku, summary) -> {
            summary.setTotalAdCost(summary.getTotalAdCost().negate());
            summary.setGrossProfit(summary.getTotalSales().add(summary.getTotalFees()).add(summary.getTotalAdCost()).add(summary.getProductCost()));

            roundSummaryFields(summary);

            addToMonthlyTotal(monthlyTotal, summary);
        });

        roundMonthlyTotalFields(monthlyTotal);
        return monthlyTotal;
    }

    private ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto createEmptyMonthlyTotal() {
        return ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto.builder()
                .parentSku(Miscellaneous.TOTAL)
                .parentSkuJapaneseName(Miscellaneous.TOTAL)
                .totalSales(BigDecimal.ZERO)
                .totalFees(BigDecimal.ZERO)
                .totalAdCost(BigDecimal.ZERO)
                .grossProfit(BigDecimal.ZERO)
                .orderCount(0)
                .productCost(BigDecimal.ZERO)
                .build();
    }

    private void roundSummaryFields(ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto summary) {
        summary.setTotalSales(summary.getTotalSales().setScale(0, RoundingMode.HALF_UP));
        summary.setTotalFees(summary.getTotalFees().setScale(0, RoundingMode.HALF_UP));
        summary.setTotalAdCost(summary.getTotalAdCost().setScale(0, RoundingMode.HALF_UP));
        summary.setProductCost(summary.getProductCost().setScale(0, RoundingMode.HALF_UP));
        summary.setGrossProfit(summary.getGrossProfit().setScale(0, RoundingMode.HALF_UP));
    }

    private void addToMonthlyTotal(ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto monthlyTotal, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto summary) {
        monthlyTotal.setTotalSales(monthlyTotal.getTotalSales().add(summary.getTotalSales()));
        monthlyTotal.setTotalFees(monthlyTotal.getTotalFees().add(summary.getTotalFees()));
        monthlyTotal.setTotalAdCost(monthlyTotal.getTotalAdCost().add(summary.getTotalAdCost()));
        monthlyTotal.setProductCost(monthlyTotal.getProductCost().add(summary.getProductCost()));
        monthlyTotal.setGrossProfit(monthlyTotal.getGrossProfit().add(summary.getGrossProfit()));
        monthlyTotal.setOrderCount(monthlyTotal.getOrderCount() + summary.getOrderCount());
    }

    private void roundMonthlyTotalFields(ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto monthlyTotal) {
        monthlyTotal.setTotalSales(monthlyTotal.getTotalSales().setScale(0, RoundingMode.HALF_UP));
        monthlyTotal.setTotalFees(monthlyTotal.getTotalFees().setScale(0, RoundingMode.HALF_UP));
        monthlyTotal.setTotalAdCost(monthlyTotal.getTotalAdCost().setScale(0, RoundingMode.HALF_UP));
        monthlyTotal.setProductCost(monthlyTotal.getProductCost().setScale(0, RoundingMode.HALF_UP));
        monthlyTotal.setGrossProfit(monthlyTotal.getGrossProfit().setScale(0, RoundingMode.HALF_UP));
    }
}