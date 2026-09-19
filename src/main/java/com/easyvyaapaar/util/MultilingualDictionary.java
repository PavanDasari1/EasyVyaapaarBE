package com.easyvyaapaar.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Loads and merges multilingual dictionaries from i18n/dict-*.json files.
 * Provides lookup methods for intent keywords, number words, units, and product names.
 *
 * Adding a new language = add a new dict-XX.json file. No Java code changes required.
 */
@Slf4j
@Component
public class MultilingualDictionary {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Merged across all languages
    private final Map<String, String> productAliasMap = new HashMap<>();        // regional name → English name
    private final List<String> addKeywords = new ArrayList<>();
    private final List<String> removeKeywords = new ArrayList<>();
    private final List<String> queryStockKeywords = new ArrayList<>();
    private final List<String> lowStockKeywords = new ArrayList<>();
    private final Map<String, Double> numberWordMap = new HashMap<>();          // number word → double
    private final Map<String, String> unitAliasMap = new HashMap<>();            // regional unit → normalized code

    private static final String[] DICT_FILES = {
            "i18n/dict-en.json",
            "i18n/dict-te.json",
            "i18n/dict-ta.json",
            "i18n/dict-kn.json",
            "i18n/dict-ml.json",
            "i18n/dict-hi.json"
    };

    @PostConstruct
    public void init() {
        for (String file : DICT_FILES) {
            try {
                ClassPathResource resource = new ClassPathResource(file);
                JsonNode root = objectMapper.readTree(resource.getInputStream());
                loadProducts(root.path("products"));
                loadKeywords(root.path("add_keywords"), addKeywords);
                loadKeywords(root.path("remove_keywords"), removeKeywords);
                loadKeywords(root.path("query_stock_keywords"), queryStockKeywords);
                loadKeywords(root.path("low_stock_keywords"), lowStockKeywords);
                loadNumberWords(root.path("number_words"));
                loadUnits(root.path("units"));
                log.info("Loaded dictionary: {}", file);
            } catch (IOException e) {
                log.warn("Could not load dictionary file: {} — {}", file, e.getMessage());
            }
        }
        log.info("MultilingualDictionary initialized. {} product aliases, {} units, {} number words loaded.",
                productAliasMap.size(), unitAliasMap.size(), numberWordMap.size());
    }

    private void loadProducts(JsonNode node) {
        if (node.isMissingNode()) return;
        node.fields().forEachRemaining(e ->
                productAliasMap.put(e.getKey().toLowerCase(), e.getValue().asText()));
    }

    private void loadKeywords(JsonNode node, List<String> target) {
        if (node.isMissingNode()) return;
        node.forEach(n -> target.add(n.asText().toLowerCase()));
    }

    private void loadNumberWords(JsonNode node) {
        if (node.isMissingNode()) return;
        node.fields().forEachRemaining(e ->
                numberWordMap.put(e.getKey().toLowerCase(), e.getValue().asDouble()));
    }

    private void loadUnits(JsonNode node) {
        if (node.isMissingNode()) return;
        node.fields().forEachRemaining(e ->
                unitAliasMap.put(e.getKey().toLowerCase(), e.getValue().asText()));
    }

    // ---- Public Lookup API ----

    public Optional<String> resolveProductAlias(String token) {
        return Optional.ofNullable(productAliasMap.get(token.toLowerCase()));
    }

    public boolean isAddKeyword(String token) {
        return addKeywords.contains(token.toLowerCase());
    }

    public boolean isRemoveKeyword(String token) {
        return removeKeywords.contains(token.toLowerCase());
    }

    public boolean isQueryStockKeyword(String token) {
        return queryStockKeywords.stream().anyMatch(kw -> token.toLowerCase().contains(kw));
    }

    public boolean isLowStockKeyword(String token) {
        return lowStockKeywords.stream().anyMatch(kw -> token.toLowerCase().contains(kw));
    }

    public Optional<Double> resolveNumberWord(String token) {
        return Optional.ofNullable(numberWordMap.get(token.toLowerCase()));
    }

    public Optional<String> resolveUnit(String token) {
        return Optional.ofNullable(unitAliasMap.get(token.toLowerCase()));
    }

    public List<String> getAllAddKeywords() { return Collections.unmodifiableList(addKeywords); }
    public List<String> getAllRemoveKeywords() { return Collections.unmodifiableList(removeKeywords); }
    public List<String> getAllQueryStockKeywords() { return Collections.unmodifiableList(queryStockKeywords); }
    public List<String> getAllLowStockKeywords() { return Collections.unmodifiableList(lowStockKeywords); }
    public Map<String, String> getProductAliasMap() { return Collections.unmodifiableMap(productAliasMap); }
    public Map<String, String> getUnitAliasMap() { return Collections.unmodifiableMap(unitAliasMap); }
    public Map<String, Double> getNumberWordMap() { return Collections.unmodifiableMap(numberWordMap); }
}
