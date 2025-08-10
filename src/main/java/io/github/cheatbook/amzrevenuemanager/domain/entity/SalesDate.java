package io.github.cheatbook.amzrevenuemanager.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SalesDate {

    @Id
    private String amazonOrderId;

    private LocalDateTime purchaseDate;
}