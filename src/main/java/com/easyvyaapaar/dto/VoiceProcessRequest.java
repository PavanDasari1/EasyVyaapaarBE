package com.easyvyaapaar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request body for POST /api/voice/process
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoiceProcessRequest {

    @NotBlank(message = "Voice text cannot be empty")
    private String text;

    private String language = "en"; // language hint: en, te, ta, kn, ml, hi
}
