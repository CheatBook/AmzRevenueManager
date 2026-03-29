package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.cheatbook.amzrevenuemanager.application.service.MonthlySummaryApplicationService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.RequiredArgsConstructor;

/**
 * 月次収益サマリーに関するコントローラークラスです。
 */
@RestController
@RequestMapping("/api/monthly-summary")
@RequiredArgsConstructor
public class MonthlyRevenueSummaryController {

    /**
     * 月次サマリーアプリケーションサービス
     */
    private final MonthlySummaryApplicationService monthlySummaryApplicationService;

    /**
     * 月次収益サマリーを取得します。
     *
     * @return 親SKUごとの月次収益サマリーDTOのリスト
     */
    @GetMapping
    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        return monthlySummaryApplicationService.getMonthlyRevenueSummary();
    }
}