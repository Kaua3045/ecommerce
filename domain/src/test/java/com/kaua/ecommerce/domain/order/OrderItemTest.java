package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.order.identifiers.OrderID;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class OrderItemTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallCreate_shouldCreateNewOrderItem() {
        final var aOrderId = OrderID.unique().getValue();
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var aOrderItem = OrderItem.create(
                aOrderId,
                aProductId,
                aSku,
                aQuantity,
                aPrice
        );

        Assertions.assertNotNull(aOrderItem);
        Assertions.assertEquals(aOrderId, aOrderItem.getOrderId());
        Assertions.assertEquals(aProductId, aOrderItem.getProductId());
        Assertions.assertEquals(aSku, aOrderItem.getSku());
        Assertions.assertEquals(aQuantity, aOrderItem.getQuantity());
        Assertions.assertEquals(aPrice, aOrderItem.getPrice());
        Assertions.assertEquals(BigDecimal.valueOf(10.0), aOrderItem.getTotal());
    }

    @Test
    void givenAValidValues_whenCallWith_shouldRestoreOrderItem() {
        final var aOrderId = OrderID.unique().getValue();
        final var aOrderItemId = "aOrderItemId";
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var aOrderItem = OrderItem.with(
                aOrderItemId,
                aOrderId,
                aProductId,
                aSku,
                aQuantity,
                aPrice
        );

        Assertions.assertNotNull(aOrderItem);
        Assertions.assertEquals(aOrderItemId, aOrderItem.getOrderItemId());
        Assertions.assertEquals(aOrderId, aOrderItem.getOrderId());
        Assertions.assertEquals(aProductId, aOrderItem.getProductId());
        Assertions.assertEquals(aSku, aOrderItem.getSku());
        Assertions.assertEquals(aQuantity, aOrderItem.getQuantity());
        Assertions.assertEquals(aPrice, aOrderItem.getPrice());
        Assertions.assertEquals(BigDecimal.valueOf(10.0), aOrderItem.getTotal());
    }

    @Test
    void testOrderItemIdEqualsAndHashCode() {
        final var aOrderItemId = OrderItem.with(
                "123456789",
                "aOrderId",
                "aProductId",
                "aSku",
                1,
                BigDecimal.valueOf(10.0));
        final var anotherOrderItemId = OrderItem.with(
                "123456789",
                "aOrderId",
                "aProductId",
                "aSku",
                1,
                BigDecimal.valueOf(10.0));

        Assertions.assertTrue(aOrderItemId.equals(anotherOrderItemId));
        Assertions.assertTrue(aOrderItemId.equals(aOrderItemId));
        Assertions.assertFalse(aOrderItemId.equals(null));
        Assertions.assertFalse(aOrderItemId.equals(""));
        Assertions.assertEquals(aOrderItemId.hashCode(), anotherOrderItemId.hashCode());
    }

    @Test
    void givenInvalidNullProductId_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final String aProductId = null;
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankProductId_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final String aProductId = "";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankSku_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final var aProductId = "aProductId";
        final String aSku = "";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullSku_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final var aProductId = "aProductId";
        final String aSku = null;
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNegativeQuantity_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = -1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", 0);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullPrice_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final BigDecimal aPrice = null;

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidZeroPrice_whenCallCreate_shouldThrowDomainException() {
        final var aOrderId = OrderID.unique().getValue();
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.ZERO;

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.greaterThan("price", 0);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullOrderId_whenCallCreate_shouldThrowDomainException() {
        final String aOrderId = null;
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("orderId");

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankOrderId_whenCallCreate_shouldThrowDomainException() {
        final String aOrderId = "";
        final var aProductId = "aProductId";
        final var aSku = "aSku";
        final var aQuantity = 1;
        final var aPrice = BigDecimal.valueOf(10.0);

        final var expectedErrorsCount = 1;
        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("orderId");

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderItem.create(
                        aOrderId,
                        aProductId,
                        aSku,
                        aQuantity,
                        aPrice
                ));

        Assertions.assertEquals(expectedErrorsCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
