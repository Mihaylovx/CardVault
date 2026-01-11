package com.mcm.api.services;

import com.mcm.api.dto.CreateListingRequest;
import com.mcm.api.entities.*;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.UserAccountRepository;
import com.mcm.api.services.ListingService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock private ListingRepository listings;
    @Mock private CardRepository cards;
    @Mock private UserAccountRepository users;

    @InjectMocks private ListingService service;

    @Test
    void search_by_seller_and_status_calls_repo() {
        // Arrange
        when(listings.findByStatusAndSeller_UsernameIgnoreCaseOrderByCreatedAtDesc(ListingStatus.ACTIVE, "alice"))
                .thenReturn(List.of());

        // Act
        service.search(null, "active", "alice");

        // Assert
        verify(listings).findByStatusAndSeller_UsernameIgnoreCaseOrderByCreatedAtDesc(ListingStatus.ACTIVE, "alice");
        verifyNoMoreInteractions(listings);
    }

    @Test
    void search_by_query_and_status_calls_repo() {
        // Arrange
        when(listings.findByCard_NameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc("luffy", ListingStatus.ACTIVE))
                .thenReturn(List.of());

        // Act
        service.search("luffy", "ACTIVE", null);

        // Assert
        verify(listings).findByCard_NameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc("luffy", ListingStatus.ACTIVE);
        verifyNoMoreInteractions(listings);
    }

    @Test
    void search_empty_calls_find_all() {
        // Arrange
        when(listings.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        // Act
        service.search(null, null, null);

        // Assert
        verify(listings).findAllByOrderByCreatedAtDesc();
        verifyNoMoreInteractions(listings);
    }

    @Test
    void etById_found_returns_listing() {
        // Arrange
        Listing l = new Listing();
        l.setId(1L);
        when(listings.findById(1L)).thenReturn(Optional.of(l));

        // Act
        Listing got = service.getById(1L);

        // Assert
        assertEquals(1L, got.getId());
    }

    // ---------------- create ----------------

    @Test
    void create_valid_saves_new_listing() {
        // Arrange
        CreateListingRequest req = new CreateListingRequest();
        req.cardId = 10L;
        req.sellerUsername = "alice";
        req.price = new BigDecimal("12.50");
        req.quantity = 2;
        req.condition = "NM";

        Card card = new Card();
        card.setId(10L);

        UserAccount seller = new UserAccount();
        seller.setId(1L);
        seller.setUsername("alice");

        when(cards.findById(10L)).thenReturn(Optional.of(card));
        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(seller));
        when(listings.save(any(Listing.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Listing saved = service.create(req);

        // Assert
        assertNotNull(saved);
        assertEquals(card, saved.getCard());
        assertEquals(seller, saved.getSeller());
        assertEquals(new BigDecimal("12.50"), saved.getPrice());
        assertEquals(2, saved.getQuantity());
        assertEquals("NM", saved.getCondition());

        verify(listings).save(any(Listing.class));
    }

    @Test
    void updateStatus_changes_status_and_saves() {
        // Arrange
        Listing existing = new Listing();
        existing.setId(1L);
        existing.setStatus(ListingStatus.ACTIVE);

        when(listings.findById(1L)).thenReturn(Optional.of(existing));
        when(listings.save(any(Listing.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Listing updated = service.updateStatus(1L, ListingStatus.SOLD);

        // Assert
        assertEquals(ListingStatus.SOLD, updated.getStatus());
        verify(listings).save(existing);
    }

    @Test
    void delete_valid_calls_deleteById() {
        // Arrange
        doNothing().when(listings).deleteById(1L);

        // Act
        service.delete(1L);

        // Assert
        verify(listings).deleteById(1L);
    }
}