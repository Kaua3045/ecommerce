package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.domain.order.OrderPayment;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.DatabaseGatewayTest;
import com.kaua.ecommerce.infrastructure.order.persistence.OrderPaymentJpaEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseGatewayTest
public class OrderPaymentMySQLGatewayTest {

    @Autowired
    private OrderPaymentMySQLGateway orderPaymentMySQLGateway;

    @Autowired
    private OrderPaymentJpaEntityRepository orderPaymentJpaEntityRepository;

    @Test
    void givenAValidOrderPayment_whenCallCreate_thenOrderPaymentIsPersisted() {
        final var aPaymentMethodId = IdUtils.generateWithoutDash();
        final var aInstallments = 3;

        final var aOrderPayment = OrderPayment.newOrderPayment(aPaymentMethodId, aInstallments);

        Assertions.assertEquals(0, this.orderPaymentJpaEntityRepository.count());

        this.orderPaymentMySQLGateway.create(aOrderPayment);

        Assertions.assertEquals(1, this.orderPaymentJpaEntityRepository.count());
    }
}
