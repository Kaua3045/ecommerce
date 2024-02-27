package com.kaua.ecommerce.application.usecases.inventory.delete.remove;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
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

public class RemoveInventoryBySkuUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @Mock
    private InventoryMovementGateway inventoryMovementGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultRemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;

    @Test
    void givenAValidSku_whenCallExecute_thenShouldDeleteInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aSku = aInventory.getSku();

        Mockito.when(this.inventoryGateway.findBySku(aSku)).thenReturn(Optional.of(aInventory));
        Mockito.doNothing().when(this.inventoryGateway).deleteBySku(aSku);
        Mockito.when(this.inventoryMovementGateway.create(Mockito.any())).thenAnswer(returnsFirstArg());
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        Assertions.assertDoesNotThrow(() -> this.removeInventoryBySkuUseCase.execute(aSku));

        Mockito.verify(this.inventoryGateway, Mockito.times(1)).findBySku(aSku);
        Mockito.verify(this.inventoryGateway, Mockito.times(1)).deleteBySku(aSku);
        Mockito.verify(this.inventoryMovementGateway, Mockito.times(1))
                .create(argThat(aCmd ->
                        Objects.equals(aInventory.getId().getValue(), aCmd.getInventoryId().getValue())
                                && Objects.equals(aInventory.getSku(), aCmd.getSku())
                                && Objects.equals(aInventory.getQuantity(), aCmd.getQuantity())
                                && Objects.equals(aCmd.getStatus(), InventoryMovementStatus.REMOVED)));
    }

    @Test
    void givenAnInvalidSku_whenCallExecute_thenShouldBeOk() {
        final var aSku = "invalid-sku";

        Mockito.when(this.inventoryGateway.findBySku(aSku)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> this.removeInventoryBySkuUseCase.execute(aSku));

        Mockito.verify(this.inventoryGateway, Mockito.times(1)).findBySku(aSku);
        Mockito.verify(this.inventoryGateway, Mockito.times(0)).deleteBySku(aSku);
        Mockito.verify(this.inventoryMovementGateway, Mockito.times(0))
                .create(Mockito.any());
    }

    @Test
    void givenAValidSku_whenCallExecuteButThrowsOnCreateInventoryMovement_thenShouldThrowTransactionFailureException() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aSku = aInventory.getSku();

        final var expectedErrorMessage = "Error on create inventories movement";

        Mockito.when(this.inventoryGateway.findBySku(aSku)).thenReturn(Optional.of(aInventory));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(
                TransactionFailureException.class,
                () -> this.removeInventoryBySkuUseCase.execute(aSku));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(inventoryGateway, Mockito.times(1)).findBySku(Mockito.any());
        Mockito.verify(inventoryGateway, Mockito.times(0))
                .deleteBySku(Mockito.any());
        Mockito.verify(inventoryMovementGateway, Mockito.times(0))
                .create(Mockito.any());
    }
}
