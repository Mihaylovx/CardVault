package com.mcm.api.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String name;

    private String setName;
    private String rarity;

    private Integer releaseYear;
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Card() {}

    public Card(String name, String setName, String rarity, Integer releaseYear, String imageUrl) {
        this.name = name;
        this.setName = setName;
        this.rarity = rarity;
        this.releaseYear = releaseYear;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSetName() { return setName; }
    public String getRarity() { return rarity; }
    public Integer getReleaseYear() { return releaseYear; }
    public String getImageUrl() { return imageUrl; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSetName(String setName) { this.setName = setName; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
