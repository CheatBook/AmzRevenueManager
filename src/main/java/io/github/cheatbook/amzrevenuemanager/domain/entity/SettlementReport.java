package io.github.cheatbook.amzrevenuemanager.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class SettlementReport {

    @Id
    @Column(name = "settlement_id")
    private Long id;
    private Date settlementStartDate;
    private Date settlementEndDate;
    private String currency;
    private Double totalAmount;
}