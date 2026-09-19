package com.easyvyaapaar.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Request body for POST /api/inventory/add and /api/inventory/remove
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    private String unit;

    private BigDecimal price;

    private String source = "MANUAL"; // VOICE or MANUAL

    private String originalVoiceText;

    private String notes;
}
