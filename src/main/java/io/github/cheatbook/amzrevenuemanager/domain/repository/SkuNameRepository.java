package io.github.cheatbook.amzrevenuemanager.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;

@Repository
public interface SkuNameRepository extends JpaRepository<SkuName, String> {
    List<SkuName> findByParentSkuIsNull();
    List<SkuName> findByParentSku(String parentSku);
}