package com.mcm.api.controllers;

import com.mcm.api.dto.CreateCardRequest;
import com.mcm.api.entities.Card;
import com.mcm.api.repository.CardRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

  private final CardRepository repo;

  public CardController(CardRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Card> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String setName,
      @RequestParam(required = false) String rarity
  ) {
    if (q != null && !q.isBlank()) return repo.findByNameContainingIgnoreCase(q);
    if (setName != null && !setName.isBlank()) return repo.findBySetNameContainingIgnoreCase(setName);
    if (rarity != null && !rarity.isBlank()) return repo.findByRarityIgnoreCase(rarity);
    return repo.findAll();
  }

  @GetMapping("/{id}")
  public Card get(@PathVariable Long id) {
    return repo.findById(id).orElseThrow(() -> new RuntimeException("Card not found"));
  }

  @PostMapping
  public Card create(@RequestBody CreateCardRequest req) {
    Card c = new Card();
    c.setName(req.getName());
    c.setSetName(req.getSetName());
    c.setRarity(req.getRarity());
    c.setReleaseYear(req.getReleaseYear());
    c.setImageUrl(req.getImageUrl());
    return repo.save(c);
  }

  @PutMapping("/{id}")
  public Card update(@PathVariable Long id, @RequestBody CreateCardRequest req) {
    Card c = repo.findById(id).orElseThrow(() -> new RuntimeException("Card not found"));
    c.setName(req.getName());
    c.setSetName(req.getSetName());
    c.setRarity(req.getRarity());
    c.setReleaseYear(req.getReleaseYear());
    c.setImageUrl(req.getImageUrl());
    return repo.save(c);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repo.deleteById(id);
  }
}
