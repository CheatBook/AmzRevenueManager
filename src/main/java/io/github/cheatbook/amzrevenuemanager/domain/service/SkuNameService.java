package io.github.cheatbook.amzrevenuemanager.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;

/**
 * SKU名に関するサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class SkuNameService {

    /**
     * SKU名リポジトリ
     */
    private final SkuNameRepository skuNameRepository;

    /**
     * 決済リポジトリ
     */
    private final SettlementRepository settlementRepository;

    /**
     * すべてのSKU名を取得します。
     *
     * @return SKU名のリスト
     */
    public List<SkuName> getAllSkuNames() {
        return skuNameRepository.findAll();
    }

    /**
     * 決済情報から重複しないSKUのリストを取得します。
     *
     * @return SKUのリスト
     */
    public List<String> findDistinctSkusFromSettlements() {
        return settlementRepository.findDistinctSkus();
    }

    /**
     * SKU名を保存します。
     *
     * @param skuName SKU名
     * @return 保存されたSKU名
     */
    @Transactional
    public SkuName saveSkuName(SkuName skuName) {
        return skuNameRepository.save(skuName);
    }

    /**
     * SKUによってSKU名を取得します。
     *
     * @param sku SKU
     * @return SKU名 (Optional)
     */
    public Optional<SkuName> findBySku(String sku) {
        return skuNameRepository.findById(sku);
    }

    /**
     * 親SKUのリストを取得します。
     *
     * @return 親SKUのリスト
     */
    public List<SkuName> findParentSkus() {
        return skuNameRepository.findByParentSkuIsNull();
    }

    /**
     * 子SKUのリストを取得します。
     *
     * @param parentSku 親SKU
     * @return 子SKUのリスト
     */
    public List<SkuName> findChildrenSkus(String parentSku) {
        return skuNameRepository.findByParentSku(parentSku);
    }

    /**
     * 親SKU名を作成します。
     *
     * @param japaneseName 日本語名
     * @return 作成された親SKU名
     */
    @Transactional
    public SkuName createParentSkuName(String japaneseName) {
        SkuName parentSku = new SkuName();
        parentSku.setSku("PARENT_" + UUID.randomUUID().toString()); // 自動生成されたSKU
        parentSku.setJapaneseName(japaneseName);
        parentSku.setParentSku(null); // 親SKUなのでnull
        return skuNameRepository.save(parentSku);
    }

    /**
     * SKU名を削除します。
     *
     * @param sku SKU
     */
    @Transactional
    public void deleteSkuName(String sku) {
        skuNameRepository.deleteById(sku);
    }
}