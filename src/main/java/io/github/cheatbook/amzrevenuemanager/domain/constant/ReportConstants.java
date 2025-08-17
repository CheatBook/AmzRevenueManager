package io.github.cheatbook.amzrevenuemanager.domain.constant;

/**
 * レポートに関する定数を定義するクラスです。
 */
public final class ReportConstants {

    /**
     * プライベートコンストラクタ
     */
    private ReportConstants() {
    }

    /**
     * 文字コード: UTF-8
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 文字コード: Shift_JIS
     */
    public static final String CHARSET_SHIFT_JIS = "Shift_JIS";

    /**
     * 広告レポートヘッダー: 日付
     */
    public static final String HEADER_AD_DATE = "日付";

    /**
     * 広告レポートヘッダー: キャンペーン名
     */
    public static final String HEADER_AD_CAMPAIGN_NAME = "キャンペーン名";

    /**
     * 広告レポートヘッダー: 広告費
     */
    public static final String HEADER_AD_COST = "広告費";

    /**
     * 決済レポートヘッダー: settlement-id
     */
    public static final String HEADER_SETTLEMENT_ID = "settlement-id";

    /**
     * 決済レポートヘッダー: settlement-start-date
     */
    public static final String HEADER_SETTLEMENT_START_DATE = "settlement-start-date";

    /**
     * 決済レポートヘッダー: settlement-end-date
     */
    public static final String HEADER_SETTLEMENT_END_DATE = "settlement-end-date";

    /**
     * 決済レポートヘッダー: currency
     */
    public static final String HEADER_CURRENCY = "currency";

    /**
     * 決済レポートヘッダー: total-amount
     */
    public static final String HEADER_TOTAL_AMOUNT = "total-amount";

    /**
     * 決済レポートヘッダー: transaction-type
     */
    public static final String HEADER_TRANSACTION_TYPE = "transaction-type";

    /**
     * 決済レポートヘッダー: order-id
     */
    public static final String HEADER_ORDER_ID = "order-id";

    /**
     * 決済レポートヘッダー: order-item-code
     */
    public static final String HEADER_ORDER_ITEM_CODE = "order-item-code";

    /**
     * 決済レポートヘッダー: merchant-order-item-id
     */
    public static final String HEADER_MERCHANT_ORDER_ITEM_ID = "merchant-order-item-id";

    /**
     * 決済レポートヘッダー: merchant-adjustment-item-id
     */
    public static final String HEADER_MERCHANT_ADJUSTMENT_ITEM_ID = "merchant-adjustment-item-id";

    /**
     * 決済レポートヘッダー: amount-type
     */
    public static final String HEADER_AMOUNT_TYPE = "amount-type";

    /**
     * 決済レポートヘッダー: amount-description
     */
    public static final String HEADER_AMOUNT_DESCRIPTION = "amount-description";

    /**
     * 決済レポートヘッダー: amount
     */
    public static final String HEADER_AMOUNT = "amount";

    /**
     * 決済レポートヘッダー: posted-date-time
     */
    public static final String HEADER_POSTED_DATE_TIME = "posted-date-time";

    /**
     * 決済レポートヘッダー: sku
     */
    public static final String HEADER_SKU = "sku";

    /**
     * 決済レポートヘッダー: quantity-purchased
     */
    public static final String HEADER_QUANTITY_PURCHASED = "quantity-purchased";
}