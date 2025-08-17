package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.PurchaseRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.summary.SummaryAggregator;
import io.github.cheatbook.amzrevenuemanager.domain.summary.calculator.AdvertisementCostCalculator;
import io.github.cheatbook.amzrevenuemanager.domain.summary.calculator.ProductCostCalculator;
import io.github.cheatbook.amzrevenuemanager.domain.summary.calculator.SalesCalculator;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 月次収益サマリーを提供するサービスクラス。
 */
@Service
@RequiredArgsConstructor
public class MonthlyRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final CacheableDataService cacheableDataService;
    private final AdvertisementRepository advertisementRepository;
    private final PurchaseRepository purchaseRepository;
    private final SalesCalculator salesCalculator;
    private final AdvertisementCostCalculator advertisementCostCalculator;
    private final ProductCostCalculator productCostCalculator;
    private final SummaryAggregator summaryAggregator;

    /**
     * 月次収益サマリーを取得する。
     *
     * @return 親SKUごとの月次収益サマリーDTOのリスト
     */
    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        // 必要なデータをすべて取得
        List<Settlement> allSettlements = settlementRepository.findAll();
        List<SkuName> skuNames = cacheableDataService.findAllSkuNames();
        List<Advertisement> allAdvertisements = advertisementRepository.findAll();
        List<Purchase> allPurchases = purchaseRepository.findAll();

        // 親SKUごとの平均単価を計算
        Map<String, Double> averageUnitPriceByParentSku = allPurchases.stream()
                .filter(p -> p.getUnitPrice() != null && p.getParentSku() != null)
                .collect(Collectors.groupingBy(Purchase::getParentSku,
                        Collectors.averagingDouble(Purchase::getUnitPrice)));

        // 各データを月ごとにグループ化
        Map<YearMonth, List<Settlement>> settlementsByMonth = allSettlements.stream()
                .collect(Collectors.groupingBy(s -> YearMonth.from(s.getPostedDateTime())));

        Map<YearMonth, List<Advertisement>> advertisementsByMonth = allAdvertisements.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.from(a.getId().getDate())));

        Set<YearMonth> yearMonths = settlementsByMonth.keySet();

        List<ParentSkuMonthlySummaryDto> summaryList = new ArrayList<>();

        // 親SKUと日本語名のマップを作成
        Map<String, String> parentSkuToJapaneseNameMap = skuNames.stream()
                .filter(s -> s.getParentSku() != null && !s.getParentSku().isEmpty() && s.getJapaneseName() != null)
                .collect(Collectors.toMap(SkuName::getParentSku, SkuName::getJapaneseName, (existing, replacement) -> existing));

        // 月ごとにサマリーを計算
        for (YearMonth yearMonth : yearMonths) {
            List<Settlement> monthlySettlements = settlementsByMonth.getOrDefault(yearMonth, Collections.emptyList());
            List<Advertisement> monthlyAdvertisements = advertisementsByMonth.getOrDefault(yearMonth, Collections.emptyList());
            // 各計算クラスを呼び出してサマリーを計算
            Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap = salesCalculator.calculate(monthlySettlements, skuNames, parentSkuToJapaneseNameMap);
            advertisementCostCalculator.calculate(monthlyAdvertisements, parentSkuSummaryMap, parentSkuToJapaneseNameMap);
            productCostCalculator.calculate(parentSkuSummaryMap, averageUnitPriceByParentSku);

            // 月次合計を集計
            ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto monthlyTotal = summaryAggregator.aggregate(parentSkuSummaryMap);

            summaryList.add(ParentSkuMonthlySummaryDto.builder()
                    .year(yearMonth.getYear())
                    .month(yearMonth.getMonthValue())
                    .parentSkuRevenues(new ArrayList<>(parentSkuSummaryMap.values()))
                    .monthlyTotal(monthlyTotal)
                    .build());
        }

        // 年月の降順でソート
        summaryList.sort(Comparator.comparing(ParentSkuMonthlySummaryDto::getYear).reversed()
                .thenComparing(Comparator.comparing(ParentSkuMonthlySummaryDto::getMonth).reversed()));

        return summaryList;
    }
}