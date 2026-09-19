package com.easyvyaapaar.service;

import com.easyvyaapaar.dto.ProductCreateRequest;
import com.easyvyaapaar.dto.ProductDTO;
import com.easyvyaapaar.dto.ProductUpdateRequest;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.exception.ProductNotFoundException;
import com.easyvyaapaar.repository.ProductRepository;
import com.easyvyaapaar.util.UnitNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UnitNormalizer unitNormalizer;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc()
                .stream().map(ProductDTO::from).collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product p = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductDTO.from(p);
    }

    public Optional<ProductDTO> findByName(String name) {
        return productRepository.findByNameIgnoreCaseAndActiveTrue(name)
                .map(ProductDTO::from);
    }

    public List<ProductDTO> searchProducts(String query) {
        return productRepository.searchByKeyword(query)
                .stream().map(ProductDTO::from).collect(Collectors.toList());
    }

    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream().map(ProductDTO::from).collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(ProductCreateRequest request) {
        // Normalize unit
        String normalizedUnit = unitNormalizer.normalize(request.getUnit())
                .orElse(request.getUnit().toUpperCase());

        // Check duplicate name
        if (productRepository.findByNameIgnoreCaseAndActiveTrue(request.getName()).isPresent()) {
            throw new IllegalArgumentException("A product named '" + request.getName() + "' already exists.");
        }

        Product product = Product.builder()
                .name(request.getName().trim())
                .category(request.getCategory())
                .unit(normalizedUnit)
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock() : java.math.BigDecimal.ZERO)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : java.math.BigDecimal.ZERO)
                .price(request.getPrice())
                .active(true)
                .build();

        Product saved = productRepository.save(product);
        log.info("Created product: id={} name='{}' unit={}", saved.getId(), saved.getName(), saved.getUnit());
        return ProductDTO.from(saved);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getUnit() != null && !request.getUnit().isBlank()) {
            String normalized = unitNormalizer.normalize(request.getUnit())
                    .orElse(request.getUnit().toUpperCase());
            product.setUnit(normalized);
        }
        if (request.getMinimumStock() != null) {
            product.setMinimumStock(request.getMinimumStock());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product saved = productRepository.save(product);
        log.info("Updated product: id={} name='{}'", saved.getId(), saved.getName());
        return ProductDTO.from(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setActive(false); // soft delete
        productRepository.save(product);
        log.info("Soft-deleted product: id={} name='{}'", id, product.getName());
    }

    /**
     * Used by NLP confirm flow — resolves product by name (case-insensitive).
     * Auto-creates the product if it doesn't exist and operation is ADD.
     */
    @Transactional
    public Product resolveOrCreateProductByName(String name, String unit, String operation) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        String trimmed = name.trim();
        Optional<Product> existing = productRepository.findByNameIgnoreCaseAndActiveTrue(trimmed);
        if (existing.isPresent()) {
            return existing.get();
        }

        if ("REMOVE".equalsIgnoreCase(operation)) {
            throw new ProductNotFoundException("Product '" + trimmed + "' does not exist in your inventory.");
        }

        // Auto-create product for ADD operation
        String normUnit = unitNormalizer.normalize(unit)
                .orElse(unit != null && !unit.isBlank() ? unit.toUpperCase() : "PIECE");

        Product newProduct = Product.builder()
                .name(trimmed)
                .category("General")
                .unit(normUnit)
                .currentStock(java.math.BigDecimal.ZERO)
                .minimumStock(java.math.BigDecimal.valueOf(5))
                .active(true)
                .build();

        Product saved = productRepository.save(newProduct);
        log.info("Auto-created product on the fly: id={} name='{}' unit={}", saved.getId(), saved.getName(), saved.getUnit());
        return saved;
    }

    public Product resolveProductByName(String name) {
        return resolveOrCreateProductByName(name, "PIECE", "ADD");
    }
}
