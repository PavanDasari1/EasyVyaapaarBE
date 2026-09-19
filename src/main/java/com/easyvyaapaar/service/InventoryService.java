package com.easyvyaapaar.service;

import com.easyvyaapaar.dto.StockUpdateRequest;
import com.easyvyaapaar.dto.TransactionDTO;
import com.easyvyaapaar.entity.InventoryTransaction;
import com.easyvyaapaar.entity.InventoryTransaction.Operation;
import com.easyvyaapaar.entity.InventoryTransaction.Source;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.exception.InsufficientStockException;
import com.easyvyaapaar.exception.ProductNotFoundException;
import com.easyvyaapaar.repository.InventoryTransactionRepository;
import com.easyvyaapaar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryTransactionRepository transactionRepository;

    /**
     * Adds stock to a product.
     * Validates: quantity > 0, product exists and is active.
     */
    @Transactional
    public TransactionDTO addStock(StockUpdateRequest request) {
        validateQuantity(request.getQuantity());

        Product product = productRepository.findById(request.getProductId())
                .filter(Product::getActive)
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        BigDecimal newStock = product.getCurrentStock().add(request.getQuantity());
        product.setCurrentStock(newStock);
        productRepository.save(product);

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .operation(Operation.ADD)
                .quantity(request.getQuantity())
                .unit(resolveUnit(request, product))
                .price(request.getPrice())
                .source(parseSource(request.getSource()))
                .originalVoiceText(request.getOriginalVoiceText())
                .notes(request.getNotes())
                .build();

        InventoryTransaction saved = transactionRepository.save(tx);
        log.info("ADD stock: product='{}' qty={} -> newStock={}", product.getName(), request.getQuantity(), newStock);
        return TransactionDTO.from(saved);
    }

    /**
     * Removes stock from a product.
     * Validates: quantity > 0, product exists, sufficient stock available.
     */
    @Transactional
    public TransactionDTO removeStock(StockUpdateRequest request) {
        validateQuantity(request.getQuantity());

        Product product = productRepository.findById(request.getProductId())
                .filter(Product::getActive)
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        // Prevent negative stock
        if (product.getCurrentStock().compareTo(request.getQuantity()) < 0) {
            throw new InsufficientStockException(
                    String.format("Only %.3f %s of %s are available. Cannot remove %.3f.",
                            product.getCurrentStock(), product.getUnit(),
                            product.getName(), request.getQuantity()));
        }

        BigDecimal newStock = product.getCurrentStock().subtract(request.getQuantity());
        product.setCurrentStock(newStock);
        productRepository.save(product);

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .operation(Operation.REMOVE)
                .quantity(request.getQuantity())
                .unit(resolveUnit(request, product))
                .price(request.getPrice())
                .source(parseSource(request.getSource()))
                .originalVoiceText(request.getOriginalVoiceText())
                .notes(request.getNotes())
                .build();

        InventoryTransaction saved = transactionRepository.save(tx);
        log.info("REMOVE stock: product='{}' qty={} -> newStock={}", product.getName(), request.getQuantity(), newStock);
        return TransactionDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactions(int page, int size) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(TransactionDTO::from);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByProduct(Long productId) {
        return transactionRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(TransactionDTO::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getRecentTransactions() {
        return transactionRepository.findTop10ByOrderByCreatedAtDesc()
                .stream().map(TransactionDTO::from).collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    private String resolveUnit(StockUpdateRequest request, Product product) {
        if (request.getUnit() != null && !request.getUnit().isBlank()) {
            return request.getUnit().toUpperCase();
        }
        return product.getUnit();
    }

    private Source parseSource(String source) {
        try {
            return Source.valueOf(source.toUpperCase());
        } catch (Exception e) {
            return Source.MANUAL;
        }
    }
}
