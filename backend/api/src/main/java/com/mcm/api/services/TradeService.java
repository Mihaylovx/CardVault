package com.mcm.api.services;

import com.mcm.api.dto.CreateTradeRequest;
import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.entities.TradeOffer;
import com.mcm.api.entities.TradeStatus;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.TradeOfferRepository;
import com.mcm.api.repository.UserAccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TradeService {

    private final TradeOfferRepository trades;
    private final ListingRepository listings;
    private final UserAccountRepository users;

    public TradeService(TradeOfferRepository trades, ListingRepository listings, UserAccountRepository users) {
        this.trades = trades;
        this.listings = listings;
        this.users = users;
    }

    public List<TradeOffer> inbox(Long userId) {
        return trades.findByToUserIdOrderByIdDesc(userId);
    }

    public List<TradeOffer> sent(Long userId) {
        return trades.findByFromUserIdOrderByIdDesc(userId);
    }

    @Transactional
    public TradeOffer create(CreateTradeRequest req) {
        if (req.getFromUserId() == null
                || req.getTargetListingId() == null
                || req.getOfferedListingIds() == null
                || req.getOfferedListingIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fields");
        }

        UserAccount fromUser = users.findById(req.getFromUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Listing target = listings.findById(req.getTargetListingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target listing not found"));

        if (target.getSeller().getId().equals(fromUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot trade with your own listing");
        }

        List<Long> offeredIds = req.getOfferedListingIds();
        List<Listing> offered = listings.findAllById(offeredIds);
        if (offered.size() != offeredIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more offered listings not found");
        }

        // only allow offering your own ACTIVE listings with quantity > 0
        for (Listing l : offered) {
            if (!l.getSeller().getId().equals(fromUser.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only offer your own listings");
            }
            if (l.getStatus() != ListingStatus.ACTIVE || l.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offered listing must be ACTIVE with quantity > 0");
            }
        }

        TradeOffer trade = new TradeOffer();
        trade.setFromUser(fromUser);
        trade.setToUser(target.getSeller());
        trade.setTargetListing(target);
        trade.setOfferedListings(offered);
        trade.setStatus(TradeStatus.PENDING);

        return trades.save(trade);
    }

    @Transactional
    public TradeOffer accept(Long id, Long userId) {
        TradeOffer offer = trades.findById(id).orElseThrow();
        if (offer.getStatus() != TradeStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "trade not pending");
        }
        if (!offer.getToUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed");
        }

        transferOneUnit(offer.getTargetListing(), offer.getFromUser());

        for (Listing l : offer.getOfferedListings()) {
            transferOneUnit(l, offer.getToUser());
        }

        offer.setStatus(TradeStatus.ACCEPTED);
        return trades.save(offer);
    }

    @Transactional
    public TradeOffer reject(Long id, Long userId) {
        TradeOffer offer = trades.findById(id).orElseThrow();
        if (offer.getStatus() != TradeStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "trade not pending");
        }

        boolean can = offer.getToUser().getId().equals(userId)
                || offer.getFromUser().getId().equals(userId);

        if (!can) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed");
        }

        offer.setStatus(TradeStatus.REJECTED);
        return trades.save(offer);
    }

    private void transferOneUnit(Listing source, UserAccount newOwner) {
        source.setQuantity(source.getQuantity() - 1);
        if (source.getQuantity() <= 0) source.setStatus(ListingStatus.SOLD);
        listings.save(source);

        Listing received = new Listing(source.getCard(), newOwner, source.getPrice(), 1, source.getCondition());
        listings.save(received);
    }
}
