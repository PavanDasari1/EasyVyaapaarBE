package com.easyvyaapaar.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String role; // SUPER_ADMIN, SHOP_KEEPER
    private String shopName;
    private Long shopId;
    private String status; // ACTIVE, DISABLED
    private LocalDateTime createdAt;
}
