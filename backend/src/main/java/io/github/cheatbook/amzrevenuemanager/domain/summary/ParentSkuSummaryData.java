/**
 * ドメイン層のサマリー関連のクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.summary;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 親SKUごとのサマリーデータを保持するクラスです。
 */
@Getter
@Setter
@AllArgsConstructor
public class ParentSkuSummaryData {
    /**
     * 親SKU
     */
    private String parentSku;

    /**
     * 合計収益
     */
    private BigDecimal totalRevenue;

    /**
     * 合計手数料
     */
    private BigDecimal totalCommission;

    /**
     * 合計送料
     */
    private BigDecimal totalShipping;

    /**
     * 合計税金
     */
    private BigDecimal totalTax;

    /**
     * 合計購入数量
     */
    private Long totalQuantityPurchased;
}