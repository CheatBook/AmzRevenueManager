package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.importer.AdvertisementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.importer.SettlementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.service.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReportApplicationService {

    private final AdvertisementImportService advertisementImportService;
    private final SettlementImportService settlementImportService;

    public void importAdvertisementReport(MultipartFile file) throws IOException, SkuNameNotFoundException {
        advertisementImportService.importReport(file);
    }

    public String importSettlementReport(MultipartFile file) throws IOException, DuplicateSettlementIdException {
        return settlementImportService.importReport(file);
    }
}