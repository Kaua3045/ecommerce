package com.kaua.ecommerce.application.usecases.coupon.activate;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class ActivateCouponUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @InjectMocks
    private DefaultActivateCouponUseCase activateCouponUseCase;

    @Test
    void givenAValidIdAndCouponAlreadyInactive_whenCallExecute_thenShouldReturnCouponActivated() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
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

        Mockito.when(couponGateway.findById(aId)).thenReturn(Optional.of(aCoupon));
        Mockito.when(couponGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.activateCouponUseCase.execute(aId);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(couponGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getId(), aCoupon.getId())
                        && Objects.equals(aCmd.getCode(), aCoupon.getCode())
                        && aCmd.isActive()));
    }

    @Test
    void givenAValidIdAndCouponAlreadyActive_whenCallExecute_thenShouldReturnCouponActivatedButNotUpdate() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aMinimumPurchase = 100.0f;
        final var aExpirationDate = InstantUtils.now().plus(1, ChronoUnit.DAYS);
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

        Mockito.when(couponGateway.findById(aId)).thenReturn(Optional.of(aCoupon));

        final var aOutput = this.activateCouponUseCase.execute(aId);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCoupon.getId().getValue(), aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(couponGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidId_whenCallExecute_thenShouldThrowNotFoundException() {
        final var aId = "invalid-id";

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aId);

        Mockito.when(couponGateway.findById(aId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.activateCouponUseCase.execute(aId));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aId);
        Mockito.verify(couponGateway, Mockito.times(0)).update(Mockito.any());
    }
}
