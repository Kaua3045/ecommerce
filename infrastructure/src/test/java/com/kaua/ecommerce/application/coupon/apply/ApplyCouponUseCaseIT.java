package com.kaua.ecommerce.application.coupon.apply;

import com.kaua.ecommerce.application.exceptions.CouponNoMoreAvailableException;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponCommand;
import com.kaua.ecommerce.application.usecases.coupon.apply.ApplyCouponUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.slot.persistence.CouponSlotJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@IntegrationTest
public class ApplyCouponUseCaseIT {

    @Autowired
    private ApplyCouponUseCase applyCouponUseCase;

    @Autowired
    private CouponSlotJpaEntityRepository couponSlotJpaEntityRepository;

    @Autowired
    private CouponJpaEntityRepository couponJpaEntityRepository;

    @Test
    void givenAValidCouponCodeWithCouponIsLimited_whenRemoveCouponSlot_thenRemoveCouponSlot() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aCouponSlotOne = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var aCouponSlotTwo = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponSlotJpaEntityRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlotOne),
                CouponSlotJpaEntity.toEntity(aCouponSlotTwo)
        ));

        final var aCouponCode = aCoupon.getCode().getValue();

        final var aCommand = ApplyCouponCommand.with(aCouponCode, 100f);

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(2, this.couponSlotJpaEntityRepository.count());

        final var aOutput = Assertions.assertDoesNotThrow(() ->
                this.applyCouponUseCase.execute(aCommand));

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(1, this.couponSlotJpaEntityRepository.count());
    }

    @Test
    void givenAValidCouponCodeWithCouponIsLimitedAndCouponSlotNotAvailable_whenRemoveCouponSlot_thenThrowCouponNoMoreAvailableException() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aCouponCode = aCoupon.getCode().getValue();

        final var aCommand = ApplyCouponCommand.with(aCouponCode, 100f);

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());

        final var aException = Assertions.assertThrows(CouponNoMoreAvailableException.class, () ->
                this.applyCouponUseCase.execute(aCommand));

        Assertions.assertEquals("Coupon no more available", aException.getMessage());

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
    }

    @Test
    void givenAnInvalidCouponCode_whenRemoveCouponSlot_thenThrowNotFoundException() {
        final var aCouponCode = "invalid-coupon-code";

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aCouponCode);

        final var aCommand = ApplyCouponCommand.with(aCouponCode, 100f);

        Assertions.assertEquals(0, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());

        final var aException = Assertions.assertThrows(Exception.class, () ->
                this.applyCouponUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Assertions.assertEquals(0, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
    }

    @Test
    void testConcurrencyOnCallRemoveCouponSlotUseCase() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(aCoupon));

        final var aCouponSlotOne = Fixture.Coupons.generateValidCouponSlot(aCoupon);
        final var aCouponSlotTwo = Fixture.Coupons.generateValidCouponSlot(aCoupon);

        this.couponSlotJpaEntityRepository.saveAll(Set.of(
                CouponSlotJpaEntity.toEntity(aCouponSlotOne),
                CouponSlotJpaEntity.toEntity(aCouponSlotTwo)
        ));

        final var aCouponCode = aCoupon.getCode().getValue();
        final var aExecutions = 5;
        final var aCountSuccessExecutionAtomic = new AtomicInteger(0);
        final var aCountErrorExecutionAtomic = new AtomicInteger(0);
        final var aExpectedSuccessExecution = 2;
        final var aExpectedErrorExecution = 3;

        final var aCommand = ApplyCouponCommand.with(aCouponCode, 100f);

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(2, this.couponSlotJpaEntityRepository.count());

        ExecutorService executorService = Executors.newFixedThreadPool(aExecutions);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < aExecutions; i++) {
            tasks.add(() -> {
                try {
                    this.applyCouponUseCase.execute(aCommand);
                    aCountSuccessExecutionAtomic.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                    aCountErrorExecutionAtomic.incrementAndGet();
                }
                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        Assertions.assertEquals(1, this.couponJpaEntityRepository.count());
        Assertions.assertEquals(0, this.couponSlotJpaEntityRepository.count());
        Assertions.assertEquals(aExpectedSuccessExecution, aCountSuccessExecutionAtomic.get());
        Assertions.assertEquals(aExpectedErrorExecution, aCountErrorExecutionAtomic.get());
    }
}
