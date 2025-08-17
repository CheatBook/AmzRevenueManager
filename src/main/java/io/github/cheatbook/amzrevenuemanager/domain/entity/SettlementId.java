package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 決済IDクラスです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementId implements Serializable {
    /**
     * 決済ID
     */
    private String settlementId;

    /**
     * 注文ID
     */
    private String orderId;

    /**
     * 注文商品コード
     */
    private Long orderItemCode;

    /**
     * 金額の説明
     */
    private String amountDescription;

    /**
     * 計上日時
     */
    private LocalDateTime postedDateTime;
}