package com.kaua.ecommerce.application.usecases.inventory.delete.remove;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.Fixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class RemoveInventoryBySkuUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private DefaultRemoveInventoryBySkuUseCase removeInventoryBySkuUseCase;

    @Test
    void givenAValidSku_whenCallExecute_thenShouldDeleteInventory() {
        final var aInventory = Fixture.Inventories.tshirtInventory();
        final var aSku = aInventory.getSku();

        Mockito.doNothing().when(this.inventoryGateway).deleteBySku(aSku);

        Assertions.assertDoesNotThrow(() -> this.removeInventoryBySkuUseCase.execute(aSku));

        Mockito.verify(this.inventoryGateway, Mockito.times(1)).deleteBySku(aSku);
    }

    @Test
    void givenAnInvalidSku_whenCallExecute_thenShouldBeOk() {
        final var aSku = "invalid-sku";

        Assertions.assertDoesNotThrow(() -> this.removeInventoryBySkuUseCase.execute(aSku));

        Mockito.verify(this.inventoryGateway, Mockito.times(1)).deleteBySku(aSku);
    }
}
