package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementReport;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 決済レポートに関するサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class SettlementReportService {

    /**
     * 決済レポートリポジトリ
     */
    private final SettlementReportRepository settlementReportRepository;

    /**
     * 決済レポートを保存します。
     *
     * @param settlementReport 決済レポート
     */
    @Transactional
    public void save(SettlementReport settlementReport) {
        settlementReportRepository.save(settlementReport);
    }
}