package com.mcm.api.dto;

import java.math.BigDecimal;

public record UserResponse(Long id, String username, BigDecimal credits) {}
