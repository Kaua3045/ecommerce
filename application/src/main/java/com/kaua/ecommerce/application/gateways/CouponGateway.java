package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.coupon.Coupon;

public interface CouponGateway {

    Coupon create(Coupon coupon);

    boolean existsByCode(String code);
}
