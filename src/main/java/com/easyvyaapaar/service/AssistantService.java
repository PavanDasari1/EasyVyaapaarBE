package com.easyvyaapaar.service;

import com.easyvyaapaar.dto.ParsedCommandDTO;
import com.easyvyaapaar.dto.ParsedCommandDTO.Intent;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles natural-language queries about inventory.
 * Always reads from the real database — never invents values.
 *
 * Supports:
 *   - "How much rice do I have?" → QUERY_STOCK
 *   - "What is running low?" → QUERY_LOW_STOCK
 *   - "What should I order?" → QUERY_LOW_STOCK
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final ProductRepository productRepository;
    private final VoiceProcessingService voiceProcessingService;

    /**
     * Process a natural-language query and return a human-readable text answer.
     *
     * @param query    The user's question
     * @param language The user's preferred response language (en, te, ta, kn, ml, hi)
     * @return Text answer based on real DB data
     */
    public String answer(String query, String language) {
        ParsedCommandDTO parsed = voiceProcessingService.process(query, language);
        Intent intent = parsed.getIntent();

        log.debug("Assistant query: '{}' → intent={} product={}", query, intent, parsed.getProduct());

        if (intent == Intent.QUERY_STOCK) {
            return answerStockQuery(parsed, language);
        } else if (intent == Intent.QUERY_LOW_STOCK) {
            return answerLowStockQuery(language);
        } else if (intent == Intent.ADD_STOCK || intent == Intent.REMOVE_STOCK) {
            return "To add or remove stock, please use voice commands on the main screen.";
        } else {
            // Try low-stock as fallback for unknown
            return answerLowStockQuery(language);
        }
    }

    private String answerStockQuery(ParsedCommandDTO parsed, String language) {
        if (parsed.getProduct() == null) {
            return localise("I couldn't identify which product you're asking about. Please mention the product name.",
                    language, null, null, null);
        }

        return productRepository.findByNameIgnoreCaseAndActiveTrue(parsed.getProduct())
                .map(p -> formatStockAnswer(p, language))
                .orElse("Product '" + parsed.getProduct() + "' not found in your inventory.");
    }

    private String answerLowStockQuery(String language) {
        List<Product> lowStock = productRepository.findLowStockProducts();

        if (lowStock.isEmpty()) {
            return localise("All products have sufficient stock. No reorder needed right now!", language, null, null, null);
        }

        String productList = lowStock.stream()
                .map(p -> p.getName() + " (" + p.getCurrentStock().stripTrailingZeros().toPlainString()
                        + " " + p.getUnit().toLowerCase() + " remaining)")
                .collect(Collectors.joining(", "));

        return localise("The following products are running low: " + productList
                + ". Please reorder soon.", language, null, null, null);
    }

    private String formatStockAnswer(Product p, String language) {
        String stock = p.getCurrentStock().stripTrailingZeros().toPlainString();
        String unit = p.getUnit().toLowerCase();
        String name = p.getName();

        String lang = (language == null) ? "en" : language;
        return switch (lang) {
            case "te" -> "మీ వద్ద " + name + " " + stock + " " + unit + " ఉన్నాయి.";
            case "hi" -> "आपके पास " + name + " के " + stock + " " + unit + " हैं।";
            case "ta" -> "உங்களிடம் " + name + " " + stock + " " + unit + " உள்ளது.";
            case "kn" -> "ನಿಮ್ಮ ಬಳಿ " + name + " " + stock + " " + unit + " ಇದೆ.";
            case "ml" -> "നിങ്ങളുടെ കൈവശം " + name + " " + stock + " " + unit + " ഉണ്ട്.";
            default -> "You have " + stock + " " + unit + " of " + name + ".";
        };
    }

    private String localise(String english, String language, String te, String hi, String ta) {
        // Provide English as safe fallback for all languages
        return english;
    }
}
