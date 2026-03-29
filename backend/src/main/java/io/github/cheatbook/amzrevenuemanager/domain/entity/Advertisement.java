/**
 * ドメイン層のエンティティを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

/**
 * 広告エンティティです。
 */
@Entity
@Data
public class Advertisement {

    /**
     * 広告ID（埋め込みID）
     */
    @EmbeddedId
    private AdvertisementId id;

    /**
     * 商品名
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * 合計コスト
     */
    @Column(name = "total_cost")
    private BigDecimal totalCost;
}