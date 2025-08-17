package io.github.cheatbook.amzrevenuemanager.domain.summary.calculator;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductCostCalculator {

    public void calculate(List<Purchase> purchases, Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap) {
        if (purchases == null) {
            return;
        }

        Map<String, List<Purchase>> purchasesByParentSku = purchases.stream()
                .filter(p -> p.getUnitPrice() != null)
                .collect(Collectors.groupingBy(Purchase::getParentSku));

        Map<String, Double> averageUnitPriceByParentSku = new HashMap<>();
        for (Map.Entry<String, List<Purchase>> entry : purchasesByParentSku.entrySet()) {
            String parentSku = entry.getKey();
            List<Purchase> purchaseList = entry.getValue();

            double sum = purchaseList.stream().mapToDouble(Purchase::getUnitPrice).sum();
            double average = purchaseList.isEmpty() ? 0.0 : sum / purchaseList.size();
            averageUnitPriceByParentSku.put(parentSku, average);
        }

        parentSkuSummaryMap.forEach((parentSku, summary) -> {
            Double averageUnitPrice = averageUnitPriceByParentSku.get(parentSku);
            if (averageUnitPrice != null) {
                BigDecimal unitPrice = BigDecimal.valueOf(averageUnitPrice);
                BigDecimal orderCount = new BigDecimal(summary.getOrderCount());
                BigDecimal cost = unitPrice.multiply(orderCount);
                summary.setProductCost(cost.negate());
            }
        });
    }
}