package com.mcm.api.services;

import com.mcm.api.dto.CreateTradeRequest;
import com.mcm.api.entities.*;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.TradeOfferRepository;
import com.mcm.api.repository.UserAccountRepository;
import com.mcm.api.services.TradeService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock private TradeOfferRepository trades;
    @Mock private ListingRepository listings;
    @Mock private UserAccountRepository users;

    @InjectMocks private TradeService service;

    @Test
    void inbox_finds_messages_from_repository() {
        // Arrange
        when(trades.findByToUserIdOrderByIdDesc(1L)).thenReturn(List.of());

        // Act
        service.inbox(1L);

        // Assert
        verify(trades).findByToUserIdOrderByIdDesc(1L);
    }

    @Test
    void sent_messages_retrieved_from_repository() {
        // Arrange
        when(trades.findByFromUserIdOrderByIdDesc(1L)).thenReturn(List.of());

        // Act
        service.sent(1L);

        // Assert
        verify(trades).findByFromUserIdOrderByIdDesc(1L);
    }

    @Test
    void create_fails_when_missing_fields() {
        // Arrange
        CreateTradeRequest req = new CreateTradeRequest();
        req.setFromUserId(null);
        req.setTargetListingId(10L);
        req.setOfferedListingIds(List.of(20L));

        // Act + Assert
        assertThrows(ResponseStatusException.class, () -> service.create(req));

        verifyNoInteractions(trades, listings, users);
    }

    @Test
    void create_success_creates_pending_trade() {
        // Arrange
        UserAccount alice = new UserAccount();
        alice.setId(1L);

        UserAccount bob = new UserAccount();
        bob.setId(2L);

        Listing target = new Listing();
        target.setId(10L);
        target.setSeller(bob);

        Listing offered = new Listing();
        offered.setId(20L);
        offered.setSeller(alice);
        offered.setStatus(ListingStatus.ACTIVE);
        offered.setQuantity(1);

        CreateTradeRequest req = new CreateTradeRequest();
        req.setFromUserId(1L);
        req.setTargetListingId(10L);
        req.setOfferedListingIds(List.of(20L));

        when(users.findById(1L)).thenReturn(Optional.of(alice));
        when(listings.findById(10L)).thenReturn(Optional.of(target));
        when(listings.findAllById(List.of(20L))).thenReturn(List.of(offered));
        when(trades.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        TradeOffer trade = service.create(req);

        // Assert
        assertEquals(alice, trade.getFromUser());
        assertEquals(bob, trade.getToUser());
        assertEquals(TradeStatus.PENDING, trade.getStatus());
        assertEquals(1, trade.getOfferedListings().size());
    }

    @Test
    void accept_only_to_user_can_accept_and_transfers_listings() {
        // Arrange
        UserAccount from = new UserAccount(); from.setId(1L);
        UserAccount to = new UserAccount(); to.setId(2L);

        Card card = new Card();
        card.setId(99L);

        Listing target = new Listing();
        target.setSeller(to);
        target.setCard(card);
        target.setPrice(new BigDecimal("5"));
        target.setQuantity(1);
        target.setStatus(ListingStatus.ACTIVE);

        Listing offered = new Listing();
        offered.setSeller(from);
        offered.setCard(card);
        offered.setPrice(new BigDecimal("7"));
        offered.setQuantity(1);
        offered.setStatus(ListingStatus.ACTIVE);

        TradeOffer offer = new TradeOffer();
        offer.setId(5L);
        offer.setFromUser(from);
        offer.setToUser(to);
        offer.setTargetListing(target);
        offer.setOfferedListings(List.of(offered));
        offer.setStatus(TradeStatus.PENDING);

        when(trades.findById(5L)).thenReturn(Optional.of(offer));
        when(trades.save(any())).thenAnswer(i -> i.getArgument(0));
        when(listings.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        TradeOffer result = service.accept(5L, 2L);

        // Assert
        assertEquals(TradeStatus.ACCEPTED, result.getStatus());
        assertEquals(ListingStatus.SOLD, target.getStatus());
        assertEquals(ListingStatus.SOLD, offered.getStatus());
        verify(listings, times(4)).save(any());
    }

    @Test
    void reject_allowed_for_from_or_to_user() {
        // Arrange
        UserAccount from = new UserAccount(); from.setId(1L);
        UserAccount to = new UserAccount(); to.setId(2L);

        TradeOffer offer = new TradeOffer();
        offer.setFromUser(from);
        offer.setToUser(to);
        offer.setStatus(TradeStatus.PENDING);

        when(trades.findById(5L)).thenReturn(Optional.of(offer));
        when(trades.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        TradeOffer result = service.reject(5L, 1L);

        // Assert
        assertEquals(TradeStatus.REJECTED, result.getStatus());
    }
}
