package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementId;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.RevenueSummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final SettlementRepository settlementRepository;
    private final SkuNameService skuNameService;

    @Transactional
    public void processReportFile(MultipartFile file) throws IOException {
        // AmazonのレポートはShift_JISの場合があるため文字コードを指定して読み込む
        // ファイルの文字コードに合わせて "UTF-8" などに変更してください
        try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(file.getInputStream(), "Shift_JIS"))) {
            processCsvData(reader);
        }
    }

    @Transactional
    public void processExcelFile(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // 最初のシートを取得
            StringBuilder csvData = new StringBuilder();
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<String> cellValues = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case STRING:
                            cellValues.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            // 指数表記を防ぐためにBigDecimalを使用
                            cellValues.add(new BigDecimal(cell.getNumericCellValue()).toPlainString());
                            break;
                        case BOOLEAN:
                            cellValues.add(String.valueOf(cell.getBooleanCellValue()));
                            break;
                        default:
                            cellValues.add("");
                    }
                }
                csvData.append(String.join("\t", cellValues)).append("\n"); // タブ区切りで結合
            }

            try (Reader reader = new StringReader(csvData.toString())) {
                processCsvData(reader);
            }
        }
    }

    private void processCsvData(Reader reader) throws IOException {
        CSVFormat format = CSVFormat.Builder.create(CSVFormat.TDF)
            .setHeader()              // 1番目のレコードをヘッダーとして扱う
            .setIgnoreHeaderCase(true)  // ヘッダーの大文字/小文字を無視する
            .setTrim(true)              // 値の前後の空白を削除する
            .build();                   // 設定を確定してCSVFormatオブジェクトを生成

        try (CSVParser csvParser = new CSVParser(reader, format)) {

            Map<SettlementId, Settlement> settlementMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss z");

            for (CSVRecord csvRecord : csvParser) {
                // 2行目の集計行はスキップ
                if ("".equals(csvRecord.get("transaction-type")) && "".equals(csvRecord.get("order-id"))) {
                    continue;
                }

                Settlement settlement = new Settlement();
                settlement.setSettlementId(csvRecord.get("settlement-id"));
                settlement.setTransactionType(csvRecord.get("transaction-type"));
                String orderId = csvRecord.get("order-id");
                settlement.setOrderId(orderId != null && !orderId.isEmpty() ? orderId : "N/A");

                String orderItemCodeStr = csvRecord.get("order-item-code");
                if (orderItemCodeStr == null || orderItemCodeStr.isEmpty()) {
                    orderItemCodeStr = csvRecord.get("merchant-order-item-id");
                }
                if (orderItemCodeStr == null || orderItemCodeStr.isEmpty()) {
                    orderItemCodeStr = csvRecord.get("merchant-adjustment-item-id");
                }

                Long orderItemCode;
                if (orderItemCodeStr != null && !orderItemCodeStr.isEmpty() && !"N/A".equals(orderItemCodeStr)) {
                    try {
                        orderItemCode = new BigDecimal(orderItemCodeStr).longValue();
                    } catch (NumberFormatException e) {
                        orderItemCode = 0L;
                    }
                } else {
                    orderItemCode = 0L;
                }
                settlement.setOrderItemCode(orderItemCode);
                
                settlement.setAmountType(csvRecord.get("amount-type"));
                settlement.setAmountDescription(csvRecord.get("amount-description"));
                settlement.setAmount(new BigDecimal(csvRecord.get("amount")));
                settlement.setPostedDateTime(LocalDateTime.parse(csvRecord.get("posted-date-time"), formatter));
                settlement.setSku(csvRecord.get("sku"));

                String quantityStr = csvRecord.get("quantity-purchased");
                if (quantityStr != null && !quantityStr.isEmpty()) {
                    settlement.setQuantityPurchased(Integer.parseInt(quantityStr));
                }

                // SKU名と日本語名を保存または更新
                String sku = settlement.getSku();
                if (sku != null && !sku.isEmpty()) {
                    skuNameService.findBySku(sku).ifPresentOrElse(
                        existingSkuName -> {
                            // 既存のSKU名があれば更新（ここでは日本語名のみ更新）
                            // existingSkuName.setJapaneseName(settlement.getProductName()); // もし商品名がCSVにあれば
                            skuNameService.saveSkuName(existingSkuName);
                        },
                        () -> {
                            // 新規SKU名として保存
                            SkuName newSkuName = new SkuName();
                            newSkuName.setSku(sku);
                            // newSkuName.setJapaneseName(settlement.getProductName()); // もし商品名がCSVにあれば
                            skuNameService.saveSkuName(newSkuName);
                        }
                    );
                }

                SettlementId id = new SettlementId(
                    settlement.getSettlementId(),
                    settlement.getOrderId(),
                    settlement.getOrderItemCode(),
                    settlement.getAmountDescription(),
                    settlement.getPostedDateTime()
                );

                Settlement existing = settlementMap.get(id);
                if (existing != null) {
                    existing.setAmount(existing.getAmount().add(settlement.getAmount()));
                } else {
                    settlementMap.put(id, settlement);
                }
            }

            List<Settlement> settlementsToSave = new ArrayList<>();
            for (Settlement newSettlement : settlementMap.values()) {
                SettlementId id = new SettlementId(
                    newSettlement.getSettlementId(),
                    newSettlement.getOrderId(),
                    newSettlement.getOrderItemCode(),
                    newSettlement.getAmountDescription(),
                    newSettlement.getPostedDateTime()
                );
                Optional<Settlement> existingSettlementOpt = settlementRepository.findById(id);
                if (existingSettlementOpt.isPresent()) {
                    Settlement existingSettlement = existingSettlementOpt.get();
                    existingSettlement.setAmount(existingSettlement.getAmount().add(newSettlement.getAmount()));
                    settlementsToSave.add(existingSettlement);
                } else {
                    settlementsToSave.add(newSettlement);
                }
            }
            settlementRepository.saveAll(settlementsToSave);
        }
    }

    public RevenueSummaryDto calculateRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Settlement> settlements;
        if (startDate != null && endDate != null) {
            settlements = settlementRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        } else {
            settlements = settlementRepository.findAll();
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalShipping = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        // "Order" と "Refund" のトランザクションのみを対象に集計
        List<Settlement> orderSettlements = settlements.stream()
            .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
            .collect(Collectors.toList());

        for(Settlement t : orderSettlements) {
            switch (t.getAmountDescription()) {
                case "Principal":
                    totalRevenue = totalRevenue.add(t.getAmount());
                    break;
                case "Tax":
                    totalTax = totalTax.add(t.getAmount());
                    break;
                case "Shipping":
                    totalShipping = totalShipping.add(t.getAmount());
                    break;
                case "Commission":
                case "FBAPerUnitFulfillmentFee":
                case "ShippingChargeback":
                case "RefundCommission":
                    totalCommission = totalCommission.add(t.getAmount());
                    break;
                default:
                    break;
            }
        }

        BigDecimal grossProfit = totalRevenue.add(totalCommission).add(totalShipping); // 手数料等はマイナス値なので加算
        
        long transactionCount = settlements.stream().map(Settlement::getOrderId).distinct().count();

        return new RevenueSummaryDto(
            totalRevenue, totalCommission, totalShipping, totalTax, grossProfit, transactionCount
        );
    }

    public List<SkuRevenueSummaryDto> calculateSkuRevenueSummary() {
        List<Settlement> settlements = settlementRepository.findAll();

        Map<String, List<Settlement>> transactionsBySku = settlements.stream()
            .filter(t -> t.getSku() != null && !t.getSku().isEmpty())
            .collect(Collectors.groupingBy(Settlement::getSku));

        List<SkuRevenueSummaryDto> skuSummaries = new ArrayList<>();

        for (Map.Entry<String, List<Settlement>> entry : transactionsBySku.entrySet()) {
            String sku = entry.getKey();
            List<Settlement> skuTransactions = entry.getValue();

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalCommission = BigDecimal.ZERO;
            BigDecimal totalShipping = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;

            List<Settlement> orderTransactions = skuTransactions.stream()
                .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
                .collect(Collectors.toList());

            for (Settlement t : orderTransactions) {
                switch (t.getAmountDescription()) {
                    case "Principal":
                        totalRevenue = totalRevenue.add(t.getAmount());
                        break;
                    case "Tax":
                        totalTax = totalTax.add(t.getAmount());
                        break;
                    case "Shipping":
                        totalShipping = totalShipping.add(t.getAmount());
                        break;
                    case "Commission":
                    case "FBAPerUnitFulfillmentFee":
                    case "ShippingChargeback":
                    case "RefundCommission":
                        totalCommission = totalCommission.add(t.getAmount());
                        break;
                    default:
                        break;
                }
            }

            BigDecimal grossProfit = totalRevenue.add(totalCommission).add(totalShipping);
            Integer transactionCount = (int) skuTransactions.stream().map(Settlement::getOrderId).distinct().count();
            
            // order_idごとにquantity_purchasedの最大値を取得し、それらを合計
            Integer totalQuantityPurchased = skuTransactions.stream()
                .filter(t -> t.getOrderId() != null && t.getQuantityPurchased() != null)
                .collect(Collectors.groupingBy(Settlement::getOrderId,
                    Collectors.mapping(Settlement::getQuantityPurchased,
                        Collectors.reducing(0, Integer::max)))) // 各order_idで最大のquantity_purchasedを取得
                .values().stream()
                .mapToInt(Integer::intValue)
                .sum(); // それらを合計

            String japaneseName = skuNameService.findBySku(sku)
                                                .map(SkuName::getJapaneseName)
                                                .orElse(null);

            skuSummaries.add(new SkuRevenueSummaryDto(
                sku, japaneseName, totalRevenue, totalCommission, totalShipping, totalTax, grossProfit, transactionCount, totalQuantityPurchased
            ));
        }

        return skuSummaries;
    }
}