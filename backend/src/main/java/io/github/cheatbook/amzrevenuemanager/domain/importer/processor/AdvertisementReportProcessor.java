/**
 * ドメイン層のインポーターのプロセッサー関連のクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.importer.processor;

import io.github.cheatbook.amzrevenuemanager.domain.constant.DateTimeFormats;
import io.github.cheatbook.amzrevenuemanager.domain.constant.ReportConstants;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.AdvertisementId;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.application.service.MessageLocalizationService;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

/**
 * 広告レポートのレコードを処理するクラスです。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdvertisementReportProcessor {

    /**
     * SKU名リポジトリ
     */
    private final SkuNameRepository skuNameRepository;

    /**
     * 広告リポジトリ
     */
    private final AdvertisementRepository advertisementRepository;

    /**
     * メッセージローカライズサービス
     */
    private final MessageLocalizationService messageLocalizationService;

    /**
     * CSVレコードを処理し、広告マップを更新します。
     *
     * @param csvRecord        CSVレコード
     * @param advertisementMap 広告マップ
     * @throws SkuNameNotFoundException SKU名が見つからない場合
     */
    public void process(CSVRecord csvRecord, Map<String, Advertisement> advertisementMap) throws SkuNameNotFoundException {
        String dateStr = csvRecord.get(ReportConstants.HEADER_AD_DATE);
        String campaignName = csvRecord.get(ReportConstants.HEADER_AD_CAMPAIGN_NAME);
        String costStr = csvRecord.get(ReportConstants.HEADER_AD_COST).replaceAll("[^\\d.]", "");

        if (dateStr == null || dateStr.isEmpty() || campaignName == null || campaignName.isEmpty() || costStr == null || costStr.isEmpty()) {
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormats.ADVERTISEMENT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.error(messageLocalizationService.getMessage("upload.advertisement.error", new Object[]{e.getMessage()}));
            return;
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
            throw new SkuNameNotFoundException(messageLocalizationService.getMessage("exception.sku_name_not_found", new Object[]{campaignName}));
        }
    }
}
