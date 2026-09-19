package com.easyvyaapaar.repository;

import com.easyvyaapaar.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByNameAsc();

    Optional<Product> findByNameIgnoreCaseAndActiveTrue(String name);

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    /**
     * Returns products whose currentStock <= minimumStock and are active.
     * These are the low-stock / reorder-needed items.
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock <= p.minimumStock ORDER BY p.name")
    List<Product> findLowStockProducts();

    /**
     * Fuzzy search by name for NLP product matching.
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.name")
    List<Product> searchByKeyword(String keyword);
}
