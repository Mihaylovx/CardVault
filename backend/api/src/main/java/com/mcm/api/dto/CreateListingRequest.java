package com.mcm.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateListingRequest {

    @NotNull
    public Long cardId;

    @NotBlank
    public String sellerUsername;

    @NotNull
    @DecimalMin("0.00")
    public BigDecimal price;

    @NotNull
    @Min(1)
    public Integer quantity;

    public String condition;
}
