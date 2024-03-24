package com.kaua.ecommerce.application.usecases.coupon.validate;

import com.kaua.ecommerce.domain.coupon.Coupon;

public record ValidateCouponOutput(
        String couponId,
        String couponCode,
        float couponPercentage,
        float couponMinimumPurchaseAmount,
        boolean couponValid
) {

    public static ValidateCouponOutput from(final Coupon aCoupon, final boolean aCouponValid) {
        return new ValidateCouponOutput(
                aCoupon.getId().getValue(),
                aCoupon.getCode().getValue(),
                aCoupon.getPercentage(),
                aCoupon.getMinimumPurchaseAmount(),
                aCouponValid
        );
    }
}
