package com.easyvyaapaar.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDTO {

    private Long id;
    private String name;
    private String ownerName;
    private String shopkeeperName;
    private String location;
    private String mobile;
    private int activeProducts;
    private BigDecimal totalStockValue;
    private String stockStatus; // NORMAL, LOW_STOCK, CRITICAL
    private String status; // ACTIVE, INACTIVE, DISABLED
    private LocalDateTime createdAt;
}
