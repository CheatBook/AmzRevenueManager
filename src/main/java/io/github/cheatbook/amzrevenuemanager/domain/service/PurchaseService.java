package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.util.List;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.entity.PurchaseId;
import io.github.cheatbook.amzrevenuemanager.domain.repository.PurchaseRepository;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.PurchaseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Transactional
    public Purchase savePurchase(PurchaseDto purchaseDto) {
        Purchase purchase = new Purchase();
        purchase.setParentSku(purchaseDto.getParentSku());
        purchase.setPurchaseDate(purchaseDto.getPurchaseDate());
        purchase.setQuantity(purchaseDto.getQuantity());
        purchase.setAmount(purchaseDto.getAmount());
        purchase.setTariff(purchaseDto.getTariff());

        if (purchaseDto.getQuantity() != null && purchaseDto.getQuantity() != 0) {
            double unitPrice = (double) (purchaseDto.getAmount() + purchaseDto.getTariff()) / purchaseDto.getQuantity();
            purchase.setUnitPrice(unitPrice);
        }

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Transactional
    public Purchase updatePurchase(String parentSku, LocalDate purchaseDate, PurchaseDto purchaseDto) {
        PurchaseId purchaseId = new PurchaseId(parentSku, purchaseDate);
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + purchaseId));

        // キーの更新は通常行わないが、必要であればロジックを追加
        // purchase.setParentSku(purchaseDto.getParentSku());
        // purchase.setPurchaseDate(purchaseDto.getPurchaseDate());
        purchase.setPurchaseDate(purchaseDto.getPurchaseDate());
        purchase.setQuantity(purchaseDto.getQuantity());
        purchase.setAmount(purchaseDto.getAmount());
        purchase.setTariff(purchaseDto.getTariff());

        if (purchaseDto.getQuantity() != null && purchaseDto.getQuantity() != 0) {
            double unitPrice = (double) (purchaseDto.getAmount() + purchaseDto.getTariff()) / purchaseDto.getQuantity();
            purchase.setUnitPrice(unitPrice);
        }

        return purchaseRepository.save(purchase);
    }
}