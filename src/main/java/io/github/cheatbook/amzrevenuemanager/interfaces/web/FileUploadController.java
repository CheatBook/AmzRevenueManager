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

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 開発のため全オリジンを許可。本番では特定のオリジンに限定すべき
public class FileUploadController {

    private final ReportApplicationService reportApplicationService;

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
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(file.getOriginalFilename() + " の処理中にエラーが発生しました: " + e.getMessage());
            }
        }

        if (warningMessageBuilder.length() > 0) {
            responseMessageBuilder.append("\n警告:\n").append(warningMessageBuilder);
        }

        return ResponseEntity.ok(responseMessageBuilder.toString());
    }

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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("広告レポートの処理中にエラーが発生しました: " + e.getMessage());
        }
    }


}