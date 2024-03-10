package com.kaua.ecommerce.infrastructure.coupon.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record ListCouponsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("code") String code,
        @JsonProperty("percentage") float percentage,
        @JsonProperty("expiration_date") Instant expirationDate,
        @JsonProperty("is_active") boolean isActive,
        @JsonProperty("type") String type,
        @JsonProperty("created_at") Instant createdAt
) {
}
