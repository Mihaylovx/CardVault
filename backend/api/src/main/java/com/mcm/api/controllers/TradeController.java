package com.mcm.api.controllers;

import com.mcm.api.dto.CreateTradeOfferRequest;
import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.TradeOffer;
import com.mcm.api.entities.TradeStatus;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.TradeOfferRepository;
import com.mcm.api.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeOfferRepository trades;
    private final ListingRepository listings;
    private final UserAccountRepository users;

    public TradeController(TradeOfferRepository trades, ListingRepository listings, UserAccountRepository users) {
        this.trades = trades;
        this.listings = listings;
        this.users = users;
    }

    @GetMapping
    public List<TradeOffer> list(@RequestParam String user,
                                @RequestParam(defaultValue = "inbox") String role,
                                @RequestParam(required = false) TradeStatus status) {

        if ("sent".equalsIgnoreCase(role)) {
            return status == null
                    ? trades.findByFromUser_UsernameIgnoreCaseOrderByCreatedAtDesc(user)
                    : trades.findByFromUser_UsernameIgnoreCaseAndStatusOrderByCreatedAtDesc(user, status);
        }
        return status == null
                ? trades.findByToUser_UsernameIgnoreCaseOrderByCreatedAtDesc(user)
                : trades.findByToUser_UsernameIgnoreCaseAndStatusOrderByCreatedAtDesc(user, status);
    }

    @PostMapping
    @Transactional
    public TradeOffer create(@Valid @RequestBody CreateTradeOfferRequest req) {
        UserAccount from = users.findByUsernameIgnoreCase(req.fromUsername).orElseThrow();
        Listing target = listings.findById(req.targetListingId).orElseThrow();
        UserAccount to = target.getSeller();

        if (target.getStatus() != ListingStatus.ACTIVE || target.getQuantity() < 1) {
            throw new IllegalStateException("target listing not available");
        }
        if (to.getUsername().equalsIgnoreCase(from.getUsername())) {
            throw new IllegalStateException("cannot trade with yourself");
        }

        List<Listing> offered = new ArrayList<>();
        for (Long id : req.offeredListingIds) {
            Listing l = listings.findById(id).orElseThrow();
            if (!l.getSeller().getUsername().equalsIgnoreCase(from.getUsername())) {
                throw new IllegalStateException("offered listing must belong to the requesting user");
            }
            if (l.getStatus() != ListingStatus.ACTIVE || l.getQuantity() < 1) {
                throw new IllegalStateException("offered listing not available");
            }
            offered.add(l);
        }

        TradeOffer offer = new TradeOffer(from, to, target, offered);
        return trades.save(offer);
    }

    @PutMapping("/{id}/accept")
    @Transactional
    public TradeOffer accept(@PathVariable Long id, @RequestParam String user) {
        TradeOffer offer = trades.findById(id).orElseThrow();
        if (offer.getStatus() != TradeStatus.PENDING) throw new IllegalStateException("trade not pending");

        if (!offer.getToUser().getUsername().equalsIgnoreCase(user)) {
            throw new IllegalStateException("only the receiver can accept");
        }

        Listing target = offer.getTargetListing();
        if (target.getStatus() != ListingStatus.ACTIVE || target.getQuantity() < 1) {
            throw new IllegalStateException("target listing not available");
        }

        // Transfer 1 unit of each offered listing to the receiver (toUser)
        for (Listing offered : offer.getOfferedListings()) {
            if (offered.getStatus() != ListingStatus.ACTIVE || offered.getQuantity() < 1) {
                throw new IllegalStateException("an offered listing is no longer available");
            }
            transferOneUnit(offered, offer.getToUser());
        }

        // Transfer 1 unit of the target listing to the requester (fromUser)
        transferOneUnit(target, offer.getFromUser());

        offer.setStatus(TradeStatus.ACCEPTED);
        return trades.save(offer);
    }

    @PutMapping("/{id}/reject")
    public TradeOffer reject(@PathVariable Long id, @RequestParam String user) {
        TradeOffer offer = trades.findById(id).orElseThrow();
        if (offer.getStatus() != TradeStatus.PENDING) throw new IllegalStateException("trade not pending");

        boolean can = offer.getToUser().getUsername().equalsIgnoreCase(user)
                || offer.getFromUser().getUsername().equalsIgnoreCase(user);
        if (!can) throw new IllegalStateException("not allowed");

        offer.setStatus(TradeStatus.REJECTED);
        return trades.save(offer);
    }

    private void transferOneUnit(Listing source, UserAccount newOwner) {
        // Reduce the source listing by 1.
        source.setQuantity(source.getQuantity() - 1);
        if (source.getQuantity() <= 0) source.setStatus(ListingStatus.SOLD);
        listings.save(source);

        // Create a new listing for the new owner (simple "inventory as listing" approach).
        Listing received = new Listing(source.getCard(), newOwner, source.getPrice(), 1, source.getCondition());
        listings.save(received);
    }
}
