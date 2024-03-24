package com.kaua.ecommerce.application.coupon.activate;

import com.kaua.ecommerce.application.usecases.coupon.activate.ActivateCouponUseCase;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@IntegrationTest
public class ActivateCouponUseCaseIT {

    @Autowired
    private ActivateCouponUseCase activateCouponUseCase;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Test
    void givenAValidIdAndCouponAlreadyInactive_whenCallExecute_thenShouldReturnCouponActivated() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = false;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aMinimumPurchase,
                aExpirationDate,
                aIsActive,
                aType
        );

        final var aId = aCoupon.getId().getValue();

        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aOutput = this.activateCouponUseCase.execute(aId);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        final var aCouponEntity = this.couponJpaEntityRepository.findById(aId).get();

        Assertions.assertTrue(aCouponEntity.isActive());
    }

    @Test
    void givenAValidIdAndCouponAlreadyActive_whenCallExecute_thenShouldReturnCouponActivated() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = CouponType.UNLIMITED;

        final var aCoupon = Coupon.newCoupon(
                aCode,
                aPercentage,
                aMinimumPurchase,
                aExpirationDate,
                aIsActive,
                aType
        );

        final var aId = aCoupon.getId().getValue();

        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aOutput = this.activateCouponUseCase.execute(aId);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        final var aCouponEntity = this.couponJpaEntityRepository.findById(aId).get();

        Assertions.assertTrue(aCouponEntity.isActive());
    }
}
