package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final SkuNameRepository skuNameRepository;
    private final AdvertisementRepository advertisementRepository;

    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        List<Settlement> settlements = settlementRepository.findAll();
        List<SkuName> skuNames = skuNameRepository.findAll();
        List<Advertisement> advertisements = advertisementRepository.findAll();

        Map<String, String> skuToParentSkuMap = skuNames.stream()
                .filter(s -> s.getParentSku() != null)
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getParentSku, (existing, replacement) -> existing));
        Map<String, String> parentSkuToJapaneseNameMap = skuNames.stream()
                .filter(s -> s.getParentSku() != null && !s.getParentSku().isEmpty() && s.getJapaneseName() != null)
                .collect(Collectors.toMap(SkuName::getParentSku, SkuName::getJapaneseName, (existing, replacement) -> existing));

        Map<YearMonth, List<Settlement>> transactionsByMonth = new HashMap<>();
        for (Settlement t : settlements) {
            if ("other-transaction".equals(t.getTransactionType())) {
                t.setParentSku("その他");
            } else {
                t.setParentSku(skuToParentSkuMap.get(t.getSku()));
            }
            YearMonth yearMonth = YearMonth.from(t.getPostedDateTime());
            transactionsByMonth.computeIfAbsent(yearMonth, k -> new ArrayList<>()).add(t);
        }

        Map<YearMonth, Map<String, BigDecimal>> adCostByMonthAndSku = advertisements.stream()
            .collect(Collectors.groupingBy(ad -> YearMonth.from(ad.getDate()),
                Collectors.groupingBy(Advertisement::getSku,
                    Collectors.reducing(BigDecimal.ZERO, Advertisement::getTotalCost, BigDecimal::add))));

        List<ParentSkuMonthlySummaryDto> summaryList = new ArrayList<>();
        for (Map.Entry<YearMonth, List<Settlement>> entry : transactionsByMonth.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            List<Settlement> monthlyTransactions = entry.getValue();

            Map<String, ParentSkuRevenueForMonthDto> parentSkuSummaryMap = new HashMap<>();
            Map<String, List<String>> parentSkuToOrderIdsMap = new HashMap<>();

            for (Settlement t : monthlyTransactions) {
                String parentSku = t.getParentSku() != null ? t.getParentSku() : "N/A";
                ParentSkuRevenueForMonthDto summary = parentSkuSummaryMap.computeIfAbsent(parentSku, k -> ParentSkuRevenueForMonthDto.builder()
                        .parentSku(k)
                        .parentSkuJapaneseName("その他".equals(k) ? "その他" : parentSkuToJapaneseNameMap.getOrDefault(k, k))
                        .totalSales(BigDecimal.ZERO)
                        .totalFees(BigDecimal.ZERO)
                        .totalAdCost(BigDecimal.ZERO)
                        .grossProfit(BigDecimal.ZERO)
                        .orderCount(0)
                        .build());

                String amountDescription = t.getAmountDescription() != null ? t.getAmountDescription().trim() : "";
                BigDecimal amount = t.getAmount();

                if ("Principal".equals(amountDescription) || "Tax".equals(amountDescription) || "ShippingTax".equals(amountDescription)) {
                    summary.setTotalSales(summary.getTotalSales().add(amount));
                } else if ("Shipping".equals(amountDescription)) {
                    summary.setTotalFees(summary.getTotalFees().add(amount));
                } else if ("TaxDiscount".equals(amountDescription)) {
                    summary.setTotalFees(summary.getTotalFees().add(amount));
                } else {
                    summary.setTotalFees(summary.getTotalFees().add(amount));
                }
                
                if (!"その他".equals(parentSku) && t.getOrderId() != null && !t.getOrderId().isEmpty()) {
                    parentSkuToOrderIdsMap.computeIfAbsent(parentSku, k -> new ArrayList<>()).add(t.getOrderId());
                }
            }

            adCostByMonthAndSku.getOrDefault(yearMonth, new HashMap<>()).forEach((sku, cost) -> {
                String parentSku = skuToParentSkuMap.getOrDefault(sku, "N/A");
                ParentSkuRevenueForMonthDto summary = parentSkuSummaryMap.computeIfAbsent(parentSku, k -> ParentSkuRevenueForMonthDto.builder()
                    .parentSku(k)
                    .parentSkuJapaneseName("その他".equals(k) ? "その他" : parentSkuToJapaneseNameMap.getOrDefault(k, k))
                    .totalSales(BigDecimal.ZERO)
                    .totalFees(BigDecimal.ZERO)
                    .totalAdCost(BigDecimal.ZERO)
                    .grossProfit(BigDecimal.ZERO)
                    .orderCount(0)
                    .build());
                summary.setTotalAdCost(summary.getTotalAdCost().add(cost));
            });

            ParentSkuRevenueForMonthDto monthlyTotal = ParentSkuRevenueForMonthDto.builder()
                .parentSku("合計")
                .parentSkuJapaneseName("合計")
                .totalSales(BigDecimal.ZERO)
                .totalFees(BigDecimal.ZERO)
                .totalAdCost(BigDecimal.ZERO)
                .grossProfit(BigDecimal.ZERO)
                .orderCount(0)
                .build();

            parentSkuSummaryMap.forEach((parentSku, summary) -> {
                summary.setGrossProfit(summary.getTotalSales().add(summary.getTotalFees()).add(summary.getTotalAdCost()));
                summary.setOrderCount((int) parentSkuToOrderIdsMap.getOrDefault(parentSku, new ArrayList<>()).stream().distinct().count());

                monthlyTotal.setTotalSales(monthlyTotal.getTotalSales().add(summary.getTotalSales()));
                monthlyTotal.setTotalFees(monthlyTotal.getTotalFees().add(summary.getTotalFees()));
                monthlyTotal.setTotalAdCost(monthlyTotal.getTotalAdCost().add(summary.getTotalAdCost()));
                monthlyTotal.setGrossProfit(monthlyTotal.getGrossProfit().add(summary.getGrossProfit()));
                monthlyTotal.setOrderCount(monthlyTotal.getOrderCount() + summary.getOrderCount());
            });

            summaryList.add(ParentSkuMonthlySummaryDto.builder()
                    .year(yearMonth.getYear())
                    .month(yearMonth.getMonthValue())
                    .parentSkuRevenues(new ArrayList<>(parentSkuSummaryMap.values()))
                    .monthlyTotal(monthlyTotal)
                    .build());
        }

        summaryList.sort(Comparator.comparing(ParentSkuMonthlySummaryDto::getYear).reversed()
                .thenComparing(Comparator.comparing(ParentSkuMonthlySummaryDto::getMonth).reversed()));

        return summaryList;
    }
}