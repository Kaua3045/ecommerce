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

    @Test
    void givenAValidOrderDelivery_whenCallFindById_thenOrderDeliveryIsReturned() {
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

        this.orderDeliveryMySQLGateway.create(aOrderDelivery);

        final var aResult = this.orderDeliveryMySQLGateway.findById(aOrderDelivery.getId().getValue());

        Assertions.assertTrue(aResult.isPresent());
        Assertions.assertEquals(aOrderDelivery.getId().getValue(), aResult.get().getId().getValue());
        Assertions.assertEquals(aFreightType, aResult.get().getFreightType());
        Assertions.assertEquals(aFreightPrice, aResult.get().getFreightPrice());
        Assertions.assertEquals(aDeliveryEstimatedDays, aResult.get().getDeliveryEstimated());
        Assertions.assertEquals(aDeliveryStreet, aResult.get().getStreet());
        Assertions.assertEquals(aDeliveryNumber, aResult.get().getNumber());
        Assertions.assertEquals(aDeliveryComplement, aResult.get().getComplement().get());
        Assertions.assertEquals(aDeliveryDistrict, aResult.get().getDistrict());
        Assertions.assertEquals(aDeliveryCity, aResult.get().getCity());
        Assertions.assertEquals(aDeliveryState, aResult.get().getState());
        Assertions.assertEquals(aDeliveryZipCode, aResult.get().getZipCode());
    }
}
