package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Transaction;
import io.github.cheatbook.amzrevenuemanager.domain.repository.TransactionRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.RevenueSummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final TransactionRepository transactionRepository;
    private final SkuNameService skuNameService;

    @Transactional
    public void processReportFile(org.springframework.web.multipart.MultipartFile file) throws IOException {

        CSVFormat format = CSVFormat.Builder.create(CSVFormat.TDF)
            .setHeader()              // 1番目のレコードをヘッダーとして扱う
            .setIgnoreHeaderCase(true)  // ヘッダーの大文字/小文字を無視する
            .setTrim(true)              // 値の前後の空白を削除する
            .build();                   // 設定を確定してCSVFormatオブジェクトを生成

        // AmazonのレポートはShift_JISの場合があるため文字コードを指定して読み込む
        // ファイルの文字コードに合わせて "UTF-8" などに変更してください
        try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(file.getInputStream(), "Shift_JIS"));
             CSVParser csvParser = new CSVParser(reader, format)) {

            List<Transaction> transactions = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss z");

            for (CSVRecord csvRecord : csvParser) {
                // 2行目の集計行はスキップ
                if ("".equals(csvRecord.get("transaction-type")) && "".equals(csvRecord.get("order-id"))) {
                    continue;
                }

                Transaction transaction = new Transaction();
                transaction.setSettlementId(csvRecord.get("settlement-id"));
                transaction.setTransactionType(csvRecord.get("transaction-type"));
                transaction.setOrderId(csvRecord.get("order-id"));
                transaction.setAmountType(csvRecord.get("amount-type"));
                transaction.setAmountDescription(csvRecord.get("amount-description"));
                transaction.setAmount(new BigDecimal(csvRecord.get("amount")));
                transaction.setPostedDateTime(LocalDateTime.parse(csvRecord.get("posted-date-time"), formatter));
                transaction.setSku(csvRecord.get("sku"));

                String quantityStr = csvRecord.get("quantity-purchased");
                if (quantityStr != null && !quantityStr.isEmpty()) {
                    transaction.setQuantityPurchased(Integer.parseInt(quantityStr));
                }

                transactions.add(transaction);

                // SKU名と日本語名を保存または更新
                String sku = transaction.getSku();
                if (sku != null && !sku.isEmpty()) {
                    skuNameService.findBySku(sku).ifPresentOrElse(
                        existingSkuName -> {
                            // 既存のSKU名があれば更新（ここでは日本語名のみ更新）
                            // existingSkuName.setJapaneseName(transaction.getProductName()); // もし商品名がCSVにあれば
                            skuNameService.saveSkuName(existingSkuName);
                        },
                        () -> {
                            // 新規SKU名として保存
                            SkuName newSkuName = new SkuName();
                            newSkuName.setSku(sku);
                            // newSkuName.setJapaneseName(transaction.getProductName()); // もし商品名がCSVにあれば
                            skuNameService.saveSkuName(newSkuName);
                        }
                    );
                }
            }
            transactionRepository.saveAll(transactions);
        }
    }

    public RevenueSummaryDto calculateRevenueSummary(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByPostedDateTimeBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        } else {
            transactions = transactionRepository.findAll();
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalShipping = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        // "Order" と "Refund" のトランザクションのみを対象に集計
        List<Transaction> orderTransactions = transactions.stream()
            .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
            .collect(Collectors.toList());

        for(Transaction t : orderTransactions) {
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
        
        long transactionCount = transactions.stream().map(Transaction::getOrderId).distinct().count();

        return new RevenueSummaryDto(
            totalRevenue, totalCommission, totalShipping, totalTax, grossProfit, transactionCount
        );
    }

    public List<SkuRevenueSummaryDto> calculateSkuRevenueSummary() {
        List<Transaction> transactions = transactionRepository.findAll();

        Map<String, List<Transaction>> transactionsBySku = transactions.stream()
            .filter(t -> t.getSku() != null && !t.getSku().isEmpty())
            .collect(Collectors.groupingBy(Transaction::getSku));

        List<SkuRevenueSummaryDto> skuSummaries = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : transactionsBySku.entrySet()) {
            String sku = entry.getKey();
            List<Transaction> skuTransactions = entry.getValue();

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalCommission = BigDecimal.ZERO;
            BigDecimal totalShipping = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;

            List<Transaction> orderTransactions = skuTransactions.stream()
                .filter(t -> "Order".equals(t.getTransactionType()) || "Refund".equals(t.getTransactionType()))
                .collect(Collectors.toList());

            for (Transaction t : orderTransactions) {
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
            Integer transactionCount = (int) skuTransactions.stream().map(Transaction::getOrderId).distinct().count();
            
            // order_idごとにquantity_purchasedの最大値を取得し、それらを合計
            Integer totalQuantityPurchased = skuTransactions.stream()
                .filter(t -> t.getOrderId() != null && t.getQuantityPurchased() != null)
                .collect(Collectors.groupingBy(Transaction::getOrderId,
                    Collectors.mapping(Transaction::getQuantityPurchased,
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