package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.coupon.CouponSlot;

import java.util.Set;

public interface CouponSlotGateway {

    Set<CouponSlot> createInBatch(Set<CouponSlot> couponSlots);

    boolean existsByCouponId(String couponId);

    void deleteAllByCouponId(String couponId);

    boolean deleteFirstSlotByCouponId(String couponId);
}
