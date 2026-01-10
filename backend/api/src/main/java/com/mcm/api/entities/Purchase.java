package com.mcm.api.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Listing listing;

    @ManyToOne(optional = false)
    private UserAccount buyer;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Purchase() {}

    public Purchase(Listing listing, UserAccount buyer, Integer quantity, BigDecimal totalPrice) {
        this.listing = listing;
        this.buyer = buyer;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Long getId() { return id; }
    public Listing getListing() { return listing; }
    public UserAccount getBuyer() { return buyer; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setListing(Listing listing) { this.listing = listing; }
    public void setBuyer(UserAccount buyer) { this.buyer = buyer; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
