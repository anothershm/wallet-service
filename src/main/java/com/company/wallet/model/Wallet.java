package com.company.wallet.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a wallet entity with a unique identifier, balance, creation timestamp, and version for optimistic locking.
 * Provides methods to top up the wallet balance.
 * For the porpuse of this exercise, this object is both jpa entity and domain object, but in a real application I would separate them.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id; // uuid is used because it's better for distributed systems + prevents guessable ids

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Version
    private Long version;

    public void topUp(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

}
