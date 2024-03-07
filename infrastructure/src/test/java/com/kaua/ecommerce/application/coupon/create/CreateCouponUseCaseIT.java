package com.kaua.ecommerce.application.coupon.create;

import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.create.CreateCouponUseCase;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@IntegrationTest
public class CreateCouponUseCaseIT {

    @Autowired
    private CreateCouponUseCase createCouponUseCase;

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaEntityRepository;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Test
    void givenAValidValuesWithUnlimitedType_whenCallCreateCouponUseCase_thenShouldCreateCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Assertions.assertEquals(0, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());

        final var aResult = this.createCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult.couponId());
        Assertions.assertEquals(aCode, aResult.code());

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
    }

    @Test
    void givenAValidValuesWithLimitedType_whenCallCreateCouponUseCase_thenShouldCreateCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "LIMITED";
        final var aMaxUses = 10;

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                aMaxUses
        );

        Assertions.assertEquals(0, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());

        final var aResult = this.createCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aResult.couponId());
        Assertions.assertEquals(aCode, aResult.code());

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(10, this.couponSlotJpaEntityRepository.count());
    }
}
