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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        List<Settlement> settlements = settlementRepository.findAll();
        List<SkuName> skuNames = skuNameRepository.findAll();
        List<Advertisement> advertisements = advertisementRepository.findAll();
        List<Purchase> purchases = purchaseRepository.findAll();

        MonthlySummaryContext context = new MonthlySummaryContext(settlements, skuNames, advertisements, purchases);
        context.setParentSkuToJapaneseNameMap(skuNames.stream()
                .filter(s -> s.getParentSku() != null && !s.getParentSku().isEmpty() && s.getJapaneseName() != null)
                .collect(Collectors.toMap(SkuName::getParentSku, SkuName::getJapaneseName, (existing, replacement) -> existing)));

        salesCalculator.calculate(context); // This calculates transactionsByMonth

        List<ParentSkuMonthlySummaryDto> summaryList = new ArrayList<>();
        Map<YearMonth, List<Settlement>> transactionsByMonth = context.getTransactionsByMonth();

        for (Map.Entry<YearMonth, List<Settlement>> entry : transactionsByMonth.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            List<Settlement> monthlyTransactions = entry.getValue();

            // Create a new context for each month to ensure data isolation
            MonthlySummaryContext monthlyContext = new MonthlySummaryContext(monthlyTransactions, skuNames, advertisements, purchases);
            monthlyContext.setParentSkuToJapaneseNameMap(context.getParentSkuToJapaneseNameMap());
            monthlyContext.setSkuToParentSkuMap(context.getSkuToParentSkuMap());
            monthlyContext.setTransactionsByMonth(transactionsByMonth); // Set original transactionsByMonth for advertisement cost calculation

            // Recalculate sales and ad cost for the specific month
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