package com.mcm.api.services;

import com.mcm.api.dto.CreateCardRequest;
import com.mcm.api.entities.Card;
import com.mcm.api.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cards;

    public CardService(CardRepository cards) {
        this.cards = cards;
    }

    public List<Card> list(String q, String setName, String rarity) {
        if (q != null && !q.isBlank()) return cards.findByNameContainingIgnoreCase(q);
        if (setName != null && !setName.isBlank()) return cards.findBySetNameContainingIgnoreCase(setName);
        if (rarity != null && !rarity.isBlank()) return cards.findByRarityIgnoreCase(rarity);
        return cards.findAll();
    }

    public Card get(Long id) {
        return cards.findById(id).orElseThrow(() -> new RuntimeException("Card not found"));
    }

    public Card create(CreateCardRequest req) {
        Card c = new Card();
        c.setName(req.getName());
        c.setSetName(req.getSetName());
        c.setRarity(req.getRarity());
        c.setReleaseYear(req.getReleaseYear());
        c.setImageUrl(req.getImageUrl());
        return cards.save(c);
    }

    public Card update(Long id, CreateCardRequest req) {
        Card c = cards.findById(id).orElseThrow(() -> new RuntimeException("Card not found"));
        c.setName(req.getName());
        c.setSetName(req.getSetName());
        c.setRarity(req.getRarity());
        c.setReleaseYear(req.getReleaseYear());
        c.setImageUrl(req.getImageUrl());
        return cards.save(c);
    }

    public void delete(Long id) {
        cards.deleteById(id);
    }
}
