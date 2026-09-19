package com.easyvyaapaar.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Request body for POST /api/products
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be at most 100 characters")
    private String name;

    @Size(max = 100)
    private String category;

    @NotBlank(message = "Unit is required")
    private String unit;

    @DecimalMin(value = "0.0", message = "Stock cannot be negative")
    private BigDecimal currentStock = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Minimum stock cannot be negative")
    private BigDecimal minimumStock = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;
}
