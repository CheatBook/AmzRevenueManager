package io.github.cheatbook.amzrevenuemanager.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 売上日エンティティです。
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SalesDate {

    /**
     * Amazon注文ID
     */
    @Id
    private String amazonOrderId;

    /**
     * 購入日
     */
    private LocalDateTime purchaseDate;
}