package com.kaua.ecommerce.application.usecases.order.retrieve.get;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.order.OrderDeliveryGateway;
import com.kaua.ecommerce.application.gateways.order.OrderGateway;
import com.kaua.ecommerce.application.gateways.order.OrderPaymentGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.order.Order;
import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.domain.order.OrderPayment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class GetOrderByCodeUseCaseTest extends UseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderDeliveryGateway orderDeliveryGateway;

    @Mock
    private OrderPaymentGateway orderPaymentGateway;

    @InjectMocks
    private DefaultGetOrderByCodeUseCase getOrderByCodeUseCase;

    @Test
    void givenAValidOrderCode_whenCallGetOrderByCode_thenReturnOrder() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aOrder = Fixture.Orders.orderWithCoupon();

        final var aOrderCode = aOrder.getOrderCode().getValue();

        Mockito.when(orderGateway.findByCode(aOrderCode)).thenReturn(Optional.of(aOrder));
        Mockito.when(orderDeliveryGateway.findById(aOrder.getOrderDeliveryId().getValue()))
                .thenReturn(Optional.of(aOrderDelivery));
        Mockito.when(orderPaymentGateway.findById(aOrder.getOrderPaymentId().getValue()))
                .thenReturn(Optional.of(aOrderPayment));

        final var aOutput = this.getOrderByCodeUseCase.execute(aOrderCode);

        Assertions.assertEquals(aOrder.getId().getValue(), aOutput.orderId());
        Assertions.assertEquals(aOrder.getOrderCode().getValue(), aOutput.orderCode());
        Assertions.assertEquals(aOrder.getOrderStatus().name(), aOutput.orderStatus());
        Assertions.assertEquals(aOrder.getCustomerId(), aOutput.customerId());
        Assertions.assertEquals(aOrder.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aOrder.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aOrder.getCouponCode().get(), aOutput.couponCode());
        Assertions.assertEquals(aOrder.getCouponPercentage(), aOutput.couponPercentage());
        Assertions.assertEquals(aOrder.getTotalAmount(), aOutput.totalAmount());
        Assertions.assertEquals(aOrder.getVersion(), aOutput.version());
        Assertions.assertEquals(aOrder.getOrderItems().size(), aOutput.orderItems().size());
        Assertions.assertEquals(aOrder.getOrderDeliveryId().getValue(), aOutput.orderDelivery().orderDeliveryId());
        Assertions.assertEquals(aOrderPayment.getId().getValue(), aOutput.orderPayment().orderPaymentId());
    }

    @Test
    void givenAnInvalidOrderCode_whenCallGetOrderByCode_thenThrowNotFoundException() {
        final var aOrderCode = "invalid-order-code";

        final var expectedErrorMessage = Fixture.notFoundMessage(Order.class, aOrderCode);

        Mockito.when(orderGateway.findByCode(aOrderCode)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.getOrderByCodeUseCase.execute(aOrderCode)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnInvalidOrderDeliveryId_whenCallGetOrderByCode_thenThrowNotFoundException() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aOrder = Fixture.Orders.orderWithCoupon();

        final var aOrderCode = aOrder.getOrderCode().getValue();

        Mockito.when(orderGateway.findByCode(aOrderCode)).thenReturn(Optional.of(aOrder));
        Mockito.when(orderDeliveryGateway.findById(aOrder.getOrderDeliveryId().getValue()))
                .thenReturn(Optional.empty());

        final var expectedErrorMessage = Fixture.notFoundMessage(OrderDelivery.class, aOrder.getOrderDeliveryId().getValue());

        final var aException = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.getOrderByCodeUseCase.execute(aOrderCode)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnInvalidOrderPaymentId_whenCallGetOrderByCode_thenThrowNotFoundException() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aOrder = Fixture.Orders.orderWithCoupon();

        final var aOrderCode = aOrder.getOrderCode().getValue();

        Mockito.when(orderGateway.findByCode(aOrderCode)).thenReturn(Optional.of(aOrder));
        Mockito.when(orderDeliveryGateway.findById(aOrder.getOrderDeliveryId().getValue()))
                .thenReturn(Optional.of(aOrderDelivery));
        Mockito.when(orderPaymentGateway.findById(aOrder.getOrderPaymentId().getValue()))
                .thenReturn(Optional.empty());

        final var expectedErrorMessage = Fixture.notFoundMessage(OrderPayment.class, aOrder.getOrderPaymentId().getValue());

        final var aException = Assertions.assertThrows(
                NotFoundException.class,
                () -> this.getOrderByCodeUseCase.execute(aOrderCode)
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }
}
