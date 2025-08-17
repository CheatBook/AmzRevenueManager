package io.github.cheatbook.amzrevenuemanager.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;

/**
 * SKU名リポジトリです。
 */
@Repository
public interface SkuNameRepository extends JpaRepository<SkuName, String> {
    /**
     * 親SKUがnullのSKU名を取得します。
     *
     * @return SKU名のリスト
     */
    List<SkuName> findByParentSkuIsNull();

    /**
     * 親SKUによってSKU名を取得します。
     *
     * @param parentSku 親SKU
     * @return SKU名のリスト
     */
    List<SkuName> findByParentSku(String parentSku);

    /**
     * 日本語名によってSKU名を取得します。
     *
     * @param japaneseName 日本語名
     * @return SKU名 (Optional)
     */
    java.util.Optional<SkuName> findByJapaneseName(String japaneseName);
}