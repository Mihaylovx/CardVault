package com.mcm.api.services;

import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.Purchase;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.PurchaseRepository;
import com.mcm.api.repository.UserAccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PurchaseService {

    private final ListingRepository listings;
    private final UserAccountRepository users;
    private final PurchaseRepository purchases;

    public PurchaseService(ListingRepository listings, UserAccountRepository users, PurchaseRepository purchases) {
        this.listings = listings;
        this.users = users;
        this.purchases = purchases;
    }

    @Transactional
    public Purchase buy(Long listingId, String buyerUsername, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("quantity must be >= 1");

        Listing listing = listings.findById(listingId).orElseThrow();
        if (listing.getStatus() != ListingStatus.ACTIVE) throw new IllegalStateException("listing not active");
        if (listing.getQuantity() < quantity) throw new IllegalStateException("not enough quantity");

        UserAccount buyerAcc = users.findByUsernameIgnoreCase(buyerUsername).orElseThrow();
        UserAccount sellerAcc = listing.getSeller();

        BigDecimal total = listing.getPrice().multiply(new BigDecimal(quantity));
        if (buyerAcc.getCredits().compareTo(total) < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough credits");

        buyerAcc.setCredits(buyerAcc.getCredits().subtract(total));
        sellerAcc.setCredits(sellerAcc.getCredits().add(total));

        listing.setQuantity(listing.getQuantity() - quantity);
        if (listing.getQuantity() <= 0) listing.setStatus(ListingStatus.SOLD);

        users.save(buyerAcc);
        users.save(sellerAcc);
        listings.save(listing);

        Purchase p = new Purchase(listing, buyerAcc, quantity, total);
        return purchases.save(p);
    }
}
