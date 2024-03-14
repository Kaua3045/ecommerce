package com.kaua.ecommerce.application.usecases.freight.list;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.application.usecases.freight.list.DefaultListFreightsByCepUseCase;
import com.kaua.ecommerce.application.usecases.freight.list.ListFreightsByCepCommand;
import com.kaua.ecommerce.domain.freight.Freight;
import com.kaua.ecommerce.domain.freight.FreightType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class ListFreightsByCepUseCaseTest extends UseCaseTest {

    @Mock
    private FreightGateway freightGateway;

    @InjectMocks
    private DefaultListFreightsByCepUseCase listFreightsByCepUseCase;

    @Test
    void givenAValidListFreightsByCepCommand_whenExecute_thenShouldReturnFreights() {
        final var aCep = "12345678";
        final var aHeight = 10.0;
        final var aWidth = 10.0;
        final var aLength = 10.0;
        final var aWeight = 10.0;

        final var aCommand = ListFreightsByCepCommand.with(
                aCep,
                aHeight,
                aWidth,
                aLength,
                aWeight
        );

        Mockito.when(freightGateway.listFreights(Mockito.any())).thenReturn(List.of(
                Freight.newFreight(aCep, FreightType.SEDEX, 25.0F, 3),
                Freight.newFreight(aCep, FreightType.PAC, 15.0F, 5)
        ));

        final var aFreights = this.listFreightsByCepUseCase.execute(aCommand);

        Assertions.assertNotNull(aFreights);
        Assertions.assertEquals(2, aFreights.size());
    }
}
