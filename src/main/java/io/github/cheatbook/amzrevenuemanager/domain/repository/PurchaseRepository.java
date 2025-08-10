package io.github.cheatbook.amzrevenuemanager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.entity.PurchaseId;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, PurchaseId> {
}