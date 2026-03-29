package io.github.cheatbook.amzrevenuemanager.domain.summary.calculator;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品原価を計算するクラスです。
 */
@Component
public class ProductCostCalculator {

    /**
     * 商品原価を計算し、親SKUごとのサマリーマップを更新します。
     *
     * @param parentSkuSummaryMap         親SKUごとのサマリーマップ
     * @param averageUnitPriceByParentSku 親SKUごとの平均単価マップ
     */
    public void calculate(Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap, Map<String, Double> averageUnitPriceByParentSku) {
        parentSkuSummaryMap.forEach((parentSku, summary) -> {
            Double averageUnitPrice = averageUnitPriceByParentSku.get(parentSku);
            if (averageUnitPrice != null) {
                BigDecimal unitPrice = BigDecimal.valueOf(averageUnitPrice);
                BigDecimal orderCount = new BigDecimal(summary.getOrderCount());
                BigDecimal cost = unitPrice.multiply(orderCount);
                summary.setProductCost(cost);
            }
        });
    }
}