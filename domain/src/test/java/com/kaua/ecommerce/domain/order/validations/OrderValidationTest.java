package com.kaua.ecommerce.domain.order.validations;

import com.kaua.ecommerce.domain.TestValidationHandler;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.exceptions.NotAcceptDuplicatedItemsException;
import com.kaua.ecommerce.domain.order.*;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

public class OrderValidationTest extends UnitTest {

    @Test
    void givenAInvalidDuplicatedSku_whenCallNewOrder_shouldThrowsNotAcceptDuplicatedItemsException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);

        final var expectedErrorMessage = "The item with SKU aSkuOne is already in the order";

        final var aItemOne = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );

        final var aItemTwo = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                2,
                BigDecimal.valueOf(20.0)
        );

        final var aItems = Set.of(aItemOne, aItemTwo);

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );

        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );
        final var aOrderPaymentId = aOrderPayment.getId();

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPaymentId
        );

        final var aOutput = Assertions.assertThrows(NotAcceptDuplicatedItemsException.class,
                () -> aItems.forEach(aOrder::addItem));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());
    }

    @Test
    void givenInvalidBlankCustomerId_whenCallNewOrder_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = " ";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aItem = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("customerId");
        final var expectedErrorCount = 1;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );
        aOrder.addItem(aItem);
        aOrder.calculateTotalAmount(aOrderDelivery);

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullCustomerId_whenCallNewOrder_shouldReturnDomainException() {
        final var aSequence = 0;
        final String aCustomerId = null;
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aItem = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("customerId");
        final var expectedErrorCount = 1;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );
        aOrder.addItem(aItem);
        aOrder.calculateTotalAmount(aOrderDelivery);

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankCouponCode_whenCallNewOrder_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = " ";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aItem = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.blankMessage("couponCode");
        final var expectedErrorCount = 1;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );
        aOrder.addItem(aItem);
        aOrder.calculateTotalAmount(aOrderDelivery);

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNegativeCouponPercentage_whenCallNewOrder_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = -1.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aItem = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("couponPercentage", -1);
        final var expectedErrorCount = 1;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );
        aOrder.addItem(aItem);
        aOrder.calculateTotalAmount(aOrderDelivery);

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullOrderDeliveryId_whenCallNewOrder_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("orderDeliveryId");
        final var expectedErrorCount = 1;

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.newOrder(
                        aOrderCode,
                        aCustomerId,
                        aCouponCode,
                        aCouponPercentage,
                        null,
                        aOrderPayment.getId()
                ));

        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullOrderPaymentId_whenCallNewOrder_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aItem = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );

        final var expectedErrorMessage = CommonErrorMessage.nullMessage("orderPaymentId");
        final var expectedErrorCount = 1;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                null
        );
        aOrder.addItem(aItem);
        aOrder.calculateTotalAmount(aOrderDelivery);

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenInvalidNullOrderCode_whenCallOrderCodeWith_shouldThrowDomainException() {
        final String aOrderCode = null;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("orderCode");
        final var expectedErrorCount = 1;

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderCode.with(aOrderCode));

        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidBlankOrderCode_whenCallOrderCodeWith_shouldThrowDomainException() {
        final String aOrderCode = " ";

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("orderCode");
        final var expectedErrorCount = 1;

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> OrderCode.with(aOrderCode));

        Assertions.assertEquals(expectedErrorCount, aException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenInvalidZeroOrderItem_whenCallValidate_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.minSize("orderItems", 1);
        final var expectedErrorCount = 2;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }

    @Test
    void givenValidOrderWithItemsButNotCallCalculate_whenCallValidate_shouldReturnDomainException() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderCode = OrderCode.create(aSequence);
        final var aItem = OrderItem.create(
                "aOrderId",
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "sedex",
                5.0F,
                5,
                "aStreet",
                "235",
                null,
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                "aPaymentMethodId",
                0
        );

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("totalAmount", 0);
        final var expectedErrorCount = 1;

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );
        aOrder.addItem(aItem);

        final var aTestValidationHandler = new TestValidationHandler();
        aOrder.validate(aTestValidationHandler);

        Assertions.assertEquals(expectedErrorCount, aTestValidationHandler.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aTestValidationHandler.getErrors().get(0).message());
    }
}
