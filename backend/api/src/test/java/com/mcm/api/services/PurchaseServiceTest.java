package com.mcm.api.services;

import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.Purchase;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.PurchaseRepository;
import com.mcm.api.repository.UserAccountRepository;
import com.mcm.api.services.PurchaseService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock private ListingRepository listings;
    @Mock private UserAccountRepository users;
    @Mock private PurchaseRepository purchases;

    @InjectMocks private PurchaseService service;

    @Test
    void purchase_fails_if_quantity_less_than_one() {
        assertThrows(IllegalArgumentException.class, () -> service.buy(10L, "alice", 0));
    }

    @Test
    void purchase_fails_if_listing_not_active() {
        Listing listing = new Listing();
        listing.setId(10L);
        listing.setStatus(ListingStatus.SOLD);
        listing.setQuantity(1);
        listing.setPrice(new BigDecimal("10"));
        listing.setSeller(new UserAccount());

        when(listings.findById(10L)).thenReturn(Optional.of(listing));

        assertThrows(IllegalStateException.class, () -> service.buy(10L, "alice", 1));
        verifyNoInteractions(users, purchases);
    }

    @Test
    void purchase_fails_if_not_enough_credits() {
        UserAccount buyer = new UserAccount();
        buyer.setId(1L);
        buyer.setUsername("alice");
        buyer.setCredits(new BigDecimal("5"));

        UserAccount seller = new UserAccount();
        seller.setId(2L);
        seller.setUsername("bob");
        seller.setCredits(new BigDecimal("0"));

        Listing listing = new Listing();
        listing.setId(10L);
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setQuantity(1);
        listing.setPrice(new BigDecimal("10"));

        when(listings.findById(10L)).thenReturn(Optional.of(listing));
        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(buyer));
        
        assertThrows(ResponseStatusException.class, () -> service.buy(10L, "alice", 1));

        // should not save purchase when failing
        verify(purchases, never()).save(any(Purchase.class));
    }

    @Test
    void purchase_success_updates_credits_and_quantity_and_creates_purchase() {
        UserAccount buyer = new UserAccount();
        buyer.setId(1L);
        buyer.setUsername("alice");
        buyer.setCredits(new BigDecimal("100"));

        UserAccount seller = new UserAccount();
        seller.setId(2L);
        seller.setUsername("bob");
        seller.setCredits(new BigDecimal("0"));

        Listing listing = new Listing();
        listing.setId(10L);
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setQuantity(3);
        listing.setPrice(new BigDecimal("10"));

        when(listings.findById(10L)).thenReturn(Optional.of(listing));
        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(buyer));

        when(users.save(any(UserAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(listings.save(any(Listing.class))).thenAnswer(i -> i.getArgument(0));
        when(purchases.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        Purchase p = service.buy(10L, "alice", 2);

        // credits: 100 - (10*2) = 80
        assertEquals(new BigDecimal("80"), buyer.getCredits());
        // seller: 0 + 20 = 20
        assertEquals(new BigDecimal("20"), seller.getCredits());

        // qty: 3 - 2 = 1
        assertEquals(1, listing.getQuantity());
        assertEquals(ListingStatus.ACTIVE, listing.getStatus());

        assertNotNull(p);
        // If your Purchase getters have different names, adjust these 2 lines:
        assertEquals(2, p.getQuantity());
        assertEquals(new BigDecimal("20"), p.getTotalPrice());

        verify(users, times(2)).save(any(UserAccount.class));
        verify(listings).save(listing);
        verify(purchases).save(any(Purchase.class));
    }

    @Test
    void purchase_when_quantity_reaches_zero_marks_listing_sold() {
        UserAccount buyer = new UserAccount();
        buyer.setUsername("alice");
        buyer.setCredits(new BigDecimal("100"));

        UserAccount seller = new UserAccount();
        seller.setCredits(new BigDecimal("0"));

        Listing listing = new Listing();
        listing.setId(10L);
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setQuantity(2);
        listing.setPrice(new BigDecimal("10"));

        when(listings.findById(10L)).thenReturn(Optional.of(listing));
        when(users.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(buyer));

        when(users.save(any(UserAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(listings.save(any(Listing.class))).thenAnswer(i -> i.getArgument(0));
        when(purchases.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

        service.buy(10L, "alice", 2);

        assertEquals(0, listing.getQuantity());
        assertEquals(ListingStatus.SOLD, listing.getStatus());
    }
}