package com.kaua.ecommerce.application.usecases.coupon.update;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record UpdateCouponOutput(
        String couponId,
        String code
) {

    public static UpdateCouponOutput from(final Coupon aCoupon) {
        return new UpdateCouponOutput(aCoupon.getId().getValue(), aCoupon.getCode().getValue());
    }
}
