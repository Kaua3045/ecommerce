package com.kaua.ecommerce.infrastructure.order;

import com.kaua.ecommerce.application.gateways.order.OrderFreightGateway;
import com.kaua.ecommerce.application.usecases.freight.calculate.CalculateFreightUseCase;
import com.kaua.ecommerce.domain.freight.Freight;
import com.kaua.ecommerce.domain.freight.FreightType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

@IntegrationTest
public class OrderFreightGatewayImplTest {

    @MockBean
    private CalculateFreightUseCase calculateFreightUseCase;

    @Autowired
    private OrderFreightGatewayImpl orderFreightGatewayImpl;

    @Test
    void givenAValidInput_whenCallCalculateFreight_thenReturnOrderFreightDetails() {
        final var aZipCode = "12345678";
        final var aType = "PAC";
        final var aHeight = 15.0;
        final var aWidth = 30.0;
        final var aLength = 45.0;
        final var aWeight = 55.5;

        final var aFreight = Freight.newFreight(aZipCode, FreightType.PAC, 10.0f, 3);

        final var aCommand = new OrderFreightGateway.CalculateOrderFreightInput(
                aZipCode,
                aType,
                Set.of(new OrderFreightGateway.CalculateOrderFreightItemsInput(
                        aWeight,
                        aWidth,
                        aHeight,
                        aLength
                ))
        );

        Mockito.when(this.calculateFreightUseCase.execute(Mockito.any()))
                .thenReturn(aFreight);

        final var aOutput = this.orderFreightGatewayImpl.calculateFreight(aCommand);

        Assertions.assertEquals(aFreight.getType().name(), aOutput.type());
        Assertions.assertEquals(aFreight.getPrice(), aOutput.price());
        Assertions.assertEquals(aFreight.getDeadline(), aOutput.deadlineInDays());
    }
}
