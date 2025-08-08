package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.DailyRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final AdvertisementRepository advertisementRepository;

    public List<DailyRevenueSummaryDto> getDailyRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Settlement> settlements;
        List<Advertisement> advertisements;

        if (startDate != null && endDate != null) {
            settlements = settlementRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
            advertisements = advertisementRepository.findByDateBetween(startDate, endDate);
        } else {
            settlements = settlementRepository.findAll();
            advertisements = advertisementRepository.findAll();
        }

        Map<LocalDate, List<Settlement>> transactionsByDate = settlements.stream()
                .collect(Collectors.groupingBy(t -> t.getPostedDateTime().toLocalDate()));
        
        Map<LocalDate, BigDecimal> adCostByDate = advertisements.stream()
                .collect(Collectors.groupingBy(Advertisement::getDate,
                        Collectors.reducing(BigDecimal.ZERO, Advertisement::getTotalCost, BigDecimal::add)));

        return transactionsByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Settlement> dailyTransactions = entry.getValue();

                    BigDecimal totalRevenue = BigDecimal.ZERO;
                    BigDecimal totalCommission = BigDecimal.ZERO;
                    BigDecimal totalShipping = BigDecimal.ZERO;
                    BigDecimal totalTax = BigDecimal.ZERO;
                    BigDecimal totalAdCost = adCostByDate.getOrDefault(date, BigDecimal.ZERO);
                    long settlementCount = dailyTransactions.stream().map(Settlement::getOrderId).distinct().count();

                    List<Settlement> orderTransactions = dailyTransactions.stream()
                        .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
                        .collect(Collectors.toList());

                    for (Settlement t : orderTransactions) {
                        switch (t.getAmountDescription()) {
                            case "Principal":
                                totalRevenue = totalRevenue.add(t.getAmount());
                                break;
                            case "Tax":
                                totalTax = totalTax.add(t.getAmount());
                                break;
                            case "Shipping":
                                totalShipping = totalShipping.add(t.getAmount());
                                break;
                            case "Commission":
                            case "FBAPerUnitFulfillmentFee":
                            case "ShippingChargeback":
                            case "RefundCommission":
                                totalCommission = totalCommission.add(t.getAmount());
                                break;
                            default:
                                break;
                        }
                    }

                    BigDecimal grossProfit = totalRevenue.add(totalCommission).add(totalShipping).add(totalAdCost);

                    return new DailyRevenueSummaryDto(date, totalRevenue, totalCommission, totalShipping, totalTax, totalAdCost, grossProfit, settlementCount);
                })
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }
}