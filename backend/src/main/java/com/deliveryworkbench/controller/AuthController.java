package com.deliveryworkbench.controller;

import com.deliveryworkbench.dto.AppUserResponse;
import com.deliveryworkbench.dto.LoginRequest;
import com.deliveryworkbench.dto.LoginResponse;
import com.deliveryworkbench.dto.RegisterRequest;
import com.deliveryworkbench.entity.AppUser;
import com.deliveryworkbench.entity.UserRole;
import com.deliveryworkbench.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .map(u -> ResponseEntity.ok(LoginResponse.builder()
                        .token("mock-jwt-token-for-dev")
                        .tokenType("Bearer")
                        .expiresInMs(3600000L)
                        .user(AppUserResponse.builder()
                                .id(u.getId())
                                .username(u.getUsername())
                                .fullName(u.getFullName())
                                .email(u.getEmail())
                                .role(u.getRole())
                                .active(u.isActive())
                                .createdAt(u.getCreatedAt())
                                .build())
                        .build()))
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        AppUser newUser = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(UserRole.valueOf(request.getRole()))
                .active(true)
                .build();
                
        AppUser saved = userRepository.save(newUser);
        
        return ResponseEntity.ok(AppUserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .active(saved.isActive())
                .createdAt(saved.getCreatedAt())
                .build());
    }
}
