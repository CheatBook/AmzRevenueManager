package io.github.cheatbook.amzrevenuemanager.application.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.service.SkuNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SKU名に関するアプリケーションサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class SkuNameApplicationService {

    /**
     * SKU名サービスクラス
     */
    private final SkuNameService skuNameService;

    /**
     * すべてのSKU名を取得します。
     *
     * @return SKU名のリスト
     */
    public List<SkuName> getAllSkuNames() {
        return skuNameService.getAllSkuNames();
    }

    /**
     * SKUによってSKU名を取得します。
     *
     * @param sku SKU
     * @return SKU名 (Optional)
     */
    public Optional<SkuName> getSkuNameBySku(String sku) {
        return skuNameService.findBySku(sku);
    }

    /**
     * SKU名を保存します。
     *
     * @param skuName SKU名
     * @return 保存されたSKU名
     */
    public SkuName saveSkuName(SkuName skuName) {
        return skuNameService.saveSkuName(skuName);
    }

    /**
     * SKU名を削除します。
     *
     * @param sku SKU
     */
    public void deleteSkuName(String sku) {
        skuNameService.deleteSkuName(sku);
    }

    /**
     * 親SKUのリストを取得します。
     *
     * @return 親SKUのリスト
     */
    public List<SkuName> findParentSkus() {
        return skuNameService.findParentSkus();
    }
}