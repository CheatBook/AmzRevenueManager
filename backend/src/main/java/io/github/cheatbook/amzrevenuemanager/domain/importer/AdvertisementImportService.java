/**
 * ドメイン層のインポーター関連のクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.importer;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.importer.processor.AdvertisementReportProcessor;
import io.github.cheatbook.amzrevenuemanager.domain.importer.reader.AdvertisementReportReader;
import io.github.cheatbook.amzrevenuemanager.domain.repository.AdvertisementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.exception.SkuNameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 広告レポートのインポートサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class AdvertisementImportService {

    /**
     * 広告リポジトリ
     */
    private final AdvertisementRepository advertisementRepository;

    /**
     * 広告レポートプロセッサー
     */
    private final AdvertisementReportProcessor processor;

    /**
     * 広告レポートをインポートします。
     *
     * @param file アップロードされた広告レポートファイル
     * @throws IOException              ファイルの読み込みに失敗した場合
     * @throws SkuNameNotFoundException SKU名が見つからない場合
     */
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