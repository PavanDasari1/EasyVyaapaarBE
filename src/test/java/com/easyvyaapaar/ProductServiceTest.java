package com.easyvyaapaar;

import com.easyvyaapaar.dto.ProductCreateRequest;
import com.easyvyaapaar.dto.ProductDTO;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.repository.ProductRepository;
import com.easyvyaapaar.service.ProductService;
import com.easyvyaapaar.util.UnitNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private ProductRepository productRepository;
    private UnitNormalizer unitNormalizer;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        unitNormalizer = Mockito.mock(UnitNormalizer.class);
        when(unitNormalizer.normalize("BAG")).thenReturn(Optional.of("BAG"));
        when(unitNormalizer.normalize("KG")).thenReturn(Optional.of("KG"));
        productService = new ProductService(productRepository, unitNormalizer);
    }

    @Test
    void testCreateProduct() {
        ProductCreateRequest req = new ProductCreateRequest();
        req.setName("Almonds");
        req.setCategory("Nuts");
        req.setUnit("KG");
        req.setCurrentStock(BigDecimal.valueOf(10));
        req.setMinimumStock(BigDecimal.valueOf(2));
        req.setPrice(BigDecimal.valueOf(800));

        Product saved = Product.builder()
                .id(100L)
                .name("Almonds")
                .category("Nuts")
                .unit("KG")
                .currentStock(BigDecimal.valueOf(10))
                .minimumStock(BigDecimal.valueOf(2))
                .price(BigDecimal.valueOf(800))
                .active(true)
                .build();

        when(productRepository.findByNameIgnoreCaseAndActiveTrue("Almonds")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductDTO res = productService.createProduct(req);
        assertNotNull(res);
        assertEquals("Almonds", res.getName());
        assertEquals("KG", res.getUnit());
    }

    @Test
    void testResolveOrCreateProductByNameAutoCreation() {
        Product created = Product.builder()
                .id(101L)
                .name("Cashews")
                .category("General")
                .unit("KG")
                .currentStock(BigDecimal.ZERO)
                .minimumStock(BigDecimal.valueOf(5))
                .active(true)
                .build();

        when(productRepository.findByNameIgnoreCaseAndActiveTrue("Cashews")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(created);

        Product res = productService.resolveOrCreateProductByName("Cashews", "KG", "ADD");
        assertNotNull(res);
        assertEquals("Cashews", res.getName());
        assertEquals("KG", res.getUnit());
    }
}
