package com.kaua.ecommerce.infrastructure.coupon.slot.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponSlotJpaEntityRepository extends JpaRepository<CouponSlotJpaEntity, String> {

    boolean existsByCouponId(String couponId);

    void deleteAllByCouponId(String couponId);
}
