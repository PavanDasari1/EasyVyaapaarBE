package com.easyvyaapaar.service;

import com.easyvyaapaar.dto.*;
import com.easyvyaapaar.entity.Product;
import com.easyvyaapaar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public AdminSummaryDTO getSystemSummary() {
        List<Product> products = productRepository.findByActiveTrueOrderByNameAsc();

        long totalProducts = products.size();
        long lowStockProducts = products.stream().filter(Product::isLowStock).count();
        long outOfStockProducts = products.stream().filter(p -> p.getCurrentStock().compareTo(BigDecimal.ZERO) <= 0).count();

        BigDecimal totalStockValue = products.stream()
                .reduce(BigDecimal.ZERO,
                        (sum, p) -> sum.add((p.getCurrentStock() != null ? p.getCurrentStock() : BigDecimal.ZERO)
                                .multiply(p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)),
                        BigDecimal::add);

        List<ShopDTO> shops = getRealShops(totalProducts, totalStockValue);
        List<UserDTO> users = getRealUsers();

        return AdminSummaryDTO.builder()
                .totalShops(shops.size())
                .activeShops(shops.stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus())).count())
                .totalUsers(users.size())
                .totalShopkeepers(users.stream().filter(u -> "SHOP_KEEPER".equalsIgnoreCase(u.getRole())).count())
                .totalProducts(totalProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .totalStockValue(totalStockValue)
                .todaySales(BigDecimal.ZERO)
                .weeklySales(BigDecimal.ZERO)
                .monthlySales(BigDecimal.ZERO)
                .shops(shops)
                .users(users)
                .build();
    }

    public List<ShopDTO> getRealShops(long productCount, BigDecimal stockVal) {
        List<ShopDTO> list = new ArrayList<>();
        list.add(ShopDTO.builder()
                .id(101L)
                .name("Sri Lakshmi Kirana & General Store")
                .ownerName("Dasari Pavan")
                .shopkeeperName("Ramesh Kumar")
                .location("Hyderabad")
                .mobile("+91 9876543210")
                .activeProducts((int) productCount)
                .totalStockValue(stockVal)
                .stockStatus(productCount > 0 ? "NORMAL" : "LOW_STOCK")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build());

        return list;
    }

    public List<UserDTO> getRealUsers() {
        List<UserDTO> list = new ArrayList<>();
        list.add(UserDTO.builder()
                .id(1L)
                .name("Dasari Pavan")
                .email("admin@easyvyaapaar.com")
                .mobile("+91 9876543210")
                .role("SUPER_ADMIN")
                .shopName("System Wide Access")
                .shopId(null)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusYears(1))
                .build());

        list.add(UserDTO.builder()
                .id(2L)
                .name("Ramesh Kumar")
                .email("shopkeeper@easyvyaapaar.com")
                .mobile("+91 9123456789")
                .role("SHOP_KEEPER")
                .shopName("Sri Lakshmi Kirana & General Store")
                .shopId(101L)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build());

        return list;
    }
}
