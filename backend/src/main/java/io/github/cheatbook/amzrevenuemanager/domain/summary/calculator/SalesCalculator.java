package io.github.cheatbook.amzrevenuemanager.domain.summary.calculator;

import io.github.cheatbook.amzrevenuemanager.domain.constant.AmountDescription;
import io.github.cheatbook.amzrevenuemanager.domain.constant.Miscellaneous;
import io.github.cheatbook.amzrevenuemanager.domain.constant.TransactionType;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 売上を計算するクラスです。
 */
@Component
public class SalesCalculator {

    /**
     * 売上を計算し、親SKUごとのサマリーマップを返します。
     *
     * @param settlements                決済リスト
     * @param skuNames                   SKU名リスト
     * @param parentSkuToJapaneseNameMap 親SKUと日本語名のマップ
     * @return 親SKUごとのサマリーマップ
     */
    public Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> calculate(
            List<Settlement> settlements, List<SkuName> skuNames, Map<String, String> parentSkuToJapaneseNameMap) {

        Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap = new HashMap<>();
        Map<String, List<String>> parentSkuToOrderIdsMap = new HashMap<>();

        Map<String, String> skuToParentSkuMap = skuNames.stream()
                .filter(s -> s.getParentSku() != null && !s.getParentSku().isEmpty())
                .collect(Collectors.toMap(SkuName::getSku, SkuName::getParentSku, (existing, replacement) -> existing));

        for (Settlement settlement : settlements) {
            String sku = settlement.getSku();
            String parentSku;

            if (TransactionType.OTHER.getValue().equals(settlement.getTransactionType())) {
                parentSku = Miscellaneous.OTHER_TRANSACTION_PARENT_SKU;
            } else {
                parentSku = skuToParentSkuMap.get(sku);
                if (parentSku == null) {
                    parentSku = sku != null ? sku : Miscellaneous.NOT_APPLICABLE;
                }
            }
            settlement.setParentSku(parentSku);

            ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto summary = parentSkuSummaryMap
                    .computeIfAbsent(parentSku, k -> createEmptySummary(k, parentSkuToJapaneseNameMap));

            String amountDescriptionStr = settlement.getAmountDescription() != null ? settlement.getAmountDescription().trim() : "";
            AmountDescription amountDescription = AmountDescription.fromString(amountDescriptionStr);
            BigDecimal amount = settlement.getAmount();

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

            if (!Miscellaneous.OTHER_TRANSACTION_PARENT_SKU.equals(parentSku) && settlement.getOrderId() != null && !settlement.getOrderId().isEmpty()) {
                parentSkuToOrderIdsMap.computeIfAbsent(parentSku, k -> new ArrayList<>()).add(settlement.getOrderId());
            }
        }

        parentSkuSummaryMap.forEach((parentSku, summary) -> {
            summary.setOrderCount((int) parentSkuToOrderIdsMap.getOrDefault(parentSku, new ArrayList<>()).stream().distinct().count());
        });

        return parentSkuSummaryMap;
    }

    /**
     * 空のサマリーオブジェクトを作成します。
     *
     * @param parentSku                  親SKU
     * @param parentSkuToJapaneseNameMap 親SKUと日本語名のマップ
     * @return 空のサマリーオブジェクト
     */
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