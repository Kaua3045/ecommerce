package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.domain.order.OrderDelivery;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderDeliveryJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseGatewayTest
public class OrderDeliveryMySQLGatewayTest {

    @Autowired
    private OrderDeliveryMySQLGateway orderDeliveryMySQLGateway;

    @Autowired
    private OrderDeliveryJpaEntityRepository orderDeliveryJpaEntityRepository;

    @Test
    void givenAValidOrderDelivery_whenCallCreate_thenOrderDeliveryIsPersisted() {
        final var aFreightType = "SEDEX";
        final var aFreightPrice = 10.0f;
        final var aDeliveryEstimatedDays = 3;
        final var aDeliveryStreet = "Rua dos Bobos";
        final var aDeliveryNumber = "0";
        final var aDeliveryComplement = "Apto 123";
        final var aDeliveryDistrict = "Centro";
        final var aDeliveryCity = "São Paulo";
        final var aDeliveryState = "SP";
        final var aDeliveryZipCode = "00000-000";

        final var aOrderDelivery = OrderDelivery.newOrderDelivery(
                aFreightType,
                aFreightPrice,
                aDeliveryEstimatedDays,
                aDeliveryStreet,
                aDeliveryNumber,
                aDeliveryComplement,
                aDeliveryDistrict,
                aDeliveryCity,
                aDeliveryState,
                aDeliveryZipCode
        );

        Assertions.assertEquals(0, this.orderDeliveryJpaEntityRepository.count());

        this.orderDeliveryMySQLGateway.create(aOrderDelivery);

        Assertions.assertEquals(1, this.orderDeliveryJpaEntityRepository.count());
    }
}
