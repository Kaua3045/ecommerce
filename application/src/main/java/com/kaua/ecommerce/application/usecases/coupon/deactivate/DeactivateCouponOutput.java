package com.kaua.ecommerce.application.usecases.coupon.deactivate;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record DeactivateCouponOutput(
        String couponId,
        String code
) {

    public static DeactivateCouponOutput from(final Coupon aCoupon) {
        return new DeactivateCouponOutput(aCoupon.getId().getValue(), aCoupon.getCode().getValue());
    }
}
