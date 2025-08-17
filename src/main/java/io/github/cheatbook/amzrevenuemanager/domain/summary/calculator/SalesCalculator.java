package io.github.cheatbook.amzrevenuemanager.domain.summary.calculator;

import io.github.cheatbook.amzrevenuemanager.domain.constant.AmountDescription;
import io.github.cheatbook.amzrevenuemanager.domain.constant.Miscellaneous;
import io.github.cheatbook.amzrevenuemanager.domain.constant.TransactionType;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.summary.context.MonthlySummaryContext;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SalesCalculator {

    public void calculate(MonthlySummaryContext context) {
        Map<String, String> skuToParentSkuMap = context.getSkuNames().stream()
                .filter(s -> s.getParentSku() != null)
                .collect(Collectors.toMap(s -> s.getSku(), s -> s.getParentSku(), (existing, replacement) -> existing));
        context.setSkuToParentSkuMap(skuToParentSkuMap);

        if (context.getTransactionsByMonth() == null) {
            Map<YearMonth, List<Settlement>> transactionsByMonth = new HashMap<>();
            for (Settlement t : context.getSettlements()) {
                if (TransactionType.OTHER.getValue().equals(t.getTransactionType())) {
                    t.setParentSku(Miscellaneous.OTHER_TRANSACTION_PARENT_SKU);
                } else {
                    t.setParentSku(context.getSkuToParentSkuMap().get(t.getSku()));
                }
                YearMonth yearMonth = YearMonth.from(t.getPostedDateTime());
                transactionsByMonth.computeIfAbsent(yearMonth, k -> new ArrayList<>()).add(t);
            }
            context.setTransactionsByMonth(transactionsByMonth);
        }
        calculateMonthlySales(context, context.getSettlements());
    }

    private void calculateMonthlySales(MonthlySummaryContext context, List<Settlement> monthlyTransactions) {
        for (Settlement t : monthlyTransactions) {
            String parentSku = t.getParentSku() != null ? t.getParentSku() : Miscellaneous.NOT_APPLICABLE;
            ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto summary = context.getParentSkuSummaryMap()
                    .computeIfAbsent(parentSku, k -> createEmptySummary(k, context.getParentSkuToJapaneseNameMap()));

            String amountDescriptionStr = t.getAmountDescription() != null ? t.getAmountDescription().trim() : "";
            AmountDescription amountDescription = AmountDescription.fromString(amountDescriptionStr);
            BigDecimal amount = t.getAmount();

            if (amountDescription != null) {
                switch (amountDescription) {
                    case PRINCIPAL:
                    case TAX:
                    case SHIPPING_TAX:
                    case SHIPPING:
                        summary.setTotalSales(summary.getTotalSales().add(amount));
                        break;
                    case TAX_DISCOUNT:
                    default:
                        summary.setTotalFees(summary.getTotalFees().add(amount));
                        break;
                }
            } else {
                summary.setTotalFees(summary.getTotalFees().add(amount));
            }

            if (!Miscellaneous.OTHER_TRANSACTION_PARENT_SKU.equals(parentSku) && t.getOrderId() != null && !t.getOrderId().isEmpty()) {
                context.getParentSkuToOrderIdsMap().computeIfAbsent(parentSku, k -> new ArrayList<>()).add(t.getOrderId());
            }
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