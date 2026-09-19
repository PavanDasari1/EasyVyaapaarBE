package com.easyvyaapaar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a product/item in the shop inventory.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(length = 100)
    private String category;

    /**
     * Normalized unit code: BAG, KG, LITRE, PIECE, GRAM, ML, CARTON, BOX, DOZEN, QUINTAL
     */
    @Column(nullable = false, length = 50)
    private String unit;

    @Column(name = "current_stock", nullable = false, precision = 12, scale = 3)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "minimum_stock", nullable = false, precision = 12, scale = 3)
    private BigDecimal minimumStock = BigDecimal.ZERO;

    /**
     * Price per unit in INR — BigDecimal to avoid floating-point money bugs.
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentStock == null) currentStock = BigDecimal.ZERO;
        if (minimumStock == null) minimumStock = BigDecimal.ZERO;
        if (active == null) active = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Returns true if currentStock is at or below minimumStock.
     */
    public boolean isLowStock() {
        return currentStock.compareTo(minimumStock) <= 0;
    }
}
