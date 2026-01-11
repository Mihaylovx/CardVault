package com.mcm.api.controllers;

import com.mcm.api.entities.Card;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ListingControllerIT {

    @Autowired MockMvc mvc;
    @Autowired CardRepository cards;
    @Autowired UserAccountRepository users;

    private Long cardId;

    @BeforeEach
    void setup() {
        // Seed seller once (Arrange)
        users.findByUsernameIgnoreCase("alice").orElseGet(() -> {
            UserAccount u = new UserAccount();
            u.setUsername("alice");
            u.setPasswordHash("x");
            u.setCredits(new BigDecimal("500.00"));
            return users.save(u);
        });

        // Seed card once (Arrange)
        Card card = cards.findAll().stream()
                .filter(c -> "Luffy".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Card c = new Card();
                    c.setName("Luffy");
                    return cards.save(c);
                });

        cardId = card.getId();
    }

    @Test
    void create_listing_then_search_by_query() throws Exception {
        // Arrange
        String body = """
            {
              "cardId": %d,
              "sellerUsername": "alice",
              "price": 10.00,
              "quantity": 2,
              "condition": "NM"
            }
            """.formatted(cardId);

        // Act + Assert (create)
        mvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card.name").value("Luffy"))
                .andExpect(jsonPath("$.seller.username").value("alice"));

        // Act + Assert (search)
        mvc.perform(get("/api/listings").param("q", "luf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].card.name").value("Luffy"));
    }
}