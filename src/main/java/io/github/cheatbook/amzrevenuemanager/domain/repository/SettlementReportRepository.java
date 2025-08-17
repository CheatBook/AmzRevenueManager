package io.github.cheatbook.amzrevenuemanager.domain.repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementReport;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 決済レポートリポジトリです。
 */
public interface SettlementReportRepository extends JpaRepository<SettlementReport, Long> {
}