package io.github.cheatbook.amzrevenuemanager.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AmountDescription {
    // Sales
    PRINCIPAL("Principal"),

    // Taxes
    TAX("Tax"),
    SHIPPING_TAX("ShippingTax"),
    TAX_DISCOUNT("TaxDiscount"),

    // Shipping
    SHIPPING("Shipping"),
    SHIPPING_CHARGEBACK("ShippingChargeback"),

    // Fees
    COMMISSION("Commission"),
    FBA_PER_UNIT_FULFILLMENT_FEE("FBAPerUnitFulfillmentFee"),
    REFUND_COMMISSION("RefundCommission");

    private final String value;

    public static AmountDescription fromString(String text) {
        return Stream.of(values())
                .filter(e -> e.value.equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}