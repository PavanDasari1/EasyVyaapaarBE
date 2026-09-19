package com.easyvyaapaar.service;

import com.easyvyaapaar.dto.ParsedCommandDTO;
import com.easyvyaapaar.dto.ParsedCommandDTO.Intent;
import com.easyvyaapaar.repository.ProductRepository;
import com.easyvyaapaar.util.MultilingualDictionary;
import com.easyvyaapaar.util.NumberWordParser;
import com.easyvyaapaar.util.UnitNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Rule-based multilingual NLP parser.
 *
 * This is the default implementation of VoiceProcessingService.
 * It works WITHOUT any LLM API key.
 *
 * To add LLM support later:
 *   1. Create LlmParserService implements VoiceProcessingService
 *   2. Add @Primary to that class (or use a config toggle)
 *   3. No other code changes needed
 *
 * Pipeline:
 *   1. Normalize (lowercase, trim)
 *   2. Scan for intent keywords (across all loaded language dicts)
 *   3. Extract quantity (digit or number-word)
 *   4. Extract unit (from UnitNormalizer)
 *   5. Extract product (from dict alias + fuzzy DB match)
 *   6. Return ParsedCommandDTO with confidence score
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NlpParserService implements VoiceProcessingService {

    private final MultilingualDictionary dictionary;
    private final NumberWordParser numberWordParser;
    private final UnitNormalizer unitNormalizer;
    private final ProductRepository productRepository;

    @Override
    public ParsedCommandDTO process(String text, String languageHint) {
        log.debug("NLP processing: '{}' (lang={})", text, languageHint);

        ParsedCommandDTO result = new ParsedCommandDTO();
        result.setOriginalText(text);
        result.setParsed(false);

        if (text == null || text.isBlank()) {
            result.setIntent(Intent.UNKNOWN);
            result.setErrorMessage("Empty input");
            return result;
        }

        String normalized = text.toLowerCase().trim();
        String[] tokens = normalized.split("[\\s,।、]+");

        // ── Step 1: Detect intent ──────────────────────────────────
        Intent intent = detectIntent(normalized, tokens);
        result.setIntent(intent);

        // For query-only intents, handle them differently
        if (intent == Intent.QUERY_STOCK || intent == Intent.QUERY_LOW_STOCK) {
            result.setOperation(null);
            result.setParsed(true);
            result.setConfidence(0.8);
            // Try to extract product for QUERY_STOCK
            if (intent == Intent.QUERY_STOCK) {
                extractProduct(tokens, normalized).ifPresent(result::setProduct);
            }
            return result;
        }

        // ── Step 2: Extract quantity ───────────────────────────────
        Optional<Double> qty = numberWordParser.extractQuantity(text);
        qty.ifPresent(result::setQuantity);

        // ── Step 3: Extract unit ───────────────────────────────────
        String detectedUnit = extractUnit(tokens);
        result.setUnit(detectedUnit);

        // ── Step 4: Extract product ────────────────────────────────
        Optional<String> product = extractProduct(tokens, normalized);
        product.ifPresent(result::setProduct);

        // ── Step 5: Set operation ──────────────────────────────────
        if (intent == Intent.ADD_STOCK) result.setOperation("ADD");
        else if (intent == Intent.REMOVE_STOCK) result.setOperation("REMOVE");

        // ── Step 6: Compute confidence ─────────────────────────────
        double confidence = computeConfidence(result);
        result.setConfidence(confidence);
        result.setParsed(result.getProduct() != null && result.getQuantity() != null);

        if (!result.isParsed()) {
            result.setErrorMessage(buildErrorMessage(result));
        }

        log.debug("NLP result: intent={} product='{}' qty={} unit={} confidence={}",
                intent, result.getProduct(), result.getQuantity(), result.getUnit(), confidence);

        return result;
    }

    // ── Private helpers ─────────────────────────────────────────────

    private Intent detectIntent(String normalized, String[] tokens) {
        // Check low-stock first (subset of query)
        if (dictionary.getAllLowStockKeywords().stream().anyMatch(normalized::contains)) {
            return Intent.QUERY_LOW_STOCK;
        }
        // Query stock
        if (dictionary.getAllQueryStockKeywords().stream().anyMatch(normalized::contains)) {
            return Intent.QUERY_STOCK;
        }
        // Remove
        boolean hasRemove = Arrays.stream(tokens).anyMatch(dictionary::isRemoveKeyword)
                || dictionary.getAllRemoveKeywords().stream().anyMatch(normalized::contains);
        if (hasRemove) {
            return Intent.REMOVE_STOCK;
        }
        // Add
        boolean hasAdd = Arrays.stream(tokens).anyMatch(dictionary::isAddKeyword)
                || dictionary.getAllAddKeywords().stream().anyMatch(normalized::contains);
        if (hasAdd) {
            return Intent.ADD_STOCK;
        }
        // Default heuristic: If user specifies quantity/product without explicit 'remove', assume ADD
        return Intent.ADD_STOCK;
    }

    private String extractUnit(String[] tokens) {
        for (String token : tokens) {
            Optional<String> unit = unitNormalizer.normalize(token);
            if (unit.isPresent()) return unit.get();
        }
        return null;
    }

    private Optional<String> extractProduct(String[] tokens, String normalized) {
        // 1. Check dictionary aliases (regional words → English product name)
        for (String token : tokens) {
            Optional<String> alias = dictionary.resolveProductAlias(token);
            if (alias.isPresent()) return alias;
        }

        // 2. Multi-token alias check (e.g., "cooking oil" as two tokens)
        for (int i = 0; i < tokens.length - 1; i++) {
            String twoToken = tokens[i] + " " + tokens[i + 1];
            Optional<String> alias = dictionary.resolveProductAlias(twoToken);
            if (alias.isPresent()) return alias;
        }

        // 3. Fuzzy match against DB product names
        List<com.easyvyaapaar.entity.Product> allProducts = productRepository.findByActiveTrueOrderByNameAsc();
        for (String token : tokens) {
            if (token.length() < 3) continue; // skip short tokens
            for (var product : allProducts) {
                if (product.getName().toLowerCase().contains(token)
                        || token.contains(product.getName().toLowerCase())) {
                    return Optional.of(product.getName());
                }
            }
        }

        // 4. Dynamic extraction: Pick first candidate token that is not a quantity, unit, or intent word
        for (String token : tokens) {
            String cleanToken = token.replaceAll("[^a-zA-Z0-9అ-ఱఆ-ఔअ-ह]", "").trim();
            if (cleanToken.length() >= 2
                    && !unitNormalizer.normalize(cleanToken).isPresent()
                    && !numberWordParser.extractQuantity(cleanToken).isPresent()
                    && !dictionary.isAddKeyword(cleanToken)
                    && !dictionary.isRemoveKeyword(cleanToken)
                    && !cleanToken.matches("\\d+(\\.\\d+)?")) {
                String capitalized = cleanToken.substring(0, 1).toUpperCase() + cleanToken.substring(1).toLowerCase();
                return Optional.of(capitalized);
            }
        }

        return Optional.empty();
    }

    private double computeConfidence(ParsedCommandDTO cmd) {
        double score = 0.0;
        if (cmd.getIntent() != Intent.UNKNOWN) score += 0.3;
        if (cmd.getProduct() != null) score += 0.3;
        if (cmd.getQuantity() != null) score += 0.25;
        if (cmd.getUnit() != null) score += 0.15;
        return Math.min(score, 1.0);
    }

    private String buildErrorMessage(ParsedCommandDTO cmd) {
        if (cmd.getProduct() == null && cmd.getQuantity() == null) {
            return "I couldn't understand the product or quantity. Please try again.";
        }
        if (cmd.getProduct() == null) {
            return "I understood the quantity but couldn't identify the product. Please try again.";
        }
        if (cmd.getQuantity() == null) {
            return "I understood the product but couldn't identify the quantity. Please specify a number.";
        }
        return "I couldn't fully understand the command. Please try again.";
    }
}
