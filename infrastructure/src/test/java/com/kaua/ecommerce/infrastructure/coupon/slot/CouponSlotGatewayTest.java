package com.kaua.ecommerce.infrastructure.coupon.slot;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@DatabaseGatewayTest
public class CouponSlotGatewayTest {

    @Autowired
    private CouponSlotMySQLGateway couponSlotGateway;

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaRepository;

    @Autowired
    private CouponJpaEntityRepository couponJpaRepository;

    @Test
    void givenAValidCouponSlots_whenCallCreateInBatch_shouldReturnTheSameCouponSlots() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        this.couponJpaRepository.save(aEntity);

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var anotherCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        final var couponSlots = Set.of(aCouponSlot, anotherCouponSlot);

        Assertions.assertEquals(0, this.couponSlotJpaRepository.count());

        final var actualCouponSlots = this.couponSlotGateway.createInBatch(couponSlots);

        Assertions.assertEquals(couponSlots, actualCouponSlots);
        Assertions.assertEquals(2, this.couponSlotJpaRepository.count());

        final var actualCouponSlot = this.couponSlotJpaRepository.findById(aCouponSlot.getSlotId()).get()
                .toDomain();

        Assertions.assertEquals(aCouponSlot.getSlotId(), actualCouponSlot.getSlotId());

        final var actualAnotherCouponSlot = this.couponSlotJpaRepository.findById(anotherCouponSlot.getSlotId()).get();

        Assertions.assertEquals(anotherCouponSlot.getSlotId(), actualAnotherCouponSlot.getId());
    }
}
