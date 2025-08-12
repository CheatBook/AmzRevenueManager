package io.github.cheatbook.amzrevenuemanager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.AdvertisementId;

import java.time.LocalDate;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, AdvertisementId> {
    List<Advertisement> findByIdDateBetween(LocalDate startDate, LocalDate endDate);
}