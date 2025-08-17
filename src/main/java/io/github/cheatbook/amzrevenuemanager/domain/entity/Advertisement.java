package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Advertisement {

    @EmbeddedId
    private AdvertisementId id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "total_cost")
    private BigDecimal totalCost;
}