package com.mcm.api.controllers;

import com.mcm.api.dto.CreateListingRequest;
import com.mcm.api.entities.*;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingRepository listings;
    private final CardRepository cards;
    private final UserAccountRepository users;

    public ListingController(ListingRepository listings, CardRepository cards, UserAccountRepository users) {
        this.listings = listings;
        this.cards = cards;
        this.users = users;
    }

    @GetMapping
    public List<Listing> all(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String seller
    ) {
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

    @GetMapping("/{id}")
    public Listing byId(@PathVariable Long id) {
        return listings.findById(id).orElseThrow();
    }

    @PostMapping
    public Listing create(@Valid @RequestBody CreateListingRequest req) {
        Card card = cards.findById(req.cardId).orElseThrow();
        UserAccount seller = users.findByUsernameIgnoreCase(req.sellerUsername).orElseThrow();
        Listing l = new Listing(card, seller, req.price, req.quantity, req.condition);
        return listings.save(l);
    }

    @PutMapping("/{id}")
    public Listing update(@PathVariable Long id, @RequestBody Listing update) {
        Listing existing = listings.findById(id).orElseThrow();
        if (update.getPrice() != null) existing.setPrice(update.getPrice());
        if (update.getQuantity() != null) existing.setQuantity(update.getQuantity());
        if (update.getCondition() != null) existing.setCondition(update.getCondition());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        return listings.save(existing);
    }

    @PutMapping("/{id}/status")
    public Listing updateStatus(@PathVariable Long id, @RequestParam ListingStatus status) {
        Listing existing = listings.findById(id).orElseThrow();
        existing.setStatus(status);
        return listings.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listings.deleteById(id);
    }
}
