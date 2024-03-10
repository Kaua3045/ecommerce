package com.kaua.ecommerce.application.usecases.coupon.retrieve.list;

import com.kaua.ecommerce.domain.coupon.Coupon;

import java.time.Instant;

public record ListCouponsOutput(
        String id,
        String code,
        float percentage,
        Instant expirationDate,
        boolean isActive,
        String type,
        Instant createdAt
) {

    public static ListCouponsOutput from(final Coupon aCoupon) {
        return new ListCouponsOutput(
                aCoupon.getId().getValue(),
                aCoupon.getCode().getValue(),
                aCoupon.getPercentage(),
                aCoupon.getExpirationDate(),
                aCoupon.isActive(),
                aCoupon.getType().name(),
                aCoupon.getCreatedAt()
        );
    }
}
