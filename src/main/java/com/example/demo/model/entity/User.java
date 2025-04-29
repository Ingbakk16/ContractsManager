package com.example.demo.model.entity;

import com.example.demo.model.entity.Contract;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {


        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        private String name;

        private String lastname;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Contract> contracts = new ArrayList<>();

        // getters, setters, constructors
    }
