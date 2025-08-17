package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParentSkuRevenueSummaryService {

    private final SettlementRepository settlementRepository;
    private final CacheableDataService cacheableDataService;
    private final RevenueSummaryService revenueSummaryService;

    public List<SkuRevenueSummaryDto> getParentSkuRevenueSummary() {
        List<Settlement> settlements = settlementRepository.findAll();
        List<SkuName> skuNames = cacheableDataService.findAllSkuNames();
        Map<String, SkuRevenueSummaryDto> skuSummaryMap = revenueSummaryService.calculateSkuRevenueSummaries(settlements, skuNames);
        return new ArrayList<>(skuSummaryMap.values());
    }
}