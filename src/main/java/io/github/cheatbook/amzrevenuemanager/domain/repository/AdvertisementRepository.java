/**
 * ドメイン層のリポジトリを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.AdvertisementId;

import java.time.LocalDate;
import java.util.List;

/**
 * 広告リポジトリです。
 */
public interface AdvertisementRepository extends JpaRepository<Advertisement, AdvertisementId> {
    /**
     * 指定された期間内の広告を取得します。
     *
     * @param startDate 開始日
     * @param endDate   終了日
     * @return 広告のリスト
     */
    List<Advertisement> findByIdDateBetween(LocalDate startDate, LocalDate endDate);
}