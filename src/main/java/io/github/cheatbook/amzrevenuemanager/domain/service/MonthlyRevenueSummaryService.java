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
import io.github.cheatbook.amzrevenuemanager.domain.summary.context.MonthlySummaryContext;
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

@Service
@RequiredArgsConstructor
public class MonthlyRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final SkuNameRepository skuNameRepository;
    private final AdvertisementRepository advertisementRepository;
    private final PurchaseRepository purchaseRepository;
    private final SalesCalculator salesCalculator;
    private final AdvertisementCostCalculator advertisementCostCalculator;
    private final ProductCostCalculator productCostCalculator;
    private final SummaryAggregator summaryAggregator;

    public List<ParentSkuMonthlySummaryDto> getMonthlyRevenueSummary() {
        List<Settlement> allSettlements = settlementRepository.findAll();
        List<SkuName> skuNames = skuNameRepository.findAll();
        List<Advertisement> allAdvertisements = advertisementRepository.findAll();
        List<Purchase> allPurchases = purchaseRepository.findAll();

        Map<YearMonth, List<Settlement>> settlementsByMonth = allSettlements.stream()
                .collect(Collectors.groupingBy(s -> YearMonth.from(s.getPostedDateTime())));

        Map<YearMonth, List<Advertisement>> advertisementsByMonth = allAdvertisements.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.from(a.getId().getDate())));

        Map<YearMonth, List<Purchase>> purchasesByMonth = allPurchases.stream()
                .collect(Collectors.groupingBy(p -> YearMonth.from(p.getPurchaseDate())));

        Set<YearMonth> yearMonths = settlementsByMonth.keySet();

        List<ParentSkuMonthlySummaryDto> summaryList = new ArrayList<>();

        for (YearMonth yearMonth : yearMonths) {
            List<Settlement> monthlySettlements = settlementsByMonth.getOrDefault(yearMonth, Collections.emptyList());
            List<Advertisement> monthlyAdvertisements = advertisementsByMonth.getOrDefault(yearMonth, Collections.emptyList());
            List<Purchase> monthlyPurchases = purchasesByMonth.getOrDefault(yearMonth, Collections.emptyList());

            MonthlySummaryContext monthlyContext = new MonthlySummaryContext(monthlySettlements, skuNames, monthlyAdvertisements, monthlyPurchases);
            monthlyContext.setParentSkuToJapaneseNameMap(skuNames.stream()
                    .filter(s -> s.getParentSku() != null && !s.getParentSku().isEmpty() && s.getJapaneseName() != null)
                    .collect(Collectors.toMap(SkuName::getParentSku, SkuName::getJapaneseName, (existing, replacement) -> existing)));

            salesCalculator.calculate(monthlyContext);
            advertisementCostCalculator.calculate(monthlyContext);
            productCostCalculator.calculate(monthlyContext);

            ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto monthlyTotal = summaryAggregator.aggregate(monthlyContext);

            summaryList.add(ParentSkuMonthlySummaryDto.builder()
                    .year(yearMonth.getYear())
                    .month(yearMonth.getMonthValue())
                    .parentSkuRevenues(new ArrayList<>(monthlyContext.getParentSkuSummaryMap().values()))
                    .monthlyTotal(monthlyTotal)
                    .build());
        }

        summaryList.sort(Comparator.comparing(ParentSkuMonthlySummaryDto::getYear).reversed()
                .thenComparing(Comparator.comparing(ParentSkuMonthlySummaryDto::getMonth).reversed()));

        return summaryList;
    }
}