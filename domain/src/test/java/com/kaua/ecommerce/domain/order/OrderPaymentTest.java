package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.order.identifiers.OrderID;
import com.kaua.ecommerce.domain.order.identifiers.OrderPaymentID;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderPaymentTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewOrderPayment_shouldCreateNewOrderPayment() {
        final var aPaymentMethodId = "aPaymentMethodId";
        final var aInstallments = 0;

        final var aOrderPayment = OrderPayment.newOrderPayment(
                aPaymentMethodId,
                aInstallments
        );

        Assertions.assertNotNull(aOrderPayment);
        Assertions.assertEquals(aPaymentMethodId, aOrderPayment.getPaymentMethodId());
        Assertions.assertEquals(aInstallments, aOrderPayment.getInstallments());
        Assertions.assertDoesNotThrow(() -> aOrderPayment.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValues_whenCallOrderPaymentWith_shouldRestoreOrderPayment() {
        final var aOrderPaymentId = "aOrderPaymentId";
        final var aPaymentMethodId = "aPaymentMethodId";
        final var aInstallments = 0;

        final var aOrderPayment = OrderPayment.with(
                aOrderPaymentId,
                aPaymentMethodId,
                aInstallments
        );

        Assertions.assertNotNull(aOrderPayment);
        Assertions.assertEquals(aOrderPaymentId, aOrderPayment.getId().getValue());
        Assertions.assertEquals(aPaymentMethodId, aOrderPayment.getPaymentMethodId());
        Assertions.assertEquals(aInstallments, aOrderPayment.getInstallments());
        Assertions.assertDoesNotThrow(() -> aOrderPayment.validate(new ThrowsValidationHandler()));
    }

    @Test
    void testOrderPaymentIdEqualsAndHashCode() {
        final var aOrderPaymentId = OrderPaymentID.from("123456789");
        final var anotherOrderPaymentId = OrderPaymentID.from("123456789");

        Assertions.assertTrue(aOrderPaymentId.equals(anotherOrderPaymentId));
        Assertions.assertTrue(aOrderPaymentId.equals(aOrderPaymentId));
        Assertions.assertFalse(aOrderPaymentId.equals(null));
        Assertions.assertFalse(aOrderPaymentId.equals(""));
        Assertions.assertEquals(aOrderPaymentId.hashCode(), anotherOrderPaymentId.hashCode());
    }

    @Test
    void givenAOrderPayment_whenCallToString_shouldReturnStringRepresentation() {
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var aStringRepresentation = aOrderPayment.toString();

        Assertions.assertNotNull(aStringRepresentation);
        Assertions.assertTrue(aStringRepresentation.contains("OrderPayment("));
        Assertions.assertTrue(aStringRepresentation.contains("id='"));
        Assertions.assertTrue(aStringRepresentation.contains("paymentMethodId="));
        Assertions.assertTrue(aStringRepresentation.contains("installments="));
    }
}
