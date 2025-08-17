package io.github.cheatbook.amzrevenuemanager.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * トランザクションの種類を表す列挙型です。
 */
@Getter
@RequiredArgsConstructor
public enum TransactionType {
    /**
     * 注文
     */
    ORDER("Order"),

    /**
     * 返金
     */
    REFUND("Refund"),

    /**
     * その他
     */
    OTHER("other-transaction");

    /**
     * トランザクションの種類の文字列表現
     */
    private final String value;

    /**
     * 文字列からTransactionTypeを取得します。
     *
     * @param text トランザクションの種類の文字列
     * @return 対応するTransactionType、見つからない場合はnull
     */
    public static TransactionType fromString(String text) {
        for (TransactionType b : TransactionType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}