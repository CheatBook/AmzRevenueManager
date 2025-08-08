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
    private final SkuNameRepository skuNameRepository;

    public List<HierarchicalSkuRevenueSummaryDto> getHierarchicalSkuRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Settlement> allSettlements;
        if (startDate != null && endDate != null) {
            allSettlements = settlementRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        } else {
            allSettlements = settlementRepository.findAll();
        }
        List<SkuName> skuNames = skuNameRepository.findAll();

        // 1. Create a map of SKU to Japanese name
        Map<String, String> skuToJapaneseNameMap = skuNames.stream()
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getJapaneseName, (name1, name2) -> name1));

        // 2. Filter for relevant transactions
        List<Settlement> orderSettlements = allSettlements.stream()
            .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
            .collect(Collectors.toList());

        // 3. Calculate financial summary for every SKU with transactions
        Map<String, SkuRevenueSummaryDto> skuSummaryMap = new HashMap<>();
        Map<String, Map<String, Integer>> skuToOrderIdQuantityMap = new HashMap<>(); // SKU -> OrderId -> Quantity

        for (Settlement settlement : orderSettlements) {
            String sku = settlement.getSku();
            if (sku == null || sku.isEmpty()) {
                continue;
            }

            SkuRevenueSummaryDto summary = skuSummaryMap.computeIfAbsent(sku, s -> {
                SkuRevenueSummaryDto newDto = new SkuRevenueSummaryDto();
                newDto.setSku(s);
                newDto.setJapaneseName(skuToJapaneseNameMap.getOrDefault(s, ""));
                newDto.setTotalRevenue(BigDecimal.ZERO);
                newDto.setTotalCommission(BigDecimal.ZERO);
                newDto.setTotalShipping(BigDecimal.ZERO);
                newDto.setTotalTax(BigDecimal.ZERO);
                newDto.setSettlementCount(0);
                newDto.setTotalQuantityPurchased(0);
                return newDto;
            });

            BigDecimal amount = settlement.getAmount();
            String amountDescription = settlement.getAmountDescription();
            Integer quantityPurchased = settlement.getQuantityPurchased() != null ? settlement.getQuantityPurchased() : 0;
            String orderId = settlement.getOrderId();

            if (amountDescription != null) {
                 switch (amountDescription) {
                    case "Principal":
                        summary.setTotalRevenue(summary.getTotalRevenue().add(amount));
                        // Principalのトランザクションのみquantity_purchasedを考慮
                        if (orderId != null && !orderId.isEmpty()) {
                            skuToOrderIdQuantityMap.computeIfAbsent(sku, k -> new HashMap<>())
                                                   .merge(orderId, quantityPurchased, Integer::max); // 同じorderIdのquantity_purchasedは最大値を取る
                        }
                        break;
                    case "Tax":
                    case "ShippingTax":
                        summary.setTotalTax(summary.getTotalTax().add(amount));
                        break;
                    case "Shipping":
                        summary.setTotalShipping(summary.getTotalShipping().add(amount));
                        break;
                    case "Commission":
                    case "FBAPerUnitFulfillmentFee":
                    case "ShippingChargeback":
                    case "RefundCommission":
                        summary.setTotalCommission(summary.getTotalCommission().add(amount));
                        break;
                    default:
                        break;
                }
            }
            // settlementCountはorderIdのユニーク数で計算するため、ここでは更新しない
        }
        
        // 各SKUのorderIdのユニーク数を計算
        orderSettlements.stream() // allSettlementsではなくorderSettlementsを使用
            .collect(Collectors.groupingBy(Settlement::getSku, Collectors.mapping(Settlement::getOrderId, Collectors.toSet())))
            .forEach((sku, orderIds) -> {
                if (skuSummaryMap.containsKey(sku)) {
                    skuSummaryMap.get(sku).setSettlementCount(orderIds.size());
                }
            });

        // 最終的な totalQuantityPurchased の計算
        skuToOrderIdQuantityMap.forEach((sku, orderIdQuantityMap) -> {
            if (skuSummaryMap.containsKey(sku)) {
                int totalQuantity = orderIdQuantityMap.values().stream().mapToInt(Integer::intValue).sum();
                skuSummaryMap.get(sku).setTotalQuantityPurchased(totalQuantity);
            }
        });

        // Calculate gross profit for all summaries
        skuSummaryMap.values().forEach(summary -> {
            BigDecimal grossProfit = summary.getTotalRevenue()
                .add(summary.getTotalCommission())
                .add(summary.getTotalShipping());
            summary.setGrossProfit(grossProfit);
        });

        // 4. Build hierarchy and process summaries
        List<HierarchicalSkuRevenueSummaryDto> hierarchicalSummaries = new ArrayList<>();
        Set<String> processedSkus = new HashSet<>(); // To track SKUs that are part of a hierarchy

        // Create a map of parent SKUs to their children
        Map<String, List<String>> parentToChildrenMap = new HashMap<>();
        for (SkuName skuName : skuNames) {
            if (skuName.getParentSku() != null && !skuName.getParentSku().isEmpty()) {
                parentToChildrenMap.computeIfAbsent(skuName.getParentSku(), k -> new ArrayList<>()).add(skuName.getSku());
            }
        }

        // Process each parent and its children
        for (Map.Entry<String, List<String>> entry : parentToChildrenMap.entrySet()) {
            String parentSku = entry.getKey();
            List<String> childSkus = entry.getValue();

            // Create parent summary DTO
            SkuRevenueSummaryDto parentSummary = new SkuRevenueSummaryDto(
                parentSku,
                skuToJapaneseNameMap.getOrDefault(parentSku, ""),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0 // 新しいコンストラクタに合わせる
            );

            List<SkuRevenueSummaryDto> childrenSummaries = new ArrayList<>();
            for (String childSku : childSkus) {
                SkuRevenueSummaryDto childSummary = skuSummaryMap.getOrDefault(childSku,
                    new SkuRevenueSummaryDto(childSku, skuToJapaneseNameMap.getOrDefault(childSku, ""), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0) // 新しいコンストラクタに合わせる
                );
                childrenSummaries.add(childSummary);
                processedSkus.add(childSku);

                // Add child's values to parent
                parentSummary.setTotalRevenue(parentSummary.getTotalRevenue().add(childSummary.getTotalRevenue()));
                parentSummary.setTotalCommission(parentSummary.getTotalCommission().add(childSummary.getTotalCommission()));
                parentSummary.setTotalShipping(parentSummary.getTotalShipping().add(childSummary.getTotalShipping()));
                parentSummary.setTotalTax(parentSummary.getTotalTax().add(childSummary.getTotalTax()));
                parentSummary.setGrossProfit(parentSummary.getGrossProfit().add(childSummary.getGrossProfit())); // GrossProfitも加算
                parentSummary.setSettlementCount(parentSummary.getSettlementCount() + childSummary.getSettlementCount());
                parentSummary.setTotalQuantityPurchased(parentSummary.getTotalQuantityPurchased() + childSummary.getTotalQuantityPurchased()); // 新しいフィールドを加算
            }

            // Calculate gross profit for the parent (再計算は不要、子から加算済み)
            // BigDecimal parentGrossProfit = parentSummary.getTotalRevenue()
            //     .add(parentSummary.getTotalCommission())
            //     .add(parentSummary.getTotalShipping());
            // parentSummary.setGrossProfit(parentGrossProfit);

            childrenSummaries.sort(Comparator.comparing(SkuRevenueSummaryDto::getSku));
            hierarchicalSummaries.add(new HierarchicalSkuRevenueSummaryDto(parentSummary, childrenSummaries));
            processedSkus.add(parentSku);
        }

        // 5. Add standalone SKUs (those with transactions but not in any hierarchy)
        for (Map.Entry<String, SkuRevenueSummaryDto> entry : skuSummaryMap.entrySet()) {
            if (!processedSkus.contains(entry.getKey())) {
                hierarchicalSummaries.add(new HierarchicalSkuRevenueSummaryDto(entry.getValue(), new ArrayList<>()));
            }
        }

        // 6. Sort and return
        return hierarchicalSummaries.stream()
                .sorted(Comparator.comparing(dto -> dto.getParentSummary().getSku()))
                .collect(Collectors.toList());
    }
}