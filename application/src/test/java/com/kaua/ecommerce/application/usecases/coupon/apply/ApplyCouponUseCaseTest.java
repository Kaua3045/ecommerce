package com.kaua.ecommerce.application.usecases.coupon.apply;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class ApplyCouponUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @Mock
    private CouponSlotGateway couponSlotGateway;

    @InjectMocks
    private DefaultApplyCouponUseCase applyCouponUseCase;

    @Test
    void givenAValidCouponCodeWithTypeLimited_whenCallExecute_thenShouldRemoveCouponSlot() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();

        Mockito.when(couponGateway.findByCode(aCoupon.getCode().getValue()))
                .thenReturn(Optional.of(aCoupon));
        Mockito.when(couponSlotGateway.deleteFirstSlotByCouponId(aCoupon.getId().getValue()))
                .thenReturn(true);

        final var aOutput = Assertions.assertDoesNotThrow(() ->
                this.applyCouponUseCase.execute(aCoupon.getCode().getValue()));

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(aCoupon.getCode().getValue());
        Mockito.verify(couponSlotGateway, Mockito.times(1)).deleteFirstSlotByCouponId(aCoupon.getId().getValue());
    }

    @Test
    void givenAValidCouponCodeWithTypeLimitedAndCouponSlotNotAvailable_whenCallExecute_thenShouldThrowDomainException() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponCode = aCoupon.getCode().getValue();

        final var expectedErrorMessage = "Coupon no more available";

        Mockito.when(couponGateway.findByCode(aCoupon.getCode().getValue()))
                .thenReturn(Optional.of(aCoupon));
        Mockito.when(couponSlotGateway.deleteFirstSlotByCouponId(aCoupon.getId().getValue()))
                .thenReturn(false);

        final var aException = Assertions.assertThrows(DomainException.class, () ->
                this.applyCouponUseCase.execute(aCouponCode));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(aCoupon.getCode().getValue());
        Mockito.verify(couponSlotGateway, Mockito.times(1)).deleteFirstSlotByCouponId(aCoupon.getId().getValue());
    }

    @Test
    void givenAnInvalidCouponCode_whenCallExecute_thenShouldThrowNotFoundException() {
        final var aCouponCode = "invalid-coupon-code";

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aCouponCode);

        Mockito.when(couponGateway.findByCode(aCouponCode))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class, () ->
                this.applyCouponUseCase.execute(aCouponCode));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(aCouponCode);
        Mockito.verify(couponSlotGateway, Mockito.never()).deleteFirstSlotByCouponId(Mockito.any());
    }

    @Test
    void givenAValidCouponCodeButCouponIsNotActive_whenCallExecute_thenShouldThrowDomainException() {
        final var aCoupon = Fixture.Coupons.limitedCouponDeactivated();
        final var aCouponCode = aCoupon.getCode().getValue();

        final var expectedErrorMessage = "Coupon no more available";

        Mockito.when(couponGateway.findByCode(aCoupon.getCode().getValue()))
                .thenReturn(Optional.of(aCoupon));

        final var aException = Assertions.assertThrows(DomainException.class, () ->
                this.applyCouponUseCase.execute(aCouponCode));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(aCoupon.getCode().getValue());
        Mockito.verify(couponSlotGateway, Mockito.times(0)).deleteFirstSlotByCouponId(Mockito.any());
    }

    @Test
    void givenAValidCouponButTypeIsUnlimited_whenCallExecute_thenShouldBeOk() {
        final var aCoupon = Fixture.Coupons.unlimitedCouponActivated();
        final var aCouponCode = aCoupon.getCode().getValue();

        Mockito.when(couponGateway.findByCode(aCoupon.getCode().getValue()))
                .thenReturn(Optional.of(aCoupon));

        final var aOutput = Assertions.assertDoesNotThrow(() ->
                this.applyCouponUseCase.execute(aCouponCode));

        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCoupon.getCode().getValue(), aOutput.couponCode());

        Mockito.verify(couponGateway, Mockito.times(1)).findByCode(aCoupon.getCode().getValue());
        Mockito.verify(couponSlotGateway, Mockito.times(0)).deleteFirstSlotByCouponId(Mockito.any());
    }
}
