package com.mcm.api.controllers;

import com.mcm.api.dto.CreateTradeRequest;
import com.mcm.api.entities.TradeOffer;
import com.mcm.api.services.TradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/inbox")
    public List<TradeOffer> inbox(@RequestParam("userId") Long userId) {
        return tradeService.inbox(userId);
    }

    @GetMapping("/sent")
    public List<TradeOffer> sent(@RequestParam("userId") Long userId) {
        return tradeService.sent(userId);
    }

    @PostMapping
    public TradeOffer create(@RequestBody CreateTradeRequest req) {
        return tradeService.create(req);
    }

    @PutMapping("/{id}/accept")
    public TradeOffer accept(@PathVariable Long id, @RequestParam("userId") Long userId) {
        return tradeService.accept(id, userId);
    }

    @PutMapping("/{id}/reject")
    public TradeOffer reject(@PathVariable Long id, @RequestParam("userId") Long userId) {
        return tradeService.reject(id, userId);
    }
}
