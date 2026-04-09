package com.example.authservice.service;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.RefreshTokenRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.entity.Customer;
import com.example.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

//    public String register(RegisterRequest request) {
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username is already taken");
//        }
//
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email is already taken");
//        }
//
//        String role = (request.getRole() != null && !request.getRole().isEmpty()) ? request.getRole() : "CUSTOMER";
//
//        User user = User.builder()
//                .username(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .email(request.getEmail())
//                .role(role)
//                .build();
//
//        userRepository.save(user);
//
//        return "User registered successfully";
//    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshTokens(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Check if refresh token is valid and not expired
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        jwtService.validateToken(refreshToken, userDetails); // This will throw TokenExpiredException if it's expired

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = refreshToken; // By default, reuse the old refresh token

        // Check if the refresh token is about to expire (e.g., within the next 2 hours)
        Date expirationDate = jwtService.extractExpiration(refreshToken);
        long twoHoursInMillis = 2 * 60 * 60 * 1000;
        if (expirationDate.getTime() - System.currentTimeMillis() < twoHoursInMillis) {
            newRefreshToken = jwtService.generateRefreshToken(userDetails);
        }

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void validateToken(String token) {
        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // This will now throw a specific exception if validation fails
        jwtService.validateToken(token, userDetails);
    }
}
