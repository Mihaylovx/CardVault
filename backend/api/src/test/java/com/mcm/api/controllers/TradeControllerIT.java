package com.mcm.api.controllers;

import com.mcm.api.entities.*;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.TradeOfferRepository;
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
class TradeControllerIT {

    @Autowired MockMvc mvc;

    @Autowired UserAccountRepository users;
    @Autowired CardRepository cards;
    @Autowired ListingRepository listings;
    @Autowired TradeOfferRepository trades;

    private Long aliceId;
    private Long bobId;

    private Long bobTargetListingId;
    private Long aliceOfferedListingId;

    @BeforeEach
    void setup() {
        // Arrange: users
        UserAccount alice = new UserAccount();
        alice.setUsername("alice_trade");
        alice.setPasswordHash("x");
        alice.setCredits(new BigDecimal("500.00"));
        users.save(alice);
        aliceId = alice.getId();

        UserAccount bob = new UserAccount();
        bob.setUsername("bob_trade");
        bob.setPasswordHash("x");
        bob.setCredits(new BigDecimal("500.00"));
        users.save(bob);
        bobId = bob.getId();

        // Arrange: cards
        Card c1 = new Card();
        c1.setName("Card A");
        cards.save(c1);

        Card c2 = new Card();
        c2.setName("Card B");
        cards.save(c2);

        // Arrange: listings
        Listing bobTarget = new Listing(c1, bob, new BigDecimal("10.00"), 1, "NM");
        bobTarget.setStatus(ListingStatus.ACTIVE);
        listings.save(bobTarget);
        bobTargetListingId = bobTarget.getId();

        Listing aliceOffer = new Listing(c2, alice, new BigDecimal("10.00"), 1, "NM");
        aliceOffer.setStatus(ListingStatus.ACTIVE);
        listings.save(aliceOffer);
        aliceOfferedListingId = aliceOffer.getId();
    }

    @Test
    void create_trade_then_inbox_and_sent_show_it() throws Exception {
        // Arrange
        String body = """
            {
              "fromUserId": %d,
              "targetListingId": %d,
              "offeredListingIds": [%d]
            }
            """.formatted(aliceId, bobTargetListingId, aliceOfferedListingId);

        // Act + Assert (create)
        mvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.fromUser.username").value("alice_trade"))
                .andExpect(jsonPath("$.toUser.username").value("bob_trade"));

        // Act + Assert (inbox for bob)
        mvc.perform(get("/api/trades/inbox").param("userId", String.valueOf(bobId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].fromUser.username").value("alice_trade"));

        // Act + Assert (sent for alice)
        mvc.perform(get("/api/trades/sent").param("userId", String.valueOf(aliceId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].toUser.username").value("bob_trade"));
    }

    @Test
    void accept_trade_changes_status_to_accepted() throws Exception {
        // Arrange: create trade first (directly via controller)
        String body = """
            {
              "fromUserId": %d,
              "targetListingId": %d,
              "offeredListingIds": [%d]
            }
            """.formatted(aliceId, bobTargetListingId, aliceOfferedListingId);

        String response = mvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Get trade id (simplest: fetch from repo, since we’re in IT)
        Long tradeId = trades.findAll().get(0).getId();

        // Act + Assert (accept by bob)
        mvc.perform(put("/api/trades/{id}/accept", tradeId)
                        .param("userId", String.valueOf(bobId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void reject_trade_changes_status_to_rejected() throws Exception {
        // Arrange
        String body = """
            {
              "fromUserId": %d,
              "targetListingId": %d,
              "offeredListingIds": [%d]
            }
            """.formatted(aliceId, bobTargetListingId, aliceOfferedListingId);

        mvc.perform(post("/api/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        Long tradeId = trades.findAll().get(0).getId();

        // Act + Assert (reject by alice or bob is allowed)
        mvc.perform(put("/api/trades/{id}/reject", tradeId)
                        .param("userId", String.valueOf(aliceId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }
}
