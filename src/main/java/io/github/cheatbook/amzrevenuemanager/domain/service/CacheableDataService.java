/**
 * ドメイン層のサービスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * キャッシュ可能なデータを提供するサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class CacheableDataService {

    /**
     * SKU名リポジトリ
     */
    private final SkuNameRepository skuNameRepository;

    /**
     * すべてのSKU名をキャッシュを有効にして取得します。
     *
     * @return SKU名のリスト
     */
    @Cacheable("skuNames")
    public List<SkuName> findAllSkuNames() {
        return skuNameRepository.findAll();
    }
}