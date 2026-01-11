package com.mcm.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trade_offer")
public class TradeOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties({"credits"})
    private UserAccount fromUser;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties({"credits"})
    private UserAccount toUser;

    @ManyToOne(optional = false)
    private Listing targetListing;

    @ManyToMany
    @JoinTable(
        name = "trade_offer_offered_listings",
        joinColumns = @JoinColumn(name = "trade_offer_id"),
        inverseJoinColumns = @JoinColumn(name = "listing_id")
    )
    private List<Listing> offeredListings = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status = TradeStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public TradeOffer() {}

    public TradeOffer(UserAccount fromUser, UserAccount toUser, Listing targetListing, List<Listing> offeredListings) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.targetListing = targetListing;
        this.offeredListings = offeredListings;
        this.status = TradeStatus.PENDING;
    }

    public Long getId() { return id; }
    public UserAccount getFromUser() { return fromUser; }
    public UserAccount getToUser() { return toUser; }
    public Listing getTargetListing() { return targetListing; }
    public List<Listing> getOfferedListings() { return offeredListings; }
    public TradeStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setFromUser(UserAccount fromUser) { this.fromUser = fromUser; }
    public void setToUser(UserAccount toUser) { this.toUser = toUser; }
    public void setTargetListing(Listing targetListing) { this.targetListing = targetListing; }
    public void setOfferedListings(List<Listing> offeredListings) { this.offeredListings = offeredListings; }
    public void setStatus(TradeStatus status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
