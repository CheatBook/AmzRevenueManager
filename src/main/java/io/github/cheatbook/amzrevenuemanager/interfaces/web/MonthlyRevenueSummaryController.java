package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.cheatbook.amzrevenuemanager.application.service.MonthlySummaryApplicationService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/monthly-summary")
@RequiredArgsConstructor
public class MonthlyRevenueSummaryController {

    private final MonthlySummaryApplicationService monthlySummaryApplicationService;

    @GetMapping
    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        return monthlySummaryApplicationService.getMonthlyRevenueSummary();
    }
}