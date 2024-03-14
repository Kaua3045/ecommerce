package com.kaua.ecommerce.infrastructure.freight;

import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.application.gateways.commands.CalculateFreightCommand;
import com.kaua.ecommerce.application.gateways.commands.ListFreightsCommand;
import com.kaua.ecommerce.domain.freight.FreightType;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class FreightGatewayImplTest {

    @Autowired
    private FreightGateway freightGateway;

    @Test
    void givenAValidSedexFreightType_whenCalculateFreight_thenShouldReturnFreight() {
        final var aCommand = CalculateFreightCommand.with(
                "12345678",
                FreightType.SEDEX,
                110.0,
                150.0,
                20.0,
                50.0
        );

        final var aFreight = freightGateway.calculateFreight(aCommand);

        Assertions.assertNotNull(aFreight);
        Assertions.assertEquals(25.0, aFreight.getPrice());
        Assertions.assertEquals("12345678", aFreight.getCep());
        Assertions.assertEquals(FreightType.SEDEX, aFreight.getType());
        Assertions.assertEquals(3, aFreight.getDeadline());
    }

    @Test
    void givenAValidPACFreightType_whenCalculateFreight_thenShouldReturnFreight() {
        final var aCommand = CalculateFreightCommand.with(
                "12345678",
                FreightType.PAC,
                10.0,
                10.0,
                10.0,
                10.0
        );

        final var aFreight = freightGateway.calculateFreight(aCommand);

        Assertions.assertNotNull(aFreight);
        Assertions.assertEquals(15.0, aFreight.getPrice());
        Assertions.assertEquals("12345678", aFreight.getCep());
        Assertions.assertEquals(FreightType.PAC, aFreight.getType());
        Assertions.assertEquals(5, aFreight.getDeadline());
    }

    @Test
    void givenAValidCalculateFreightCommand_whenListFreights_thenShouldReturnFreights() {
        final var aCommand = ListFreightsCommand.with(
                "12345678",
                10.0,
                10.0,
                10.0,
                10.0
        );

        final var aFreights = freightGateway.listFreights(aCommand);

        Assertions.assertNotNull(aFreights);
        Assertions.assertEquals(2, aFreights.size());
    }

    @Test
    void givenAnInvalidFreightType_whenCalculateFreight_thenShouldThrowException() {
        final var aCommand = CalculateFreightCommand.with(
                "12345678",
                FreightType.UNKNOWN,
                10.0,
                10.0,
                10.0,
                10.0
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> freightGateway.calculateFreight(aCommand));
    }
}
