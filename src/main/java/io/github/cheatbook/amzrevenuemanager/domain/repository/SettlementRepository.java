package io.github.cheatbook.amzrevenuemanager.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementId;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, SettlementId> {
    @Query("SELECT DISTINCT t.sku FROM Settlement t")
    List<String> findDistinctSkus();
    List<Settlement> findByPostedDateTimeBetween(LocalDateTime start, LocalDateTime end);
    boolean existsBySettlementId(String settlementId);
}