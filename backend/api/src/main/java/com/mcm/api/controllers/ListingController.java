package com.mcm.api.controllers;

import com.mcm.api.dto.CreateListingRequest;
import com.mcm.api.entities.Listing;
import com.mcm.api.entities.ListingStatus;
import com.mcm.api.services.ListingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public List<Listing> all(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String seller
    ) {
        return listingService.search(q, status, seller);
    }

    @GetMapping("/{id}")
    public Listing byId(@PathVariable Long id) {
        return listingService.getById(id);
    }

    @PostMapping
    public Listing create(@Valid @RequestBody CreateListingRequest req) {
        return listingService.create(req);
    }

    @PutMapping("/{id}")
    public Listing update(@PathVariable Long id, @RequestBody Listing update) {
        return listingService.update(id, update);
    }

    @PutMapping("/{id}/status")
    public Listing updateStatus(@PathVariable Long id, @RequestParam ListingStatus status) {
        return listingService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listingService.delete(id);
    }
}
