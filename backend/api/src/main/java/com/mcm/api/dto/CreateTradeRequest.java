package com.mcm.api.dto;

import java.util.List;

public class CreateTradeRequest {

    private Long fromUserId;
    private Long targetListingId;
    private List<Long> offeredListingIds;

    public CreateTradeRequest() {
        // Jackson needs a no-arg constructor
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getTargetListingId() {
        return targetListingId;
    }

    public void setTargetListingId(Long targetListingId) {
        this.targetListingId = targetListingId;
    }

    public List<Long> getOfferedListingIds() {
        return offeredListingIds;
    }

    public void setOfferedListingIds(List<Long> offeredListingIds) {
        this.offeredListingIds = offeredListingIds;
    }
}