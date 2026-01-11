package com.mcm.api.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class CreateTradeOfferRequest {

    @NotBlank
    public String fromUsername;

    @NotNull
    public Long targetListingId;

    @NotEmpty
    public List<Long> offeredListingIds;
}
