package com.kaua.ecommerce.application.usecases.coupon.activate;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record ActivateCouponOutput(
        String couponId,
        String code
) {

    public static ActivateCouponOutput from(final Coupon aCoupon) {
        return new ActivateCouponOutput(aCoupon.getId().getValue(), aCoupon.getCode().getValue());
    }
}
