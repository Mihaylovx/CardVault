package com.mcm.api.controllers;

import com.mcm.api.entities.Purchase;
import com.mcm.api.services.PurchaseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public Purchase buy(
            @RequestParam Long listingId,
            @RequestParam String buyer,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        return purchaseService.buy(listingId, buyer, quantity);
    }
}
