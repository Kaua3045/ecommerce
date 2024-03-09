package com.kaua.ecommerce.infrastructure.coupon.slot;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity;
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

    @Test
    void givenAValidCouponId_whenCallDeleteAllByCouponId_shouldDeleteAllCouponSlots() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        this.couponJpaRepository.save(aEntity);

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var anotherCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponSlotJpaRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlot),
                CouponSlotJpaEntity.toEntity(anotherCouponSlot)
        ));

        Assertions.assertEquals(2, this.couponSlotJpaRepository.count());

        this.couponSlotGateway.deleteAllByCouponId(aCoupon.getId().getValue());

        Assertions.assertEquals(0, this.couponSlotJpaRepository.count());
    }

    @Test
    void givenAValidCouponId_whenCallDeleteAllByCouponIdAndCouponSlotsDoesNotExist_shouldNotDeleteAnyCouponSlots() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        this.couponJpaRepository.save(aEntity);

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var anotherCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponSlotJpaRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlot),
                CouponSlotJpaEntity.toEntity(anotherCouponSlot)
        ));

        Assertions.assertEquals(2, this.couponSlotJpaRepository.count());

        this.couponSlotGateway.deleteAllByCouponId("invalid-coupon-id");

        Assertions.assertEquals(2, this.couponSlotJpaRepository.count());
    }

    @Test
    void givenAValidCouponId_whenCallExistsByCouponId_shouldReturnTrue() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        this.couponJpaRepository.save(aEntity);

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        this.couponSlotJpaRepository.save(CouponSlotJpaEntity.toEntity(aCouponSlot));

        final var actual = this.couponSlotGateway.existsByCouponId(aCoupon.getId().getValue());

        Assertions.assertTrue(actual);
    }

    @Test
    void givenAnInvalidCouponId_whenCallExistsByCouponId_shouldReturnFalse() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        this.couponJpaRepository.save(aEntity);

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        this.couponSlotJpaRepository.save(CouponSlotJpaEntity.toEntity(aCouponSlot));

        final var actual = this.couponSlotGateway.existsByCouponId("invalid-coupon-id");

        Assertions.assertFalse(actual);
    }

    @Test
    void givenAValidCouponId_whenCallDeleteFirstSlotByCouponId_shouldDeleteFirstCouponSlot() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aEntity = CouponJpaEntity.toEntity(aCoupon);
        this.couponJpaRepository.save(aEntity);

        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var anotherCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponSlotJpaRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlot),
                CouponSlotJpaEntity.toEntity(anotherCouponSlot)
        ));

        Assertions.assertEquals(2, this.couponSlotJpaRepository.count());

        this.couponSlotGateway.deleteFirstSlotByCouponId(aCoupon.getId().getValue());

        Assertions.assertEquals(1, this.couponSlotJpaRepository.count());
    }
}
