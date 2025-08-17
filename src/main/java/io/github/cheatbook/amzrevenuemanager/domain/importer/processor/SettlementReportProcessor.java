package io.github.cheatbook.amzrevenuemanager.domain.importer.processor;

import io.github.cheatbook.amzrevenuemanager.domain.constant.DateTimeFormats;
import io.github.cheatbook.amzrevenuemanager.domain.constant.Miscellaneous;
import io.github.cheatbook.amzrevenuemanager.domain.constant.ReportConstants;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementId;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 決済レポートのレコードを処理するクラスです。
 */
@Component
@RequiredArgsConstructor
public class SettlementReportProcessor {

    /**
     * SKU名サービス
     */
    private final SkuNameService skuNameService;

    /**
     * CSVレコードを処理し、決済マップを更新します。
     *
     * @param csvRecord     CSVレコード
     * @param settlementMap 決済マップ
     */
    public void process(CSVRecord csvRecord, Map<SettlementId, Settlement> settlementMap) {
        if (isSummaryRow(csvRecord)) {
            return;
        }

        Settlement settlement = buildSettlement(csvRecord);
        updateSkuName(settlement.getSku());

        SettlementId id = new SettlementId(
                settlement.getSettlementId(),
                settlement.getOrderId(),
                settlement.getOrderItemCode(),
                settlement.getAmountDescription(),
                settlement.getPostedDateTime()
        );

        settlementMap.merge(id, settlement,
                (existing, newOne) -> {
                    existing.setAmount(existing.getAmount().add(newOne.getAmount()));
                    return existing;
                });
    }

    /**
     * サマリー行かどうかを判定します。
     *
     * @param csvRecord CSVレコード
     * @return サマリー行の場合はtrue、そうでない場合はfalse
     */
    private boolean isSummaryRow(CSVRecord csvRecord) {
        return "".equals(csvRecord.get(ReportConstants.HEADER_TRANSACTION_TYPE)) && "".equals(csvRecord.get(ReportConstants.HEADER_ORDER_ID));
    }

    /**
     * CSVレコードから決済エンティティを構築します。
     *
     * @param csvRecord CSVレコード
     * @return 決済エンティティ
     */
    private Settlement buildSettlement(CSVRecord csvRecord) {
        Settlement settlement = new Settlement();
        settlement.setSettlementId(csvRecord.get(ReportConstants.HEADER_SETTLEMENT_ID));
        settlement.setTransactionType(csvRecord.get(ReportConstants.HEADER_TRANSACTION_TYPE));
        settlement.setOrderId(getOrderId(csvRecord));
        settlement.setOrderItemCode(getOrderItemCode(csvRecord));
        settlement.setAmountType(csvRecord.get(ReportConstants.HEADER_AMOUNT_TYPE));
        settlement.setAmountDescription(csvRecord.get(ReportConstants.HEADER_AMOUNT_DESCRIPTION));
        settlement.setAmount(new BigDecimal(csvRecord.get(ReportConstants.HEADER_AMOUNT)));
        settlement.setPostedDateTime(LocalDateTime.parse(csvRecord.get(ReportConstants.HEADER_POSTED_DATE_TIME), DateTimeFormats.SETTLEMENT_DATE_TIME_FORMAT));
        settlement.setSku(csvRecord.get(ReportConstants.HEADER_SKU));
        settlement.setQuantityPurchased(getQuantityPurchased(csvRecord));
        return settlement;
    }

    /**
     * 注文IDを取得します。
     *
     * @param csvRecord CSVレコード
     * @return 注文ID
     */
    private String getOrderId(CSVRecord csvRecord) {
        String orderId = csvRecord.get(ReportConstants.HEADER_ORDER_ID);
        return (orderId != null && !orderId.isEmpty()) ? orderId : Miscellaneous.NOT_APPLICABLE;
    }

    /**
     * 注文商品コードを取得します。
     *
     * @param csvRecord CSVレコード
     * @return 注文商品コード
     */
    private Long getOrderItemCode(CSVRecord csvRecord) {
        String orderItemCodeStr = csvRecord.get(ReportConstants.HEADER_ORDER_ITEM_CODE);
        if (orderItemCodeStr == null || orderItemCodeStr.isEmpty()) {
            orderItemCodeStr = csvRecord.get(ReportConstants.HEADER_MERCHANT_ORDER_ITEM_ID);
        }
        if (orderItemCodeStr == null || orderItemCodeStr.isEmpty()) {
            orderItemCodeStr = csvRecord.get(ReportConstants.HEADER_MERCHANT_ADJUSTMENT_ITEM_ID);
        }

        if (orderItemCodeStr != null && !orderItemCodeStr.isEmpty() && !Miscellaneous.NOT_APPLICABLE.equals(orderItemCodeStr)) {
            try {
                return new BigDecimal(orderItemCodeStr).longValue();
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    /**
     * 購入数量を取得します。
     *
     * @param csvRecord CSVレコード
     * @return 購入数量
     */
    private Integer getQuantityPurchased(CSVRecord csvRecord) {
        String quantityStr = csvRecord.get(ReportConstants.HEADER_QUANTITY_PURCHASED);
        if (quantityStr != null && !quantityStr.isEmpty()) {
            return Integer.parseInt(quantityStr);
        }
        return null;
    }

    /**
     * SKU名を更新または作成します。
     *
     * @param sku SKU
     */
    private void updateSkuName(String sku) {
        if (sku != null && !sku.isEmpty()) {
            skuNameService.findBySku(sku).ifPresentOrElse(
                skuNameService::saveSkuName,
                () -> {
                    SkuName newSkuName = new SkuName();
                    newSkuName.setSku(sku);
                    skuNameService.saveSkuName(newSkuName);
                }
            );
        }
    }
}