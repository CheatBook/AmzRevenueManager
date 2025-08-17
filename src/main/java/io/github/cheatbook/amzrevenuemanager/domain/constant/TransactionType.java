package io.github.cheatbook.amzrevenuemanager.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    ORDER("Order"),
    REFUND("Refund"),
    OTHER("other-transaction");

    private final String value;

    public static TransactionType fromString(String text) {
        for (TransactionType b : TransactionType.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}