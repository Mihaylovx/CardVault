package com.mcm.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcm.api.dto.LoginRequest;
import com.mcm.api.dto.RegisterRequest;
import com.mcm.api.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserAccountRepository users;
    @Autowired com.mcm.api.repository.TradeOfferRepository trades;
    @Autowired com.mcm.api.repository.PurchaseRepository purchases;
    @Autowired com.mcm.api.repository.ListingRepository listings;
    @Autowired com.mcm.api.repository.CardRepository cards;

    @BeforeEach
    void cleanup() {
        trades.deleteAll();
        purchases.deleteAll();
        listings.deleteAll();
        cards.deleteAll();
        users.deleteAll();
    }

    @Test
    void register_then_login_success() throws Exception {
        // Arrange
        RegisterRequest reg = new RegisterRequest("alice", "secret");
        LoginRequest login = new LoginRequest("alice", "secret");

        // Act + Assert (register)
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.credits").exists());

        // Act + Assert (login)
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }
}
