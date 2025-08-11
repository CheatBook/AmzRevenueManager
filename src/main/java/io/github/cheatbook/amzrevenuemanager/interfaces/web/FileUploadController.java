package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import io.github.cheatbook.amzrevenuemanager.domain.service.CsvService;
import io.github.cheatbook.amzrevenuemanager.domain.service.DuplicateSettlementIdException;
import io.github.cheatbook.amzrevenuemanager.domain.service.ParentSkuRevenueSummaryService;
import io.github.cheatbook.amzrevenuemanager.domain.service.HierarchicalRevenueSummaryService;
import io.github.cheatbook.amzrevenuemanager.domain.service.DailyRevenueSummaryService;
import io.github.cheatbook.amzrevenuemanager.domain.service.AdvertisementService;
import io.github.cheatbook.amzrevenuemanager.domain.service.ParentSkuDailySummaryService;
import io.github.cheatbook.amzrevenuemanager.domain.service.SalesDateService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.SkuRevenueSummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.HierarchicalSkuRevenueSummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.DailyRevenueSummaryDto;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.DailySummaryWithParentSkuDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 開発のため全オリジンを許可。本番では特定のオリジンに限定すべき
public class FileUploadController {

    private final CsvService csvService;
    private final ParentSkuRevenueSummaryService parentSkuRevenueSummaryService;
    private final HierarchicalRevenueSummaryService hierarchicalRevenueSummaryService;
    private final DailyRevenueSummaryService dailyRevenueSummaryService;
    private final ParentSkuDailySummaryService parentSkuDailySummaryService;
    private final AdvertisementService advertisementService;
    private final SalesDateService salesDateService;

    @PostMapping("/upload-sales-date")
    public ResponseEntity<String> handleSalesDateUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ファイルが空です。");
        }
        try {
            salesDateService.saveSalesDates(file);
            return ResponseEntity.ok("販売日データが正常にアップロードされ、処理されました。");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("販売日データの処理中にエラーが発生しました: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ファイルが空です。");
        }
        try {
            String warningMessage = null;
            if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".xlsx")) {
                warningMessage = csvService.processExcelFile(file);
            } else {
                warningMessage = csvService.processReportFile(file);
            }
            
            String responseMessage = "ファイルが正常にアップロードされ、データが処理されました。";
            if (warningMessage != null) {
                responseMessage += "\n" + warningMessage;
            }
            return ResponseEntity.ok(responseMessage);
        } catch (DuplicateSettlementIdException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ファイルの処理中にエラーが発生しました: " + e.getMessage());
        }
    }

    @PostMapping("/upload-advertisement")
    public ResponseEntity<String> handleAdvertisementUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ファイルが空です。");
        }
        try {
            advertisementService.processAdvertisementReport(file);
            return ResponseEntity.ok("広告レポートが正常にアップロードされ、処理されました。");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("広告レポートの処理中にエラーが発生しました: " + e.getMessage());
        }
    }


    @GetMapping("/sku-summary")
    public ResponseEntity<List<SkuRevenueSummaryDto>> getSkuRevenueSummary() {
        try {
            List<SkuRevenueSummaryDto> skuSummary = csvService.calculateSkuRevenueSummary();
            return ResponseEntity.ok(skuSummary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/daily-summary")
    public ResponseEntity<List<DailyRevenueSummaryDto>> getDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<DailyRevenueSummaryDto> dailySummary = dailyRevenueSummaryService.getDailyRevenueSummary(startDate, endDate);
            return ResponseEntity.ok(dailySummary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/parent-sku-summary")
    public ResponseEntity<List<SkuRevenueSummaryDto>> getParentSkuRevenueSummary() {
        try {
            List<SkuRevenueSummaryDto> parentSkuSummary = parentSkuRevenueSummaryService.getParentSkuRevenueSummary();
            return ResponseEntity.ok(parentSkuSummary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/hierarchical-sku-summary")
    public ResponseEntity<List<HierarchicalSkuRevenueSummaryDto>> getHierarchicalSkuRevenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<HierarchicalSkuRevenueSummaryDto> hierarchicalSummary = hierarchicalRevenueSummaryService.getHierarchicalSkuRevenueSummary(startDate, endDate);
            return ResponseEntity.ok(hierarchicalSummary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/parent-sku-daily-summary")
    public ResponseEntity<List<DailySummaryWithParentSkuDto>> getParentSkuDailySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<DailySummaryWithParentSkuDto> result = parentSkuDailySummaryService.getDailyParentSkuSummary(startDate, endDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}