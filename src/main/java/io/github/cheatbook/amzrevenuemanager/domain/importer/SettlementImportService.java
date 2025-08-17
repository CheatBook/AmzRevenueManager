package io.github.cheatbook.amzrevenuemanager.domain.importer;

import io.github.cheatbook.amzrevenuemanager.domain.constant.DateTimeFormats;
import io.github.cheatbook.amzrevenuemanager.domain.constant.ReportConstants;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementId;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementReport;
import io.github.cheatbook.amzrevenuemanager.domain.importer.processor.SettlementReportProcessor;
import io.github.cheatbook.amzrevenuemanager.domain.importer.reader.SettlementReportReader;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.service.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.service.SettlementReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettlementImportService {

    private final SettlementRepository settlementRepository;
    private final SettlementReportService settlementReportService;
    private final SettlementReportProcessor processor;

    @Transactional(rollbackFor = DuplicateSettlementIdException.class)
    public String importReport(MultipartFile file) throws IOException, DuplicateSettlementIdException {
        try (SettlementReportReader reader = new SettlementReportReader(file)) {
            Iterator<CSVRecord> csvIterator = reader.getCsvIterator();
            if (!csvIterator.hasNext()) {
                return null;
            }

            CSVRecord summaryRecord = csvIterator.next();
            String settlementIdStr = summaryRecord.get(ReportConstants.HEADER_SETTLEMENT_ID);
            if (settlementRepository.existsBySettlementId(settlementIdStr)) {
                throw new DuplicateSettlementIdException("Settlement ID " + settlementIdStr + " は既に存在します。");
            }

            saveSettlementReport(summaryRecord);

            Map<SettlementId, Settlement> settlementMap = new HashMap<>();
            while (csvIterator.hasNext()) {
                processor.process(csvIterator.next(), settlementMap);
            }

            settlementRepository.saveAll(settlementMap.values());

            return validateTotalAmount(summaryRecord, settlementMap);
        }
    }

    private void saveSettlementReport(CSVRecord summaryRecord) {
        SettlementReport settlementReport = new SettlementReport();
        settlementReport.setId(Long.parseLong(summaryRecord.get(ReportConstants.HEADER_SETTLEMENT_ID)));

        String startDateStr = summaryRecord.get(ReportConstants.HEADER_SETTLEMENT_START_DATE);
        if (startDateStr != null && !startDateStr.isEmpty()) {
            LocalDateTime startDate = LocalDateTime.parse(startDateStr, DateTimeFormats.SETTLEMENT_DATE_TIME_FORMAT);
            settlementReport.setSettlementStartDate(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
        }

        String endDateStr = summaryRecord.get(ReportConstants.HEADER_SETTLEMENT_END_DATE);
        if (endDateStr != null && !endDateStr.isEmpty()) {
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, DateTimeFormats.SETTLEMENT_DATE_TIME_FORMAT);
            settlementReport.setSettlementEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
        }

        settlementReport.setCurrency(summaryRecord.get(ReportConstants.HEADER_CURRENCY));
        BigDecimal totalAmountFromFile = new BigDecimal(summaryRecord.get(ReportConstants.HEADER_TOTAL_AMOUNT));
        settlementReport.setTotalAmount(totalAmountFromFile.doubleValue());
        settlementReportService.save(settlementReport);
    }

    private String validateTotalAmount(CSVRecord summaryRecord, Map<SettlementId, Settlement> settlementMap) {
        BigDecimal totalAmountFromFile = new BigDecimal(summaryRecord.get(ReportConstants.HEADER_TOTAL_AMOUNT));
        BigDecimal calculatedTotalAmount = settlementMap.values().stream()
                .map(Settlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmountFromFile.compareTo(calculatedTotalAmount) != 0) {
            return String.format("警告: ファイルの合計金額(%s)と計算された合計金額(%s)が一致しません。",
                    totalAmountFromFile.toPlainString(), calculatedTotalAmount.toPlainString());
        }
        return null;
    }
}