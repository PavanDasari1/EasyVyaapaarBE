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

        List<ShopDTO> shops = getSampleShops(totalProducts, totalStockValue);
        List<UserDTO> users = getSampleUsers();

        return AdminSummaryDTO.builder()
                .totalShops(shops.size())
                .activeShops(shops.stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus())).count())
                .totalUsers(users.size())
                .totalShopkeepers(users.stream().filter(u -> "SHOP_KEEPER".equalsIgnoreCase(u.getRole())).count())
                .totalProducts(totalProducts)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .totalStockValue(totalStockValue)
                .todaySales(new BigDecimal("14850.00"))
                .weeklySales(new BigDecimal("98400.00"))
                .monthlySales(new BigDecimal("412000.00"))
                .shops(shops)
                .users(users)
                .build();
    }

    public List<ShopDTO> getSampleShops(long productCount, BigDecimal stockVal) {
        List<ShopDTO> list = new ArrayList<>();
        list.add(ShopDTO.builder()
                .id(101L)
                .name("Sri Lakshmi Kirana & General Store")
                .ownerName("Dasari Pavan")
                .shopkeeperName("Ramesh Kumar")
                .location("Kukatpally, Hyderabad")
                .mobile("+91 9876543210")
                .activeProducts((int) productCount)
                .totalStockValue(stockVal)
                .stockStatus("NORMAL")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build());

        list.add(ShopDTO.builder()
                .id(102L)
                .name("Venkateshwara Traders")
                .ownerName("Srinivas Rao")
                .shopkeeperName("Suresh Reddy")
                .location("M.G. Road, Vijayawada")
                .mobile("+91 9848022334")
                .activeProducts(28)
                .totalStockValue(new BigDecimal("185400.00"))
                .stockStatus("LOW_STOCK")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(4))
                .build());

        list.add(ShopDTO.builder()
                .id(103L)
                .name("Balaji Retail Supermart")
                .ownerName("Kothari Brothers")
                .shopkeeperName("Mahesh Sharma")
                .location("RTC X Roads, Guntur")
                .mobile("+91 9912345678")
                .activeProducts(42)
                .totalStockValue(new BigDecimal("340500.00"))
                .stockStatus("NORMAL")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(2))
                .build());

        return list;
    }

    public List<UserDTO> getSampleUsers() {
        List<UserDTO> list = new ArrayList<>();
        list.add(UserDTO.builder()
                .id(1L)
                .name("Dasari Pavan")
                .email("admin@easyvyaapaar.com")
                .mobile("+91 9876543210")
                .role("SUPER_ADMIN")
                .shopName("System Wide")
                .shopId(null)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusYears(1))
                .build());

        list.add(UserDTO.builder()
                .id(2L)
                .name("Ramesh Kumar")
                .email("ramesh@lakshmikirana.com")
                .mobile("+91 9876500111")
                .role("SHOP_KEEPER")
                .shopName("Sri Lakshmi Kirana & General Store")
                .shopId(101L)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build());

        list.add(UserDTO.builder()
                .id(3L)
                .name("Suresh Reddy")
                .email("suresh@venkateshwara.com")
                .mobile("+91 9848022334")
                .role("SHOP_KEEPER")
                .shopName("Venkateshwara Traders")
                .shopId(102L)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(4))
                .build());

        list.add(UserDTO.builder()
                .id(4L)
                .name("Mahesh Sharma")
                .email("mahesh@balajisupermart.com")
                .mobile("+91 9912345678")
                .role("SHOP_KEEPER")
                .shopName("Balaji Retail Supermart")
                .shopId(103L)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now().minusMonths(2))
                .build());

        return list;
    }
}
