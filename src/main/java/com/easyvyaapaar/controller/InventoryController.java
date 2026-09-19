package com.easyvyaapaar.controller;

import com.easyvyaapaar.dto.ApiResponse;
import com.easyvyaapaar.dto.ProductDTO;
import com.easyvyaapaar.dto.StockUpdateRequest;
import com.easyvyaapaar.dto.TransactionDTO;
import com.easyvyaapaar.service.InventoryService;
import com.easyvyaapaar.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<TransactionDTO>> addStock(@Valid @RequestBody StockUpdateRequest request) {
        TransactionDTO tx = inventoryService.addStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock added successfully.", tx));
    }

    @PostMapping("/remove")
    public ResponseEntity<ApiResponse<TransactionDTO>> removeStock(@Valid @RequestBody StockUpdateRequest request) {
        TransactionDTO tx = inventoryService.removeStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock removed successfully.", tx));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success(productService.getLowStockProducts()));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactions(page, size)));
    }

    @GetMapping("/transactions/{productId}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getTransactionsByProduct(productId)));
    }

    @GetMapping("/transactions/recent")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getRecentTransactions() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getRecentTransactions()));
    }
}
