package com.kaua.ecommerce.application.usecases.coupon.update;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
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

public class UpdateCouponUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @InjectMocks
    private DefaultUpdateCouponUseCase updateCouponUseCase;

    @Test
    void givenAValidValues_whenCallUpdateCouponUseCaseExecute_thenCouponShouldBeUpdated() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final var aExpirationDate = InstantUtils.now().plus(5, ChronoUnit.DAYS).toString();

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        Mockito.when(couponGateway.findById(aCouponId)).thenReturn(Optional.of(aCoupon));
        Mockito.when(couponGateway.existsByCode(aCode)).thenReturn(false);
        Mockito.when(couponGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCouponId, aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aCouponId);
        Mockito.verify(couponGateway, Mockito.times(1)).existsByCode(aCode);
        Mockito.verify(couponGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getCode().getValue(), aCode)
                        && aCmd.getPercentage() == aPercentage
                        && aCmd.getExpirationDate().toString().equals(aExpirationDate)));
    }

    @Test
    void givenAnInvalidExistsCode_whenCallUpdateCouponUseCaseExecute_thenShouldReturnDomainException() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final var aExpirationDate = InstantUtils.now().minus(5, ChronoUnit.DAYS).toString();

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        final var expectedErrorMessage = "'code' already exists";
        final var expectedErrorCount = 1;

        Mockito.when(couponGateway.findById(aCouponId)).thenReturn(Optional.of(aCoupon));
        Mockito.when(couponGateway.existsByCode(aCode)).thenReturn(true);

        final var aOutput = this.updateCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertTrue(aOutput.hasError());
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aCouponId);
        Mockito.verify(couponGateway, Mockito.times(1)).existsByCode(aCode);
        Mockito.verify(couponGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidCouponId_whenCallUpdateCouponUseCaseExecute_thenShouldThrowNotFoundException() {
        final var aCouponId = "123456789";
        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final var aExpirationDate = InstantUtils.now().minus(5, ChronoUnit.DAYS).toString();

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        final var expectedErrorMessage = Fixture.notFoundMessage(Coupon.class, aCouponId);

        Mockito.when(couponGateway.findById(aCouponId)).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateCouponUseCase.execute(aCommand));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aCouponId);
        Mockito.verify(couponGateway, Mockito.times(0)).existsByCode(aCode);
        Mockito.verify(couponGateway, Mockito.times(0)).update(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankExpirationDate_whenCallUpdateCouponUseCaseExecute_thenShouldReturnCouponWithOldExpirationDate() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final var aExpirationDate = "";

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        Mockito.when(couponGateway.findById(aCouponId)).thenReturn(Optional.of(aCoupon));
        Mockito.when(couponGateway.existsByCode(aCode)).thenReturn(false);
        Mockito.when(couponGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCouponId, aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aCouponId);
        Mockito.verify(couponGateway, Mockito.times(1)).existsByCode(aCode);
        Mockito.verify(couponGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getCode().getValue(), aCode)
                        && aCmd.getPercentage() == aPercentage
                        && aCmd.getExpirationDate() == aCoupon.getExpirationDate()));
    }

    @Test
    void givenAnInvalidNullExpirationDate_whenCallUpdateCouponUseCaseExecute_thenShouldReturnCouponWithOldExpirationDate() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 200.0f;
        final String aExpirationDate = null;

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        Mockito.when(couponGateway.findById(aCouponId)).thenReturn(Optional.of(aCoupon));
        Mockito.when(couponGateway.existsByCode(aCode)).thenReturn(false);
        Mockito.when(couponGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aCouponId, aOutput.couponId());
        Assertions.assertEquals(aCode, aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aCouponId);
        Mockito.verify(couponGateway, Mockito.times(1)).existsByCode(aCode);
        Mockito.verify(couponGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getCode().getValue(), aCode)
                        && aCmd.getPercentage() == aPercentage
                        && aCmd.getExpirationDate() == aCoupon.getExpirationDate()));
    }

    @Test
    void givenAnInvalidPercentageZero_whenCallUpdateCouponUseCaseExecute_thenShouldReturnDomainException() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aCouponId = aCoupon.getId().getValue();

        final var aCode = "NEWCODE";
        final var aPercentage = 0.0f;
        final var aExpirationDate = InstantUtils.now().plus(5, ChronoUnit.DAYS).toString();

        final var aCommand = UpdateCouponCommand.with(aCouponId, aCode, aPercentage, aExpirationDate);

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("percentage", 0);

        Mockito.when(couponGateway.findById(aCouponId)).thenReturn(Optional.of(aCoupon));
        Mockito.when(couponGateway.existsByCode(aCode)).thenReturn(false);

        final var aOutput = this.updateCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertTrue(aOutput.hasError());
        Assertions.assertEquals(1, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1)).findById(aCouponId);
        Mockito.verify(couponGateway, Mockito.times(1)).existsByCode(aCode);
        Mockito.verify(couponGateway, Mockito.times(0)).update(Mockito.any());
    }
}
