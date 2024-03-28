package com.kaua.ecommerce.domain.order;

import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.UnitTest;
import com.kaua.ecommerce.domain.order.identifiers.OrderID;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

public class OrderTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewOrder_shouldCreateNewOrder() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
        final var aOrderCode = OrderCode.create(aSequence);
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;

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

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aOrderDelivery,
                aOrderPayment.getId()
        );

        final var aItemOne = OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aItemTwo = OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSkuTwo",
                2,
                BigDecimal.valueOf(20.0)
        );

        aOrder.addItem(aItemOne);
        aOrder.addItem(aItemTwo);

        aOrder.calculateTotalAmount(aOrderDelivery);
        aOrder.applyCoupon(aCouponCode, aCouponPercentage);

        Assertions.assertEquals(aOrderCode, aOrder.getOrderCode());
        Assertions.assertEquals(2, aOrder.getOrderItems().size());
        Assertions.assertEquals(new BigDecimal("49.50"), aOrder.getTotalAmount());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aCouponCode, aOrder.getCouponCode().get());
        Assertions.assertEquals(aCouponPercentage, aOrder.getCouponPercentage());
        Assertions.assertEquals(OrderStatus.WAITING_PAYMENT, aOrder.getOrderStatus());
        Assertions.assertEquals(aOrderDelivery.getId(), aOrder.getOrderDeliveryId());
        Assertions.assertEquals(aOrderPayment.getId(), aOrder.getOrderPaymentId());
        Assertions.assertEquals(0, aOrder.getVersion());
        Assertions.assertNotNull(aOrder.getCreatedAt());
        Assertions.assertNotNull(aOrder.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aOrderDelivery.validate(new ThrowsValidationHandler()));
        Assertions.assertDoesNotThrow(() -> aOrderPayment.validate(new ThrowsValidationHandler()));
        Assertions.assertDoesNotThrow(() -> aOrder.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValues_whenCallWith_shouldInstantiateOrder() {
        final var aOrderId = "aOrderId";
        final var aVersion = 0L;
        final var aOrderCode = OrderCode.create(0);
        final var aOrderStatus = OrderStatus.WAITING_PAYMENT;
        final var aOrderItems = Set.of(
                OrderItem.create(
                        "aOrderId",
                        "aProductId",
                        "aSkuOne",
                        1,
                        BigDecimal.valueOf(10.0)
                ),
                OrderItem.create(
                        "aOrderId",
                        "aProductId",
                        "aSkuTwo",
                        2,
                        BigDecimal.valueOf(20.0)
                )
        );
        final var aTotalAmount = new BigDecimal("49.50");
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;
        final var aOrderDeliveryId = "aOrderDeliveryId";
        final var aOrderPaymentId = "aOrderPaymentId";
        final var aCreatedAt = InstantUtils.now();
        final var aUpdatedAt = InstantUtils.now();

        final var aOrder = Order.with(
                aOrderId,
                aVersion,
                aOrderCode.getValue(),
                aOrderStatus,
                aOrderItems,
                aTotalAmount,
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDeliveryId,
                aOrderPaymentId,
                aCreatedAt,
                aUpdatedAt
        );

        Assertions.assertEquals(aOrderId, aOrder.getId().getValue());
        Assertions.assertEquals(aVersion, aOrder.getVersion());
        Assertions.assertEquals(aOrderCode.getValue(), aOrder.getOrderCode().getValue());
        Assertions.assertEquals(aOrderStatus, aOrder.getOrderStatus());
        Assertions.assertEquals(aOrderItems, aOrder.getOrderItems());
        Assertions.assertEquals(aTotalAmount, aOrder.getTotalAmount());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aCouponCode, aOrder.getCouponCode().get());
        Assertions.assertEquals(aCouponPercentage, aOrder.getCouponPercentage());
        Assertions.assertEquals(aOrderDeliveryId, aOrder.getOrderDeliveryId().getValue());
        Assertions.assertEquals(aOrderPaymentId, aOrder.getOrderPaymentId().getValue());
        Assertions.assertEquals(aCreatedAt, aOrder.getCreatedAt());
        Assertions.assertEquals(aUpdatedAt, aOrder.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aOrder.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValuesWithoutCoupon_whenCallNewOrder_shouldCreateNewOrder() {
        final var aSequence = 0;
        final var aCustomerId = "aCustomerId";
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

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aOrderDelivery,
                aOrderPayment.getId()
        );

        final var aItemOne = OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aItemTwo = OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSkuTwo",
                2,
                BigDecimal.valueOf(20.0)
        );

        aOrder.addItem(aItemOne);
        aOrder.addItem(aItemTwo);

        aOrder.calculateTotalAmount(aOrderDelivery);

        Assertions.assertEquals(aOrderCode, aOrder.getOrderCode());
        Assertions.assertEquals(2, aOrder.getOrderItems().size());
        Assertions.assertEquals(new BigDecimal("55.00"), aOrder.getTotalAmount());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertTrue(aOrder.getCouponCode().isEmpty());
        Assertions.assertEquals(0, aOrder.getCouponPercentage());
        Assertions.assertEquals(OrderStatus.WAITING_PAYMENT, aOrder.getOrderStatus());
        Assertions.assertEquals(aOrderDelivery.getId(), aOrder.getOrderDeliveryId());
        Assertions.assertEquals(aOrderPayment.getId(), aOrder.getOrderPaymentId());
        Assertions.assertEquals(0, aOrder.getVersion());
        Assertions.assertNotNull(aOrder.getCreatedAt());
        Assertions.assertNotNull(aOrder.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aOrderDelivery.validate(new ThrowsValidationHandler()));
        Assertions.assertDoesNotThrow(() -> aOrderPayment.validate(new ThrowsValidationHandler()));
        Assertions.assertDoesNotThrow(() -> aOrder.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidValue_whenCallOrderStatusOf_shouldReturnOrderStatus() {
        final var aOrderStatusName = "WAITING_PAYMENT";
        final var aOrderStatus = OrderStatus.of(aOrderStatusName);

        Assertions.assertEquals(OrderStatus.WAITING_PAYMENT, aOrderStatus.get());
    }

    @Test
    void testOrderIdEqualsAndHashCode() {
        final var aOrderId = OrderID.from("123456789");
        final var anotherOrderId = OrderID.from("123456789");

        Assertions.assertTrue(aOrderId.equals(anotherOrderId));
        Assertions.assertTrue(aOrderId.equals(aOrderId));
        Assertions.assertFalse(aOrderId.equals(null));
        Assertions.assertFalse(aOrderId.equals(""));
        Assertions.assertEquals(aOrderId.hashCode(), anotherOrderId.hashCode());
    }

    @Test
    void givenAValidOrder_whenCallToString_shouldReturnString() {
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

        final var aOrder = Order.newOrder(
                aOrderCode,
                aCustomerId,
                aOrderDelivery,
                aOrderPayment.getId()
        );

        final var aItemOne = OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSkuOne",
                1,
                BigDecimal.valueOf(10.0)
        );
        final var aItemTwo = OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSkuTwo",
                2,
                BigDecimal.valueOf(20.0)
        );

        aOrder.addItem(aItemOne);
        aOrder.addItem(aItemTwo);

        aOrder.calculateTotalAmount(aOrderDelivery);
        aOrder.applyCoupon(aCouponCode, aCouponPercentage);

        final var aExpected = "Order(" +
                "id=" + aOrder.getId().getValue() +
                ", orderCode=" + aOrderCode.getValue() +
                ", orderStatus=" + aOrder.getOrderStatus().name() +
                ", orderItems=" + aOrder.getOrderItems().size() +
                ", customerId='" + aCustomerId + '\'' +
                ", couponCode='" + aCouponCode + '\'' +
                ", couponPercentage=" + aCouponPercentage +
                ", orderDeliveryId=" + aOrderDelivery.getId().getValue() +
                ", orderPaymentId=" + aOrderPayment.getId().getValue() +
                ", totalAmount=" + aOrder.getTotalAmount() +
                ", createdAt=" + aOrder.getCreatedAt() +
                ", updatedAt=" + aOrder.getUpdatedAt() +
                ", version=" + aOrder.getVersion() +
                ')';

        Assertions.assertEquals(aExpected, aOrder.toString());
    }

    @Test
    void givenAValidOrder_whenCallWith_shouldInstantiateOrder() {
        final var aOrder = Fixture.Orders.orderWithCoupon();

        final var aOrderWith = Order.with(aOrder);

        Assertions.assertEquals(aOrder.getId().getValue(), aOrderWith.getId().getValue());
        Assertions.assertEquals(aOrder.getVersion(), aOrderWith.getVersion());
        Assertions.assertEquals(aOrder.getOrderCode().getValue(), aOrderWith.getOrderCode().getValue());
        Assertions.assertEquals(aOrder.getOrderStatus(), aOrderWith.getOrderStatus());
        Assertions.assertEquals(aOrder.getOrderItems(), aOrderWith.getOrderItems());
        Assertions.assertEquals(aOrder.getTotalAmount(), aOrderWith.getTotalAmount());
        Assertions.assertEquals(aOrder.getCustomerId(), aOrderWith.getCustomerId());
        Assertions.assertEquals(aOrder.getCouponCode().get(), aOrderWith.getCouponCode().get());
        Assertions.assertEquals(aOrder.getCouponPercentage(), aOrderWith.getCouponPercentage());
        Assertions.assertEquals(aOrder.getOrderDeliveryId().getValue(), aOrderWith.getOrderDeliveryId().getValue());
        Assertions.assertEquals(aOrder.getOrderPaymentId().getValue(), aOrderWith.getOrderPaymentId().getValue());
        Assertions.assertEquals(aOrder.getCreatedAt(), aOrderWith.getCreatedAt());
        Assertions.assertEquals(aOrder.getUpdatedAt(), aOrderWith.getUpdatedAt());
        Assertions.assertDoesNotThrow(() -> aOrderWith.validate(new ThrowsValidationHandler()));
    }

    @Test
    void givenAValidOrder_whenCallApplyCouponWithNull_shouldNotApplyCoupon() {
        final var aOrder = Fixture.Orders.orderWithoutCoupon();

        aOrder.applyCoupon(null, 0.0F);

        Assertions.assertTrue(aOrder.getCouponCode().isEmpty());
        Assertions.assertEquals(0.0f, aOrder.getCouponPercentage());
    }
}
