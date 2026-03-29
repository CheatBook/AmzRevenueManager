package io.github.cheatbook.amzrevenuemanager.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKU名エンティティです。
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuName {

    /**
     * SKU
     */
    @Id
    private String sku;

    /**
     * 日本語名
     */
    private String japaneseName;

    /**
     * 親SKU
     */
    private String parentSku;
}