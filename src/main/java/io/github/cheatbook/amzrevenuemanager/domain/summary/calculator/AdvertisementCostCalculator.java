package io.github.cheatbook.amzrevenuemanager.domain.summary.calculator;

import io.github.cheatbook.amzrevenuemanager.domain.constant.Miscellaneous;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.summary.context.MonthlySummaryContext;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdvertisementCostCalculator {

    public void calculate(MonthlySummaryContext context) {
        Map<YearMonth, Map<String, BigDecimal>> adCostByMonthAndParentSku = context.getAdvertisements().stream()
                .collect(Collectors.groupingBy(ad -> YearMonth.from(ad.getId().getDate()),
                        Collectors.groupingBy(ad -> ad.getId().getParentSku(),
                                Collectors.reducing(BigDecimal.ZERO, Advertisement::getTotalCost, BigDecimal::add))));

        if (!context.getSettlements().isEmpty()) {
            YearMonth yearMonth = YearMonth.from(context.getSettlements().get(0).getPostedDateTime());
            adCostByMonthAndParentSku.getOrDefault(yearMonth, new HashMap<>()).forEach((parentSku, cost) -> {
                ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto summary = context.getParentSkuSummaryMap()
                        .computeIfAbsent(parentSku, k -> createEmptySummary(k, context.getParentSkuToJapaneseNameMap()));
                summary.setTotalAdCost(summary.getTotalAdCost().add(cost));
            });
        }
    }

    private ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto createEmptySummary(String parentSku, Map<String, String> parentSkuToJapaneseNameMap) {
        String japaneseName = Miscellaneous.OTHER_TRANSACTION_PARENT_SKU.equals(parentSku)
                ? Miscellaneous.OTHER_TRANSACTION_PARENT_SKU
                : parentSkuToJapaneseNameMap.getOrDefault(parentSku, parentSku);

        return ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto.builder()
                .parentSku(parentSku)
                .parentSkuJapaneseName(japaneseName)
                .totalSales(BigDecimal.ZERO)
                .totalFees(BigDecimal.ZERO)
                .totalAdCost(BigDecimal.ZERO)
                .grossProfit(BigDecimal.ZERO)
                .orderCount(0)
                .productCost(BigDecimal.ZERO)
                .build();
    }
}