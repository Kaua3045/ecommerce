package com.kaua.ecommerce.application.usecases.coupon.create;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record CreateCouponOutput(
        String couponId,
        String code
) {

    public static CreateCouponOutput from(final Coupon aCoupon) {
        return new CreateCouponOutput(aCoupon.getId().getValue(), aCoupon.getCode());
    }
}
