package com.easyvyaapaar.dto;

import com.easyvyaapaar.entity.Product;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read-only DTO returned by GET /api/products endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private String name;
    private String category;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private BigDecimal price;
    private boolean active;
    private boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductDTO from(Product p) {
        return ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .unit(p.getUnit())
                .currentStock(p.getCurrentStock())
                .minimumStock(p.getMinimumStock())
                .price(p.getPrice())
                .active(p.getActive())
                .lowStock(p.isLowStock())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
