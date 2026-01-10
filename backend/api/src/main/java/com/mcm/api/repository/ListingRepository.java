package com.mcm.api.repository;

import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findBySeller_UsernameIgnoreCaseOrderByCreatedAtDesc(String username);
    List<Listing> findByStatusOrderByCreatedAtDesc(ListingStatus status);
    List<Listing> findByCard_NameContainingIgnoreCaseOrderByCreatedAtDesc(String q);
    List<Listing> findByCard_NameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(String q, ListingStatus status);
    List<Listing> findByStatusAndSeller_UsernameIgnoreCaseOrderByCreatedAtDesc(ListingStatus status, String username);
    List<Listing> findAllByOrderByCreatedAtDesc();
}
