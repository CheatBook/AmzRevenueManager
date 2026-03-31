/**
 * インターフェース層（Web）のクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import io.github.cheatbook.amzrevenuemanager.application.service.MessageLocalizationService;
import io.github.cheatbook.amzrevenuemanager.application.service.ReportApplicationService;
import io.github.cheatbook.amzrevenuemanager.domain.exception.BusinessException;
import io.github.cheatbook.amzrevenuemanager.domain.exception.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.exception.SkuNameNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ファイルアップロードに関するコントローラークラスです。
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 開発のため全オリジンを許可。本番では特定のオリジンに限定すべき
public class FileUploadController {

    /**
     * レポートアプリケーションサービス
     */
    private final ReportApplicationService reportApplicationService;

    /**
     * メッセージローカライズサービス
     */
    private final MessageLocalizationService messageLocalizationService;

    /**
     * 決済レポートファイルをアップロードします。
     *
     * @param files アップロードされた決済レポートファイルのリスト
     * @return レスポンスエンティティ
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new BusinessException(messageLocalizationService.getMessage("error.file_empty"), HttpStatus.BAD_REQUEST);
        }

        StringBuilder responseMessageBuilder = new StringBuilder();
        StringBuilder warningMessageBuilder = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                String warningMessage = reportApplicationService.importSettlementReport(file);
                if (warningMessage != null) {
                    warningMessageBuilder.append(file.getOriginalFilename()).append(": ").append(warningMessage).append("\n");
                }
                responseMessageBuilder.append(messageLocalizationService.getMessage("upload.success", new Object[]{file.getOriginalFilename()})).append("\n");
            } catch (DuplicateSettlementIdException e) {
                throw e; // GlobalExceptionHandler で処理
            } catch (Exception e) {
                throw new BusinessException(messageLocalizationService.getMessage("upload.error", new Object[]{file.getOriginalFilename(), e.getMessage()}), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (!warningMessageBuilder.isEmpty()) {
            responseMessageBuilder.append("\n").append(messageLocalizationService.getMessage("upload.warning")).append("\n").append(warningMessageBuilder);
        }

        return ResponseEntity.ok(responseMessageBuilder.toString());
    }

    /**
     * 広告レポートファイルをアップロードします。
     *
     * @param file アップロードされた広告レポートファイル
     * @return レスポンスエンティティ
     */
    @PostMapping("/upload-advertisement")
    public ResponseEntity<String> handleAdvertisementUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(messageLocalizationService.getMessage("error.file_empty"), HttpStatus.BAD_REQUEST);
        }
        try {
            reportApplicationService.importAdvertisementReport(file);
            return ResponseEntity.ok(messageLocalizationService.getMessage("upload.advertisement.success"));
        } catch (SkuNameNotFoundException e) {
            throw e; // GlobalExceptionHandler で処理
        } catch (Exception e) {
            throw new BusinessException(messageLocalizationService.getMessage("upload.advertisement.error", new Object[]{e.getMessage()}), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 販売日管理レポートファイルをアップロードします。
     *
     * @param files アップロードされた販売日管理レポートファイルのリスト
     * @return レスポンスエンティティ
     */
    @PostMapping("/upload-sales-date")
    public ResponseEntity<String> handleSalesDateUpload(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new BusinessException(messageLocalizationService.getMessage("error.file_empty"), HttpStatus.BAD_REQUEST);
        }

        StringBuilder responseMessageBuilder = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                reportApplicationService.importSalesDateReport(file);
                responseMessageBuilder.append(messageLocalizationService.getMessage("upload.success", new Object[]{file.getOriginalFilename()})).append("\n");
            } catch (Exception e) {
                throw new BusinessException(messageLocalizationService.getMessage("upload.error", new Object[]{file.getOriginalFilename(), e.getMessage()}), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return ResponseEntity.ok(responseMessageBuilder.toString());
    }
}