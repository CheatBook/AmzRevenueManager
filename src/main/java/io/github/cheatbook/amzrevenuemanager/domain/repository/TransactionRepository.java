package io.github.cheatbook.amzrevenuemanager.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Transaction;
import io.github.cheatbook.amzrevenuemanager.domain.entity.TransactionId;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, TransactionId> {
    @Query("SELECT DISTINCT t.sku FROM Transaction t")
    List<String> findDistinctSkus();
    List<Transaction> findByPostedDateTimeBetween(LocalDateTime start, LocalDateTime end);

}
