package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.summary.ParentSkuSummaryData;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParentSkuRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final SkuNameRepository skuNameRepository;

    public List<SkuRevenueSummaryDto> getParentSkuRevenueSummary() {
        List<ParentSkuSummaryData> summaryDataList = settlementRepository.findParentSkuSummaryData();
        List<SkuName> skuNames = skuNameRepository.findAll();
        Map<String, String> skuToJapaneseNameMap = skuNames.stream()
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getJapaneseName, (existing, replacement) -> existing));

        return summaryDataList.stream()
                .map(summaryData -> {
                    BigDecimal totalRevenue = summaryData.getTotalRevenue() != null ? summaryData.getTotalRevenue() : BigDecimal.ZERO;
                    BigDecimal totalCommission = summaryData.getTotalCommission() != null ? summaryData.getTotalCommission() : BigDecimal.ZERO;
                    BigDecimal totalShipping = summaryData.getTotalShipping() != null ? summaryData.getTotalShipping() : BigDecimal.ZERO;
                    BigDecimal totalTax = summaryData.getTotalTax() != null ? summaryData.getTotalTax() : BigDecimal.ZERO;
                    int totalQuantityPurchased = summaryData.getTotalQuantityPurchased() != null ? summaryData.getTotalQuantityPurchased().intValue() : 0;

                    BigDecimal grossProfit = totalRevenue.add(totalCommission).add(totalShipping);

                    return new SkuRevenueSummaryDto(
                            summaryData.getParentSku(),
                            skuToJapaneseNameMap.getOrDefault(summaryData.getParentSku(), ""),
                            totalRevenue,
                            totalCommission,
                            totalShipping,
                            totalTax,
                            grossProfit,
                            0, // settlementCount is not calculated in this query
                            totalQuantityPurchased
                    );
                })
                .collect(Collectors.toList());
    }
}