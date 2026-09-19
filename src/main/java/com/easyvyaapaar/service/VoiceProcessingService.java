package com.easyvyaapaar.service;

import com.easyvyaapaar.dto.ParsedCommandDTO;

/**
 * Abstraction layer for voice/text command processing.
 *
 * Default implementation: NlpParserService (rule-based, no API key needed)
 * Future implementation: LlmParserService (using GPT/Gemini/etc.)
 *
 * To switch implementations, either:
 *   - Add @Primary to the new implementation, OR
 *   - Use a @ConditionalOnProperty toggle in AppConfig
 */
public interface VoiceProcessingService {

    /**
     * Process raw voice/text input and return a parsed command.
     *
     * @param text         The raw speech-to-text string
     * @param languageHint ISO-639-1 language code hint (e.g., "te", "hi", "en")
     * @return ParsedCommandDTO with intent, product, quantity, unit, confidence
     */
    ParsedCommandDTO process(String text, String languageHint);
}
