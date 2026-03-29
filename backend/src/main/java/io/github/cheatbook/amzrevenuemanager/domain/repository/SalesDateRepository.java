package io.github.cheatbook.amzrevenuemanager.domain.repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.SalesDate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 売上日リポジトリです。
 */
public interface SalesDateRepository extends JpaRepository<SalesDate, String> {
}