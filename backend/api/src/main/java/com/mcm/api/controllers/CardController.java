package com.mcm.api.controllers;

import com.mcm.api.dto.CreateCardRequest;
import com.mcm.api.entities.Card;
import com.mcm.api.services.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @GetMapping
  public List<Card> list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String setName,
      @RequestParam(required = false) String rarity
  ) {
    return cardService.list(q, setName, rarity);
  }

  @GetMapping("/{id}")
  public Card get(@PathVariable Long id) {
    return cardService.get(id);
  }

  @PostMapping
  public Card create(@RequestBody CreateCardRequest req) {
    return cardService.create(req);
  }

  @PutMapping("/{id}")
  public Card update(@PathVariable Long id, @RequestBody CreateCardRequest req) {
    return cardService.update(id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    cardService.delete(id);
  }
}
