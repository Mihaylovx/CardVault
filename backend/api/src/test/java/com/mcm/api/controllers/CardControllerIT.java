package com.mcm.api.controllers;

import com.mcm.api.entities.Card;
import com.mcm.api.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CardControllerIT {

    @Autowired MockMvc mvc;
    @Autowired CardRepository cards;

    @BeforeEach
    void setup() {
        // Arrange (optional seed)
        if (cards.count() == 0) {
            Card c = new Card();
            c.setName("Luffy");
            cards.save(c);
        }
    }

    @Test
    void list_cards_returns_ok_and_contains_seed() throws Exception {
        // Act + Assert
        mvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void create_card_then_list_contains_it() throws Exception {
        // Arrange
        String body = """
            {
              "name": "Zoro"
            }
            """;

        // Act + Assert (create)
        mvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Zoro"));

        // Act + Assert (list)
        mvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Zoro')]").exists());
    }
}