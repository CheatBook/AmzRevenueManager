package io.github.cheatbook.amzrevenuemanager.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 広告ID（埋め込みID）クラスです。
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementId implements Serializable {

    /**
     * 親SKU
     */
    private String parentSku;

    /**
     * 日付
     */
    private LocalDate date;

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
        AdvertisementId that = (AdvertisementId) o;
        return Objects.equals(parentSku, that.parentSku) &&
               Objects.equals(date, that.date);
    }

    /**
     * オブジェクトのハッシュコードを返します。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(parentSku, date);
    }
}