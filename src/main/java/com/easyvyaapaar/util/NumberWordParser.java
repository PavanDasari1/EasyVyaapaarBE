package com.easyvyaapaar.util;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Robust multilingual quantity extractor.
 * Handles:
 *   1. ASCII digits (5, 2.5, 100)
 *   2. Regional Indic script digits (Telugu ౦-౯, Hindi ०-९, Tamil ௦-൯, Kannada ೦-೯, Malayalam ൦-൯)
 *   3. Digits attached to words or units (5kg, 5బస్తాలు, 10కేజీలు)
 *   4. Regional number words (ఒకటి, రెండు, మూడు, ఐదు, పది, iravai, etc.)
 *   5. Substring prefix matching for attached number words (e.g. ഐദുkg, ఐదుబస్తాలు)
 */
@Component
public class NumberWordParser {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    private final MultilingualDictionary dictionary;

    public NumberWordParser(MultilingualDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Optional<Double> extractQuantity(String rawText) {
        if (rawText == null || rawText.isBlank()) return Optional.empty();

        // 1. Normalize Indic digits (Telugu, Hindi, Tamil, Kannada, Malayalam) to standard ASCII 0-9
        String normalizedText = normalizeIndicDigits(rawText.toLowerCase().trim());

        // 2. Try regex digit match anywhere in the text (e.g., "5", "2.5", "5kg", "5బస్తాలు")
        Matcher m = DIGIT_PATTERN.matcher(normalizedText);
        if (m.find()) {
            try {
                return Optional.of(Double.parseDouble(m.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }

        // 3. Try multi-token number word lookup (e.g. "twenty five", "ఇరవై ఐదు")
        String[] tokens = normalizedText.split("[\\s,।、]+");
        for (int i = 0; i < tokens.length - 1; i++) {
            String twoToken = tokens[i] + " " + tokens[i + 1];
            Optional<Double> numWord = dictionary.resolveNumberWord(twoToken);
            if (numWord.isPresent()) {
                return numWord;
            }
        }

        // 4. Try single token number word lookup
        for (String token : tokens) {
            Optional<Double> numWord = dictionary.resolveNumberWord(token);
            if (numWord.isPresent()) {
                return numWord;
            }
        }

        // 5. Fallback: Prefix search for number words embedded inside attached words (e.g. "ఐదుబస్తాలు")
        for (String token : tokens) {
            for (var entry : dictionary.getNumberWordMap().entrySet()) {
                String word = entry.getKey();
                if (word.length() >= 2 && token.startsWith(word)) {
                    return Optional.of(entry.getValue());
                }
            }
        }

        return Optional.empty();
    }

    private String normalizeIndicDigits(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            // Telugu digits ౦ (0C66) to ౯ (0C6F)
            if (c >= '\u0C66' && c <= '\u0C6F') {
                sb.append((char) ('0' + (c - '\u0C66')));
            }
            // Devanagari (Hindi) digits ० (0966) to ९ (096F)
            else if (c >= '\u0966' && c <= '\u096F') {
                sb.append((char) ('0' + (c - '\u0966')));
            }
            // Tamil digits ௦ (0BE6) to ௯ (0BEF)
            else if (c >= '\u0BE6' && c <= '\u0BEF') {
                sb.append((char) ('0' + (c - '\u0BE6')));
            }
            // Kannada digits ೦ (0CE6) to ೯ (0CEF)
            else if (c >= '\u0CE6' && c <= '\u0CEF') {
                sb.append((char) ('0' + (c - '\u0CE6')));
            }
            // Malayalam digits ൦ (0D66) to ൯ (0D6F)
            else if (c >= '\u0D66' && c <= '\u0D6F') {
                sb.append((char) ('0' + (c - '\u0D66')));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
