package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku")
    private String sku;

    @Column(name = "asin")
    private String asin;

    @Column(name = "campaign_name")
    private String campaignName;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "impression")
    private Integer impression;

    @Column(name = "click_count")
    private Integer clickCount;

    @Column(name = "date")
    private LocalDate date;
}