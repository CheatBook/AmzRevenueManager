package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.constant.AmountDescription;
import io.github.cheatbook.amzrevenuemanager.domain.constant.TransactionType;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RevenueSummaryServiceTest {

    private RevenueSummaryService revenueSummaryService;

    @BeforeEach
    void setUp() {
        revenueSummaryService = new RevenueSummaryService();
    }

    @Test
    void testCalculateSkuRevenueSummaries() {
        // Arrange
        Settlement settlement1 = new Settlement();
        settlement1.setSku("SKU001");
        settlement1.setTransactionType(TransactionType.ORDER.getValue());
        settlement1.setAmountDescription(AmountDescription.PRINCIPAL.getValue());
        settlement1.setAmount(new BigDecimal("100.00"));
        settlement1.setQuantityPurchased(1);
        settlement1.setOrderId("ORDER001");
        settlement1.setPostedDateTime(LocalDateTime.now());

        Settlement settlement2 = new Settlement();
        settlement2.setSku("SKU001");
        settlement2.setTransactionType(TransactionType.ORDER.getValue());
        settlement2.setAmountDescription(AmountDescription.COMMISSION.getValue());
        settlement2.setAmount(new BigDecimal("-10.00"));
        settlement2.setOrderId("ORDER001");
        settlement2.setPostedDateTime(LocalDateTime.now());
        
        Settlement settlement3 = new Settlement();
        settlement3.setSku("SKU002");
        settlement3.setTransactionType(TransactionType.ORDER.getValue());
        settlement3.setAmountDescription(AmountDescription.PRINCIPAL.getValue());
        settlement3.setAmount(new BigDecimal("200.00"));
        settlement3.setQuantityPurchased(2);
        settlement3.setOrderId("ORDER002");
        settlement3.setPostedDateTime(LocalDateTime.now());

        List<Settlement> settlements = Arrays.asList(settlement1, settlement2, settlement3);
        
        SkuName skuName1 = new SkuName();
        skuName1.setSku("SKU001");
        skuName1.setJapaneseName("商品1");

        SkuName skuName2 = new SkuName();
        skuName2.setSku("SKU002");
        skuName2.setJapaneseName("商品2");

        List<SkuName> skuNames = Arrays.asList(skuName1, skuName2);

        // Act
        Map<String, SkuRevenueSummaryDto> result = revenueSummaryService.calculateSkuRevenueSummaries(settlements, skuNames);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        SkuRevenueSummaryDto summary1 = result.get("SKU001");
        assertNotNull(summary1);
        assertEquals("SKU001", summary1.getSku());
        assertEquals("商品1", summary1.getJapaneseName());
        assertEquals(0, new BigDecimal("100.00").compareTo(summary1.getTotalRevenue()));
        assertEquals(0, new BigDecimal("-10.00").compareTo(summary1.getTotalCommission()));
        assertEquals(1, summary1.getTotalQuantityPurchased());
        assertEquals(1, summary1.getSettlementCount());

        SkuRevenueSummaryDto summary2 = result.get("SKU002");
        assertNotNull(summary2);
        assertEquals("SKU002", summary2.getSku());
        assertEquals("商品2", summary2.getJapaneseName());
        assertEquals(0, new BigDecimal("200.00").compareTo(summary2.getTotalRevenue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(summary2.getTotalCommission()));
        assertEquals(2, summary2.getTotalQuantityPurchased());
        assertEquals(1, summary2.getSettlementCount());
    }

    @Test
    void testCalculateSkuRevenueSummaries_EmptyList() {
        // Arrange
        List<Settlement> settlements = Collections.emptyList();
        List<SkuName> skuNames = Collections.emptyList();

        // Act
        Map<String, SkuRevenueSummaryDto> result = revenueSummaryService.calculateSkuRevenueSummaries(settlements, skuNames);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}