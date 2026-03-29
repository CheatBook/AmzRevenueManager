package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.constant.AmountDescription;
import io.github.cheatbook.amzrevenuemanager.domain.constant.TransactionType;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 収益サマリーを計算するサービスクラス。
 */
@Service
public class RevenueSummaryService {

    /**
     * SKUごとの収益サマリーを計算する。
     *
     * @param settlements 決済リスト
     * @param skuNames    SKU名リスト
     * @return SKUをキー、SKU収益サマリーDTOを値とするマップ
     */
    public Map<String, SkuRevenueSummaryDto> calculateSkuRevenueSummaries(List<Settlement> settlements, List<SkuName> skuNames) {
        // SKUと日本語名のマップを作成
        Map<String, String> skuToJapaneseNameMap = skuNames.stream()
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getJapaneseName, (name1, name2) -> name1));

        // トランザクションタイプがOrderまたはRefundの決済情報のみを抽出
        List<Settlement> orderSettlements = settlements.stream()
                .filter(t -> TransactionType.ORDER.getValue().equals(t.getTransactionType()) || TransactionType.REFUND.getValue().equals(t.getTransactionType()))
                .collect(Collectors.toList());

        Map<String, SkuRevenueSummaryDto> skuSummaryMap = new HashMap<>();
        Map<String, Map<String, Integer>> skuToOrderIdQuantityMap = new HashMap<>();

        // 決済情報をループしてSKUごとに集計
        for (Settlement settlement : orderSettlements) {
            String sku = settlement.getSku();
            if (sku == null || sku.isEmpty()) {
                continue;
            }

            // SKUごとのサマリーDTOが存在しない場合は新規作成
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

            // 金額の説明に応じて各項目に金額を加算
            if (amountDescription != null) {
                AmountDescription desc = AmountDescription.fromString(amountDescription);
                switch (desc) {
                    case PRINCIPAL:
                        summary.setTotalRevenue(summary.getTotalRevenue().add(amount));
                        if (orderId != null && !orderId.isEmpty()) {
                            skuToOrderIdQuantityMap.computeIfAbsent(sku, k -> new HashMap<>())
                                    .merge(orderId, quantityPurchased, Integer::max);
                        }
                        break;
                    case TAX:
                    case SHIPPING_TAX:
                        summary.setTotalTax(summary.getTotalTax().add(amount));
                        break;
                    case SHIPPING:
                        summary.setTotalShipping(summary.getTotalShipping().add(amount));
                        break;
                    case COMMISSION:
                    case FBA_PER_UNIT_FULFILLMENT_FEE:
                    case SHIPPING_CHARGEBACK:
                    case REFUND_COMMISSION:
                        summary.setTotalCommission(summary.getTotalCommission().add(amount));
                        break;
                    default:
                        break;
                }
            }
        }

        // SKUごとの注文件数を計算
        orderSettlements.stream()
                .collect(Collectors.groupingBy(Settlement::getSku, Collectors.mapping(Settlement::getOrderId, Collectors.toSet())))
                .forEach((sku, orderIds) -> {
                    if (skuSummaryMap.containsKey(sku)) {
                        skuSummaryMap.get(sku).setSettlementCount(orderIds.size());
                    }
                });

        // SKUごとの合計購入数量を計算
        skuToOrderIdQuantityMap.forEach((sku, orderIdQuantityMap) -> {
            if (skuSummaryMap.containsKey(sku)) {
                int totalQuantity = orderIdQuantityMap.values().stream().mapToInt(Integer::intValue).sum();
                skuSummaryMap.get(sku).setTotalQuantityPurchased(totalQuantity);
            }
        });

        // 売上総利益を計算
        skuSummaryMap.values().forEach(summary -> {
            BigDecimal grossProfit = summary.getTotalRevenue()
                    .add(summary.getTotalCommission())
                    .add(summary.getTotalShipping());
            summary.setGrossProfit(grossProfit);
        });

        return skuSummaryMap;
    }
}