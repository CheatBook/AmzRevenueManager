package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import io.github.cheatbook.amzrevenuemanager.domain.entity.AdvertisementId;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final SkuNameRepository skuNameRepository;

    @Transactional(rollbackFor = SkuNameNotFoundException.class)
    public void processAdvertisementReport(MultipartFile file) throws IOException, SkuNameNotFoundException {
        try (BOMInputStream bomIn = new BOMInputStream(file.getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(bomIn, "UTF-8"))) {
            
            CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

            try (CSVParser csvParser = new CSVParser(reader, format)) {
                Map<String, Advertisement> advertisementMap = new HashMap<>();

                for (CSVRecord csvRecord : csvParser) {
                    String dateStr = csvRecord.get("日付");
                    String campaignName = csvRecord.get("キャンペーン名");
                    String costStr = csvRecord.get("広告費").replaceAll("[^\\d.]", "");

                    if (dateStr == null || dateStr.isEmpty() || campaignName == null || campaignName.isEmpty() || costStr == null || costStr.isEmpty()) {
                        continue;
                    }
                    
                    LocalDate date;
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy", Locale.ENGLISH);
                        date = LocalDate.parse(dateStr, formatter);
                    } catch (DateTimeParseException e) {
                        // TODO: Log error
                        continue;
                    }

                    BigDecimal cost = new BigDecimal(costStr);

                    Optional<SkuName> skuNameOpt = skuNameRepository.findByJapaneseName(campaignName);
                    if (skuNameOpt.isPresent()) {
                        String parentSku = skuNameOpt.get().getSku();
                        if (parentSku != null) {
                            String key = parentSku + "_" + date.toString();
                            Advertisement ad = advertisementMap.get(key);
                            if (ad != null) {
                                ad.setTotalCost(ad.getTotalCost().add(cost));
                            } else {
                                AdvertisementId adId = new AdvertisementId(parentSku, date);
                                Optional<Advertisement> existingAdOpt = advertisementRepository.findById(adId);
                                if (existingAdOpt.isPresent()) {
                                    Advertisement existingAd = existingAdOpt.get();
                                    existingAd.setTotalCost(existingAd.getTotalCost().add(cost));
                                    advertisementMap.put(key, existingAd);
                                } else {
                                    Advertisement newAd = new Advertisement();
                                    newAd.setId(adId);
                                    newAd.setProductName(campaignName);
                                    newAd.setTotalCost(cost);
                                    advertisementMap.put(key, newAd);
                                }
                            }
                        }
                    } else {
                        throw new SkuNameNotFoundException("キャンペーン名に紐づくSKUが見つかりません: " + campaignName);
                    }
                }
                if (!advertisementMap.isEmpty()) {
                    advertisementRepository.saveAll(advertisementMap.values());
                }
            }
        }
    }
}