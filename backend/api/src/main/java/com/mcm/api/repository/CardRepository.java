package com.mcm.api.repository;

import com.mcm.api.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

  List<Card> findByNameContainingIgnoreCase(String name);

  List<Card> findBySetNameContainingIgnoreCase(String setName);

  List<Card> findByRarityIgnoreCase(String rarity);
}
