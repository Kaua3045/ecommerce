package com.kaua.ecommerce.application.coupon.delete;

import com.kaua.ecommerce.application.usecases.coupon.delete.DeleteCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@IntegrationTest
public class DeleteCouponUseCaseIT {

    @Autowired
    private DeleteCouponUseCase deleteCouponUseCase;

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaEntityRepository;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Test
    void givenAValidIdAndCouponIsUnlimited_whenCallExecute_thenShouldDeleteCoupon() {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();
        final var aId = aCoupon.getId().getValue();

        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());

        Assertions.assertDoesNotThrow(() -> this.deleteCouponUseCase.execute(aId));

        Assertions.assertEquals(0, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
    }

    @Test
    void givenAValidIdAndCouponIsLimited_whenCallExecute_thenShouldDeleteCoupon() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aId = aCoupon.getId().getValue();

        final var aCouponSlotOne = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var aCouponSlotTwo = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        this.couponSlotJpaEntityRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlotOne),
                CouponSlotJpaEntity.toEntity(aCouponSlotTwo)
        ));

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(2, this.couponSlotJpaEntityRepository.count());

        Assertions.assertDoesNotThrow(() -> this.deleteCouponUseCase.execute(aId));

        Assertions.assertEquals(0, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
    }
}
