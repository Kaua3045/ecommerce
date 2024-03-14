package com.kaua.ecommerce.application.freight.list;

import com.kaua.ecommerce.application.usecases.freight.list.ListFreightsByCepCommand;
import com.kaua.ecommerce.application.usecases.freight.list.ListFreightsByCepUseCase;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ListFreightsByCepUseCaseIT {

    @Autowired
    private ListFreightsByCepUseCase listFreightsByCepUseCase;

    @Test
    void givenAValidValues_whenCallListFreightsByCepUseCase_thenShouldReturnFreights() {
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

        final var aFreights = this.listFreightsByCepUseCase.execute(aCommand);

        Assertions.assertNotNull(aFreights);
        Assertions.assertEquals(2, aFreights.size());
    }
}
