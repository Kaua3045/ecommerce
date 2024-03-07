package com.kaua.ecommerce.infrastructure.coupon.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCouponInput(
        @JsonProperty("code") String code,
        @JsonProperty("percentage") float percentage,
        @JsonProperty("expiration_date") String expirationDate,
        @JsonProperty("is_active") boolean isActive,
        @JsonProperty("type") String type,
        @JsonProperty("max_uses") int maxUses
) {
}
