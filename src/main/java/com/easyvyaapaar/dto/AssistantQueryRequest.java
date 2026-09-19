package com.easyvyaapaar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request for POST /api/assistant/query
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssistantQueryRequest {

    @NotBlank
    private String query;

    private String language = "en";
}
