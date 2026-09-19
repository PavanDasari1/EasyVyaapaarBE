package com.easyvyaapaar;

import com.easyvyaapaar.dto.StockUpdateRequest;
import com.easyvyaapaar.dto.TransactionDTO;
import com.easyvyaapaar.entity.InventoryTransaction;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.exception.InsufficientStockException;
import com.easyvyaapaar.repository.InventoryTransactionRepository;
import com.easyvyaapaar.repository.ProductRepository;
import com.easyvyaapaar.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class InventoryServiceTest {

    private ProductRepository productRepository;
    private InventoryTransactionRepository transactionRepository;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        transactionRepository = Mockito.mock(InventoryTransactionRepository.class);
        inventoryService = new InventoryService(productRepository, transactionRepository);
    }

    @Test
    void testAddStockSuccess() {
        Product rice = Product.builder().id(1L).name("Rice").unit("BAG").currentStock(BigDecimal.valueOf(25)).minimumStock(BigDecimal.valueOf(5)).active(true).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(rice));

        InventoryTransaction savedTx = InventoryTransaction.builder()
                .id(200L)
                .product(rice)
                .operation(InventoryTransaction.Operation.ADD)
                .quantity(BigDecimal.valueOf(5))
                .unit("BAG")
                .source(InventoryTransaction.Source.VOICE)
                .build();

        when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(savedTx);

        StockUpdateRequest req = new StockUpdateRequest();
        req.setProductId(1L);
        req.setQuantity(BigDecimal.valueOf(5));
        req.setUnit("BAG");
        req.setSource("VOICE");

        TransactionDTO res = inventoryService.addStock(req);
        assertNotNull(res);
        assertEquals("Rice", res.getProductName());
        assertEquals(BigDecimal.valueOf(5), res.getQuantity());
    }

    @Test
    void testRemoveStockInsufficientStockThrowsException() {
        Product sugar = Product.builder().id(2L).name("Sugar").unit("BAG").currentStock(BigDecimal.valueOf(2)).minimumStock(BigDecimal.valueOf(5)).active(true).build();
        when(productRepository.findById(2L)).thenReturn(Optional.of(sugar));

        StockUpdateRequest req = new StockUpdateRequest();
        req.setProductId(2L);
        req.setQuantity(BigDecimal.valueOf(10)); // Requesting 10 bags when only 2 available

        assertThrows(InsufficientStockException.class, () -> inventoryService.removeStock(req));
    }
}
