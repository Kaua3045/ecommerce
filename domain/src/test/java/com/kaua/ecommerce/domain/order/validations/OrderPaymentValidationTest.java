package com.kaua.ecommerce.domain.order.validations;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.order.OrderPayment;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderPaymentValidationTest extends UnitTest {

    @Test
    void givenInvalidNullPaymentMethodId_whenCallNewOrderPayment_shouldReturnDomainException() {
        final String aPaymentMethodId = null;
        final var aInstallments = 1;

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("paymentMethodId");

        final var aOrderPayment = OrderPayment.newOrderPayment(
                aPaymentMethodId,
                aInstallments
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderPayment.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankPaymentMethodId_whenCallNewOrderPayment_shouldReturnDomainException() {
        final String aPaymentMethodId = "";
        final var aInstallments = 1;

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("paymentMethodId");

        final var aOrderPayment = OrderPayment.newOrderPayment(
                aPaymentMethodId,
                aInstallments
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderPayment.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNegativeInstallments_whenCallNewOrderPayment_shouldReturnDomainException() {
        final String aPaymentMethodId = "creditCard";
        final var aInstallments = -1;

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("installments", -1);

        final var aOrderPayment = OrderPayment.newOrderPayment(
                aPaymentMethodId,
                aInstallments
        );

        final var aValidationHandler = new TestValidationHandler();
        aOrderPayment.validate(aValidationHandler);

        Assertions.assertEquals(expectedErrorsCount, aValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aValidationHandler.getErrors().get(0).message());
    }
}
