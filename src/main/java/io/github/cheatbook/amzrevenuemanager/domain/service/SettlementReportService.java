package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementReport;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementReportService {

    private final SettlementReportRepository settlementReportRepository;

    @Transactional
    public void save(SettlementReport settlementReport) {
        settlementReportRepository.save(settlementReport);
    }
}