package com.kaua.ecommerce.application.usecases.inventory.delete.clean;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Set;
import java.util.function.Supplier;

public class CleanInventoriesByProductIdUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @Mock
    private InventoryMovementGateway inventoryMovementGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultCleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase;

    @Test
    void givenAValidProductId_whenCallCleanInventoriesByProductId_shouldBeOk() {
        final var aProductId = ProductID.unique().getValue();
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var aInventoryMovement = InventoryMovement.newInventoryMovement(
                aInventory.getId(),
                aInventory.getSku(),
                aInventory.getQuantity(),
                InventoryMovementStatus.REMOVED
        );

        Mockito.when(inventoryGateway.findByProductId(aProductId))
                .thenReturn(Set.of(aInventory));
        Mockito.doNothing().when(inventoryGateway).cleanByProductId(aProductId);
        Mockito.when(inventoryMovementGateway.createInBatch(Mockito.any()))
                        .thenReturn(Set.of(aInventoryMovement));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        Assertions.assertDoesNotThrow(() -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));

        Mockito.verify(inventoryGateway, Mockito.times(1)).findByProductId(aProductId);
        Mockito.verify(inventoryGateway, Mockito.times(1)).cleanByProductId(aProductId);
        Mockito.verify(inventoryMovementGateway, Mockito.times(1)).createInBatch(Mockito.any());
    }

    @Test
    void givenAnInvalidProductId_whenCallCleanInventoriesByProductId_shouldBeOk() {
        final var aProductId = "1";

        Mockito.when(inventoryGateway.findByProductId(aProductId))
                .thenReturn(Set.of());

        Assertions.assertDoesNotThrow(() -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));

        Mockito.verify(inventoryGateway, Mockito.times(1)).findByProductId(aProductId);
        Mockito.verify(inventoryGateway, Mockito.times(0)).cleanByProductId(aProductId);
        Mockito.verify(inventoryMovementGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }

    @Test
    void givenAValidProductId_whenCallCleanInventoriesByProductIdButThrowsOnCreateInventoryMovement_thenShouldThrowTransactionFailureException() {
        final var aProductId = ProductID.unique().getValue();
        final var aInventory = Fixture.Inventories.tshirtInventory();

        final var expectedErrorMessage = "Error on create inventories movement";

        Mockito.when(inventoryGateway.findByProductId(aProductId))
                .thenReturn(Set.of(aInventory));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(RuntimeException.class,
                () -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(inventoryGateway, Mockito.times(1)).findByProductId(aProductId);
        Mockito.verify(inventoryGateway, Mockito.times(0)).cleanByProductId(aProductId);
        Mockito.verify(inventoryMovementGateway, Mockito.times(0)).createInBatch(Mockito.any());
    }
}
