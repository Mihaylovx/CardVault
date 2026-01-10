package com.mcm.api.controllers;

import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.Purchase;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.PurchaseRepository;
import com.mcm.api.repository.UserAccountRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final ListingRepository listings;
    private final UserAccountRepository users;
    private final PurchaseRepository purchases;

    public PurchaseController(ListingRepository listings, UserAccountRepository users, PurchaseRepository purchases) {
        this.listings = listings;
        this.users = users;
        this.purchases = purchases;
    }

    @PostMapping
    @Transactional
    public Purchase buy(@RequestParam Long listingId,
                        @RequestParam String buyer,
                        @RequestParam(defaultValue = "1") Integer quantity) {

        if (quantity == null || quantity < 1) throw new IllegalArgumentException("quantity must be >= 1");

        Listing listing = listings.findById(listingId).orElseThrow();
        if (listing.getStatus() != ListingStatus.ACTIVE) throw new IllegalStateException("listing is not active");
        if (listing.getQuantity() < quantity) throw new IllegalStateException("not enough stock");

        UserAccount buyerAcc = users.findByUsernameIgnoreCase(buyer).orElseThrow();
        UserAccount sellerAcc = listing.getSeller();

        BigDecimal total = listing.getPrice().multiply(new BigDecimal(quantity));
        if (buyerAcc.getCredits().compareTo(total) < 0) throw new IllegalStateException("not enough credits");

        // Transfer fake credits.
        buyerAcc.setCredits(buyerAcc.getCredits().subtract(total));
        sellerAcc.setCredits(sellerAcc.getCredits().add(total));

        // Reduce quantity, and auto-close the listing if it hits zero.
        listing.setQuantity(listing.getQuantity() - quantity);
        if (listing.getQuantity() <= 0) listing.setStatus(ListingStatus.SOLD);

        users.save(buyerAcc);
        users.save(sellerAcc);
        listings.save(listing);

        Purchase p = new Purchase(listing, buyerAcc, quantity, total);
        return purchases.save(p);
    }
}
