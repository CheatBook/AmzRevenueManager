package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PurchaseId.class)
public class Purchase {

    @Id
    private String parentSku;

    @Id
    private LocalDate purchaseDate;

    private Integer quantity;

    private Integer amount;

    private Integer tariff;

    private Double unitPrice;
}