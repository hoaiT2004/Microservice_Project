package com.example.authservice.controller;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RefreshTokenRequest;
import com.example.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
//        return ResponseEntity.ok(authService.register(request));
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println(request);
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshTokens(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}
