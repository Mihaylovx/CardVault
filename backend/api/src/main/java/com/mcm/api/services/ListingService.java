package com.mcm.api.services;

import com.mcm.api.dto.CreateListingRequest;
import com.mcm.api.entities.Card;
import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ListingService {

    private final ListingRepository listings;
    private final CardRepository cards;
    private final UserAccountRepository users;

    public ListingService(ListingRepository listings, CardRepository cards, UserAccountRepository users) {
        this.listings = listings;
        this.cards = cards;
        this.users = users;
    }

    public List<Listing> search(String q, String status, String seller) {
        ListingStatus st = null;
        if (status != null && !status.isBlank()) {
            st = ListingStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        }

        if (seller != null && !seller.isBlank() && st != null) {
            return listings.findByStatusAndSeller_UsernameIgnoreCaseOrderByCreatedAtDesc(st, seller.trim());
        }
        if (seller != null && !seller.isBlank()) {
            return listings.findBySeller_UsernameIgnoreCaseOrderByCreatedAtDesc(seller.trim());
        }
        if (q != null && !q.isBlank() && st != null) {
            return listings.findByCard_NameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(q.trim(), st);
        }
        if (q != null && !q.isBlank()) {
            return listings.findByCard_NameContainingIgnoreCaseOrderByCreatedAtDesc(q.trim());
        }
        if (st != null) {
            return listings.findByStatusOrderByCreatedAtDesc(st);
        }
        return listings.findAllByOrderByCreatedAtDesc();
    }

    public Listing getById(Long id) {
        return listings.findById(id).orElseThrow();
    }

    public Listing create(@Valid CreateListingRequest req) {
        Card card = cards.findById(req.cardId).orElseThrow();
        UserAccount seller = users.findByUsernameIgnoreCase(req.sellerUsername).orElseThrow();
        Listing l = new Listing(card, seller, req.price, req.quantity, req.condition);
        return listings.save(l);
    }

    public Listing update(Long id, Listing update) {
        Listing existing = listings.findById(id).orElseThrow();
        if (update.getPrice() != null) existing.setPrice(update.getPrice());
        if (update.getQuantity() != null) existing.setQuantity(update.getQuantity());
        if (update.getCondition() != null) existing.setCondition(update.getCondition());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        return listings.save(existing);
    }

    public Listing updateStatus(Long id, ListingStatus status) {
        Listing existing = listings.findById(id).orElseThrow();
        existing.setStatus(status);
        return listings.save(existing);
    }

    public void delete(Long id) {
        listings.deleteById(id);
    }
}
