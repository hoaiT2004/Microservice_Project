package com.example.authservice;

import com.example.authservice.entity.Customer;
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
                new UserSeed("customer1", "customer1@example.com", "customer123", "Nguyen Xuan Hoa", "Ha Noi"),
                new UserSeed("customer2", "customer2@example.com", "customer123", "Nguyen Van Hoa", "Ha Noi"),
                new UserSeed("customer3", "customer3@example.com", "customer123", "Tran Xuan Van", "Ha Noi"),
                new UserSeed("customer4", "customer4@example.com", "customer123", "Le Xuan Nam", "Ha Noi"),
                new UserSeed("customer5", "customer5@example.com", "customer123", "Pham Xuan Anh", "Ha Noi")
        );

        int createdUsers = 0;
        for (UserSeed seed : demoUsers) {
            Customer user = Customer.builder()
                    .username(seed.username)
                    .email(seed.email)
                    .password(passwordEncoder.encode(seed.password))
                    .name(seed.name)
                    .address(seed.address)
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
        private final String name;
        private final String address;


        public UserSeed(String username, String email, String password, String name, String address) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.name = name;
            this.address = address;
        }
    }
}
