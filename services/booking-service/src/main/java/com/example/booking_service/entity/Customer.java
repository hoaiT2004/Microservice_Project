package com.example.booking_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "customer")
public class Customer extends User {

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Builder
    public Customer(Long id, String username, String password, String email, String role, String name, String address) {
        super(id, username, password, email, role);
        this.name = name;
        this.address = address;
    }
}
