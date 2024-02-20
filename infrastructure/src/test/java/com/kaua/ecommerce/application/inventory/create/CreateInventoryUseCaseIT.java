package com.kaua.ecommerce.application.inventory.create;

import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryCommand;
import com.kaua.ecommerce.application.usecases.inventory.create.CreateInventoryUseCase;
import com.kaua.ecommerce.domain.Fixture;
import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.utils.CommonErrorMessage;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import com.kaua.ecommerce.infrastructure.inventory.persistence.InventoryJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CreateInventoryUseCaseIT {

    @Autowired
    private CreateInventoryUseCase createInventoryUseCase;

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Test
    void givenAValidValues_whenCallsCreateInventoryUseCase_thenInventoryShouldBeCreated() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = Fixture.createSku("teste");
        final var aQuantity = 10;

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        Assertions.assertEquals(0, this.inventoryRepository.count());

        final var aResult = this.createInventoryUseCase.execute(aCommand).getRight();

        Assertions.assertEquals(1, this.inventoryRepository.count());

        Assertions.assertNotNull(aResult.inventoryId());
        Assertions.assertEquals(aProductId, aResult.productId());
        Assertions.assertEquals(aSku, aResult.sku());

        final var aPersistedInventory = this.inventoryRepository.findById(aResult.inventoryId()).get();

        Assertions.assertEquals(aResult.inventoryId(), aPersistedInventory.getId());
        Assertions.assertEquals(aProductId, aPersistedInventory.getProductId());
        Assertions.assertEquals(aSku, aPersistedInventory.getSku());
        Assertions.assertEquals(aQuantity, aPersistedInventory.getQuantity());
        Assertions.assertNotNull(aPersistedInventory.getCreatedAt());
        Assertions.assertNotNull(aPersistedInventory.getUpdatedAt());
    }

    @Test
    void givenAnInvalidProductId_whenCallsCreateInventoryUseCase_shouldReturnDomainException() {
        final var aProductId = " ";
        final var aSku = Fixture.createSku("teste");
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("productId");
        final var expectedErrorCount = 1;

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        final var aResult = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.inventoryRepository.count());
    }

    @Test
    void givenAnInvalidSKU_whenCallsCreateInventoryUseCase_shouldReturnDomainException() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = " ";
        final var aQuantity = 10;

        final var expectedErrorMessage = CommonErrorMessage.nullOrBlank("sku");
        final var expectedErrorCount = 1;

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        final var aResult = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.inventoryRepository.count());
    }

    @Test
    void givenAnInvalidQuantityLessThanZero_whenCallsCreateInventoryUseCase_shouldReturnDomainException() {
        final var aProductId = ProductID.unique().getValue();
        final var aSku = Fixture.createSku("teste");
        final var aQuantity = -1;

        final var expectedErrorMessage = CommonErrorMessage.greaterThan("quantity", -1);
        final var expectedErrorCount = 1;

        final var aCommand = CreateInventoryCommand.with(
                aProductId,
                aSku,
                aQuantity
        );

        final var aResult = this.createInventoryUseCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, aResult.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, aResult.getErrors().get(0).message());
        Assertions.assertEquals(0, this.inventoryRepository.count());
    }
}
