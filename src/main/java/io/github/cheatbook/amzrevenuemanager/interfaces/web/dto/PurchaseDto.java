package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PurchaseDto {

    private String parentSku;
    private LocalDate purchaseDate;
    private Integer quantity;
    private Integer amount;
    private Integer tariff;
}