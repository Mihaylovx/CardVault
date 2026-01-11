package com.mcm.api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Fake currency (credits) to demonstrate buying.
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal credits = new BigDecimal("100.00");

    public UserAccount() {}

    public UserAccount(String username, String passwordHash, BigDecimal credits) {
        this.passwordHash = passwordHash;

        this.username = username;
        this.credits = credits;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public BigDecimal getCredits() { return credits; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
}
