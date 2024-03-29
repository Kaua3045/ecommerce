package com.kaua.ecommerce.application.usecases.freight.calculate;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.FreightGateway;
import com.kaua.ecommerce.domain.exceptions.DomainException;
import com.kaua.ecommerce.domain.freight.Freight;
import com.kaua.ecommerce.domain.freight.FreightType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Set;

public class CalculateFreightUseCaseTest extends UseCaseTest {

    @Mock
    private FreightGateway freightGateway;

    @InjectMocks
    private DefaultCalculateFreightUseCase calculateFreightUseCase;

    @Test
    void givenAValidCommand_whenCallCalculateFreight_thenShouldReturnFreight() {
        final var aZipCode = "12345678";
        final var aType = "PAC";
        final var aHeight = 15.0;
        final var aWidth = 30.0;
        final var aLength = 45.0;
        final var aWeight = 55.5;

        final var aFreight = Freight.newFreight(aZipCode, FreightType.PAC, 10.0f, 3);

        final var aCommand = CalculateFreightCommand.with(
                aZipCode,
                aType,
                Set.of(CalculateFreightItemsCommand.with(
                        aHeight,
                        aWidth,
                        aLength,
                        aWeight
                ))
        );

        Mockito.when(this.freightGateway.calculateFreight(Mockito.any()))
                .thenReturn(aFreight);

        final var aOutput = this.calculateFreightUseCase.execute(aCommand);

        Assertions.assertEquals(aFreight.getCep(), aOutput.getCep());
        Assertions.assertEquals(aFreight.getType(), aOutput.getType());
        Assertions.assertEquals(aFreight.getPrice(), aOutput.getPrice());
        Assertions.assertEquals(aFreight.getDeadline(), aOutput.getDeadline());
    }

    @Test
    void givenAValidCommandWithTwoProduct_whenCallCalculateFreight_thenShouldReturnFreight() {
        final var aZipCode = "12345678";
        final var aType = "PAC";
        final var aHeight = 15.0;
        final var aWidth = 30.0;
        final var aLength = 45.0;
        final var aWeight = 55.5;

        final var aFreight = Freight.newFreight(aZipCode, FreightType.PAC, 10.0f, 3);

        final var aCommand = CalculateFreightCommand.with(
                aZipCode,
                aType,
                Set.of(CalculateFreightItemsCommand.with(
                                aWeight,
                                aWidth,
                                aHeight,
                                aLength
                        ),
                        CalculateFreightItemsCommand.with(
                                35.5,
                                40.0,
                                55.0,
                                65.5
                        ))
        );

        Mockito.when(this.freightGateway.calculateFreight(Mockito.any()))
                .thenReturn(aFreight);

        final var aOutput = this.calculateFreightUseCase.execute(aCommand);

        Assertions.assertEquals(aFreight.getCep(), aOutput.getCep());
        Assertions.assertEquals(aFreight.getType(), aOutput.getType());
        Assertions.assertEquals(aFreight.getPrice(), aOutput.getPrice());
        Assertions.assertEquals(aFreight.getDeadline(), aOutput.getDeadline());
    }

    @Test
    void givenAValidCommandWithTwoProductWithSameMeasures_whenCallCalculateFreight_thenShouldReturnFreight() {
        final var aZipCode = "12345678";
        final var aType = "PAC";
        final var aHeight = 15.0;
        final var aWidth = 30.0;
        final var aLength = 45.0;
        final var aWeight = 55.5;

        final var aFreight = Freight.newFreight(aZipCode, FreightType.PAC, 10.0f, 3);

        final var aCommand = CalculateFreightCommand.with(
                aZipCode,
                aType,
                Set.of(CalculateFreightItemsCommand.with(
                                aWeight,
                                aWidth,
                                aHeight,
                                aLength
                        ),
                        CalculateFreightItemsCommand.with(
                                1.0f,
                                aWidth,
                                aHeight,
                                aLength
                        ))
        );

        Mockito.when(this.freightGateway.calculateFreight(Mockito.any()))
                .thenReturn(aFreight);

        final var aOutput = this.calculateFreightUseCase.execute(aCommand);

        Assertions.assertEquals(aFreight.getCep(), aOutput.getCep());
        Assertions.assertEquals(aFreight.getType(), aOutput.getType());
        Assertions.assertEquals(aFreight.getPrice(), aOutput.getPrice());
        Assertions.assertEquals(aFreight.getDeadline(), aOutput.getDeadline());
    }

    @Test
    void givenAnInvalidType_whenCallCalculateFreight_thenShouldThrowDomainException() {
        final var aZipCode = "12345678";
        final var aType = "INVALID";
        final var aHeight = 10.0;
        final var aWidth = 10.0;
        final var aLength = 10.0;
        final var aWeight = 10.0;

        final var expectedErrorMessage = "type %s was not found".formatted(aType);

        final var aCommand = CalculateFreightCommand.with(
                aZipCode,
                aType,
                Set.of(CalculateFreightItemsCommand.with(
                        aHeight,
                        aWidth,
                        aLength,
                        aWeight
                ))
        );

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.calculateFreightUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidItems_whenCallCalculateFreight_thenShouldThrowDomainException() {
        final var aZipCode = "12345678";
        final var aType = "PAC";

        final var expectedErrorMessage = "No item found to calculate freight";

        final var aCommand = CalculateFreightCommand.with(
                aZipCode,
                aType,
                Set.of()
        );

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.calculateFreightUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
