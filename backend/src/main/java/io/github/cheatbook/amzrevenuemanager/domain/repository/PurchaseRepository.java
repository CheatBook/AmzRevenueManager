package io.github.cheatbook.amzrevenuemanager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.entity.PurchaseId;

import java.time.LocalDate;
import java.util.List;

/**
 * 仕入れリポジトリです。
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, PurchaseId> {
    /**
     * 指定された期間内の仕入れを取得します。
     *
     * @param startDate 開始日
     * @param endDate   終了日
     * @return 仕入れのリスト
     */
    List<Purchase> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
}