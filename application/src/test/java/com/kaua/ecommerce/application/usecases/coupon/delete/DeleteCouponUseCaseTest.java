package com.kaua.ecommerce.application.usecases.coupon.delete;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.application.gateways.CouponSlotGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.function.Supplier;

public class DeleteCouponUseCaseTest extends UseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @Mock
    private CouponSlotGateway couponSlotGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultDeleteCouponUseCase deleteCouponUseCase;

    @Test
    void givenAValidId_whenCallExecute_thenShouldDeleteCoupon() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aId = aCoupon.getId().getValue();

        Mockito.doNothing().when(couponGateway).deleteById(aId);
        Mockito.doNothing().when(couponSlotGateway).deleteAllByCouponId(aId);
        Mockito.when(transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        Assertions.assertDoesNotThrow(() -> this.deleteCouponUseCase.execute(aId));

        Mockito.verify(couponGateway, Mockito.times(1)).deleteById(aId);
        Mockito.verify(couponSlotGateway, Mockito.times(1)).deleteAllByCouponId(aId);
    }

    @Test
    void givenAnErrorOnTransaction_whenCallExecute_thenShouldThrowTransactionFailureException() {
        final var aCoupon = Fixture.Coupons.limitedCouponActivated();
        final var aId = aCoupon.getId().getValue();

        final var expectedErrorMessage = "error on transaction";

        Mockito.when(transactionManager.execute(Mockito.any())).thenAnswer(it ->
                TransactionResult.failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(TransactionFailureException.class,
                () -> this.deleteCouponUseCase.execute(aId));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(couponGateway, Mockito.times(0)).deleteById(aId);
        Mockito.verify(couponSlotGateway, Mockito.times(0)).deleteAllByCouponId(aId);
    }
}
