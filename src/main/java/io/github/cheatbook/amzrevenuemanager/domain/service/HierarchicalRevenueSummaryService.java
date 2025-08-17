package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.HierarchicalSkuRevenueSummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HierarchicalRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final CacheableDataService cacheableDataService;
    private final RevenueSummaryService revenueSummaryService;

    public List<HierarchicalSkuRevenueSummaryDto> getHierarchicalSkuRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Settlement> allSettlements;
        if (startDate != null && endDate != null) {
            allSettlements = settlementRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        } else {
            allSettlements = settlementRepository.findAll();
        }
        List<SkuName> skuNames = cacheableDataService.findAllSkuNames();
        Map<String, String> skuToJapaneseNameMap = skuNames.stream()
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getJapaneseName, (name1, name2) -> name1));

        Map<String, SkuRevenueSummaryDto> skuSummaryMap = revenueSummaryService.calculateSkuRevenueSummaries(allSettlements, skuNames);

        // Build hierarchy and process summaries
        List<HierarchicalSkuRevenueSummaryDto> hierarchicalSummaries = new ArrayList<>();
        Set<String> processedSkus = new HashSet<>();

        Map<String, List<String>> parentToChildrenMap = skuNames.stream()
                .filter(s -> s.getParentSku() != null && !s.getParentSku().isEmpty())
                .collect(Collectors.groupingBy(SkuName::getParentSku, Collectors.mapping(SkuName::getSku, Collectors.toList())));

        for (Map.Entry<String, List<String>> entry : parentToChildrenMap.entrySet()) {
            String parentSku = entry.getKey();
            List<String> childSkus = entry.getValue();

            SkuRevenueSummaryDto parentSummary = new SkuRevenueSummaryDto(
                    parentSku,
                    skuToJapaneseNameMap.getOrDefault(parentSku, ""),
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0
            );

            List<SkuRevenueSummaryDto> childrenSummaries = new ArrayList<>();
            for (String childSku : childSkus) {
                SkuRevenueSummaryDto childSummary = skuSummaryMap.getOrDefault(childSku,
                        new SkuRevenueSummaryDto(childSku, skuToJapaneseNameMap.getOrDefault(childSku, ""), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0)
                );
                childrenSummaries.add(childSummary);
                processedSkus.add(childSku);

                parentSummary.setTotalRevenue(parentSummary.getTotalRevenue().add(childSummary.getTotalRevenue()));
                parentSummary.setTotalCommission(parentSummary.getTotalCommission().add(childSummary.getTotalCommission()));
                parentSummary.setTotalShipping(parentSummary.getTotalShipping().add(childSummary.getTotalShipping()));
                parentSummary.setTotalTax(parentSummary.getTotalTax().add(childSummary.getTotalTax()));
                parentSummary.setGrossProfit(parentSummary.getGrossProfit().add(childSummary.getGrossProfit()));
                parentSummary.setSettlementCount(parentSummary.getSettlementCount() + childSummary.getSettlementCount());
                parentSummary.setTotalQuantityPurchased(parentSummary.getTotalQuantityPurchased() + childSummary.getTotalQuantityPurchased());
            }

            childrenSummaries.sort(Comparator.comparing(SkuRevenueSummaryDto::getSku));
            hierarchicalSummaries.add(new HierarchicalSkuRevenueSummaryDto(parentSummary, childrenSummaries));
            processedSkus.add(parentSku);
        }

        for (Map.Entry<String, SkuRevenueSummaryDto> entry : skuSummaryMap.entrySet()) {
            if (!processedSkus.contains(entry.getKey())) {
                hierarchicalSummaries.add(new HierarchicalSkuRevenueSummaryDto(entry.getValue(), new ArrayList<>()));
            }
        }

        return hierarchicalSummaries.stream()
                .sorted(Comparator.comparing(dto -> dto.getParentSummary().getSku()))
                .collect(Collectors.toList());
    }
}