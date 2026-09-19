package com.easyvyaapaar.util;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resolves raw unit strings (from voice or manual input) to a normalized uppercase code.
 *
 * Supported codes: BAG, KG, GRAM, LITRE, ML, PIECE, CARTON, BOX, DOZEN, QUINTAL
 *
 * Unit lookup first goes through the multilingual dictionary (which covers regional language units),
 * then falls back to an English-only quick check.
 */
@Component
public class UnitNormalizer {

    private final MultilingualDictionary dictionary;

    public static final String DEFAULT_UNIT = "PIECE";

    public static final java.util.Set<String> VALID_UNITS = java.util.Set.of(
            "BAG", "KG", "GRAM", "LITRE", "ML", "PIECE", "CARTON", "BOX", "DOZEN", "QUINTAL", "PACKET"
    );

    public UnitNormalizer(MultilingualDictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Tries to normalize a unit string to an internal code.
     * Returns empty if the token is not recognized as a unit.
     */
    public Optional<String> normalize(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();
        String lower = raw.trim().toLowerCase();

        // 1. Dictionary lookup (covers all languages)
        Optional<String> fromDict = dictionary.resolveUnit(lower);
        if (fromDict.isPresent()) return fromDict;

        // 2. Already a valid code?
        if (VALID_UNITS.contains(raw.toUpperCase())) {
            return Optional.of(raw.toUpperCase());
        }

        return Optional.empty();
    }

    /**
     * Returns normalized unit or the provided fallback.
     */
    public String normalizeOrDefault(String raw, String fallback) {
        return normalize(raw).orElse(fallback != null ? fallback.toUpperCase() : DEFAULT_UNIT);
    }

    public boolean isValid(String unit) {
        return unit != null && VALID_UNITS.contains(unit.toUpperCase());
    }
}
