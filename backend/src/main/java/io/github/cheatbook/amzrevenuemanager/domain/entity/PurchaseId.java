package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仕入れIDクラスです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseId implements Serializable {

    /**
     * 親SKU
     */
    private String parentSku;

    /**
     * 仕入れ日
     */
    private LocalDate purchaseDate;

    /**
     * オブジェクトの等価性を比較します。
     *
     * @param o 比較対象のオブジェクト
     * @return 等しい場合はtrue、そうでない場合はfalse
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseId that = (PurchaseId) o;
        return Objects.equals(parentSku, that.parentSku) &&
               Objects.equals(purchaseDate, that.purchaseDate);
    }

    /**
     * オブジェクトのハッシュコードを返します。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(parentSku, purchaseDate);
    }
}