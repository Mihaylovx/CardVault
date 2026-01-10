package com.mcm.api.repository;

import com.mcm.api.entities.TradeOffer;
import com.mcm.api.entities.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeOfferRepository extends JpaRepository<TradeOffer, Long> {
    List<TradeOffer> findByToUser_UsernameIgnoreCaseOrderByCreatedAtDesc(String username);
    List<TradeOffer> findByFromUser_UsernameIgnoreCaseOrderByCreatedAtDesc(String username);
    List<TradeOffer> findByToUser_UsernameIgnoreCaseAndStatusOrderByCreatedAtDesc(String username, TradeStatus status);
    List<TradeOffer> findByFromUser_UsernameIgnoreCaseAndStatusOrderByCreatedAtDesc(String username, TradeStatus status);
}
