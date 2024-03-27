package com.kaua.ecommerce.application.usecases.coupon.apply;

public record ApplyCouponCommand(String couponCode, float totalAmount) {

    public static ApplyCouponCommand with(final String aCouponCode, final float aTotalAmount) {
        return new ApplyCouponCommand(aCouponCode, aTotalAmount);
    }
}
