package com.easyvyaapaar.dto;

import com.easyvyaapaar.entity.InventoryTransaction;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String operation;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal price;
    private String source;
    private String originalVoiceText;
    private String notes;
    private LocalDateTime createdAt;

    public static TransactionDTO from(InventoryTransaction t) {
        return TransactionDTO.builder()
                .id(t.getId())
                .productId(t.getProduct().getId())
                .productName(t.getProduct().getName())
                .operation(t.getOperation().name())
                .quantity(t.getQuantity())
                .unit(t.getUnit())
                .price(t.getPrice())
                .source(t.getSource().name())
                .originalVoiceText(t.getOriginalVoiceText())
                .notes(t.getNotes())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
