package io.github.cheatbook.amzrevenuemanager.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementId;
import io.github.cheatbook.amzrevenuemanager.domain.summary.ParentSkuSummaryData;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, SettlementId> {
    @Query("SELECT DISTINCT t.sku FROM Settlement t")
    List<String> findDistinctSkus();

    @Query("SELECT DISTINCT CAST(SUBSTRING(CAST(s.postedDateTime AS string), 1, 7) || '-01T00:00:00' AS LocalDateTime) FROM Settlement s")
    List<LocalDateTime> findDistinctMonths();
    
    List<Settlement> findByPostedDateTimeBetween(LocalDateTime start, LocalDateTime end);
    boolean existsBySettlementId(String settlementId);

    @Query("SELECT new io.github.cheatbook.amzrevenuemanager.domain.summary.ParentSkuSummaryData(" +
           "COALESCE(sn.parentSku, s.sku), " +
           "SUM(CASE WHEN s.amountDescription = 'Principal' THEN s.amount ELSE 0 END), " +
           "SUM(CASE WHEN s.amountDescription IN ('Commission', 'FBAPerUnitFulfillmentFee', 'ShippingChargeback', 'RefundCommission') THEN s.amount ELSE 0 END), " +
           "SUM(CASE WHEN s.amountDescription = 'Shipping' THEN s.amount ELSE 0 END), " +
           "SUM(CASE WHEN s.amountDescription IN ('Tax', 'ShippingTax') THEN s.amount ELSE 0 END), " +
           "SUM(s.quantityPurchased)) " +
           "FROM Settlement s LEFT JOIN SkuName sn ON s.sku = sn.sku " +
           "GROUP BY COALESCE(sn.parentSku, s.sku)")
    List<ParentSkuSummaryData> findParentSkuSummaryData();
}