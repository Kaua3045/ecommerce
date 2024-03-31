package com.kaua.ecommerce.application.order.retrieve.get;

import com.kaua.ecommerce.application.usecases.order.retrieve.get.GetOrderByCodeUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.order.OrderItem;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.order.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@IntegrationTest
public class GetOrderByCodeUseCaseIT {

    @Autowired
    private GetOrderByCodeUseCase getOrderByCodeUseCase;

    @Autowired
    private OrderItemJpaEntityRepository orderItemJpaEntityRepository;

    @Autowired
    private OrderDeliveryJpaEntityRepository orderDeliveryJpaEntityRepository;

    @Autowired
    private OrderPaymentJpaEntityRepository orderPaymentJpaEntityRepository;

    @Autowired
    private OrderJpaEntityRepository orderJpaEntityRepository;

    @Test
    void givenAValidOrderCode_whenCallGetOrderByCodeUseCase_thenReturnOrder() {
        final var aOrderDelivery = Fixture.Orders.orderDelivery();
        final var aOrderPayment = Fixture.Orders.orderPaymentWithZeroInstallments();
        final var aOrder = Fixture.Orders.orderWithCoupon();
        aOrder.addItem(OrderItem.create(
                aOrder.getId().getValue(),
                "123",
                Fixture.createSku("tshirt"),
                2,
                BigDecimal.valueOf(100.00)
        ));

        this.orderDeliveryJpaEntityRepository.save(OrderDeliveryJpaEntity.toEntity(aOrderDelivery));
        this.orderPaymentJpaEntityRepository.save(OrderPaymentJpaEntity.toEntity(aOrderPayment));
        this.orderItemJpaEntityRepository.saveAll(aOrder.getOrderItems().stream()
                .map(OrderItemJpaEntity::toEntity).collect(Collectors.toSet()));
        this.orderJpaEntityRepository.save(OrderJpaEntity.toEntity(aOrder));

        Assertions.assertEquals(1, this.orderJpaEntityRepository.count());

        final var aOutput = this.getOrderByCodeUseCase.execute(aOrder.getOrderCode().getValue());

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aOrder.getId().getValue(), aOutput.orderId());
        Assertions.assertEquals(aOrder.getOrderCode().getValue(), aOutput.orderCode());
        Assertions.assertEquals(aOrder.getOrderStatus().name(), aOutput.orderStatus());
        Assertions.assertEquals(aOrder.getCustomerId(), aOutput.customerId());
        Assertions.assertEquals(aOrder.getCouponCode().get(), aOutput.couponCode());
        Assertions.assertEquals(aOrder.getCouponPercentage(), aOutput.couponPercentage());
        Assertions.assertEquals(aOrder.getOrderItems().size(), aOutput.orderItems().size());
        Assertions.assertEquals(aOrder.getTotalAmount(), aOutput.totalAmount());
        Assertions.assertEquals(aOrder.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aOrder.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(1, aOutput.version());
        Assertions.assertEquals(aOrder.getOrderDeliveryId().getValue(), aOutput.orderDelivery().orderDeliveryId());
        Assertions.assertEquals(aOrder.getOrderPaymentId().getValue(), aOutput.orderPayment().orderPaymentId());
    }
}
