package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.cheatbook.amzrevenuemanager.domain.service.MonthlyRevenueSummaryService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/summary/monthly")
@RequiredArgsConstructor
public class MonthlyRevenueSummaryController {

    private final MonthlyRevenueSummaryService monthlyRevenueSummaryService;

    @GetMapping
    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        return monthlyRevenueSummaryService.getMonthlyRevenueSummary();
    }
}