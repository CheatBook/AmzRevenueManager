package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.importer.AdvertisementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.importer.SettlementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.importer.AdvertisementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.importer.SettlementImportService;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import io.github.cheatbook.amzrevenuemanager.domain.service.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.service.SalesDateService;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameNotFoundException;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.TransactionDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.IOException;
import java.util.List;

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
     * 売上日サービスクラス
     */
    private final SalesDateService salesDateService;

   /**
    * 決済リポジトリ
    */
    private final SettlementRepository settlementRepository;

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

    /**
     * 販売日管理レポートをインポートする。
     *
     * @param file アップロードされた販売日管理レポートファイル
     */
    public void importSalesDateReport(MultipartFile file) {
        salesDateService.saveSalesDates(file);
    }

   /**
    * トランザクションデータを取得する。
    *
    * @return トランザクションデータのリスト
    */
    public List<TransactionDataDto> getTransactionData() {
        return settlementRepository.findTransactionData();
    }
}