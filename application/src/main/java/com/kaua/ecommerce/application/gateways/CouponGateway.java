package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.coupon.Coupon;

import java.util.Optional;

public interface CouponGateway {

    Coupon create(Coupon coupon);

    boolean existsByCode(String code);

    Coupon update(Coupon coupon);

    Optional<Coupon> findById(String id);
}
