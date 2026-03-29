/**
 * ドメイン層の定数を定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * 金額の説明を表す列挙型です。
 * <p>
 * Amazonの決済レポートに含まれる金額の種類を定義します。
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum AmountDescription {
    /**
     * 売上
     */
    PRINCIPAL("Principal"),

    /**
     * 税金
     */
    TAX("Tax"),
    /**
     * 送料税
     */
    SHIPPING_TAX("ShippingTax"),
    /**
     * 税割引
     */
    TAX_DISCOUNT("TaxDiscount"),

    /**
     * 送料
     */
    SHIPPING("Shipping"),
    /**
     * 送料チャージバック
     */
    SHIPPING_CHARGEBACK("ShippingChargeback"),

    /**
     * 手数料
     */
    COMMISSION("Commission"),
    /**
     * FBA出荷手数料
     */
    FBA_PER_UNIT_FULFILLMENT_FEE("FBAPerUnitFulfillmentFee"),
    /**
     * 返金手数料
     */
    REFUND_COMMISSION("RefundCommission");

    /**
     * 金額の説明の文字列表現
     */
    private final String value;

    /**
     * 文字列からAmountDescriptionを取得します。
     *
     * @param text 金額の説明の文字列
     * @return 対応するAmountDescription、見つからない場合はnull
     */
    public static AmountDescription fromString(String text) {
        return Stream.of(values())
                .filter(e -> e.value.equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}