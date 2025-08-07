package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            DataFormatter dataFormatter = new DataFormatter();

            // ヘッダー行を1行スキップ
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // 空白行の可能性を考慮
                if(row.getCell(0) == null) {
                    continue;
                }
                Advertisement ad = new Advertisement();

                // 各項目を正しい列から取得
                ad.setSku(dataFormatter.formatCellValue(row.getCell(6)));
                ad.setAsin(dataFormatter.formatCellValue(row.getCell(7)));
                ad.setCampaignName(dataFormatter.formatCellValue(row.getCell(3)));

                // Impression - 数値として直接取得
                try {
                    ad.setImpression((int) row.getCell(8).getNumericCellValue());
                } catch (Exception e) {
                    ad.setImpression(0);
                }

                // Click Count - 数値として直接取得
                try {
                    ad.setClickCount((int) row.getCell(9).getNumericCellValue());
                } catch (Exception e) {
                    ad.setClickCount(0);
                }

                // Total Cost - 数値として直接取得
                try {
                    ad.setTotalCost(BigDecimal.valueOf(row.getCell(12).getNumericCellValue()));
                } catch (Exception e) {
                    ad.setTotalCost(BigDecimal.ZERO);
                }

                // Date
                try {
                    ad.setDate(row.getCell(0).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } catch (Exception e) {
                    ad.setDate(null);
                }
                
                advertisements.add(ad);
            }

            advertisementRepository.saveAll(advertisements);
        }
    }
}