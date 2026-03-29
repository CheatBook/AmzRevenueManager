package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SalesDate;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SalesDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 売上日に関するサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class SalesDateService {

    /**
     * 売上日リポジトリ
     */
    private final SalesDateRepository salesDateRepository;

    /**
     * 売上日情報をファイルから保存します。
     *
     * @param file アップロードされた売上日ファイル
     */
    @Transactional
    public void saveSalesDates(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<SalesDate> salesDates = new ArrayList<>();
            String line;
            reader.readLine(); // ヘッダーをスキップ
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                if (columns.length > 2) {
                    String amazonOrderId = columns[0];
                    OffsetDateTime odt = OffsetDateTime.parse(columns[2]);
                    LocalDateTime purchaseDate = odt.toLocalDateTime();
                    salesDates.add(new SalesDate(amazonOrderId, purchaseDate));
                }
            }
            salesDateRepository.saveAll(salesDates);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse sales date file", e);
        }
    }
}