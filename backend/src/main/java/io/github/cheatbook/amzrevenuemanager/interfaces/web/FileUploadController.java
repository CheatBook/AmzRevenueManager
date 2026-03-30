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

import io.github.cheatbook.amzrevenuemanager.application.service.ReportApplicationService;
import io.github.cheatbook.amzrevenuemanager.domain.service.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameNotFoundException;
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
     * 決済レポートファイルをアップロードします。
     *
     * @param files アップロードされた決済レポートファイルのリスト
     * @return レスポンスエンティティ
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ファイルが空です。");
        }

        StringBuilder responseMessageBuilder = new StringBuilder();
        StringBuilder warningMessageBuilder = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                String warningMessage = reportApplicationService.importSettlementReport(file);
                if (warningMessage != null) {
                    warningMessageBuilder.append(file.getOriginalFilename()).append(": ").append(warningMessage).append("\n");
                }
                responseMessageBuilder.append(file.getOriginalFilename()).append(" が正常に処理されました。\n");
            } catch (DuplicateSettlementIdException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } catch (Exception e) {
                log.error("決済レポートの処理中にエラーが発生しました: {}", file.getOriginalFilename(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(file.getOriginalFilename() + " の処理中にエラーが発生しました: " + e.getMessage());
            }
        }

        if (!warningMessageBuilder.isEmpty()) {
            responseMessageBuilder.append("\n警告:\n").append(warningMessageBuilder);
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ファイルが空です。");
        }
        try {
            reportApplicationService.importAdvertisementReport(file);
            return ResponseEntity.ok("広告レポートが正常にアップロードされ、処理されました。");
        } catch (SkuNameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("広告レポートの処理中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("広告レポートの処理中にエラーが発生しました: " + e.getMessage());
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ファイルが空です。");
        }

        StringBuilder responseMessageBuilder = new StringBuilder();

        for (MultipartFile file : files) {
            try {
                reportApplicationService.importSalesDateReport(file);
                responseMessageBuilder.append(file.getOriginalFilename()).append(" が正常に処理されました。\n");
            } catch (Exception e) {
                log.error("販売日管理レポートの処理中にエラーが発生しました: {}", file.getOriginalFilename(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(file.getOriginalFilename() + " の処理中にエラーが発生しました: " + e.getMessage());
            }
        }
        return ResponseEntity.ok(responseMessageBuilder.toString());
    }
}