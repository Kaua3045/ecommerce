package com.kaua.ecommerce.infrastructure.coupon.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApplyCouponInput(
        @JsonProperty("total_amount") float totalAmount
) {
}
