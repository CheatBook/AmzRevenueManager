package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParentSkuRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final SkuNameRepository skuNameRepository;

    public List<SkuRevenueSummaryDto> getParentSkuRevenueSummary() {
        List<Settlement> settlements = settlementRepository.findAll();
        List<SkuName> skuNames = skuNameRepository.findAll();

        Map<String, String> skuToParentSkuMap = skuNames.stream()
                .filter(an -> an.getParentSku() != null)
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getParentSku));

        Map<String, String> skuToJapaneseNameMap = skuNames.stream()
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getJapaneseName));

        Map<String, SkuRevenueSummaryDto> summaryMap = settlements.stream()
                .collect(Collectors.groupingBy(settlement -> {
                    String sku = settlement.getSku();
                    return skuToParentSkuMap.getOrDefault(sku, sku); // 親SKUがあれば親SKU、なければ自身のSKU
                }, Collectors.reducing(
                        new SkuRevenueSummaryDto("", "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0), // 新しいコンストラクタに合わせる
                        settlement -> {
                            BigDecimal amount = settlement.getAmount();
                            String amountDescription = settlement.getAmountDescription(); // amountTypeをamountDescriptionに変更
                            Integer quantityPurchased = settlement.getQuantityPurchased() != null ? settlement.getQuantityPurchased() : 0;

                            BigDecimal totalRevenue = BigDecimal.ZERO;
                            BigDecimal totalCommission = BigDecimal.ZERO;
                            BigDecimal totalShipping = BigDecimal.ZERO;
                            BigDecimal totalTax = BigDecimal.ZERO;

                            if (amountDescription != null) { // nullチェックを追加
                                switch (amountDescription) {
                                    case "Principal":
                                        totalRevenue = totalRevenue.add(amount);
                                        break;
                                    case "Tax":
                                    case "ShippingTax": // ShippingTaxを追加
                                        totalTax = totalTax.add(amount);
                                        break;
                                    case "Shipping":
                                        totalShipping = totalShipping.add(amount);
                                        break;
                                    case "Commission":
                                    case "FBAPerUnitFulfillmentFee": // FBAPerUnitFulfillmentFeeを追加
                                    case "ShippingChargeback": // ShippingChargebackを追加
                                    case "RefundCommission": // RefundCommissionを追加
                                        totalCommission = totalCommission.add(amount);
                                        break;
                                    default:
                                        break;
                                }
                            }

                            return new SkuRevenueSummaryDto(
                                    settlement.getSku(), // 仮のSKU、後で親SKUに置き換える
                                    "", // 仮の日本語名、後で親SKUの日本語名に置き換える
                                    totalRevenue,
                                    totalCommission,
                                    totalShipping,
                                    totalTax,
                                    totalRevenue.add(totalCommission).add(totalShipping), // GrossProfitの計算を修正
                                    settlement.getOrderId() != null ? 1 : 0, // orderIdのユニーク数をカウントするため、ここでは1または0
                                    quantityPurchased
                            );
                        },
                        (dto1, dto2) -> {
                            dto1.setTotalRevenue(dto1.getTotalRevenue().add(dto2.getTotalRevenue()));
                            dto1.setTotalCommission(dto1.getTotalCommission().add(dto2.getTotalCommission()));
                            dto1.setTotalShipping(dto1.getTotalShipping().add(dto2.getTotalShipping()));
                            dto1.setTotalTax(dto1.getTotalTax().add(dto2.getTotalTax()));
                            dto1.setGrossProfit(dto1.getGrossProfit().add(dto2.getGrossProfit())); // GrossProfitも加算
                            dto1.setSettlementCount(dto1.getSettlementCount() + dto2.getSettlementCount());
                            dto1.setTotalQuantityPurchased(dto1.getTotalQuantityPurchased() + dto2.getTotalQuantityPurchased()); // 新しいフィールドを加算
                            return dto1;
                        }
                )));

        // orderIdのユニーク数を正確に計算するために、別途処理を追加
        settlements.stream()
            .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
            .collect(Collectors.groupingBy(settlement -> {
                String sku = settlement.getSku();
                return skuToParentSkuMap.getOrDefault(sku, sku);
            }, Collectors.mapping(Settlement::getOrderId, Collectors.toSet())))
            .forEach((parentOrChildSku, orderIds) -> {
                if (summaryMap.containsKey(parentOrChildSku)) {
                    summaryMap.get(parentOrChildSku).setSettlementCount(orderIds.size());
                }
            });

        return summaryMap.entrySet().stream()
                .map(entry -> {
                    String parentSku = entry.getKey();
                    SkuRevenueSummaryDto dto = entry.getValue();
                    dto.setSku(parentSku);
                    dto.setJapaneseName(skuToJapaneseNameMap.getOrDefault(parentSku, ""));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}