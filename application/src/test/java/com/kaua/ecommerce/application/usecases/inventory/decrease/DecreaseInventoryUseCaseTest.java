package com.kaua.ecommerce.application.usecases.inventory.decrease;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class DecreaseInventoryUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @Mock
    private InventoryMovementGateway inventoryMovementGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultDecreaseInventoryQuantityUseCase decreaseInventoryQuantityUseCase;

    @Test
    void givenAValidCommand_whenCallExecuteDecrease_thenShouldDecreaseInventoryQuantity() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aOldInventoryQuantity = aInventory.getQuantity();

        final var aSku = aInventory.getSku();
        final var aQuantity = 5;

        final var aInventoryUpdatedAt = aInventory.getUpdatedAt();

        final var aCommand = DecreaseInventoryQuantityCommand.with(aSku, aQuantity);

        Mockito.when(inventoryGateway.findBySku(aSku)).thenReturn(Optional.of(aInventory));
        Mockito.when(inventoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(inventoryMovementGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        final var aOutput = this.decreaseInventoryQuantityUseCase.execute(aCommand).getRight();

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aInventory.getId().getValue(), aOutput.inventoryId());
        Assertions.assertEquals(aInventory.getSku(), aOutput.sku());
        Assertions.assertEquals(aInventory.getProductId(), aOutput.productId());

        Mockito.verify(inventoryGateway, Mockito.times(1)).findBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(1)).update(argThat(aCmd ->
                Objects.equals(aCmd.getQuantity(), aOldInventoryQuantity - aQuantity)
                        && aCmd.getUpdatedAt().isAfter(aInventoryUpdatedAt)));
        Mockito.verify(inventoryMovementGateway, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidSku_whenCallExecuteDecrease_shouldThrowNotFoundException() {
        final var aSku = "sku";
        final var aQuantity = 10;

        final var expectedErrorMessage = Fixture.notFoundMessage(Inventory.class, aSku);

        final var aCommand = DecreaseInventoryQuantityCommand.with(aSku, aQuantity);

        Mockito.when(inventoryGateway.findBySku(aSku)).thenReturn(Optional.empty());

        final var aOutput = Assertions.assertThrows(NotFoundException.class,
                () -> this.decreaseInventoryQuantityUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(inventoryGateway, Mockito.times(1)).findBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(inventoryMovementGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidQuantity_whenCallExecuteDecrease_shouldReturnDomainException() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aSku = aInventory.getSku();
        final var aQuantity = 0;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", 0);

        final var aCommand = DecreaseInventoryQuantityCommand.with(aSku, aQuantity);

        final var aOutput = this.decreaseInventoryQuantityUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorMessage, aOutput.getErrors().get(0).message());

        Mockito.verify(inventoryGateway, Mockito.times(0)).findBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(inventoryMovementGateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    void givenAValidCommand_whenCallExecuteDecreaseButThrowsOnCreateInventoryMovement_thenShouldThrowTransactionFailureException() {
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aSku = aInventory.getSku();
        final var aQuantity = 5;

        final var aCommand = DecreaseInventoryQuantityCommand.with(aSku, aQuantity);

        final var expectedErrorMessage = "Error on create inventory movement";

        Mockito.when(inventoryGateway.findBySku(aSku)).thenReturn(Optional.of(aInventory));
        Mockito.when(transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.decreaseInventoryQuantityUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(inventoryGateway, Mockito.times(1)).findBySku(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0)).update(Mockito.any());
        Mockito.verify(inventoryMovementGateway, Mockito.times(0)).create(Mockito.any());
    }
}
