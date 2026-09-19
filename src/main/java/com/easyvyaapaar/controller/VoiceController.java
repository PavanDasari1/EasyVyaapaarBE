package com.easyvyaapaar.controller;

import com.easyvyaapaar.dto.*;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.service.InventoryService;
import com.easyvyaapaar.service.ProductService;
import com.easyvyaapaar.service.VoiceProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceProcessingService voiceProcessingService;
    private final ProductService productService;
    private final InventoryService inventoryService;

    /**
     * Step 1 of voice flow: parse the speech text and return extracted info for user confirmation.
     * Does NOT modify the database yet.
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<ParsedCommandDTO>> processVoice(
            @Valid @RequestBody VoiceProcessRequest request) {
        log.info("Voice process: '{}' lang={}", request.getText(), request.getLanguage());
        ParsedCommandDTO parsed = voiceProcessingService.process(request.getText(), request.getLanguage());
        return ResponseEntity.ok(ApiResponse.success(parsed));
    }

    /**
     * Step 2 of voice flow: user confirmed the parsed command, now update inventory.
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<TransactionDTO>> confirmVoice(
            @Valid @RequestBody VoiceConfirmRequest request) {
        log.info("Voice confirm: product='{}' qty={} unit={} op={}",
                request.getProductName(), request.getQuantity(),
                request.getUnit(), request.getOperation());

        // Resolve product by name (or auto-create if missing for ADD operation)
        Product product = productService.resolveOrCreateProductByName(
                request.getProductName(), request.getUnit(), request.getOperation());

        StockUpdateRequest stockRequest = new StockUpdateRequest();
        stockRequest.setProductId(product.getId());
        stockRequest.setQuantity(request.getQuantity());
        stockRequest.setUnit(request.getUnit());
        stockRequest.setPrice(request.getPrice());
        stockRequest.setSource("VOICE");
        stockRequest.setOriginalVoiceText(request.getOriginalVoiceText());

        TransactionDTO tx;
        if ("ADD".equalsIgnoreCase(request.getOperation())) {
            tx = inventoryService.addStock(stockRequest);
        } else if ("REMOVE".equalsIgnoreCase(request.getOperation())) {
            tx = inventoryService.removeStock(stockRequest);
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid operation: " + request.getOperation(), "INVALID_OPERATION"));
        }

        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully.", tx));
    }
}
