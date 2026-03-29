package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.service.PurchaseService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.PurchaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 仕入れに関するアプリケーションサービスクラス。
 */
@Service
@RequiredArgsConstructor
public class PurchaseApplicationService {

    /**
     * 仕入れサービスクラス
     */
    private final PurchaseService purchaseService;

    /**
     * 仕入れ情報を保存する。
     *
     * @param purchaseDto 仕入れ情報DTO
     * @return 保存された仕入れ情報
     */
    public Purchase savePurchase(PurchaseDto purchaseDto) {
        return purchaseService.savePurchase(purchaseDto);
    }

    /**
     * すべての仕入れ情報を取得する。
     *
     * @return 仕入れ情報のリスト
     */
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    /**
     * 仕入れ情報を更新する。
     *
     * @param parentSku    親SKU
     * @param purchaseDate 仕入れ日
     * @param purchaseDto  仕入れ情報DTO
     * @return 更新された仕入れ情報
     */
    public Purchase updatePurchase(String parentSku, LocalDate purchaseDate, PurchaseDto purchaseDto) {
        return purchaseService.updatePurchase(parentSku, purchaseDate, purchaseDto);
    }
}