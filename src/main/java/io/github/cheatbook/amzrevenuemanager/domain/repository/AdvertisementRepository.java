package io.github.cheatbook.amzrevenuemanager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
}