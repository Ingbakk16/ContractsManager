package com.example.demo.model.entity;
import com.example.demo.model.enums.ContractState;
import jakarta.persistence.*;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    private LocalDate startDate;

    private BigDecimal amount;

    private int length; // in years: 1 or 2

    @Enumerated(EnumType.STRING)
    private ContractState state;

    private LocalDate actualizationDate;

    // You might also want:
    // - isExpired(): boolean
    // - getExpirationDate(): LocalDate

    // getters, setters, constructors
}
