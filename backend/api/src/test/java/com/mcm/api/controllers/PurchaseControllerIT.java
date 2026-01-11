package com.mcm.api.controllers;

import com.mcm.api.entities.Card;
import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PurchaseControllerIT {

    @Autowired MockMvc mvc;

    @Autowired UserAccountRepository users;
    @Autowired CardRepository cards;
    @Autowired ListingRepository listings;

    private Long listingId;

    @BeforeEach
    void setup() {
        // Arrange
        UserAccount seller = new UserAccount();
        seller.setUsername("seller1");
        seller.setPasswordHash("x");
        seller.setCredits(new BigDecimal("0.00"));
        users.save(seller);

        UserAccount buyer = new UserAccount();
        buyer.setUsername("buyer1");
        buyer.setPasswordHash("x");
        buyer.setCredits(new BigDecimal("500.00"));
        users.save(buyer);

        Card c = new Card();
        c.setName("Zoro");
        cards.save(c);

        Listing l = new Listing(c, seller, new BigDecimal("10.00"), 2, "NM");
        l.setStatus(ListingStatus.ACTIVE);
        listings.save(l);

        listingId = l.getId();
    }

    @Test
    void buy_success_returns_purchase_and_updates_listing() throws Exception {
        // Act + Assert
        mvc.perform(post("/api/purchases")
                        .param("listingId", String.valueOf(listingId))
                        .param("buyer", "buyer1")
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.totalPrice").value(10.00));
    }

    @Test
    void buy_fails_if_not_enough_credits() throws Exception {
        // Arrange
        UserAccount buyer = users.findByUsernameIgnoreCase("buyer1").orElseThrow();
        buyer.setCredits(new BigDecimal("1.00"));
        users.save(buyer);

        // Act + Assert
        mvc.perform(post("/api/purchases")
                        .param("listingId", String.valueOf(listingId))
                        .param("buyer", "buyer1")
                        .param("quantity", "1"))
                .andExpect(status().isBadRequest());
    }
}