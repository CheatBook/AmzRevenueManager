package io.github.cheatbook.amzrevenuemanager.domain.summary.calculator;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class AdvertisementCostCalculator {

    public void calculate(List<Advertisement> advertisements, Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap, Map<String, String> parentSkuToJapaneseNameMap) {
        if (advertisements == null) {
            return;
        }

        advertisements.forEach(ad -> {
            String parentSku = ad.getId().getParentSku();
            BigDecimal cost = ad.getTotalCost();

            ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto summary = parentSkuSummaryMap
                    .computeIfAbsent(parentSku, k -> createEmptySummary(k, parentSkuToJapaneseNameMap));
            summary.setTotalAdCost(summary.getTotalAdCost().add(cost));
        });
    }

    private ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto createEmptySummary(String parentSku, Map<String, String> parentSkuToJapaneseNameMap) {
        return ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto.builder()
                .parentSku(parentSku)
                .parentSkuJapaneseName(parentSkuToJapaneseNameMap.getOrDefault(parentSku, parentSku))
                .totalSales(BigDecimal.ZERO)
                .totalFees(BigDecimal.ZERO)
                .totalAdCost(BigDecimal.ZERO)
                .grossProfit(BigDecimal.ZERO)
                .orderCount(0)
                .productCost(BigDecimal.ZERO)
                .build();
    }
}