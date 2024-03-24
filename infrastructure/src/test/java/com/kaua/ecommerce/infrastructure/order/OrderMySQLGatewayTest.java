package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.domain.order.*;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.order.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

@DatabaseGatewayTest
public class OrderMySQLGatewayTest {

    @Autowired
    private OrderMySQLGateway orderMySQLGateway;

    @Autowired
    private OrderJpaEntityRepository orderJpaEntityRepository;

    @Autowired
    private OrderDeliveryJpaEntityRepository orderDeliveryJpaEntityRepository;

    @Autowired
    private OrderPaymentJpaEntityRepository orderPaymentJpaEntityRepository;

    @Autowired
    private OrderItemJpaEntityRepository orderItemJpaEntityRepository;

    @Test
    void givenAValidOrder_whenCreateOrder_thenOrderIsPersisted() {
        final var aCustomerId = "aCustomerId";
        final var aCouponCode = "aCouponCode";
        final var aCouponPercentage = 10.0F;

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                "SEDEX",
                10.0f,
                5,
                "aStreet",
                "aNumber",
                "aComplement",
                "aDistrict",
                "aCity",
                "aState",
                "aZipCode"
        );
        final var aOrderPayment = OrderPayment.newOrderPayment(
                IdUtils.generateWithoutDash(),
                0
        );

        final var aOrder = Order.newOrder(
                OrderCode.create(1L),
                aCustomerId,
                aCouponCode,
                aCouponPercentage,
                aOrderDelivery,
                aOrderPayment.getId()
        );
        aOrder.addItem(OrderItem.create(
                aOrder.getId().getValue(),
                "aProductId",
                "aSku",
                1,
                BigDecimal.valueOf(10.0)
        ));
        aOrder.calculateTotalAmount(aOrderDelivery);

        Assertions.assertEquals(0, this.orderJpaEntityRepository.count());
        Assertions.assertEquals(0, this.orderDeliveryJpaEntityRepository.count());
        Assertions.assertEquals(0, this.orderPaymentJpaEntityRepository.count());

        this.orderMySQLGateway.createInBatch(aOrder.getOrderItems());
        this.orderDeliveryJpaEntityRepository.save(OrderDeliveryJpaEntity.toEntity(aOrderDelivery));
        this.orderPaymentJpaEntityRepository.save(OrderPaymentJpaEntity.toEntity(aOrderPayment));

        final var aResult = this.orderMySQLGateway.create(aOrder);

        Assertions.assertEquals(1, this.orderJpaEntityRepository.count());
        Assertions.assertEquals(1, this.orderDeliveryJpaEntityRepository.count());
        Assertions.assertEquals(1, this.orderPaymentJpaEntityRepository.count());
        Assertions.assertEquals(aOrder.getId().getValue(), aResult.getId().getValue());
    }

    @Test
    void testCallOrderCount() {
        final var aCount = this.orderMySQLGateway.count();
        Assertions.assertEquals(0, aCount);
    }

    @Test
    void givenAValidOrderItems_whenCallCreateInBatch_thenOrderItemsArePersisted() {
        final var aOrderItems = Set.of(
                OrderItem.create(
                        "aOrderId",
                        "aProductId",
                        "aSku",
                        1,
                        BigDecimal.valueOf(10.0)
                ),
                OrderItem.create(
                        "aOrderId",
                        "aProductId2",
                        "aSku2",
                        2,
                        BigDecimal.valueOf(20.0)
                )
        );

        Assertions.assertEquals(0, this.orderItemJpaEntityRepository.count());

        this.orderMySQLGateway.createInBatch(aOrderItems);

        Assertions.assertEquals(2, this.orderItemJpaEntityRepository.count());
    }
}
