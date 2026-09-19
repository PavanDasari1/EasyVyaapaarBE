package com.easyvyaapaar.controller;

import com.easyvyaapaar.dto.AdminSummaryDTO;
import com.easyvyaapaar.dto.ApiResponse;
import com.easyvyaapaar.dto.ShopDTO;
import com.easyvyaapaar.dto.UserDTO;
import com.easyvyaapaar.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminSummaryDTO>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getSystemSummary()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getSampleUsers()));
    }

    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<ShopDTO>>> getShops() {
        AdminSummaryDTO summary = adminService.getSystemSummary();
        return ResponseEntity.ok(ApiResponse.success(summary.getShops()));
    }
}
