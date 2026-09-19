package com.easyvyaapaar;

import com.easyvyaapaar.dto.ParsedCommandDTO;
import com.easyvyaapaar.dto.ParsedCommandDTO.Intent;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.repository.ProductRepository;
import com.easyvyaapaar.service.NlpParserService;
import com.easyvyaapaar.util.MultilingualDictionary;
import com.easyvyaapaar.util.NumberWordParser;
import com.easyvyaapaar.util.UnitNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class NlpParserServiceTest {

    private MultilingualDictionary dictionary;
    private NumberWordParser numberWordParser;
    private UnitNormalizer unitNormalizer;
    private ProductRepository productRepository;
    private NlpParserService nlpParserService;

    @BeforeEach
    void setUp() {
        dictionary = new MultilingualDictionary();
        dictionary.init();
        unitNormalizer = new UnitNormalizer(dictionary);
        numberWordParser = new NumberWordParser(dictionary);
        productRepository = Mockito.mock(ProductRepository.class);

        Product rice = Product.builder().id(1L).name("Rice").unit("BAG").currentStock(BigDecimal.valueOf(25)).minimumStock(BigDecimal.valueOf(5)).active(true).build();
        Product sugar = Product.builder().id(2L).name("Sugar").unit("BAG").currentStock(BigDecimal.valueOf(10)).minimumStock(BigDecimal.valueOf(5)).active(true).build();
        when(productRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(rice, sugar));

        nlpParserService = new NlpParserService(dictionary, numberWordParser, unitNormalizer, productRepository);
    }

    @Test
    void testParseEnglishAddCommand() {
        ParsedCommandDTO res = nlpParserService.process("Add 5 bags of rice", "en");
        assertEquals(Intent.ADD_STOCK, res.getIntent());
        assertEquals("Rice", res.getProduct());
        assertEquals(5.0, res.getQuantity());
        assertEquals("BAG", res.getUnit());
        assertEquals("ADD", res.getOperation());
        assertTrue(res.isParsed());
    }

    @Test
    void testParseTeluguAddCommandScript() {
        ParsedCommandDTO res = nlpParserService.process("బియ్యం 2 బస్తాలు వచ్చాయి", "te");
        assertEquals(Intent.ADD_STOCK, res.getIntent());
        assertEquals("Rice", res.getProduct());
        assertEquals(2.0, res.getQuantity());
        assertEquals("BAG", res.getUnit());
        assertEquals("ADD", res.getOperation());
        assertTrue(res.isParsed());
    }

    @Test
    void testParseTeluguIndicDigits() {
        ParsedCommandDTO res = nlpParserService.process("బియ్యం ౫ బస్తాలు వచ్చాయి", "te");
        assertEquals(Intent.ADD_STOCK, res.getIntent());
        assertEquals("Rice", res.getProduct());
        assertEquals(5.0, res.getQuantity());
        assertEquals("BAG", res.getUnit());
        assertEquals("ADD", res.getOperation());
        assertTrue(res.isParsed());
    }

    @Test
    void testParseMixedLanguageCommand() {
        ParsedCommandDTO res = nlpParserService.process("biyyam 5 basthalu add cheyyi", "te");
        assertEquals(Intent.ADD_STOCK, res.getIntent());
        assertEquals("Rice", res.getProduct());
        assertEquals(5.0, res.getQuantity());
        assertEquals("BAG", res.getUnit());
        assertEquals("ADD", res.getOperation());
        assertTrue(res.isParsed());
    }

    @Test
    void testParseHindiAddCommand() {
        ParsedCommandDTO res = nlpParserService.process("5 बोरी चावल आया", "hi");
        assertEquals(Intent.ADD_STOCK, res.getIntent());
        assertEquals("Rice", res.getProduct());
        assertEquals(5.0, res.getQuantity());
        assertEquals("BAG", res.getUnit());
        assertEquals("ADD", res.getOperation());
        assertTrue(res.isParsed());
    }

    @Test
    void testParseRemoveStockCommand() {
        ParsedCommandDTO res = nlpParserService.process("Remove 2 bags sugar", "en");
        assertEquals(Intent.REMOVE_STOCK, res.getIntent());
        assertEquals("Sugar", res.getProduct());
        assertEquals(2.0, res.getQuantity());
        assertEquals("BAG", res.getUnit());
        assertEquals("REMOVE", res.getOperation());
        assertTrue(res.isParsed());
    }

    @Test
    void testParseQueryStockCommand() {
        ParsedCommandDTO res = nlpParserService.process("How much rice do I have?", "en");
        assertEquals(Intent.QUERY_STOCK, res.getIntent());
        assertEquals("Rice", res.getProduct());
        assertTrue(res.isParsed());
    }
}
