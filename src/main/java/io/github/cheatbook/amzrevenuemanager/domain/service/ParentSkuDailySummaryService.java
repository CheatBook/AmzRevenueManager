package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.DailySummaryWithParentSkuDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuRevenueForDailyDto;

@Service
@RequiredArgsConstructor
public class ParentSkuDailySummaryService {

    private final SettlementRepository settlementRepository;
    private final SkuNameRepository skuNameRepository;
    private final AdvertisementRepository advertisementRepository;

    public List<DailySummaryWithParentSkuDto> getDailyParentSkuSummary(LocalDate startDate, LocalDate endDate) {
        List<Settlement> settlements;
        List<Advertisement> advertisements;
        if (startDate != null && endDate != null) {
            settlements = settlementRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
            advertisements = advertisementRepository.findByIdDateBetween(startDate, endDate);
        } else {
            settlements = settlementRepository.findAll();
            advertisements = advertisementRepository.findAll();
        }

        Map<String, String> skuToParentSkuMap = skuNameRepository.findAll().stream()
                .filter(skuName -> skuName.getParentSku() != null && !skuName.getParentSku().isEmpty())
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getParentSku, (existing, replacement) -> existing));
        
        Map<String, String> parentSkuToNameMap = skuNameRepository.findAll().stream()
                .filter(skuName -> skuName.getParentSku() != null && !skuName.getParentSku().isEmpty() && skuName.getJapaneseName() != null)
                .collect(Collectors.toMap(SkuName::getParentSku, SkuName::getJapaneseName, (existing, replacement) -> existing));

        settlements.forEach(t -> {
            if ("other-transaction".equals(t.getTransactionType())) {
                t.setParentSku("その他");
            } else {
                t.setParentSku(skuToParentSkuMap.get(t.getSku()));
            }
        });

        Map<LocalDate, Map<String, BigDecimal>> adCostByDateAndParentSku = advertisements.stream()
            .collect(Collectors.groupingBy(ad -> ad.getId().getDate(),
                Collectors.groupingBy(ad -> ad.getId().getParentSku(),
                    Collectors.reducing(BigDecimal.ZERO, Advertisement::getTotalCost, BigDecimal::add))));

        Map<LocalDate, List<Settlement>> dailyTransactions = settlements.stream()
                .filter(t -> t.getParentSku() != null)
                .collect(Collectors.groupingBy(t -> t.getPostedDateTime().toLocalDate()));

        List<DailySummaryWithParentSkuDto> result = dailyTransactions.entrySet().stream()
            .map(entry -> {
                LocalDate date = entry.getKey();
                List<Settlement> txns = entry.getValue();

                Map<String, List<Settlement>> transactionsByParentSku = txns.stream()
                    .collect(Collectors.groupingBy(Settlement::getParentSku));

                List<ParentSkuRevenueForDailyDto> parentSkuSummaries = transactionsByParentSku.entrySet().stream()
                    .map(parentSkuEntry -> {
                        String parentSku = parentSkuEntry.getKey();
                        List<Settlement> parentSkuTransactions = parentSkuEntry.getValue();
                        String parentSkuName = "その他".equals(parentSku) ? "その他" : parentSkuToNameMap.getOrDefault(parentSku, parentSku);

                        BigDecimal totalRevenue = BigDecimal.ZERO;
                        BigDecimal totalCommission = BigDecimal.ZERO;
                        BigDecimal totalAdCost = BigDecimal.ZERO;
                        long settlementCount = 0;
                        if (!"その他".equals(parentSku)) {
                            settlementCount = parentSkuTransactions.stream().map(Settlement::getOrderId).distinct().count();
                        }

                        for (Settlement t : parentSkuTransactions) {
                            String amountDescription = t.getAmountDescription() != null ? t.getAmountDescription().trim() : "";
                            String transactionType = t.getTransactionType() != null ? t.getTransactionType().trim() : "";

                            if ("other-transaction".equals(transactionType)) {
                                totalCommission = totalCommission.add(t.getAmount());
                            } else {
                                if ("Principal".equals(amountDescription) || "Tax".equals(amountDescription) || "ShippingTax".equals(amountDescription)) {
                                    totalRevenue = totalRevenue.add(t.getAmount());
                                } else if ("Shipping".equals(amountDescription)) {
                                    totalCommission = totalCommission.add(t.getAmount());
                                } else if ("Commission".equals(amountDescription) || "FBAPerUnitFulfillmentFee".equals(amountDescription) || "ShippingChargeback".equals(amountDescription) || "RefundCommission".equals(amountDescription) || "TaxDiscount".equals(amountDescription)) {
                                    totalCommission = totalCommission.add(t.getAmount());
                                }
                            }
                        }
                        
                        if (adCostByDateAndParentSku.containsKey(date)) {
                            totalAdCost = adCostByDateAndParentSku.get(date).getOrDefault(parentSku, BigDecimal.ZERO);
                        }

                        BigDecimal grossProfit = totalRevenue.add(totalCommission).add(totalAdCost);

                        return new ParentSkuRevenueForDailyDto(parentSkuName, totalRevenue, totalCommission, totalAdCost, grossProfit, settlementCount);
                    }).collect(Collectors.toList());

                ParentSkuRevenueForDailyDto dailyTotal = new ParentSkuRevenueForDailyDto("合計", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
                for(ParentSkuRevenueForDailyDto summary : parentSkuSummaries) {
                    dailyTotal.setTotalRevenue(dailyTotal.getTotalRevenue().add(summary.getTotalRevenue()));
                    dailyTotal.setTotalCommission(dailyTotal.getTotalCommission().add(summary.getTotalCommission()));
                    dailyTotal.setTotalAdCost(dailyTotal.getTotalAdCost().add(summary.getTotalAdCost()));
                    dailyTotal.setGrossProfit(dailyTotal.getGrossProfit().add(summary.getGrossProfit()));
                    dailyTotal.setSettlementCount(dailyTotal.getSettlementCount() + summary.getSettlementCount());
                }

                return new DailySummaryWithParentSkuDto(date, parentSkuSummaries, dailyTotal);
            })
            .sorted(Comparator.comparing(DailySummaryWithParentSkuDto::getDate))
            .collect(Collectors.toList());

        return result;
    }
}