package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private LocalDate date;
    private String portfolioName;
    private String campaignName;
    private String adGroupName;
    private String targeting;
    private String matchType;
    private Integer impressions;
    private Integer clicks;
    private BigDecimal cost;
    private BigDecimal sales;
    private String advertisedSku;
}