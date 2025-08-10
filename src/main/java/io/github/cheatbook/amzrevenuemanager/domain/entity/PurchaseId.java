package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseId implements Serializable {

    private String parentSku;
    private LocalDate purchaseDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseId that = (PurchaseId) o;
        return Objects.equals(parentSku, that.parentSku) &&
               Objects.equals(purchaseDate, that.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentSku, purchaseDate);
    }
}