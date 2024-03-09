package com.kaua.ecommerce.application.usecases.coupon.validate;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class ValidateCouponUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @Mock
    private CouponSlotGateway couponSlotGateway;

    @InjectMocks
    private DefaultValidateCouponUseCase validateCouponUseCase;

    @Test
    void givenAValidCouponCodeWithValidCouponLimited_whenExecute_thenShouldReturnTrue() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        Mockito.when(couponGateway.findByCode(Mockito.any()))
                .thenReturn(Optional.of(aCoupon));
        Mockito.when(couponSlotGateway.existsByCouponId(Mockito.any()))
                .thenReturn(true);

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertTrue(aOutput.couponValid());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(1)).existsByCouponId(Mockito.any());
    }

    @Test
    void givenAValidCouponCodeWithValidCouponUnlimited_whenExecute_thenShouldReturnTrue() {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();

        Mockito.when(couponGateway.findByCode(Mockito.any()))
                .thenReturn(Optional.of(aCoupon));

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertTrue(aOutput.couponValid());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0)).existsByCouponId(Mockito.any());
    }

    @Test
    void givenAValidCouponCodeWithInvalidCouponLimited_whenExecute_thenShouldReturnFalse() {
        final var aCoupon = Fixture.Coupons.limitedCouponDeactivated();

        Mockito.when(couponGateway.findByCode(Mockito.any()))
                .thenReturn(Optional.of(aCoupon));
        Mockito.when(couponSlotGateway.existsByCouponId(Mockito.any()))
                .thenReturn(true);

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertFalse(aOutput.couponValid());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(1)).existsByCouponId(Mockito.any());
    }

    @Test
    void givenAValidCouponCodeWithInvalidCouponUnlimited_whenExecute_thenShouldReturnFalse() {
        final var aCoupon = Fixture.Coupons.unlimitedCouponDeactivated();

        Mockito.when(couponGateway.findByCode(Mockito.any()))
                .thenReturn(Optional.of(aCoupon));

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertFalse(aOutput.couponValid());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0)).existsByCouponId(Mockito.any());
    }

    @Test
    void givenAValidCouponCodeButNotContainsMoreSlots_whenExecute_thenShouldReturnFalse() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        Mockito.when(couponGateway.findByCode(Mockito.any()))
                .thenReturn(Optional.of(aCoupon));
        Mockito.when(couponSlotGateway.existsByCouponId(Mockito.any()))
                .thenReturn(false);

        final var aOutput = this.validateCouponUseCase.execute(aCoupon.getCode().getValue());

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());
        Assertions.assertEquals(aCoupon.getPercentage(), aOutput.couponPercentage());
        Assertions.assertFalse(aOutput.couponValid());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(1)).existsByCouponId(Mockito.any());
    }

    @Test
    void givenAnInvalidCouponCode_whenExecute_thenShouldThrowNotFoundException() {
        final var aCouponCode = "invalid-coupon-code";

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aCouponCode);

        Mockito.when(couponGateway.findByCode(Mockito.any()))
                .thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.validateCouponUseCase.execute(aCouponCode)
        );

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0)).existsByCouponId(Mockito.any());
    }
}
