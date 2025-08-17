/**
 * アプリケーションサービス層のパッケージです。
 * ドメインサービスを呼び出し、Web層との連携を担当します。
 */
package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.service.MonthlyRevenueSummaryService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 月次収益サマリーに関するアプリケーションサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class MonthlySummaryApplicationService {

    /**
     * 月次収益サマリーサービスクラス
     */
    private final MonthlyRevenueSummaryService monthlyRevenueSummaryService;

    /**
     * 月次収益サマリーを取得します。
     *
     * @return 親SKUごとの月次収益サマリーDTOのリスト
     */
    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        return monthlyRevenueSummaryService.getMonthlyRevenueSummary();
    }
}