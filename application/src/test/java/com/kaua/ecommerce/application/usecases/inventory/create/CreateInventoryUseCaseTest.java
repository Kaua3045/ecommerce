package com.kaua.ecommerce.application.usecases.inventory.create;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class CreateInventoryUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private DefaultCreateInventoryUseCase createInventoryUseCase;

    @Test
    void givenAValidCommand_whenCallExecute_thenShouldCreateInventory() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        Mockito.when(this.inventoryGateway.existsBySku(Mockito.any())).thenReturn(false);
        Mockito.when(this.inventoryGateway.create(Mockito.any())).then(returnsFirstArg());

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertNotNull(aOutput.inventoryId());
        Assertions.assertNotNull(aOutput.productId());
        Assertions.assertNotNull(aOutput.sku());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(1)).create(argThat(aCmd ->
                Objects.equals(aCmd.getProductId(), aProductId)
                        && Objects.equals(aCmd.getSku(), aSku)
                        && Objects.equals(aCmd.getQuantity(), aQuantity)
                        && Objects.nonNull(aCmd.getCreatedAt())
                        && Objects.nonNull(aCmd.getUpdatedAt())));
    }

    @Test
    void givenAnInvalidSkuAlreadyExists_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = "'sku' already exists";

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        Mockito.when(this.inventoryGateway.existsBySku(Mockito.any())).thenReturn(true);

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankProductId_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = " ";
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        Mockito.when(this.inventoryGateway.existsBySku(Mockito.any())).thenReturn(false);

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidBlankSku_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = " ";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        Mockito.when(this.inventoryGateway.existsBySku(Mockito.any())).thenReturn(false);

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidQuantityLessThanZero_whenCallExecute_shouldReturnDomainException() {
        final var aProductId = "1";
        final var aSku = "sku";
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        Mockito.when(this.inventoryGateway.existsBySku(Mockito.any())).thenReturn(false);

        final var aOutput = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(1)).existsBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).create(Mockito.any());
    }
}
