package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.application.service.PurchaseApplicationService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.PurchaseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 仕入れに関するコントローラークラスです。
 */
@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PurchaseController {

    /**
     * 仕入れアプリケーションサービス
     */
    private final PurchaseApplicationService purchaseApplicationService;

    /**
     * 仕入れ情報を保存します。
     *
     * @param purchaseDto 仕入れ情報DTO
     * @return レスポンスエンティティ
     */
    @PostMapping
    public ResponseEntity<Purchase> savePurchase(@RequestBody PurchaseDto purchaseDto) {
        try {
            Purchase savedPurchase = purchaseApplicationService.savePurchase(purchaseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPurchase);
        } catch (Exception e) {
            log.error("仕入れ情報の保存中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * すべての仕入れ情報を取得します。
     *
     * @return レスポンスエンティティ
     */
    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases() {
        try {
            List<Purchase> purchases = purchaseApplicationService.getAllPurchases();
            return ResponseEntity.ok(purchases);
        } catch (Exception e) {
            log.error("すべての仕入れ情報の取得中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 仕入れ情報を更新します。
     *
     * @param parentSku    親SKU
     * @param purchaseDate 仕入れ日
     * @param purchaseDto  仕入れ情報DTO
     * @return レスポンスエンティティ
     */
    @PutMapping("/{parentSku}/{purchaseDate}")
    public ResponseEntity<Purchase> updatePurchase(
            @PathVariable String parentSku,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDate,
            @RequestBody PurchaseDto purchaseDto) {
        try {
            Purchase updatedPurchase = purchaseApplicationService.updatePurchase(parentSku, purchaseDate, purchaseDto);
            return ResponseEntity.ok(updatedPurchase);
        } catch (Exception e) {
            log.error("仕入れ情報の更新中にエラーが発生しました: parentSku={}, purchaseDate={}", parentSku, purchaseDate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}