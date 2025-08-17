package io.github.cheatbook.amzrevenuemanager.domain.service;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.domain.repository.SkuNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheableDataService {

    private final SkuNameRepository skuNameRepository;

    @Cacheable("skuNames")
    public List<SkuName> findAllSkuNames() {
        return skuNameRepository.findAll();
    }
}