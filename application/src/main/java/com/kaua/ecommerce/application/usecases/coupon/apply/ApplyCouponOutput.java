package com.kaua.ecommerce.application.usecases.coupon.apply;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record ApplyCouponOutput(
        String couponId,
        String couponCode,
        float couponPercentage,
        float couponMinimumPurchaseAmount
) {

    public static ApplyCouponOutput from(final Coupon aCoupon) {
        return new ApplyCouponOutput(
                aCoupon.getId().getValue(),
                aCoupon.getCode().getValue(),
                aCoupon.getPercentage(),
                aCoupon.getMinimumPurchaseAmount()
        );
    }
}
