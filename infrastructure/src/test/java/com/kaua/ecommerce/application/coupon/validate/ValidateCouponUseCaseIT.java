package com.kaua.ecommerce.application.coupon.validate;

import com.kaua.ecommerce.application.usecases.coupon.validate.ValidateCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ValidateCouponUseCaseIT {

    @Autowired
    private ValidateCouponUseCase validateCouponUseCase;

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaEntityRepository;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Test
    void givenAValidCouponCodeWithValidCouponLimited_whenExecute_thenShouldReturnTrue() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponSlot = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        this.couponSlotJpaEntityRepository.save(CouponSlotJpaEntity.toEntity(aCouponSlot));

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertTrue(aOutput.couponValid());
    }

    @Test
    void givenAValidCouponCodeWithValidCouponUnlimited_whenExecute_thenShouldReturnTrue() {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertTrue(aOutput.couponValid());
    }
}
