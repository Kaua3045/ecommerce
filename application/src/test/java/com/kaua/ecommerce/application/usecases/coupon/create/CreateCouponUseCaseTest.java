package com.kaua.ecommerce.application.usecases.coupon.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateCouponUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @Mock
    private CouponSlotGateway couponSlotGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultCreateCouponUseCase createCouponUseCase;

    @Test
    void givenAValidCommandWithLimitedType_whenCallExecute_thenShouldCreateCoupon() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "LIMITED";
        final var aMaxUse = 10;

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                aMaxUse
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);
        Mockito.when(couponGateway.create(Mockito.any())).then(returnsFirstArg());
        Mockito.when(couponSlotGateway.createInBatch(Mockito.any())).then(returnsFirstArg());
        Mockito.when(transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        final var aOutput = this.createCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.couponId());
        Assertions.assertNotNull(aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(1)).create(argThat(aCmd ->
                Objects.equals(aCode, aCmd.getCode().getValue())
                        && Objects.equals(aPercentage, aCmd.getPercentage())
                        && Objects.equals(aIsActive, aCmd.isActive())
                        && Objects.equals(aType, aCmd.getType().name())));
        Mockito.verify(couponSlotGateway, Mockito.times(1))
                .createInBatch(argThat(aCmd -> Objects.equals(10, aCmd.size())));
    }

    @Test
    void givenAValidCommandWithUnlimitedType_whenCallExecute_thenShouldCreateCoupon() {
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

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);
        Mockito.when(couponGateway.create(Mockito.any())).then(returnsFirstArg());
        Mockito.when(transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        final var aOutput = this.createCouponUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.couponId());
        Assertions.assertNotNull(aOutput.code());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(1)).create(argThat(aCmd ->
                Objects.equals(aCode, aCmd.getCode().getValue())
                        && Objects.equals(aPercentage, aCmd.getPercentage())
                        && Objects.equals(aIsActive, aCmd.isActive())
                        && Objects.equals(aType, aCmd.getType().name())));
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenACommandWithExistingCode_whenCallExecute_thenShouldReturnDomainException() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'code' already exists";

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(true);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAnInvalidNullCode_whenCallExecute_thenShouldReturnDomainException() {
        final String aCode = null;
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("code");

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAnInvalidBlankCode_whenCallExecute_thenShouldReturnDomainException() {
        final var aCode = " ";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("code");

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAnInvalidCodeMoreThan100Characters_whenCallExecute_thenShouldReturnDomainException() {
        final var aCode = "a".repeat(101);
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.maxSize("code", 100);

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAnInvalidNegativePercentage_whenCallExecute_thenShouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = -10.0f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("percentage", 0);

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAnInvalidExpirationDateInThePast_whenCallExecute_thenShouldReturnDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = Instant.now().minus(1, ChronoUnit.DAYS);
        final var aIsActive = true;
        final var aType = "UNLIMITED";

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.dateMustBeFuture("expirationDate");

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate.toString(),
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAnInvalidType_whenCallExecute_thenShouldThrowDomainException() {
        final var aCode = "BLACKFRIDAY";
        final var aPercentage = 10.0f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final String aType = null;

        final var expectedErrorMessage = "type null was not found";

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                0
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = Assertions.assertThrows(DomainException.class,
                () -> this.createCouponUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAValidCommandWithLimitedTypeAndMaxUsesLessThanOne_whenCallExecute_thenShouldReturnDomainException() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "LIMITED";
        final var aMaxUse = 0;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("maxUses", 0);

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                aMaxUse
        );

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);

        final var aOutput = this.createCouponUseCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedErrorCount, aOutput.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAValidCommandWithLimitedTypeAndTransactionManagerThrows_whenCallExecute_thenShouldThrowTransactionFailureException() {
        final var aCode = "BLACK_FRIDAY";
        final var aPercentage = 10.5f;
        final var aExpirationDate = Instant.now().plus(1, ChronoUnit.DAYS).toString();
        final var aIsActive = true;
        final var aType = "LIMITED";
        final var aMaxUse = 10;

        final var aCommand = CreateCouponCommand.with(
                aCode,
                aPercentage,
                aExpirationDate,
                aIsActive,
                aType,
                aMaxUse
        );

        final var expectedErrorMessage = "Error on create coupon";

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);
        Mockito.when(transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.createCouponUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }

    @Test
    void givenAValidCommandWithUnlimitedTypeAndTransactionManagerThrows_whenCallExecute_thenShouldThrowTransactionFailureException() {
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

        final var expectedErrorMessage = "Error on create coupon";

        Mockito.when(couponGateway.existsByCode(Mockito.any())).thenReturn(false);
        Mockito.when(transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.createCouponUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(couponGateway, Mockito.times(1))
                .existsByCode(Mockito.any());
        Mockito.verify(couponGateway, Mockito.times(0)).create(Mockito.any());
        Mockito.verify(couponSlotGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
    }
}
