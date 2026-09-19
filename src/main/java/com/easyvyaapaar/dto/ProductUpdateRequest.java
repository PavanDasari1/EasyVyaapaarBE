package com.easyvyaapaar.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Request body for PUT /api/products/{id}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String category;

    private String unit;

    @DecimalMin(value = "0.0")
    private BigDecimal minimumStock;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    private Boolean active;
}
