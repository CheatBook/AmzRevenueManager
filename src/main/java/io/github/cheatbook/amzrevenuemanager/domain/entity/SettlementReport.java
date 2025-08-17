package io.github.cheatbook.amzrevenuemanager.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

/**
 * 決済レポートエンティティです。
 */
@Entity
@Data
public class SettlementReport {

    /**
     * 決済ID
     */
    @Id
    @Column(name = "settlement_id")
    private Long id;

    /**
     * 決済開始日
     */
    private Date settlementStartDate;

    /**
     * 決済終了日
     */
    private Date settlementEndDate;

    /**
     * 通貨
     */
    private String currency;

    /**
     * 合計金額
     */
    private Double totalAmount;
}