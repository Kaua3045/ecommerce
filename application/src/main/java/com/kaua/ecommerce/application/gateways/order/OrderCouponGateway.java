package com.kaua.ecommerce.application.gateways.order;

public interface OrderCouponGateway {

    OrderCouponApplyOutput applyCoupon(String couponCode, float totalAmount);

    record OrderCouponApplyOutput(
            String couponId,
            String couponCode,
            float couponPercentage
    ) {
    }
}
