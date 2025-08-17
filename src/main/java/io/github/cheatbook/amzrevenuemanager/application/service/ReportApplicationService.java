package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.importer.AdvertisementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.importer.SettlementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.service.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * レポートに関するアプリケーションサービスクラス。
 */
@Service
@RequiredArgsConstructor
public class ReportApplicationService {

    /**
     * 広告レポートインポートサービスクラス
     */
    private final AdvertisementImportService advertisementImportService;

    /**
     * 決済レポートインポートサービスクラス
     */
    private final SettlementImportService settlementImportService;

    /**
     * 広告レポートをインポートする。
     *
     * @param file アップロードされた広告レポートファイル
     * @throws IOException              ファイルの読み込みに失敗した場合
     * @throws SkuNameNotFoundException SKU名が見つからない場合
     */
    public void importAdvertisementReport(MultipartFile file) throws IOException, SkuNameNotFoundException {
        advertisementImportService.importReport(file);
    }

    /**
     * 決済レポートをインポートする。
     *
     * @param file アップロードされた決済レポートファイル
     * @return インポート結果のメッセージ
     * @throws IOException                    ファイルの読み込みに失敗した場合
     * @throws DuplicateSettlementIdException 重複した決済IDが存在する場合
     */
    public String importSettlementReport(MultipartFile file) throws IOException, DuplicateSettlementIdException {
        return settlementImportService.importReport(file);
    }
}