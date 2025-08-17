package io.github.cheatbook.amzrevenuemanager.domain.importer;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.importer.processor.AdvertisementReportProcessor;
import io.github.cheatbook.amzrevenuemanager.domain.importer.reader.AdvertisementReportReader;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdvertisementImportService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementReportProcessor processor;

    @Transactional(rollbackFor = SkuNameNotFoundException.class)
    public void importReport(MultipartFile file) throws IOException, SkuNameNotFoundException {
        try (AdvertisementReportReader reader = new AdvertisementReportReader(file)) {
            Map<String, Advertisement> advertisementMap = new HashMap<>();
            for (CSVRecord csvRecord : reader.getCsvParser()) {
                processor.process(csvRecord, advertisementMap);
            }
            if (!advertisementMap.isEmpty()) {
                advertisementRepository.saveAll(advertisementMap.values());
            }
        }
    }
}