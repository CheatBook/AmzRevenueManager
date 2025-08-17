package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.service.PurchaseService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.PurchaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseApplicationService {

    private final PurchaseService purchaseService;

    public Purchase savePurchase(PurchaseDto purchaseDto) {
        return purchaseService.savePurchase(purchaseDto);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    public Purchase updatePurchase(String parentSku, LocalDate purchaseDate, PurchaseDto purchaseDto) {
        return purchaseService.updatePurchase(parentSku, purchaseDate, purchaseDto);
    }
}