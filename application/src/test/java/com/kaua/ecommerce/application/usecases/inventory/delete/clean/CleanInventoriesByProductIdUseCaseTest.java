package com.kaua.ecommerce.application.usecases.inventory.delete.clean;

import com.kaua.ecommerce.application.UseCaseTest;
import com.kaua.ecommerce.application.gateways.InventoryGateway;
import com.kaua.ecommerce.domain.product.ProductID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class CleanInventoriesByProductIdUseCaseTest extends UseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private DefaultCleanInventoriesByProductIdUseCase cleanInventoriesByProductIdUseCase;

    @Test
    void givenAValidProductId_whenCallCleanInventoriesByProductId_shouldBeOk() {
        final var aProductId = ProductID.unique().getValue();

        Mockito.doNothing().when(inventoryGateway).cleanByProductId(aProductId);

        Assertions.assertDoesNotThrow(() -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));
    }

    @Test
    void givenAnInvalidProductId_whenCallCleanInventoriesByProductId_shouldBeOk() {
        final var aProductId = "1";

        Mockito.doNothing().when(inventoryGateway).cleanByProductId(aProductId);

        Assertions.assertDoesNotThrow(() -> this.cleanInventoriesByProductIdUseCase.execute(aProductId));
    }
}
