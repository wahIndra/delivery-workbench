package com.deliveryworkbench.dto;

import com.deliveryworkbench.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/** Response DTO for AppUser — password is never included. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean active;
    private OffsetDateTime createdAt;
}
