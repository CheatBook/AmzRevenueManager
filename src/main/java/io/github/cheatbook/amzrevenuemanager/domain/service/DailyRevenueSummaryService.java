package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Transaction;
import io.github.cheatbook.amzrevenuemanager.domain.repository.TransactionRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.DailyRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyRevenueSummaryService {

    private final TransactionRepository transactionRepository;

    public List<DailyRevenueSummaryDto> getDailyRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        } else {
            transactions = transactionRepository.findAll();
        }

        Map<LocalDate, List<Transaction>> transactionsByDate = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getPostedDateTime().toLocalDate()));

        return transactionsByDate.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Transaction> dailyTransactions = entry.getValue();

                    BigDecimal totalRevenue = BigDecimal.ZERO;
                    BigDecimal totalCommission = BigDecimal.ZERO;
                    BigDecimal totalShipping = BigDecimal.ZERO;
                    BigDecimal totalTax = BigDecimal.ZERO;
                    long transactionCount = dailyTransactions.stream().map(Transaction::getOrderId).distinct().count();

                    List<Transaction> orderTransactions = dailyTransactions.stream()
                        .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
                        .collect(Collectors.toList());

                    for (Transaction t : orderTransactions) {
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

                    BigDecimal grossProfit = totalRevenue.add(totalCommission).add(totalShipping);

                    return new DailyRevenueSummaryDto(date, totalRevenue, totalCommission, totalShipping, totalTax, grossProfit, transactionCount);
                })
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }
}