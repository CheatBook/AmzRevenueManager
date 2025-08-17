package io.github.cheatbook.amzrevenuemanager.domain.constant;

public final class ReportConstants {

    private ReportConstants() {
    }

    // Character Encodings
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_SHIFT_JIS = "Shift_JIS";

    // Advertisement Report Headers
    public static final String HEADER_AD_DATE = "日付";
    public static final String HEADER_AD_CAMPAIGN_NAME = "キャンペーン名";
    public static final String HEADER_AD_COST = "広告費";

    // Settlement Report Headers
    public static final String HEADER_SETTLEMENT_ID = "settlement-id";
    public static final String HEADER_SETTLEMENT_START_DATE = "settlement-start-date";
    public static final String HEADER_SETTLEMENT_END_DATE = "settlement-end-date";
    public static final String HEADER_CURRENCY = "currency";
    public static final String HEADER_TOTAL_AMOUNT = "total-amount";
    public static final String HEADER_TRANSACTION_TYPE = "transaction-type";
    public static final String HEADER_ORDER_ID = "order-id";
    public static final String HEADER_ORDER_ITEM_CODE = "order-item-code";
    public static final String HEADER_MERCHANT_ORDER_ITEM_ID = "merchant-order-item-id";
    public static final String HEADER_MERCHANT_ADJUSTMENT_ITEM_ID = "merchant-adjustment-item-id";
    public static final String HEADER_AMOUNT_TYPE = "amount-type";
    public static final String HEADER_AMOUNT_DESCRIPTION = "amount-description";
    public static final String HEADER_AMOUNT = "amount";
    public static final String HEADER_POSTED_DATE_TIME = "posted-date-time";
    public static final String HEADER_SKU = "sku";
    public static final String HEADER_QUANTITY_PURCHASED = "quantity-purchased";
}