package io.github.cheatbook.amzrevenuemanager.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;

import java.time.LocalDate;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByDateBetween(LocalDate startDate, LocalDate endDate);
}