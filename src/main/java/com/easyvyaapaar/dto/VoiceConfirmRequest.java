package com.easyvyaapaar.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * After NLP parsing, the user confirms or edits, then sends this to /api/voice/confirm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoiceConfirmRequest {

    @NotBlank
    private String productName;

    @NotNull
    @DecimalMin(value = "0.001", message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotBlank
    private String unit;

    @NotBlank
    private String operation; // ADD or REMOVE

    private BigDecimal price;

    private String originalVoiceText;
}
