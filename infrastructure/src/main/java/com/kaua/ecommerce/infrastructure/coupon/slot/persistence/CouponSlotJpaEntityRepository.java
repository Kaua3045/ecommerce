package com.kaua.ecommerce.infrastructure.coupon.slot.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponSlotJpaEntityRepository extends JpaRepository<CouponSlotJpaEntity, String> {

    boolean existsByCouponId(String couponId);

    void deleteAllByCouponId(String couponId);

    @Modifying
    @Query(value = "DELETE FROM coupons_slots c WHERE c.coupon_id = :couponId LIMIT 1", nativeQuery = true)
    int deleteFirstSlotByCouponId(@Param("couponId") String couponId);
}
