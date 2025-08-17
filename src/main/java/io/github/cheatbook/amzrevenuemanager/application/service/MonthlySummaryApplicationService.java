package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.service.MonthlyRevenueSummaryService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlySummaryApplicationService {

    private final MonthlyRevenueSummaryService monthlyRevenueSummaryService;

    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        return monthlyRevenueSummaryService.getMonthlyRevenueSummary();
    }
}