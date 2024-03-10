package com.kaua.ecommerce.application.usecases.inventory.retrieve.get;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.inventory.Inventory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

public class GetInventoryBySkuUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private DefaultGetInventoryBySkuUseCase getInventoryBySkuUseCase;

    @Test
    void givenAValidSku_whenCallGetInventoryBySkuExecute_thenReturnInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aSku = aInventory.getSku();

        Mockito.when(inventoryGateway.findBySku(aSku)).thenReturn(Optional.of(aInventory));

        final var aOutput = this.getInventoryBySkuUseCase.execute(aSku);

        Assertions.assertEquals(aInventory.getId().getValue(), aOutput.id());
        Assertions.assertEquals(aInventory.getProductId(), aOutput.productId());
        Assertions.assertEquals(aInventory.getSku(), aOutput.sku());
        Assertions.assertEquals(aInventory.getQuantity(), aOutput.quantity());
        Assertions.assertEquals(aInventory.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aInventory.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aInventory.getVersion(), aOutput.version());
    }

    @Test
    void givenAnInvalidSku_whenCallGetInventoryBySkuExecute_thenThrowNotFoundException() {
        final var aSku = "invalid-sku";

        final var expectedErrorMessage = Fixture.notFoundMessage(Inventory.class, aSku);

        Mockito.when(inventoryGateway.findBySku(aSku)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.getInventoryBySkuUseCase.execute(aSku));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }
}
