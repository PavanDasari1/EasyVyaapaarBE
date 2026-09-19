package com.easyvyaapaar.dto;

import lombok.*;

/**
 * Result of NLP parsing a voice command.
 * Returned to the frontend for user confirmation before any DB write.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedCommandDTO {

    public enum Intent {
        ADD_STOCK,
        REMOVE_STOCK,
        QUERY_STOCK,
        QUERY_LOW_STOCK,
        UNKNOWN
    }

    private Intent intent;
    private String product;
    private Double quantity;
    private String unit;
    private String operation; // ADD or REMOVE
    private Double price;
    private double confidence; // 0.0 - 1.0
    private String originalText;
    private String responseText; // For QUERY intents
    private String errorMessage;  // If parsing failed
    private boolean parsed;       // true if successfully extracted
}
