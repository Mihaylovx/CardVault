package com.mcm.api.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "listing")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Card card;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties({"credits"})
    private UserAccount seller;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status = ListingStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Listing() {}

    public Listing(Card card, UserAccount seller, BigDecimal price, Integer quantity, String condition) {
        this.card = card;
        this.seller = seller;
        this.price = price;
        this.quantity = quantity;
        this.condition = condition;
        this.status = ListingStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public Card getCard() { return card; }
    public UserAccount getSeller() { return seller; }
    public String getSellerName() { return seller != null ? seller.getUsername() : null; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public String getCondition() { return condition; }
    public ListingStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setCard(Card card) { this.card = card; }
    public void setSeller(UserAccount seller) { this.seller = seller; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setStatus(ListingStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
