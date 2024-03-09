package com.kaua.ecommerce.application.usecases.coupon.slot.remove;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record RemoveCouponSlotOutput(
        String couponId,
        String couponCode
) {

    public static RemoveCouponSlotOutput from(final Coupon aCoupon) {
        return new RemoveCouponSlotOutput(
                aCoupon.getId().getValue(),
                aCoupon.getCode().getValue()
        );
    }
}
