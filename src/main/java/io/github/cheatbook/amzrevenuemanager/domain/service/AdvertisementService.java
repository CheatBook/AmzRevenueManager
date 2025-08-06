package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    @Transactional
    public void processAdvertisementReport(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Advertisement> advertisements = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月 dd, yyyy", Locale.JAPANESE);
            DataFormatter dataFormatter = new DataFormatter();

            // 最初の2行はヘッダーなのでスキップ
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next();
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Advertisement ad = new Advertisement();

                // DataFormatterを使用してセルの値を安全に文字列として取得
                String dateStr = dataFormatter.formatCellValue(row.getCell(0));
                if (dateStr != null && !dateStr.isEmpty()) {
                    ad.setDate(LocalDate.parse(dateStr, formatter));
                }

                ad.setPortfolioName(dataFormatter.formatCellValue(row.getCell(1)));
                ad.setCampaignName(dataFormatter.formatCellValue(row.getCell(3)));
                ad.setAdGroupName(dataFormatter.formatCellValue(row.getCell(4)));
                ad.setTargeting(dataFormatter.formatCellValue(row.getCell(5)));
                ad.setMatchType(dataFormatter.formatCellValue(row.getCell(6)));
                ad.setAdvertisedSku(dataFormatter.formatCellValue(row.getCell(7)));

                // Impressions
                try {
                    String impressionsStr = dataFormatter.formatCellValue(row.getCell(8));
                    ad.setImpressions(Integer.parseInt(impressionsStr));
                } catch (NumberFormatException e) {
                    ad.setImpressions(0);
                }

                // Clicks
                try {
                    String clicksStr = dataFormatter.formatCellValue(row.getCell(9));
                    ad.setClicks(Integer.parseInt(clicksStr));
                } catch (NumberFormatException e) {
                    ad.setClicks(0);
                }

                // Cost
                try {
                    String costStr = dataFormatter.formatCellValue(row.getCell(11));
                    String cleanedCostStr = costStr.replaceAll("[^\\d.-]", "");
                    ad.setCost(new BigDecimal(cleanedCostStr));
                } catch (NumberFormatException | ArithmeticException e) {
                    ad.setCost(BigDecimal.ZERO);
                }

                // Sales
                try {
                    String salesStr = dataFormatter.formatCellValue(row.getCell(13));
                    String cleanedSalesStr = salesStr.replaceAll("[^\\d.-]", "");
                    ad.setSales(new BigDecimal(cleanedSalesStr));
                } catch (NumberFormatException | ArithmeticException e) {
                    ad.setSales(BigDecimal.ZERO);
                }
                
                advertisements.add(ad);
            }

            advertisementRepository.saveAll(advertisements);
        }
    }
}