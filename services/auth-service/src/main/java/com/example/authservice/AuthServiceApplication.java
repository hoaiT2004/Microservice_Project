package com.example.authservice;

import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @PostConstruct
    public void seedUsers() {
        List<UserSeed> demoUsers = Arrays.asList(
                new UserSeed("admin", "admin@example.com", "admin123", "ADMIN"),
                new UserSeed("manager", "manager@example.com", "manager123", "ADMIN"),
                new UserSeed("customer1", "customer1@example.com", "customer123", "CUSTOMER"),
                new UserSeed("customer2", "customer2@example.com", "customer123", "CUSTOMER"),
                new UserSeed("customer3", "customer3@example.com", "customer123", "CUSTOMER")
        );

        int createdUsers = 0;
        for (UserSeed seed : demoUsers) {
            User user = User.builder()
                    .username(seed.username)
                    .email(seed.email)
                    .password(passwordEncoder.encode(seed.password))
                    .role(seed.role)
                    .build();

            userRepository.save(user);
            createdUsers++;
        }

        System.out.println(createdUsers);
}

    private static class UserSeed {
        private final String username;
        private final String email;
        private final String password;
        private final String role;

        private UserSeed(String username, String email, String password, String role) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }
}
