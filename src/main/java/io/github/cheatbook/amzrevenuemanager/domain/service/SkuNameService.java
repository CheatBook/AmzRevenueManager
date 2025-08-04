package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkuNameService {

    private final SkuNameRepository skuNameRepository;
    private final TransactionRepository transactionRepository;

    public List<SkuName> findAllSkuNames() {
        return skuNameRepository.findAll();
    }

    public List<String> findDistinctSkusFromTransactions() {
        return transactionRepository.findDistinctSkus();
    }

    @Transactional
    public SkuName saveSkuName(SkuName skuName) {
        return skuNameRepository.save(skuName);
    }

    public Optional<SkuName> findBySku(String sku) {
        return skuNameRepository.findById(sku);
    }

    public List<SkuName> findParentSkus() {
        return skuNameRepository.findByParentSkuIsNull();
    }

    public List<SkuName> findChildrenSkus(String parentSku) {
        return skuNameRepository.findByParentSku(parentSku);
    }
    @Transactional
    public SkuName createParentSkuName(String japaneseName) {
        SkuName parentSku = new SkuName();
        parentSku.setSku("PARENT_" + UUID.randomUUID().toString()); // 自動生成されたSKU
        parentSku.setJapaneseName(japaneseName);
        parentSku.setParentSku(null); // 親SKUなのでnull
        return skuNameRepository.save(parentSku);
    }
}