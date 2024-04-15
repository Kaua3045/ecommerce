package com.kaua.ecommerce.application.usecases.inventory.rollback;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.adapters.TransactionManager;
import com.kaua.ecommerce.application.adapters.responses.TransactionResult;
import com.kaua.ecommerce.application.exceptions.TransactionFailureException;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.application.gateways.InventoryMovementGateway;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

public class RollbackInventoryBySkuUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryMovementGateway inventoryMovementGateway;

    @Mock
    private InventoryGateway inventoryGateway;

    @Mock
    private TransactionManager transactionManager;

    @InjectMocks
    private DefaultRollbackInventoryBySkuUseCase rollbackBySkuUseCase;

    @Test
    void givenAValidValues_whenCallExecute_shouldRollbackInventory() {
        final var aProductId = "1";
        final var aSku = "123-tshirt-red-m";

        final var aInventoryMovement = InventoryMovement.newInventoryMovement(
                InventoryID.unique(),
                aSku,
                10,
                InventoryMovementStatus.REMOVED
        );

        final var aCommand = RollbackInventoryBySkuCommand.with(aSku, aProductId);

        Mockito.when(inventoryMovementGateway.findBySkuAndCreatedAtDescAndStatusRemoved(aSku))
                .thenReturn(Optional.of(aInventoryMovement));
        Mockito.when(inventoryGateway.createInBatch(Mockito.anySet()))
                .then(returnsFirstArg());
        Mockito.when(inventoryMovementGateway.create(Mockito.any()))
                .then(returnsFirstArg());
        Mockito.doNothing().when(inventoryMovementGateway)
                .deleteById(aInventoryMovement.getId().getValue());
        Mockito.when(transactionManager.execute(Mockito.any())).thenAnswer(it -> {
            final var aSupplier = it.getArgument(0, Supplier.class);
            return TransactionResult.success(aSupplier.get());
        });

        Assertions.assertDoesNotThrow(() -> this.rollbackBySkuUseCase.execute(aCommand));

        Mockito.verify(inventoryMovementGateway, Mockito.times(1))
                .findBySkuAndCreatedAtDescAndStatusRemoved(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(1))
                .createInBatch(argThat(it -> it.size() == 1));
        Mockito.verify(inventoryMovementGateway, Mockito.times(1))
                .create(argThat(it -> it.getSku().equals(aSku)));
        Mockito.verify(inventoryMovementGateway, Mockito.times(1))
                .deleteById(aInventoryMovement.getId().getValue());
    }

    @Test
    void givenAnInvalidSku_whenCallExecute_shouldNotRollbackInventory() {
        final var aProductId = "1";
        final var aSku = "123-tshirt-red-m";

        final var aCommand = RollbackInventoryBySkuCommand.with(aSku, aProductId);

        Mockito.when(inventoryMovementGateway.findBySkuAndCreatedAtDescAndStatusRemoved(aSku))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> this.rollbackBySkuUseCase.execute(aCommand));

        Mockito.verify(inventoryMovementGateway, Mockito.times(1))
                .findBySkuAndCreatedAtDescAndStatusRemoved(aSku);
        Mockito.verify(inventoryGateway, Mockito.never()).createInBatch(Mockito.anySet());
        Mockito.verify(inventoryMovementGateway, Mockito.never()).create(Mockito.any());
        Mockito.verify(inventoryMovementGateway, Mockito.never()).deleteById(Mockito.any());
    }

    @Test
    void givenAValidValues_whenCallExecuteButCreateInBatchThrows_shouldThrowTransactionFailureException() {
        final var aProductId = "1";
        final var aSku = "123-tshirt-red-m";

        final var aInventoryMovement = InventoryMovement.newInventoryMovement(
                InventoryID.unique(),
                aSku,
                10,
                InventoryMovementStatus.REMOVED
        );

        final var aCommand = RollbackInventoryBySkuCommand.with(aSku, aProductId);

        final var expectedErrorMessage = "Error on create inventory";

        Mockito.when(inventoryMovementGateway.findBySkuAndCreatedAtDescAndStatusRemoved(aSku))
                .thenReturn(Optional.of(aInventoryMovement));
        Mockito.when(this.transactionManager.execute(Mockito.any())).thenReturn(TransactionResult
                .failure(new Error(expectedErrorMessage)));

        final var aOutput = Assertions.assertThrows(TransactionFailureException.class,
                () -> this.rollbackBySkuUseCase.execute(aCommand));

        Assertions.assertEquals(expectedErrorMessage, aOutput.getMessage());

        Mockito.verify(inventoryMovementGateway, Mockito.times(1))
                .findBySkuAndCreatedAtDescAndStatusRemoved(aSku);
        Mockito.verify(inventoryGateway, Mockito.times(0))
                .createInBatch(Mockito.anySet());
        Mockito.verify(inventoryMovementGateway, Mockito.never()).create(Mockito.any());
        Mockito.verify(inventoryMovementGateway, Mockito.never()).deleteById(Mockito.any());
    }
}
