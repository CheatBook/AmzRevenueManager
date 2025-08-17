package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkuNameApplicationService {

    private final SkuNameService skuNameService;

    public List<SkuName> getAllSkuNames() {
        return skuNameService.getAllSkuNames();
    }

    public Optional<SkuName> getSkuNameBySku(String sku) {
        return skuNameService.findBySku(sku);
    }

    public SkuName saveSkuName(SkuName skuName) {
        return skuNameService.saveSkuName(skuName);
    }

    public void deleteSkuName(String sku) {
        skuNameService.deleteSkuName(sku);
    }

    public List<SkuName> findParentSkus() {
        return skuNameService.findParentSkus();
    }
}