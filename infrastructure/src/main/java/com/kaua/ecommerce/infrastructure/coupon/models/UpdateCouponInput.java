package com.kaua.ecommerce.infrastructure.coupon.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCouponInput(
        @JsonProperty("code") String code,
        @JsonProperty("percentage") float percentage,
        @JsonProperty("expiration_date") String expirationDate
) {
}
