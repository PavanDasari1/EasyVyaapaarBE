package com.easyvyaapaar.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSummaryDTO {

    private long totalShops;
    private long activeShops;
    private long totalUsers;
    private long totalShopkeepers;
    private long totalProducts;
    private long lowStockProducts;
    private long outOfStockProducts;
    private BigDecimal totalStockValue;
    private BigDecimal todaySales;
    private BigDecimal weeklySales;
    private BigDecimal monthlySales;

    private List<ShopDTO> shops;
    private List<UserDTO> users;
}
